plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}

android {
  namespace = "com.example"
  compileSdk { version = release(37) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.durakcardgame.vzkqmp"
    minSdk = 24
    targetSdk = 37
    versionCode = 5
    versionName = "1.2.0"

    testInstrumentationRunner = "androidx.test.runner.AndJUnitRunner"
  }

  buildTypes {
    release {
      isCrunchPngs = true
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("debug")
    }
    debug {
      signingConfig = signingConfigs.getByName("debug")
      // Enable debug optimizations
      isMinifyEnabled = false
      isShrinkResources = false
    }
  }
  
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  
  kotlinOptions {
    jvmTarget = "17"
    // Enable R8 optimizations for release
    freeCompilerArgs += listOf(
      "-Xjvm-target=17",
      "-Xexplicit-api=strict"
    )
  }
  
  buildFeatures {
    compose = true
    buildConfig = true
    // Enable R8 for release builds
    if (buildType == "release") {
      shrinkResources = true
      minifyEnabled = true
    }
  }
  
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      isReturnDefaultValues = true
    }
  }
  
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
  
  // Enable R8 full mode for better optimization
  androidResources {
    ignoreAssetsPattern = "!*.svg:!*.md"
  }
  
  // Packaging options
  packaging {
    jniLibs {
      useLegacyPackaging = false
    }
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      excludes += "META-INF/versions/9/license.txt"
    }
  }
}

dependencies {
  // Compose BOM - Latest stable
  implementation(platform(libs.androidx.compose.bom))
  
  // AndroidX Core
  implementation(libs.androidx.core.ktx)
  
  // Activity Compose
  implementation(libs.androidx.activity.compose)
  
  // Material 3 - Latest stable
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  
  // Compose UI
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  
  // Lifecycle
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  
  // Room Database
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  
  // Coroutines
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  
  // Testing
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  
  // Android Testing
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  
  // Debug only
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  
  // KSP for Room
  "ksp"(libs.androidx.room.compiler)
}
