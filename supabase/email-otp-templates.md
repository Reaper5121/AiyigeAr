# Aiyige Email OTP Templates

In Supabase, open **Authentication → Emails → Templates**. Replace the body of
**Confirm signup** and **Reset password** with the matching template below.
Use `{{ .Token }}` exactly as written. It is Supabase's six-digit email code.

## Confirm signup

**Subject:** Verify your Aiyige account

```html
<div style="font-family:Arial,sans-serif;background:#fff7eb;padding:32px;color:#121826">
  <div style="max-width:520px;margin:auto;background:#ffffff;border-radius:20px;padding:32px;text-align:center">
    <h1 style="margin:0 0 12px;color:#f77f2c">Welcome to Aiyige</h1>
    <p style="line-height:1.6">Use this verification code to finish creating your account.</p>
    <div style="margin:24px 0;padding:18px;border-radius:14px;background:#fff1df;font-size:30px;font-weight:700;letter-spacing:8px">{{ .Token }}</div>
    <p style="font-size:13px;color:#667085;line-height:1.5">This code expires soon. If you did not create an Aiyige account, you can safely ignore this email.</p>
  </div>
</div>
```

## Reset password

**Subject:** Reset your Aiyige password

```html
<div style="font-family:Arial,sans-serif;background:#fff7eb;padding:32px;color:#121826">
  <div style="max-width:520px;margin:auto;background:#ffffff;border-radius:20px;padding:32px;text-align:center">
    <h1 style="margin:0 0 12px;color:#f77f2c">Password reset</h1>
    <p style="line-height:1.6">Enter this code in the Aiyige app to reset your password.</p>
    <div style="margin:24px 0;padding:18px;border-radius:14px;background:#fff1df;font-size:30px;font-weight:700;letter-spacing:8px">{{ .Token }}</div>
    <p style="font-size:13px;color:#667085;line-height:1.5">This code expires soon. If you did not request a password reset, you can safely ignore this email.</p>
  </div>
</div>
```
