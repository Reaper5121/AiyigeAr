# Decision 0002: Evaluate EasyAR Sense Native for unsupported ARCore devices

## Status

Integrated as the AR backend for devices that cannot run ARCore; production
use and device validation are pending a suitable license and live testing.

## Context

The app is native Android Compose with SceneView/ARCore. The Redmi Turbo 3
cannot install Google Play Services for AR, so its current ARCore path cannot
run. The camera overlay fallback is not spatial AR. The required experience
needs six-degree-of-freedom tracking, multiple vertical wall planes, horizontal
ceiling planes, and physically scaled panel anchors.

## Decision

Integrate EasyAR Sense Native in the existing Android app. EasyAR's current
motion-tracking device list labels Redmi Turbo 3 as Good. Its Android SDK
provides Kotlin APIs, and its native PlaneData API reports both horizontal and
vertical planes. The app retains ARCore/SceneView on ARCore-supported devices
and selects EasyAR when ARCore is unavailable and a valid local EasyAR license
is configured. The camera overlay remains a labeled manual fallback.

## Constraints and risks

- EasyAR's SDK archive is distributed through its developer portal and needs
  an authorization license. The app consumes the 4.11.0 Community AAR supplied
  in `app/libs`; the license is read from ignored local `local.properties`.
- EasyAR Personal is not licensed for commercial use and includes a watermark.
  Production distribution requires a license matching intended usage.
- Device-list qualification is not a substitute for real-world validation.
  Test tracking, wall/ceiling plane detection, stable placements, and measured
  scale on the Redmi Turbo 3 before treating this path as production-ready.
- Android phones without either usable backend need a clearly labeled manual
  fallback. That mode must not claim automatic room scanning.

## Validation

The Android debug build succeeds with the SDK included. Physical validation
still needs a valid local license and connected Redmi Turbo 3. See [AR
validation status](../ar.md#validation-status).
