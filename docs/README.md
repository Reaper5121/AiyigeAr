# Aiyige Project Documentation

This folder is the project knowledge base and is designed to mirror into an
Obsidian vault.

## Contents

- `architecture.md` — system boundaries and data ownership.
- `workflows/order-quotation-delivery.md` — quotation-first checkout and external courier flow.
- `ar.md` — AR preview, room capture, and AR limitations.
- `database.md` — local state and Supabase schema/migration notes.
- `decisions/` — durable architecture decisions and tradeoffs.

## Validation

The project has two runtime surfaces:

1. Node/admin web: `npm run dev` (keeps the backend running and restarts it when server code changes).
2. Full local workflow: `npm start` (backend watch mode plus Android install when a device is connected).
3. Android customer app: `gradlew.bat :compose-app:assembleDebug`.

The admin browser serves static files directly from the project, so refresh the
browser after HTML, CSS, or client JavaScript changes. Android Compose changes
still need a Gradle build/install because they are compiled into the APK.
While `npm start` is running, unplugging the Android USB cable does not stop the
backend. The launcher watches for the device to reconnect and restores ADB port
forwarding automatically; stop the workflow with Ctrl+C when finished.

Live Supabase checks and physical-device Scene Viewer checks must be reported
separately from local syntax or build checks.

## First administrator setup

Fresh installations start without preset staff accounts. Start the admin server
with `npm run dev` and open `http://localhost:3000`; the sign-in screen will
offer a one-time form to create the first administrator. Choose a unique
password with at least 12 characters. Once an account exists, the setup form is
disabled and staff accounts can be managed from the dashboard.

Runtime state is stored in `data/admin-state.json` and is excluded from Git.
Keep that file and any production credentials in protected runtime storage; do
not add customer or account data to the repository.
