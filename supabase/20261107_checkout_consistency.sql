-- Align Supabase with quotation-first checkout and customer-selected couriers.
-- Apply after 20261102_integrity_and_ar.sql.

alter table public.payment_requests
  add column if not exists street_address text not null default '',
  add column if not exists city_province text not null default '',
  add column if not exists postal_code text not null default '',
  add column if not exists payment_method text not null default '',
  add column if not exists delivery_method text not null default '',
  add column if not exists summary text not null default '',
  add column if not exists room_capture_url text,
  add column if not exists source text not null default 'Customer App',
  add column if not exists updated_at timestamptz not null default now();

alter table public.quotations
  alter column courier set default '';

alter table public.payment_requests
  alter column quote_number drop not null;

create or replace function public.reject_automatic_quotation_edits()
returns trigger
language plpgsql
as $$
begin
  if old.quotation_no is distinct from new.quotation_no
    or old.source is distinct from new.source
    or old.items_snapshot is distinct from new.items_snapshot
    or old.subtotal is distinct from new.subtotal
    or old.discount is distinct from new.discount
    or old.delivery_fee is distinct from new.delivery_fee
    or old.vat is distinct from new.vat
    or old.total is distinct from new.total
    or old.customer_name is distinct from new.customer_name
    or old.room_capture_url is distinct from new.room_capture_url
    or (old.order_id is not null and old.order_id is distinct from new.order_id) then
    raise exception 'Automatic quotation snapshots are immutable';
  end if;
  return new;
end;
$$;
