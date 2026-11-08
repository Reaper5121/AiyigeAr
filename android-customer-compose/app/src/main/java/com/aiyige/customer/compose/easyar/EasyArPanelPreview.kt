package com.aiyige.customer.compose.easyar

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.imageLoader
import coil.request.ImageRequest
import com.aiyige.customer.compose.ArDropRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

@Composable
internal fun EasyArPanelPreview(
    imageUrl: String?,
    size: String,
    dropRequest: ArDropRequest?,
    onDropHandled: () -> Unit,
    onStatusChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var status by remember { mutableStateOf("Starting EasyAR room scan…") }
    var rootOrigin by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val dimensionsMm = remember(size) {
        Regex("\\d+(?:[.,]\\d+)?").findAll(size).mapNotNull {
            it.value.replace(',', '.').toFloatOrNull()
        }.take(2).toList().takeIf { it.size == 2 && it.all { value -> value > 0f } }
            ?: listOf(2400f, 200f)
    }
    val orderedDimensions = remember(dimensionsMm) {
        max(dimensionsMm[0], dimensionsMm[1]) to minOf(dimensionsMm[0], dimensionsMm[1])
    }
    val view = remember(context) {
        EasyArPanelSurfaceView(context) { message -> status = message }
    }

    LaunchedEffect(status) { onStatusChanged(status) }
    LaunchedEffect(imageUrl, view) {
        if (imageUrl.isNullOrBlank()) return@LaunchedEffect
        val bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val result = context.imageLoader.execute(
                    ImageRequest.Builder(context).data(imageUrl).allowHardware(false).build()
                )
                (result.drawable as? BitmapDrawable)?.bitmap
            }.getOrNull()
        }
        view.setPanelBitmap(bitmap)
    }
    LaunchedEffect(view, orderedDimensions) {
        view.setPanelSize(orderedDimensions.first / 1000f, orderedDimensions.second / 1000f)
    }
    LaunchedEffect(dropRequest?.token, rootOrigin, view) {
        val request = dropRequest ?: return@LaunchedEffect
        if (rootOrigin == androidx.compose.ui.geometry.Offset.Zero) return@LaunchedEffect
        val local = request.rootPoint - rootOrigin
        view.requestPlacement(local.x, local.y)
        onDropHandled()
    }
    DisposableEffect(lifecycleOwner, view) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> view.onResume()
                Lifecycle.Event.ON_PAUSE -> view.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) view.onResume()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            view.onPause()
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .onGloballyPositioned { rootOrigin = it.positionInRoot() }
    ) {
        AndroidView(
            factory = { view },
            modifier = Modifier.fillMaxSize(),
            update = { it.setStatusListener { message -> status = message } }
        )
        Card(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 104.dp).widthIn(max = 300.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.68f))
        ) {
            Text(
                status,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 78.dp, end = 10.dp)
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(onClick = { view.deleteSelected() }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 1.dp)) {
                Text("Delete", color = Color(0xFFB42318), fontSize = 10.sp)
            }
            TextButton(onClick = { view.clearPanels() }, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 1.dp)) {
                Text("Reset", color = Color(0xFF121826), fontSize = 10.sp)
            }
        }
    }
}
