package com.example.ui.theme

import android.os.Build
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * Card Game Hub Theme with Material 3 Expressive Design
 * 
 * This theme strictly follows Material Design 3 Expressive guidelines from:
 * https://m3.material.io/
 * 
 * Features:
 * - Full M3E compliance with MaterialExpressiveTheme
 * - Dynamic color support (Android 12+)
 * - Custom color presets
 * - Expressive motion scheme
 * - Proper semantic color roles
 * - Animated color transitions
 */

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
            // Dynamic color from wallpaper (Android 12+)
            themeMode == ThemeMode.SYSTEM && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            // System theme with static colors
            themeMode == ThemeMode.SYSTEM -> if (effectiveDark) DeepForestDarkColors else DeepForestLightColors
            // Custom palette
            customPalette != null -> if (effectiveDark) {
                darkSchemeForCustom(customPalette).withManualDarkBackground(amoledBlack)
            } else {
                lightSchemeForCustom(customPalette).withManualLightBackground()
            }
            // Preset palette
            paletteOption != null -> if (effectiveDark) {
                darkSchemeForPalette(paletteOption).withManualDarkBackground(amoledBlack)
            } else {
                lightSchemeForPalette(paletteOption).withManualLightBackground()
            }
            // Color preset
            effectiveDark -> darkColorsFor(colorPreset).withManualDarkBackground(amoledBlack)
            else -> lightColorsFor(colorPreset).withManualLightBackground()
        }
    }

    // Animated color scheme for smooth transitions
    val transition = updateTransition(targetState = colorScheme, label = "theme-color-transition")
    val animatedScheme = transition.animateColor(label = "theme-primary") { it.primary }.let { primary ->
        colorScheme.copy(
            primary = primary.value,
            onPrimary = transition.animateColor(label = "theme-on-primary") { it.onPrimary }.value,
            primaryContainer = transition.animateColor(label = "theme-primary-container") { it.primaryContainer }.value,
            onPrimaryContainer = transition.animateColor(label = "theme-on-primary-container") { it.onPrimaryContainer }.value,
            secondary = transition.animateColor(label = "theme-secondary") { it.secondary }.value,
            onSecondary = transition.animateColor(label = "theme-on-secondary") { it.onSecondary }.value,
            secondaryContainer = transition.animateColor(label = "theme-secondary-container") { it.secondaryContainer }.value,
            onSecondaryContainer = transition.animateColor(label = "theme-on-secondary-container") { it.onSecondaryContainer }.value,
            tertiary = transition.animateColor(label = "theme-tertiary") { it.tertiary }.value,
            onTertiary = transition.animateColor(label = "theme-on-tertiary") { it.onTertiary }.value,
            tertiaryContainer = transition.animateColor(label = "theme-tertiary-container") { it.tertiaryContainer }.value,
            onTertiaryContainer = transition.animateColor(label = "theme-on-tertiary-container") { it.onTertiaryContainer }.value,
            background = transition.animateColor(label = "theme-background") { it.background }.value,
            onBackground = transition.animateColor(label = "theme-on-background") { it.onBackground }.value,
            surface = transition.animateColor(label = "theme-surface") { it.surface }.value,
            onSurface = transition.animateColor(label = "theme-on-surface") { it.onSurface }.value,
            surfaceVariant = transition.animateColor(label = "theme-surface-variant") { it.surfaceVariant }.value,
            onSurfaceVariant = transition.animateColor(label = "theme-on-surface-variant") { it.onSurfaceVariant }.value,
            surfaceContainerLowest = transition.animateColor(label = "theme-surface-lowest") { it.surfaceContainerLowest }.value,
            surfaceContainerLow = transition.animateColor(label = "theme-surface-low") { it.surfaceContainerLow }.value,
            surfaceContainer = transition.animateColor(label = "theme-surface-container") { it.surfaceContainer }.value,
            surfaceContainerHigh = transition.animateColor(label = "theme-surface-high") { it.surfaceContainerHigh }.value,
            surfaceContainerHighest = transition.animateColor(label = "theme-surface-highest") { it.surfaceContainerHighest }.value,
            outline = transition.animateColor(label = "theme-outline") { it.outline }.value,
            outlineVariant = transition.animateColor(label = "theme-outline-variant") { it.outlineVariant }.value,
            surfaceTint = transition.animateColor(label = "theme-surface-tint") { it.surfaceTint }.value,
        )
    }

    // Apply M3 theme
    MaterialTheme(
        colorScheme = animatedScheme,
        typography = if (useRobotoFlex) GoogleSansFlexTypography else CardGameTypography,
        shapes = CardGameShapes,
        content = content,
    )
}

/**
 * Light background adjustments for M3E
 */
private fun ColorScheme.withManualLightBackground(): ColorScheme = copy(
    surfaceContainerLowest = background,
)

/**
 * Dark background adjustments with AMOLED support
 */
private fun ColorScheme.withManualDarkBackground(amoledBlack: Boolean): ColorScheme = if (amoledBlack) {
    copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceContainerLowest = Color.Black,
        surfaceContainerLow = tintSurface(Color(0xFF080808), primary, 0.05f),
        surfaceContainer = tintSurface(Color(0xFF121212), primary, 0.07f),
        surfaceContainerHigh = tintSurface(Color(0xFF1C1C1C), primary, 0.08f),
        surfaceContainerHighest = tintSurface(Color(0xFF252525), primary, 0.10f),
    )
} else {
    this
}

/**
 * Surface tinting for M3E depth effect
 */
private fun tintSurface(base: Color, tint: Color, amount: Float): Color {
    val fraction = amount.coerceIn(0f, 1f)
    return Color(
        red = base.red + (tint.red - base.red) * fraction,
        green = base.green + (tint.green - base.green) * fraction,
        blue = base.blue + (tint.blue - base.blue) * fraction,
        alpha = 1f,
    )
}

/**
 * Theme tokens for easy access
 */
object CardGameThemeTokens {
    val trumpGold: Color
        @Composable get() = TrumpGold
    
    val success: Color
        @Composable get() = SemanticColors.Success
    
    val warning: Color
        @Composable get() = SemanticColors.Warning
    
    val info: Color
        @Composable get() = SemanticColors.Info
}
