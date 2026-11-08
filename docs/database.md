# Database and Persistence

## Local development

The admin backend stores development state in `data/admin-state.json`. It
supports automatic quotation hydration for older `savedQuotations` records.

## Supabase

Run migrations in this order when initializing or upgrading the project:

1. `seed_project_data.sql`
2. `payment_requests.sql`
3. `add-relationships.sql`
4. `20261101_auto_quotations.sql`
5. `20261102_integrity_and_ar.sql`
6. `20261107_checkout_consistency.sql`

Automatic quotations are immutable snapshots prepared before order confirmation.
`order_id` may be linked once when the customer confirms; snapshot fields remain
immutable. `payment_requests` includes the address, payment and delivery choices,
summary, room capture, source, and timestamps sent by Android. Courier choice is
customer supplied and defaults to blank for older or unselected records. AR
captures are references and do not represent purchases.

The Node backend can synchronize local records to Supabase when
`SUPABASE_URL` and `SUPABASE_SERVICE_ROLE_KEY` are configured in the server
environment. Never commit the service-role key.

The local admin state endpoint still reads and writes a complete state snapshot.
Use paginated database-backed reads and targeted writes when operational data
grows beyond a small deployment; Supabase sync alone does not make the admin
read path paginated.
