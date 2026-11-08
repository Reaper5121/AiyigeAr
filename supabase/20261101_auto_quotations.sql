-- Automatic quotations: prepared before the customer confirms an order.
-- Existing quotation rows are intentionally left untouched; new columns are
-- nullable so this migration does not require a backfill.

alter table public.quotations
  add column if not exists quotation_no text,
  add column if not exists order_id bigint,
  add column if not exists source text not null default 'CATALOG',
  add column if not exists items_snapshot jsonb not null default '[]'::jsonb,
  add column if not exists delivery_fee numeric not null default 0,
  add column if not exists total numeric not null default 0,
  add column if not exists created_at timestamptz not null default now();

create unique index if not exists quotations_quotation_no_unique_idx
  on public.quotations (quotation_no)
  where quotation_no is not null;

create unique index if not exists quotations_order_id_unique_idx
  on public.quotations (order_id)
  where order_id is not null;

do $$ begin
  alter table public.quotations
    add constraint quotations_order_id_fkey
    foreign key (order_id) references public.orders(id) on delete restrict;
exception when duplicate_object then null; end $$;

alter table public.quotations enable row level security;

drop policy if exists "Authenticated users can read automatic quotations" on public.quotations;
create policy "Authenticated users can read automatic quotations"
on public.quotations for select to authenticated using (true);

drop policy if exists "Customers can insert automatic quotations" on public.quotations;
create policy "Customers can insert automatic quotations"
on public.quotations for insert to authenticated with check (true);

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
    or old.delivery_fee is distinct from new.delivery_fee
    or old.vat is distinct from new.vat
    or old.total is distinct from new.total then
    raise exception 'Automatic quotations are immutable';
  end if;
  return new;
end;
$$;

drop trigger if exists quotations_immutable_snapshot on public.quotations;
create trigger quotations_immutable_snapshot
before update on public.quotations
for each row execute function public.reject_automatic_quotation_edits();

