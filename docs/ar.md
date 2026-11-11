# Google Model Viewer and Room Capture

## Android product preview and AR

- The Android customer app uses the Google `<model-viewer>` web component for
  bundled 3D previews and AR launch. Its script is packaged at
  `app/src/main/assets/model-viewer.min.js` and served to WebView through
  AndroidX WebKit's asset loader.
- AR is restricted to `ar-modes="scene-viewer"`; WebXR is not selected. The
  AR action opens Google Scene Viewer and requests wall placement with
  `ar-placement="wall"`. Scene Viewer receives one selected GLB at a time.
- The AR tab opens on the live CameraX room preview. The selected GLB preview
  opens only after the customer presses **View panel in AR**; its **Room camera**
  control returns to the live camera. The app must not automatically replace
  the camera with the model preview when a product is selected or the app resumes.
- The AR tab keeps product quantity and quotation handling in the app. It does
  not build a custom multi-panel placement grid or derive quote quantity from
  tracked anchors. Customers set quantity explicitly before requesting a quote.
- The existing CameraX camera remains only for optional room-reference photos.
  It is unbound while the model viewer is open so it does not compete with
  Scene Viewer for camera access.
- GLB URLs passed to Scene Viewer must be reachable by Google's app on the
  device. Public backend URLs work best; localhost-only development URLs may
  not be reachable by Scene Viewer.
- The embedded WebView also loads bundled `/3dPanels/*.glb` files through a
  local asset response, with CORS enabled. The Node backend adds CORS headers to
  static files so externally hosted development GLBs can be fetched by the
  viewer. A model-load error is shown in the viewer, and the Scene Viewer
  handoff is blocked until the model has loaded.

## Runtime requirements and limitations

- The Android app no longer embeds the ARCore or EasyAR SDKs. Google Scene
  Viewer still requires a compatible Android device, the Google app, and
  Google Play Services for AR to provide its native AR mode. Without those,
  Scene Viewer may fall back to its 3D viewer.
- Scene Viewer owns the camera, tracking, surface detection, and placement UI.
  The app does not receive anchors or placed-object callbacks, so it cannot
  build a multi-panel wall layout or automatically count panels from AR.
- Wall placement aligns the GLB bounding box to a vertical surface. Check each
  source model's axes, origin, dimensions, and front/back orientation before
  relying on it for customer-facing visualization.

## Web product viewer

The admin panel model editor uses the same model-viewer library. Its preview is
configured to launch Scene Viewer only, request wall placement, and keep the
source model scale fixed in AR.

## Validation

The debug APK build verifies Kotlin compilation and packaging. Real wall
placement still needs a manual check on a supported physical Android phone:

1. Open the AR tab and choose a panel with a valid GLB URL.
2. Tap the model-viewer AR button and confirm Scene Viewer opens.
3. Place the model on a wall and check its orientation and apparent scale.
4. Return to the app and confirm the selected quantity still drives the quote.
5. Capture a room photo separately if it should accompany the quote.

References:

- [Google model-viewer AR attributes](https://modelviewer.dev/docs/index.html)
- [Google Scene Viewer requirements](https://developers.google.com/ar/develop/scene-viewer)
