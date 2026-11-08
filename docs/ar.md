# AR and Room Capture

## AR backends

- ARCore/SceneView remains the markerless AR path for Android phones where
  Google Play Services for AR is supported and installed.
- Phones that cannot run ARCore can use the integrated EasyAR Sense Native
  path. The Android app bundles EasyAR Sense 4.11 Community, which includes
  motion tracking and sparse spatial mapping. On the Redmi Turbo 3, EasyAR's
  current device list rates motion tracking as Good.
- EasyAR progressively maps the room and outlines detected planes. Hold and drag
  the Ready panel card to place a panel on a detected surface; tap a placed
  panel to select it, drag it to move, and pinch with two fingers to resize.
  Tap another detected surface to add another panel. Delete and Reset manage
  the layout.
- The EasyAR camera now uses 640-pixel capture, 20 fps rendering, and SLAM mode
  to reduce device workload. The camera is stopped when the AR view pauses.- The CameraX screen-space overlay remains a manual fallback when neither
  spatial backend can start. It does not scan or anchor content to the room.

## EasyAR local setup

The app reads `easyar.licenseKey` from the root `local.properties` file at
build time. Add the EasyAR Sense license created for Android package
`com.aiyige.customer.compose`:

```properties
easyar.licenseKey=YOUR_EASYAR_LICENSE_KEY
```

Do not commit `local.properties` or expose the key in chat. EasyAR Personal is
for evaluation only, does not permit commercial use, and displays a watermark;
choose a license that matches the app's intended release.

The SDK AAR used by the app is `android-customer-compose/app/libs/EasyAR.aar`.
It came from EasyAR Sense 4.11.0 Community. The unpacked vendor archive is not
needed to build once this AAR is present.

## Room placement behavior

- EasyAR tracks phone motion and detects room planes while the customer scans.
  Detection quality depends on lighting, visible texture, camera motion, and
  device calibration. Guide customers to move slowly and sweep each wall and
  the ceiling.
- Planes and placements are session-local. This is progressive room mapping,
  not an automatically complete architectural floor plan.
- Panel width and height are derived from the selected product's stated size in
  millimeters and rendered at tracked scale. Resizing preserves that ratio.
- The camera scan does not create an order or quotation. Room capture remains
  an optional reference asset attached to the customer flow.

## Validation status

The Android debug APK with EasyAR Sense and the local license is installed on
Redmi Turbo 3. The camera preview and motion tracking work, but the on-device
status reported no detected planes, so panel placement is not yet validated.
The latest build lowers camera workload and changes resize to tap-to-select
followed by a two-finger pinch. Verify wall and ceiling plane detection,
placement across camera movement, and known-size accuracy on the phone. A
successful build alone does not verify tracking or optical alignment.
Official references:

- [EasyAR supported motion-tracking devices](https://www.easyar.com/doc/en/develop/motion-tracking/devices-easyar.html)
- [EasyAR native Android setup](https://help.easyar.com/EasyAR%20Sense/latest/GettingStarted/Setting-up-EasyAR-Android.html)
- [EasyAR native plane data](https://www.easyar.com/doc/en/api/native/easyar.PlaneData.html)
- [EasyAR Sense license terms](https://www.easyar.com/doc/en/develop/license-sense.html)
- [ARCore supported devices](https://developers.google.com/ar/devices)
