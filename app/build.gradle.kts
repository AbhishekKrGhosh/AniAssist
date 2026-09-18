import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

// Read secrets from the project-root .env (git-ignored)
val envProps = Properties().apply {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) load(envFile.inputStream())
}
val mapsApiKey: String = envProps.getProperty("MAPS_API_KEY", "")
val uploadStorePassword: String = envProps.getProperty("KEYSTORE_PASSWORD", "")
val uploadKeyAlias: String = envProps.getProperty("KEY_ALIAS", "upload")
val uploadKeyPassword: String = envProps.getProperty("KEY_PASSWORD", "")
val firebaseDbUrl: String = envProps.getProperty("FIREBASE_DB_URL", "")

android {
    namespace = "abhishek.aniassist"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "abhishek.aniassist"
        minSdk = 24
        targetSdk = 37
        // IMPORTANT: versionCode must be HIGHER than your last published version.
        // Check your old Play Store listing and set this accordingly.
        versionCode = 8
        versionName = "1.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "FIREBASE_DB_URL", "\"$firebaseDbUrl\"")
    }

    signingConfigs {
        create("upload") {
            storeFile = file("aniassist-upload.jks")
            storePassword = uploadStorePassword
            keyAlias = uploadKeyAlias
            keyPassword = uploadKeyPassword
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Sign with the upload key from .env — Play requires this cert
            // to match the existing listing's upload key.
            if (uploadStorePassword.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("upload")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Firebase (BOM manages versions)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    // Navigation
    implementation(libs.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // Coil (image loading - replaces Picasso)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Lottie animations
    implementation(libs.lottie.compose)

    // Location (Google Play Services)
    implementation(libs.play.services.location)

    // Google Maps (free tier — map loads only, no Geocoding/Directions APIs)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    // DataStore (replaces SharedPreferences)
    implementation(libs.datastore.preferences)

    // Image cropper (profile photo crop UI)
    implementation(libs.android.image.cropper)

    // CameraX (in-app photo capture — front for profile, back for reports)
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
