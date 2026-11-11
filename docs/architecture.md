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

## Android device layouts

The customer Compose shell fills narrow phone windows and caps its centered
content width at 840 dp on larger displays. The activity supports Android
window resizing so split-screen layouts and the on-screen keyboard can resize
the available content area. AR hardware remains optional; catalog, account,
and checkout flows do not require an AR-capable device. Customer pages use
scrollable content, compact navigation labels, viewport-sized home cards, and
reflowed cart and registration layouts so narrow windows do not force fixed
width controls beyond the screen.

## Data ownership

Checkout prepares and saves a quotation before the customer confirms it. Order
confirmation attaches that quotation to an order and creates its payment record.
The quotation snapshot supplies the confirmed item and price data. AR room capture
is included as a reference when checkout prepares the quotation; it is not an order.

Supabase synchronization is optional and must use server-side environment
secrets. The service-role key must never be placed in Android or browser code.

## Shared data updates

Supabase `project_data` stores one private `app_state/shared` snapshot as the
shared operational source. The backend loads it at startup and writes each
successful state change to it. Supabase Realtime broadcasts snapshot changes to
all backend instances; each instance updates its local cache and emits a
server-sent `state-changed` event on `/api/events`. A ten-second Supabase poll
acts as a recovery path if a Realtime connection is unavailable. Staff pages
refresh when an event arrives or a hidden tab becomes active. The mobile app
polls the focused `/api/mobile/sync` response while open: inventory every 20
seconds and signed-in customer orders, quotations, profile, and payment methods
every 15 seconds. Changes made through the application therefore appear across
connected app instances without restarting them.

Apply `supabase/20261110_shared_realtime_state.sql` before enabling the cloud
snapshot. The backend checks the migration marker and keeps cloud state sync
disabled until the migration enables the `project_data` Realtime publication
and prevents authenticated customer clients from reading the private full-system snapshot.
Use the application APIs to modify business data; direct edits to the legacy
`inventory`, `orders`, or other mirrored tables do not modify the canonical
`app_state/shared` snapshot. The Node backend remains the trusted writer and
requires `SUPABASE_URL` plus a server-only Supabase secret key.

## Dashboard role boundaries

- **Administrator:** creates, updates, and deletes employee accounts only. The
  administrator does not manage inventory, payments, quotations, or delivery.
- **Inventory Staff:** views and edits inventory only.
- **Owner:** reads business, inventory, sales, and employee summaries; owner
  access is read-only.
- Sales roles own quotation, payment, and delivery operations.

The admin UI enforces these role boundaries through its view and action guards.
The current local state API is not a production authorization boundary; protect
it behind authenticated server-side access before exposing it publicly.

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
