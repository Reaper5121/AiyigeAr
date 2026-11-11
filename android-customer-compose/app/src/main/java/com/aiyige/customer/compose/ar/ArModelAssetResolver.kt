package com.aiyige.customer.compose.ar

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.util.Locale

/** Resolves catalog GLBs to an Android asset path or a cached local file URI. */
internal object ArModelAssetResolver {
    suspend fun resolve(context: Context, modelUrl: String): String? = withContext(Dispatchers.IO) {
        val bundledPath = bundledAssetPath(modelUrl)
        if (bundledPath != null && isValidGlb(context.assets.open(bundledPath))) return@withContext bundledPath

        runCatching {
            val file = File(context.cacheDir, "ar-panel-${modelUrl.hashCode().toUInt()}.glb")
            if (!file.exists() || file.length() < 12L || !isValidGlb(file.inputStream())) {
                val connection = URL(modelUrl).openConnection().apply {
                    connectTimeout = 10_000
                    readTimeout = 20_000
                }
                connection.getInputStream().use { input -> file.outputStream().use(input::copyTo) }
                if (file.length() < 12L || !isValidGlb(file.inputStream())) {
                    file.delete()
                    return@runCatching null
                }
            }
            Uri.fromFile(file).toString()
        }.getOrNull()
    }

    private fun bundledAssetPath(modelUrl: String): String? {
        val path = runCatching { Uri.parse(modelUrl).path.orEmpty() }.getOrDefault("")
        val marker = "/3dPanels/"
        val start = path.indexOf(marker)
        if (start < 0) return null
        val relativePath = path.substring(start + marker.length)
        if (relativePath.isBlank() || relativePath.split('/').any { it == ".." }) return null
        return "models/$relativePath"
    }

    private fun isValidGlb(input: java.io.InputStream): Boolean = input.use { stream ->
        val magic = ByteArray(4)
        stream.read(magic) == magic.size &&
            magic.toString(Charsets.US_ASCII).lowercase(Locale.ROOT) == "gltf"
    }
}
