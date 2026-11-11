package com.aiyige.customer.compose

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader

/** Embedded Google model-viewer, with Android AR handed off to Scene Viewer. */
@Composable
internal fun PanelModelViewer(
    modelUrl: String,
    modifier: Modifier = Modifier,
    allowAr: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var modelLoaded by remember(modelUrl) { mutableStateOf(false) }
    var modelFailed by remember(modelUrl) { mutableStateOf(false) }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    val bridge = remember(modelUrl) {
        ModelViewerBridge { loaded ->
            mainHandler.post {
                modelLoaded = loaded
                modelFailed = !loaded
            }
        }
    }
    val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()
    val html = modelViewerHtml(modelUrl, allowAr)
    val pageKey = "$modelUrl|$allowAr"

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                WebView(viewContext).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    addJavascriptInterface(bridge, "AiyigeViewer")
                    webViewClient = object : WebViewClient() {
                        override fun shouldInterceptRequest(
                            view: WebView,
                            request: WebResourceRequest
                        ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)
                            ?: bundledModelResponse(viewContext, request)

                        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                            val url = request.url.toString()
                            if (!url.startsWith("intent://")) return false
                            if (!modelLoaded || modelFailed) {
                                Toast.makeText(viewContext, "The panel model has not loaded yet.", Toast.LENGTH_LONG).show()
                                return true
                            }
                            return runCatching {
                                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                viewContext.startActivity(intent)
                                true
                            }.getOrElse {
                                Toast.makeText(viewContext, "Google Scene Viewer is unavailable on this device.", Toast.LENGTH_LONG).show()
                                true
                            }
                        }

                        override fun onReceivedError(
                            view: WebView,
                            request: WebResourceRequest,
                            error: android.webkit.WebResourceError
                        ) {
                            if (request.url.path?.endsWith(".glb", ignoreCase = true) == true ||
                                request.url.path?.endsWith(".gltf", ignoreCase = true) == true
                            ) bridge.onModelError()
                        }
                    }
                    tag = pageKey
                    loadPage(html)
                }
            },
            update = { view ->
                if (view.tag != pageKey) {
                    view.tag = pageKey
                    view.loadPage(html)
                }
            },
            onRelease = { view -> view.stopLoading(); view.removeJavascriptInterface("AiyigeViewer"); view.destroy() }
        )

        if (!modelLoaded) {
            Card(
                modifier = Modifier.align(Alignment.Center),
                colors = CardDefaults.cardColors(containerColor = Color(0xEE1C1A18))
            ) {
                Text(
                    text = if (modelFailed) "Panel model failed to load. Check the connection and try again."
                    else "Loading panel model…",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    color = Color.White
                )
            }
        }
    }
}

/** Serve bundled GLBs locally when the configured backend URL points at /3dPanels. */
private fun bundledModelResponse(context: android.content.Context, request: WebResourceRequest): WebResourceResponse? {
    if (!request.method.equals("GET", ignoreCase = true)) return null
    val requestPath = Uri.decode(request.url.encodedPath.orEmpty())
    val modelMarker = "/3dPanels/"
    val markerIndex = requestPath.indexOf(modelMarker)
    if (markerIndex < 0) return null

    val relativePath = requestPath.substring(markerIndex + modelMarker.length)
    if (relativePath.isBlank() || relativePath.startsWith("..") ||
        !relativePath.endsWith(".glb", ignoreCase = true)
    ) return null

    val assetPath = "models/$relativePath"
    val stream = runCatching { context.assets.open(assetPath) }.getOrNull() ?: return null
    return WebResourceResponse(
        "model/gltf-binary",
        "binary",
        200,
        "OK",
        mapOf(
            "Access-Control-Allow-Origin" to "*",
            "Access-Control-Allow-Methods" to "GET, OPTIONS",
            "Content-Length" to stream.available().toString()
        ),
        stream
    )
}

private class ModelViewerBridge(private val onModelStateChanged: (Boolean) -> Unit) {
    @JavascriptInterface
    fun onModelLoaded() = onModelStateChanged(true)

    @JavascriptInterface
    fun onModelError() = onModelStateChanged(false)
}

private fun WebView.loadPage(html: String) {
    loadDataWithBaseURL("https://appassets.androidplatform.net/", html, "text/html", "UTF-8", null)
}

private fun modelViewerHtml(modelUrl: String, allowAr: Boolean): String {
    val escapedUrl = modelUrl
        .replace("&", "&amp;")
        .replace("\"", "&quot;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
    val arAttributes = if (allowAr) "ar ar-modes=\"scene-viewer\" ar-placement=\"wall\" ar-scale=\"fixed\"" else ""
    return """
        <!doctype html>
        <html><head><meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1" />
        <style>html,body{margin:0;width:100%;height:100%;overflow:hidden;background:#f7f3ed}model-viewer{width:100%;height:100%;background:transparent;--poster-color:transparent}</style>
        <script type="module" src="https://appassets.androidplatform.net/assets/model-viewer.min.js"></script>
        </head><body><model-viewer id="panel" src="$escapedUrl" $arAttributes camera-controls touch-action="pan-y" shadow-intensity="0.7" alt="Selected wall panel"></model-viewer>
        <script>
          const panel = document.getElementById('panel');
          panel.addEventListener('load', () => AiyigeViewer.onModelLoaded());
          panel.addEventListener('error', () => AiyigeViewer.onModelError());
        </script></body></html>
    """.trimIndent()
}
