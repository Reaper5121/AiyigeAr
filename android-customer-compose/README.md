# Aiyige Customer Native App

This is the native Jetpack Compose version of the customer-facing mobile app.

## What it includes
- Home screen
- Product catalog with search and category filters
- Google model-viewer GLB preview with Scene Viewer wall placement
- Account and quote request screen
- Bottom navigation for mobile use

## Notes
- This is a standalone Android Studio project.
- The admin dashboard is not modified.
- Android AR opens Google Scene Viewer from the bundled model-viewer component.
  Panel quantity is selected in the app; room-reference camera capture remains
  separate from AR placement.
- The catalog now tries to load live data from the local admin backend first, using the common Android host paths (`127.0.0.1`, `10.0.2.2`, and `localhost`).
- If the backend is unreachable, the app falls back to sample products so the UI still opens cleanly.

## Local development
- From the repo root, run `npm start` to boot the backend and the single Android app together.
- Use `npm run mobile` if you only want the mobile app task. It installs on a
  connected Android device; when no device is available, it builds the debug
  APK without treating that as a workflow failure.
- Use `npm run server` if you only want the Node backend.

## Open in Android Studio
Open the `android-customer-compose` folder as a project.

## Next step
If you want, I can add image loading from the admin backend next so product thumbnails also come through live.
