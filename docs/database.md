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
7. `20261109_payment_requests_schema_repair.sql`
8. `20261110_shared_realtime_state.sql`

Automatic quotations are immutable snapshots prepared before order confirmation.
`order_id` may be linked once when the customer confirms; snapshot fields remain
immutable. `payment_requests` includes the address, payment and delivery choices,
summary, room capture, source, and timestamps sent by Android. Courier choice is
customer supplied and defaults to blank for older or unselected records. AR
captures are references and do not represent purchases.

The Node backend uses Supabase as the shared state source when
`SUPABASE_URL` and the server-only `SUPABASE_SECRET_KEY` are configured. The
legacy `SUPABASE_SERVICE_ROLE_KEY` variable is also accepted. Never commit
either key.

The shared-state migration creates the private `project_data` row used by the
backend as the canonical application snapshot and enables Supabase Realtime for
that table. Run it in the Supabase SQL Editor before starting multiple backend
instances or expecting the mobile app and staff dashboard to share live data.

The admin API and mobile sync endpoint currently read a complete application
snapshot. Use paginated database-backed reads and targeted writes when
operational data grows beyond a small deployment; Realtime does not make the
snapshot read path paginated.
