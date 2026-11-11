package com.aiyige.customer.compose.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState

/** Wall/ceiling-only ARCore hit testing and anchor creation. */
internal class ArPanelPlacementController {
    fun surfaceHit(frame: Frame, x: Float, y: Float): HitResult? =
        frame.hitTest(x, y).firstOrNull { hit ->
            val plane = hit.trackable as? Plane ?: return@firstOrNull false
            isPlaceable(plane) && plane.trackingState == TrackingState.TRACKING &&
                runCatching { plane.isPoseInPolygon(hit.hitPose) }.getOrDefault(false)
        }

    fun createAnchor(hit: HitResult): Anchor? =
        runCatching {
            val plane = hit.trackable as? Plane ?: return null
            if (!isPlaceable(plane) || plane.trackingState != TrackingState.TRACKING) return null
            plane.createAnchor(hit.hitPose)
        }.getOrNull()

    private fun isPlaceable(plane: Plane): Boolean =
        plane.type == Plane.Type.VERTICAL || plane.type == Plane.Type.HORIZONTAL_DOWNWARD_FACING
}
