# Card Game Hub ProGuard/R8 Rules
# Material 3 Expressive + Jetpack Compose Optimizations

# ============================================
# R8 Full Mode Optimizations
# ============================================

# Keep all Compose functions - they use reflection
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.animation.** { *; }

# Keep Composable annotations and functions
-keep class * { @androidx.compose.runtime.Composable <methods>; }
-keep class * { @androidx.compose.ui.tooling.preview.Preview <methods>; }

# ============================================
# Room Database
# ============================================

# Room discovers the database implementation through generated names at runtime
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Embedded class * { *; }

# Keep Room type converters
-keep class * implements androidx.room.TypeConverter { *; }

# ============================================
# Kotlin Coroutines
# ============================================

# Keep coroutine suspend functions
-keep class kotlinx.coroutines.** { *; }
-keep class * { @kotlinx.coroutines.SuspendFunction <methods>; }

# ============================================
# AndroidX Lifecycle
# ============================================

-keep class androidx.lifecycle.** { *; }
-keep class * implements androidx.lifecycle.LifecycleObserver { *; }

# ============================================
# Material 3 Expressive Specific
# ============================================

# Keep MaterialExpressiveTheme and related classes
-keep class androidx.compose.material3.MaterialExpressiveTheme { *; }
-keep class androidx.compose.material3.MotionScheme { *; }
-keep class androidx.compose.material3.ExperimentalMaterial3ExpressiveApi { *; }

# Keep animation spec classes
-keep class androidx.compose.animation.core.** { *; }

# ============================================
# Debug Information (Optional - remove for release)
# ============================================

# Keep useful information in release crash traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep stack traces readable
-keepattributes *Annotation*
-keepclassmembers class * {
    public private <fields>;
    public private <methods>;
}

# ============================================
# Optimization: Remove unused code
# ============================================

# Allow R8 to remove unused code
-dontskipnonpubliclibraryclassmembers

# Remove logging in release
-assumenosideeffects class android.util.Log { *; }

# ============================================
# AndroidX Activity
# ============================================

-keep class androidx.activity.** { *; }
-keep class androidx.core.** { *; }

# ============================================
# Compose Navigation (if added in future)
# ============================================

# -keep class androidx.navigation.** { *; }
# -keep class * implements androidx.navigation.Navigator { *; }

# ============================================
# WebView (if used)
# ============================================

# -keep class android.webkit.WebViewClient { *; }
# -keep class * extends android.webkit.WebViewClient { *; }
