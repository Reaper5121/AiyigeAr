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
- forgot-password reset by six-digit OTP
- password-reset session created only after a valid OTP is entered

## 5) What is connected right now

Based on the current environment check:

- Supabase Auth is reachable from the app
- The local backend is running
- The remote `payment_requests` table is not yet available, so cloud payment saves will still fall back to local storage until that table is created
