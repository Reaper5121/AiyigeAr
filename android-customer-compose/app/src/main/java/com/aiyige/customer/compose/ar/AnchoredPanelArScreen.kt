package com.aiyige.customer.compose.ar

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.SurfaceType
import io.github.sceneview.ar.ARScene
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import java.io.File
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

internal data class ArPanelOption(
    val id: Int,
    val name: String,
    val modelUrl: String,
    val size: String
)

private data class AnchoredPanel(
    val id: Int,
    val productId: Int,
    val modelLocation: String,
    val anchor: Anchor,
    val longestSideMeters: Float,
    val baseRotationX: Float
)

@Composable
internal fun AnchoredPanelArScreen(
    modifier: Modifier = Modifier,
    selectedPanel: ArPanelOption?,
    panelOptions: List<ArPanelOption>,
    onPanelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    onOpenRoomCamera: () -> Unit,
    onSaveToQuote: (Map<Int, Int>) -> Unit
) {
    val context = LocalContext.current
    val placementController = remember { ArPanelPlacementController() }
    val latestFrame = remember { AtomicReference<Frame?>(null) }
    val placedPanels = remember { mutableStateListOf<AnchoredPanel>() }
    var nextPanelId by remember { mutableIntStateOf(1) }
    var selectedPlacedPanelId by remember { mutableIntStateOf(-1) }
    var surfaceAvailable by remember { mutableStateOf(false) }
    var trackingMessage by remember { mutableStateOf("Move your phone slowly to detect a wall.") }
    var isDraggingReady by remember { mutableStateOf(false) }
    var draggedReadyPosition by remember { mutableStateOf(Offset.Zero) }
    var sceneCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val sceneSize = remember { AtomicReference(IntSize.Zero) }
    var readyCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var modelLocation by remember(selectedPanel?.modelUrl) { mutableStateOf<String?>(null) }
    var isModelLoading by remember(selectedPanel?.modelUrl) { mutableStateOf(selectedPanel?.modelUrl != null) }

    LaunchedEffect(selectedPanel?.modelUrl) {
        val url = selectedPanel?.modelUrl
        modelLocation = null
        isModelLoading = !url.isNullOrBlank()
        if (url.isNullOrBlank()) {
            trackingMessage = "Choose a panel with a 3D model before placing it."
        } else {
            modelLocation = ArModelAssetResolver.resolve(context, url)
            isModelLoading = false
            trackingMessage = if (modelLocation == null) {
                "Could not load ${selectedPanel.name}'s 3D model. Check its GLB file."
            } else {
                "Move your phone slowly to detect a wall, then drag READY onto it."
            }
        }
    }

    fun placeReadyPanelAt(point: Offset) {
        val frame = latestFrame.get()
        val option = selectedPanel
        val model = modelLocation
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            trackingMessage = "Tracking is weak. Add light and scan the wall slowly."
            return
        }
        if (option == null || model == null) {
            trackingMessage = if (isModelLoading) "Loading the panel model…" else "Choose a panel with a valid GLB first."
            return
        }
        val hit = placementController.surfaceHit(frame, point.x, point.y)
        if (hit == null) {
            trackingMessage = "No scanned wall at that spot. Drop READY on the green reticle."
            return
        }
        val anchor = placementController.createAnchor(hit)
        if (anchor == null) {
            trackingMessage = "Could not anchor the panel there. Try another point on the wall."
            return
        }
        val panel = AnchoredPanel(
            id = nextPanelId++,
            productId = option.id,
            modelLocation = model,
            anchor = anchor,
            longestSideMeters = panelLengthMeters(option.size),
            baseRotationX = panelBaseRotationX(option.modelUrl, option.name)
        )
        placedPanels += panel
        selectedPlacedPanelId = panel.id
        trackingMessage = "Panel anchored. Tap it to select, then pinch to resize."
    }

    val gestureListener = rememberOnGestureListener(
        onSingleTapConfirmed = { _, node ->
            val panelId = node?.name?.removePrefix("aiyige-panel-")?.toIntOrNull()
            selectedPlacedPanelId = panelId ?: -1
            trackingMessage = if (panelId != null) {
                "Panel selected. Pinch it with two fingers to resize."
            } else if (placedPanels.isEmpty()) {
                "Hold READY, drag it to a scanned wall, then release."
            } else {
                "Tap a placed panel to select it, or drag READY to place another."
            }
        }
    )

    val counts = placedPanels.groupingBy { it.productId }.eachCount()
    Box(
        modifier = modifier
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                sceneCoordinates = coordinates
                sceneSize.set(coordinates.size)
            }
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            isOpaque = false,
            planeRenderer = false,
            onGestureListener = gestureListener,
            sessionConfiguration = { _, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
            },
            onSessionUpdated = { _, frame ->
                latestFrame.set(frame)
                val size = sceneSize.get()
                val centerHit = if (size.width > 0 && size.height > 0) {
                    placementController.surfaceHit(frame, size.width / 2f, size.height / 2f)
                } else null
                val hasWall = centerHit != null
                if (surfaceAvailable != hasWall) surfaceAvailable = hasWall
                if (frame.camera.trackingState != TrackingState.TRACKING) {
                    trackingMessage = "Tracking is weak. Add light and scan the wall slowly."
                } else if (hasWall && trackingMessage.startsWith("Move your phone")) {
                    trackingMessage = "Wall found. Hold READY and drag it onto the green reticle."
                } else if (!hasWall && trackingMessage.startsWith("Wall found")) {
                    trackingMessage = "Move your phone slowly to detect a wall."
                }
            },
            onSessionFailed = { exception ->
                trackingMessage = exception.message?.let { "AR could not start: $it" }
                    ?: "AR could not start. Check camera permission and Google Play Services for AR."
            }
        ) {
            placedPanels.forEach { panel ->
                val instance = rememberModelInstance(modelLoader, fileLocation = panel.modelLocation)
                AnchorNode(anchor = panel.anchor) {
                    instance?.let { model ->
                        ModelNode(
                            modelInstance = model,
                            scaleToUnits = panel.longestSideMeters,
                            centerOrigin = Position(),
                            rotation = Rotation(x = panel.baseRotationX),
                            isEditable = panel.id == selectedPlacedPanelId,
                            apply = {
                                name = "aiyige-panel-${panel.id}"
                                isPositionEditable = false
                                isRotationEditable = false
                                val startingScale = scale.x
                                editableScaleRange = startingScale * 0.5f..startingScale * 1.8f
                            }
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArOverlayButton(text = "Back", onClick = onBack)
                ArOverlayButton(text = "AR Camera", onClick = {}, enabled = false)
            }
            if (placedPanels.isEmpty()) {
                ArOverlayButton(
                    text = "Room photo",
                    onClick = onOpenRoomCamera,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                )
            }

            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 58.dp),
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.58f))
            ) {
                Text(
                    text = "${placedPanels.size} placed  ·  $trackingMessage",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    color = Color.White,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(26.dp)
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                    .padding(3.dp)
                    .background(if (surfaceAvailable) Color(0xFF72F2BE) else Color.White.copy(alpha = 0.8f), CircleShape)
            )

            if (panelOptions.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 174.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    items(panelOptions, key = { it.id }) { option ->
                        val selected = option.id == selectedPanel?.id
                        TextButton(
                            onClick = { onPanelSelected(option.id) },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (selected) Color(0xFFF77F2C) else Color.Black.copy(alpha = 0.65f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(option.name, maxLines = 1, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                }
            }

            if (selectedPanel != null) {
                val readyModifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 98.dp)
                    .onGloballyPositioned { readyCoordinates = it }
                    .alpha(if (isDraggingReady) 0f else 1f)
                    .pointerInput(selectedPanel?.id, modelLocation, sceneCoordinates, readyCoordinates) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { localStart ->
                                val root = sceneCoordinates
                                val source = readyCoordinates
                                if (root != null && source != null) {
                                    draggedReadyPosition = root.localPositionOf(source, localStart)
                                    isDraggingReady = true
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                draggedReadyPosition += dragAmount
                            },
                            onDragEnd = {
                                isDraggingReady = false
                                placeReadyPanelAt(draggedReadyPosition)
                            },
                            onDragCancel = { isDraggingReady = false }
                        )
                    }
                Card(
                    modifier = readyModifier,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.72f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("READY", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
                        Text(
                            text = if (isModelLoading) "Loading GLB…" else selectedPanel.name,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (isDraggingReady) {
                Card(
                    modifier = Modifier.offset {
                        IntOffset(
                            (draggedReadyPosition.x - 62.dp.toPx()).roundToInt(),
                            (draggedReadyPosition.y - 28.dp.toPx()).roundToInt()
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xE6F77F2C))
                ) {
                    Text("DROP PANEL", Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(
                    text = if (selectedPlacedPanelId > 0) "Tap READY and hold-drag to add another · pinch selected panel to resize"
                    else "Hold READY, drag to a detected wall or ceiling, then release",
                    color = Color.White,
                    fontSize = 11.sp
                )
                if (placedPanels.isNotEmpty()) {
                    Button(
                        onClick = { onSaveToQuote(counts) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Finish AR  ·  ${placedPanels.size} placed  ·  Get quotation", color = Color.Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArOverlayButton(text: String, onClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black.copy(alpha = 0.55f),
            contentColor = Color.White,
            disabledContainerColor = Color.Black.copy(alpha = 0.55f),
            disabledContentColor = Color.White
        ),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

private fun panelLengthMeters(size: String): Float {
    val values = Regex("\\d+(?:[.,]\\d+)?").findAll(size)
        .mapNotNull { it.value.replace(',', '.').toFloatOrNull() }
        .take(2)
        .toList()
    if (values.isEmpty()) return 2.4f
    val multiplier = when {
        size.contains("mm", ignoreCase = true) -> 0.001f
        size.contains("cm", ignoreCase = true) -> 0.01f
        size.contains("m", ignoreCase = true) -> 1f
        else -> 0.001f
    }
    return (values.maxOrNull()!! * multiplier).coerceIn(0.05f, 5f)
}

private fun panelBaseRotationX(modelUrl: String, modelName: String): Float = when {
    modelUrl.contains("Grove/PG-", ignoreCase = true) || modelName.contains("PG-", ignoreCase = true) -> -90f
    modelUrl.contains("150-05B", ignoreCase = true) || modelName.contains("150-05B", ignoreCase = true) -> -90f
    else -> 0f
}
