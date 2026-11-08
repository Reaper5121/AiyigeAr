-- Keeps the existing Supabase schema compatible with automatic quotations
-- and AR room captures. Run after seed_project_data.sql, payment_requests.sql,
-- add-relationships.sql, and 20261101_auto_quotations.sql.

alter table public.quotations
  add column if not exists quotation_no text,
  add column if not exists order_id bigint,
  add column if not exists source text not null default 'CATALOG',
  add column if not exists items_snapshot jsonb not null default '[]'::jsonb,
  add column if not exists delivery_fee numeric not null default 0,
  add column if not exists total numeric not null default 0,
  add column if not exists created_at timestamptz not null default now(),
  add column if not exists payment_method text not null default '',
  add column if not exists payment_status text not null default 'Payment pending',
  add column if not exists courier text not null default '',
  add column if not exists room_capture_url text,
  add column if not exists printed_at timestamptz;

-- Backfill the canonical columns from the original quotation columns.
update public.quotations
set quotation_no = coalesce(nullif(quotation_no, ''), quotation_number),
    items_snapshot = case
      when items_snapshot = '[]'::jsonb and items is not null then items
      else items_snapshot
    end,
    total = case
      when coalesce(total, 0) = 0 then coalesce(grand_total, 0)
      else total
    end
where quotation_no is null
   or quotation_no = ''
   or items_snapshot = '[]'::jsonb
   or coalesce(total, 0) = 0;

create unique index if not exists quotations_quotation_no_unique_idx
  on public.quotations (quotation_no)
  where quotation_no is not null and quotation_no <> '';

create unique index if not exists quotations_order_id_unique_idx
  on public.quotations (order_id)
  where order_id is not null;

-- Keep legacy references usable when a canonical quotation is inserted.
create or replace function public.sync_quotation_legacy_columns()
returns trigger
language plpgsql
as $$
begin
  if coalesce(new.quotation_number, '') = '' then
    new.quotation_number = new.quotation_no;
  end if;
  if coalesce(new.quotation_no, '') = '' then
    new.quotation_no = new.quotation_number;
  end if;
  if new.items_snapshot = '[]'::jsonb and new.items is not null then
    new.items_snapshot = new.items;
  end if;
  if coalesce(new.total, 0) = 0 then
    new.total = coalesce(new.grand_total, 0);
  end if;
  return new;
end;
$$;

drop trigger if exists sync_quotation_legacy_columns on public.quotations;
create trigger sync_quotation_legacy_columns
before insert or update on public.quotations
for each row execute function public.sync_quotation_legacy_columns();

-- Quotations are prepared by the trusted backend before order confirmation.
-- Customers and browser clients may read them, but must not create or edit snapshots.
alter table public.quotations enable row level security;
drop policy if exists "Authenticated users can manage quotations" on public.quotations;
drop policy if exists "Customers can insert automatic quotations" on public.quotations;
drop policy if exists "Authenticated users can read automatic quotations" on public.quotations;
create policy "Authenticated users can read automatic quotations"
on public.quotations for select to authenticated using (true);

-- AR captures are references attached to a real order later; they are not
-- orders or quotations by themselves.
alter table public.room_captures
  add column if not exists room_capture_url text,
  add column if not exists uploaded_at timestamptz;

-- Ensure relationship columns exist even when this migration is applied to a
-- project that did not run add-relationships.sql yet.
alter table public.orders add column if not exists quotation_id bigint;
alter table public.orders add column if not exists room_capture_url text;
alter table public.quotations add column if not exists customer_id bigint;

do $$ begin
  alter table public.orders
    add constraint orders_quotation_id_fkey
    foreign key (quotation_id) references public.quotations(id) on delete set null;
exception when duplicate_object then null; end $$;

do $$ begin
  alter table public.quotations
    add constraint quotations_order_id_fkey
    foreign key (order_id) references public.orders(id) on delete restrict;
exception when duplicate_object then null; end $$;

create or replace function public.reject_automatic_quotation_edits()
returns trigger
language plpgsql
as $$
begin
  if old.quotation_no is distinct from new.quotation_no
    or old.order_id is distinct from new.order_id
    or old.source is distinct from new.source
    or old.items_snapshot is distinct from new.items_snapshot
    or old.subtotal is distinct from new.subtotal
    or old.discount is distinct from new.discount
    or old.delivery_fee is distinct from new.delivery_fee
    or old.vat is distinct from new.vat
    or old.total is distinct from new.total
    or old.customer_name is distinct from new.customer_name
    or old.room_capture_url is distinct from new.room_capture_url then
    raise exception 'Automatic quotations are immutable';
  end if;
  return new;
end;
$$;

drop trigger if exists quotations_immutable_snapshot on public.quotations;
create trigger quotations_immutable_snapshot
before update on public.quotations
for each row execute function public.reject_automatic_quotation_edits();
