create extension if not exists pgcrypto;

create table if not exists public.payment_requests (
  id uuid primary key default gen_random_uuid(),
  quote_number text unique,
  customer_name text not null,
  customer_email text not null,
  customer_phone text not null default '',
  item_name text not null,
  panels integer not null default 0,
  unit_price integer not null default 0,
  subtotal integer not null default 0,
  vat integer not null default 0,
  total_amount integer not null default 0,
  summary text not null default '',
  street_address text not null default '',
  city_province text not null default '',
  postal_code text not null default '',
  payment_method text not null default '',
  delivery_method text not null default '',
  room_capture_url text,
  notes text not null default '',
  status text not null default 'Pending Review',
  source text not null default 'Customer App',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

alter table public.payment_requests
  add column if not exists customer_phone text not null default '',
  add column if not exists street_address text not null default '',
  add column if not exists city_province text not null default '',
  add column if not exists postal_code text not null default '',
  add column if not exists payment_method text not null default '',
  add column if not exists delivery_method text not null default '',
  add column if not exists summary text not null default '',
  add column if not exists room_capture_url text,
  add column if not exists source text not null default 'Customer App',
  add column if not exists updated_at timestamptz not null default now();

create index if not exists payment_requests_customer_email_idx on public.payment_requests (customer_email);
create index if not exists payment_requests_status_idx on public.payment_requests (status);

create or replace function public.set_payment_requests_updated_at()
returns trigger
language plpgsql
as $$
begin
  new.updated_at = now();
  return new;
end;
$$;

drop trigger if exists set_payment_requests_updated_at on public.payment_requests;
create trigger set_payment_requests_updated_at
before update on public.payment_requests
for each row execute function public.set_payment_requests_updated_at();

alter table public.payment_requests enable row level security;

drop policy if exists "Authenticated users can read payment requests" on public.payment_requests;
create policy "Authenticated users can read payment requests"
on public.payment_requests
for select
to authenticated
using (true);

drop policy if exists "Authenticated users can insert payment requests" on public.payment_requests;
create policy "Authenticated users can insert payment requests"
on public.payment_requests
for insert
to authenticated
with check (true);

drop policy if exists "Authenticated users can update payment requests" on public.payment_requests;
create policy "Authenticated users can update payment requests"
on public.payment_requests
for update
to authenticated
using (true)
with check (true);
