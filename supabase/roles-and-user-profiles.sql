-- Aiyige role and user-profile migration
-- Safe to run after seed_project_data.sql and add-relationships.sql.
-- This migration does not delete or overwrite existing business records.

create table if not exists public.role_definitions (
  role_key text primary key,
  role_name text not null unique,
  description text not null default '',
  created_at timestamptz not null default now()
);

insert into public.role_definitions (role_key, role_name, description)
values
  ('owner', 'Owner', 'Full business ownership and reporting access'),
  ('admin', 'Admin', 'System administration and staff account management'),
  ('sales_manager', 'Sales Manager', 'Sales, quotations, orders, payments, and reports'),
  ('sales_staff', 'Sales Staff', 'Customer quotations, orders, and payment recording'),
  ('inventory_staff', 'Inventory Staff', 'Product catalog and inventory management'),
  ('customer', 'Customer', 'Customer account, panel selection, room capture, orders, and payments')
on conflict (role_key) do update
set role_name = excluded.role_name,
    description = excluded.description;

create table if not exists public.user_profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text,
  role_key text not null references public.role_definitions(role_key),
  status text not null default 'active' check (status in ('active', 'inactive', 'pending')),
  phone text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists user_profiles_role_key_idx
  on public.user_profiles(role_key);

create or replace function public.set_user_profiles_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists user_profiles_updated_at on public.user_profiles;
create trigger user_profiles_updated_at
before update on public.user_profiles
for each row execute function public.set_user_profiles_updated_at();

alter table public.role_definitions enable row level security;
alter table public.user_profiles enable row level security;

-- SECURITY DEFINER prevents recursive RLS evaluation when checking the
-- current user's role inside user_profiles policies.
create or replace function public.can_manage_user_profiles()
returns boolean
language sql
security definer
set search_path = public
as $$
  select exists (
    select 1
    from public.user_profiles
    where id = auth.uid()
      and role_key in ('owner', 'admin')
      and status = 'active'
  );
$$;

revoke all on function public.can_manage_user_profiles() from public;
grant execute on function public.can_manage_user_profiles() to authenticated;

drop policy if exists "Authenticated users can read role definitions" on public.role_definitions;
create policy "Authenticated users can read role definitions"
on public.role_definitions for select to authenticated using (true);

drop policy if exists "Users can read their own profile" on public.user_profiles;
create policy "Users can read their own profile"
on public.user_profiles for select to authenticated
using (id = auth.uid());

drop policy if exists "Owners and admins can read user profiles" on public.user_profiles;
create policy "Owners and admins can read user profiles"
on public.user_profiles for select to authenticated
using (
  public.can_manage_user_profiles()
);

drop policy if exists "Owners and admins can manage user profiles" on public.user_profiles;
create policy "Owners and admins can manage user profiles"
on public.user_profiles for all to authenticated
using (
  public.can_manage_user_profiles()
)
with check (
  public.can_manage_user_profiles()
);

-- New customers can create their own profile after signing up.
drop policy if exists "Users can create their own profile" on public.user_profiles;
create policy "Users can create their own profile"
on public.user_profiles for insert to authenticated
with check (id = auth.uid() and role_key = 'customer');
