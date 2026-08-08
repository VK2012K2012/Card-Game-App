package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.durak.model.CardBackStyle
import com.example.durak.model.FeltStyle
import com.example.durak.model.ThemePalette

/**
 * Non-color game presentation preferences (felt finish, card back motif).
 * Color is deliberately NOT stored here anymore — the color scheme is
 * always derived live from the Android system palette (Dynamic Color),
 * never from a user-picked in-app theme. See [ThemePalette] usages in
 * [com.example.ui.screens.SettingsCustomizerScreen] — the palette field is
 * retained only for cosmetic labeling of felt/card-back presets, not for
 * driving MaterialTheme.colorScheme.
 */
data class CardAppThemeState(
    val palette: ThemePalette = ThemePalette.DEEP_FOREST,
    val feltStyle: FeltStyle = FeltStyle.CLASSIC_FELT,
    val cardBackStyle: CardBackStyle = CardBackStyle.RED_SCROLL,
)

val LocalCardAppTheme = staticCompositionLocalOf { CardAppThemeState() }

/**
 * App-wide Material 3 Expressive theme.
 *
 * Color strategy — **true system Dynamic Color, always**:
 * - API 31+ (Android 12+): [dynamicLightColorScheme] / [dynamicDarkColorScheme]
 *   are derived directly from the device wallpaper each time the composable
 *   recomposes, so the app automatically re-themes itself whenever the user
 *   changes their wallpaper or system accent color. There is intentionally
 *   no persisted "app theme color" and no in-app light/dark override — dark
 *   vs. light strictly follows [isSystemInDarkTheme].
 * - API < 31: Dynamic Color isn't available at the OS level, so we fall back
 *   to a static, hand-tuned "Deep Forest" M3 palette (see Color.kt) in the
 *   correct light/dark variant. This is a platform capability fallback, not
 *   a user-facing setting.
 *
 * Shape / type / motion: uses [MaterialExpressiveTheme], the M3E entry point,
 * with the app's expressive type scale, expanded shape scale, and the
 * platform's expressive spring-based [MotionScheme] for a livelier, more
 * tactile motion feel across buttons, sheets, and navigation transitions.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardGameTheme(
    themeState: CardAppThemeState = CardAppThemeState(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val supportsDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        supportsDynamicColor && darkTheme -> dynamicDarkColorScheme(context)
        supportsDynamicColor && !darkTheme -> dynamicLightColorScheme(context)
        darkTheme -> DeepForestDarkColors
        else -> DeepForestLightColors
    }

    CompositionLocalProvider(LocalCardAppTheme provides themeState) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            typography = CardGameTypography,
            shapes = CardGameShapes,
            motionScheme = MotionScheme.expressive(),
            content = content,
        )
    }
}

/** Convenience accessor mirroring [MaterialTheme] usages already spread across the codebase. */
object CardGameTheme {
    val trumpGold: androidx.compose.ui.graphics.Color
        @Composable get() = TrumpGold
}
