package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    colorPreset: ThemeColorPreset = ThemeColorPreset.SYSTEM,
    paletteOption: PaletteOption? = null,
    customPalette: CustomPalette? = null,
    amoledBlack: Boolean = false,
    useRobotoFlex: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val effectiveDark = when (themeMode) {
        ThemeMode.SYSTEM -> darkTheme
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = androidx.compose.runtime.remember(
        themeMode,
        effectiveDark,
        dynamicColor,
        colorPreset,
        paletteOption?.id,
        customPalette,
        amoledBlack,
        context,
    ) {
        when {
        themeMode == ThemeMode.SYSTEM && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeMode == ThemeMode.SYSTEM -> if (effectiveDark) DeepForestDarkColors else DeepForestLightColors
        customPalette != null -> if (effectiveDark) {
            darkSchemeForCustom(customPalette).withManualDarkBackground(amoledBlack)
        } else {
            lightSchemeForCustom(customPalette).withManualLightBackground()
        }
        paletteOption != null -> if (effectiveDark) {
            darkSchemeForPalette(paletteOption).withManualDarkBackground(amoledBlack)
        } else {
            lightSchemeForPalette(paletteOption).withManualLightBackground()
        }
        effectiveDark -> darkColorsFor(colorPreset).withManualDarkBackground(amoledBlack)
            else -> lightColorsFor(colorPreset).withManualLightBackground()
        }
    }

    Crossfade(
        targetState = colorScheme,
        animationSpec = tween(durationMillis = 220),
        label = "theme-color-transition",
    ) { animatedScheme ->
        MaterialExpressiveTheme(
            colorScheme = animatedScheme,
            motionScheme = MotionScheme.expressive(),
            typography = if (useRobotoFlex) GoogleSansFlexTypography else CardGameTypography,
            shapes = CardGameShapes,
            content = content,
        )
    }
}

private fun ColorScheme.withManualLightBackground(): ColorScheme = copy(
    background = Color.White,
    surface = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF8F8F8),
    surfaceContainer = Color(0xFFF2F2F2),
    surfaceContainerHigh = Color(0xFFECECEC),
    surfaceContainerHighest = Color(0xFFE6E6E6),
)

private fun ColorScheme.withManualDarkBackground(amoledBlack: Boolean): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = if (amoledBlack) Color.Black else Color(0xFF0A0A0A),
    surfaceContainer = if (amoledBlack) Color.Black else Color(0xFF121212),
    surfaceContainerHigh = if (amoledBlack) Color.Black else Color(0xFF1C1C1C),
    surfaceContainerHighest = if (amoledBlack) Color.Black else Color(0xFF242424),
)

object CardGameThemeTokens {
    val trumpGold: androidx.compose.ui.graphics.Color
        @Composable get() = TrumpGold
}
