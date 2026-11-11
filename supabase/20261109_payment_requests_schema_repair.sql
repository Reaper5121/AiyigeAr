-- Add fields expected by the customer app's payment request payload.
-- Safe to run more than once; this does not delete or rewrite existing rows.
alter table public.payment_requests
  add column if not exists customer_phone text not null default '',
  add column if not exists summary text not null default '',
  add column if not exists street_address text not null default '',
  add column if not exists city_province text not null default '',
  add column if not exists postal_code text not null default '',
  add column if not exists payment_method text not null default '',
  add column if not exists delivery_method text not null default '',
  add column if not exists room_capture_url text,
  add column if not exists source text not null default 'Customer App',
  add column if not exists updated_at timestamptz not null default now();
