# Architecture

## Components

- `android-customer-compose/` — customer catalog, customization, AR preview,
  room capture, checkout, and customer quotation view.
- `server.js` — local HTTP backend, static asset server, state persistence,
  quotation preparation, order confirmation, and optional Supabase synchronization.
- `src/` — admin dashboard modules for quotations, orders, payments, inventory,
  courier operations, permissions, and UI state.
- `data/admin-state.json` — local development state and compatibility store.
- `supabase/` — database schema, policies, migrations, and seed scripts.

## Data ownership

Checkout prepares and saves a quotation before the customer confirms it. Order
confirmation attaches that quotation to an order and creates its payment record.
The quotation snapshot supplies the confirmed item and price data. AR room capture
is included as a reference when checkout prepares the quotation; it is not an order.

Supabase synchronization is optional and must use server-side environment
secrets. The service-role key must never be placed in Android or browser code.

## State size and scaling

The admin currently loads and saves the complete state snapshot through
`/api/state`, and local development persists that snapshot in one JSON file.
Compact JSON responses, in-place asset normalization after a single input copy,
and incremental room-capture synchronization reduce repeated processing, but
they do not remove the full-snapshot cost. This storage path is suitable for
small deployments and development data; it is not an unbounded scaling design.
For larger production datasets, move operational collections to database-backed
endpoints with pagination and targeted writes before the state snapshot becomes
large enough to affect request time or memory usage.
