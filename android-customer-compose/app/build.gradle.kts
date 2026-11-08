import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.inputStream()?.use { stream -> load(stream) }
}
val easyArLicenseKey = localProperties.getProperty("easyar.licenseKey", "")
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

android {
    namespace = "com.aiyige.customer.compose"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aiyige.customer.compose"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "SUPABASE_URL", "\"https://ffbrjiikxlgszoxnzynf.supabase.co\"")
        buildConfigField("String", "SUPABASE_PUBLISHABLE_KEY", "\"sb_publishable_UZ82L-SLZDeJzc1f9ucIzA_EYAFb3H3\"")
        buildConfigField("String", "EASYAR_LICENSE_KEY", "\"$easyArLicenseKey\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

// Keep the native viewer's bundled models synchronized with the source
// 3dPanels folder before every APK build. A few placeholder/empty GLBs in
// that folder are intentionally ignored so they cannot replace a working
// model with a blank preview.
val sync3dPanelAssets = tasks.register<Copy>("sync3dPanelAssets") {
    val sourceModels = rootProject.projectDir.resolve("3dPanels")
    from(sourceModels) {
        include("**/*.glb")
        eachFile {
            if (file.length() < 1024L) exclude()
        }
    }
    into(project.projectDir.resolve("src/main/assets/models"))
}

// Bundle the catalog thumbnails with the customer app so the customization
// preview does not fall back to the same procedural material when the backend
// image URL is unavailable or slow.
val syncCatalogPreviewAssets = tasks.register<Copy>("syncCatalogPreviewAssets") {
    val sourceCatalog = rootProject.projectDir.resolve("data/assets")
    from(sourceCatalog) {
        include("catalog-*.jpg")
    }
    into(project.projectDir.resolve("src/main/assets/catalog"))
}

val syncInventoryPreviewAssets = tasks.register<Copy>("syncInventoryPreviewAssets") {
    val sourceInventory = rootProject.projectDir.resolve("data/assets")
    from(sourceInventory) {
        include("inventory-*.jpg")
    }
    into(project.projectDir.resolve("src/main/assets/inventory"))
}

tasks.named("preBuild") {
    dependsOn(sync3dPanelAssets)
    dependsOn(syncCatalogPreviewAssets)
    dependsOn(syncInventoryPreviewAssets)
}

dependencies {
    implementation(files("libs/EasyAR.aar"))
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    // ARCore-backed SceneView for markerless wall/ceiling placement.
    implementation("io.github.sceneview:arsceneview:3.3.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
