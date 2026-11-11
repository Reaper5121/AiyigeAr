# Supabase Auth Setup

This project already points to the Supabase project in the app build config, but the cloud `payment_requests` table still needs to be created in Supabase before remote payment saves can work end-to-end.

## 1) Run the payment table SQL

Open the Supabase SQL Editor and run:

- `supabase/payment_requests.sql`

If this SQL has not been applied, the app will fall back to local device storage for payment requests.

## 2) Configure delivery through Gmail (or another SMTP provider)

Supabase's built-in SMTP is only for limited testing: it sends only to authorised
project-team addresses and is rate limited. To deliver signup-confirmation and
password-reset emails to normal Gmail inboxes, configure custom SMTP.

1. In Supabase, open `Authentication` > `Settings` > `SMTP Settings`.
2. Enable **Custom SMTP**. Without this, Supabase's test mailer does not reliably deliver to normal customer inboxes (and may only send to project-team addresses).
3. For a Gmail sender (personal or Google Workspace), use `smtp.gmail.com`,
   port `465` (SSL) or `587` (TLS), the complete Gmail address as the username,
   and a Google **App Password** as the password. The Google account must have
   2-Step Verification enabled. This uses the Gmail account you already have;
   it does not require a paid email provider.
4. Set the sender address to that same Gmail/Workspace address, save, then send
   a test confirmation email.

For a production app, a transactional provider such as Resend, Postmark, SES, or
SendGrid is generally more reliable than a personal Gmail account.

## 3) Configure the app redirect URL

In Supabase open `Authentication` > `URL Configuration` and add this exact
additional redirect URL:

`aiyigecustomer://auth-callback`

Also add this callback variant so a path appended by the provider is accepted:

`aiyigecustomer://auth-callback/**`

The Android manifest registers that scheme for legacy link-based emails. The
current app uses six-digit OTPs instead, so the user enters the code in the app
and no email-link redirect is required.

### Dashboard checklist

1. Open your Supabase project.
2. Go to `Authentication`.
3. Open `Auth Hooks`.
4. Disable the `Send Email` hook if it is enabled.
5. Go to `URL Configuration` and add `aiyigecustomer://auth-callback`.
6. Go to `Email Templates`.
7. Replace the `Confirm signup` and `Reset password` templates with the OTP
   templates in `supabase/email-otp-templates.md`.
8. Make sure both templates use `{{ .Token }}`, not `{{ .ConfirmationURL }}`.
9. Open `Authentication` > `Logs` after testing a signup or recovery request.
10. Check for delivery errors or rate-limit errors if the email still does not arrive.

The Android app can confirm that Supabase accepted an email request, but it cannot
confirm inbox delivery. SMTP must be configured in the Supabase dashboard by a
project administrator; the publishable key bundled in the app does not have
permission to change mail settings.

## 4) App-side auth flow

The app now supports:

- signup confirmation by six-digit OTP
- resending signup confirmation and recovery codes
- restoring a pending signup verification after the app is reopened; only the
  profile fields are retained locally, never the signup password
- forgot-password reset by six-digit OTP
- password-reset session created only after a valid OTP is entered

## 5) Configure the backend Supabase credentials

The admin backend uses a Secret key for server-side database sync. It reads
these values from the root `.env` file; `.env` is ignored by Git.

1. Copy `.env.example` to `.env` in the repository root.
2. In Supabase, open `Project Settings` > `API Keys` and copy the project URL and
   Secret key into `SUPABASE_URL` and `SUPABASE_SECRET_KEY`. Add the project's
   publishable key as `SUPABASE_PUBLISHABLE_KEY` for Android builds.
3. Restart the Node backend. It prints a warning when these values are missing
   and cloud sync is disabled.

Keep the Secret key only in the backend `.env`. Never add it to the Android
app, `local.properties`, or source control. The Android app uses the project's
URL and public publishable key for Supabase Auth; those are not substitutes for
the backend Secret key. The backend also accepts the legacy
`SUPABASE_SERVICE_ROLE_KEY` variable for existing setups.

When backend Supabase sync is configured, mobile quotation and order submission
also send the customer's short-lived Supabase access token. The backend checks
that token with Supabase Auth and requires its account email to match the quote.
The Secret key stays on the backend and is never sent by the app.

The Android Gradle build reads only `SUPABASE_URL` and
`SUPABASE_PUBLISHABLE_KEY` from the root `.env`; it never embeds the backend
Secret key. The Node backend reads `SUPABASE_URL` and `SUPABASE_SECRET_KEY`.

Cloud sync also requires the SQL migrations and tables used by the backend. If
credentials or columns are missing, relevant data may remain in local backend
or device storage. A configured `.env` alone does not prove the live database
schema is ready; verify each required column after applying SQL.

## 6) Connection status

Read-only checks on 2026-11-09 confirmed that the `orders`, `quotations`,
`payments`, and `room_captures` columns used by the backend are accessible. The
`payment_requests` table exists but is missing ten columns required by the app:
`customer_phone`, `summary`, `street_address`, `city_province`, `postal_code`,
`payment_method`, `delivery_method`, `room_capture_url`, `source`, and
`updated_at`. Run `supabase/20261109_payment_requests_schema_repair.sql` in the
Supabase SQL Editor, then run `npm run check:supabase` from the project root to
re-run the read-only schema check. The check makes no database changes and does
not read or print customer rows.

The Auth settings endpoint returned HTTP 200. Email sign-in is enabled, email
auto-confirm is disabled, and signups are enabled. SMTP delivery, OTP email
templates, and redirect URLs still require verification in the Supabase
dashboard; the public settings endpoint does not confirm those dashboard values.

For shared live application data, run `supabase/20261110_shared_realtime_state.sql`
after `supabase/20261109_payment_requests_schema_repair.sql` in the SQL Editor,
then restart the Node backend once. It initializes the shared state snapshot
and subscribes the backend to Supabase Realtime. After that, application changes
propagate across running instances without restarting the apps. Run
`npm run check:supabase` to verify the `project_data` columns and migration marker.
