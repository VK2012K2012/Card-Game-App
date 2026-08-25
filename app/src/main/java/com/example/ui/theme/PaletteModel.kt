package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

enum class ThemeMode(val key: String, val title: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark");

    companion object {
        fun fromKey(value: String?): ThemeMode = entries.firstOrNull { it.key == value } ?: SYSTEM
    }
}

data class CustomPalette(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
)

data class PaletteOption(
    val id: String,
    val title: String,
    val seed: Color,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme,
)

private val curatedPaletteOptions = listOf(
    materialPalette("coral", "Coral", 0xFFFF5A6D, 0xFFE86A78, 0xFFC43B5B),
    materialPalette("pink", "Pink", 0xFFFF5FA2, 0xFFD94F8B, 0xFFFF9CB8),
    materialPalette("violet", "Violet", 0xFF9B5BD6, 0xFF70418F, 0xFFC17BFF),
    materialPalette("orange", "Orange", 0xFFFF7A3D, 0xFFD75A35, 0xFFFFB36B),
    materialPalette("amber", "Amber", 0xFFFFB000, 0xFFD48700, 0xFFFFD05A),
    materialPalette("forest", "Green", 0xFF3A934B, 0xFF2F6D48, 0xFF8DBB5A),
    materialPalette("teal", "Teal", 0xFF20BFA9, 0xFF278B88, 0xFF70D9CC),
    materialPalette("ocean", "Blue", 0xFF1769E8, 0xFF345BA4, 0xFF62B8FF),
)

private fun materialPalette(
    id: String,
    title: String,
    primarySeed: Long,
    secondarySeed: Long,
    tertiarySeed: Long,
): PaletteOption {
    val primary = Color(primarySeed)
    val secondary = Color(secondarySeed)
    val tertiary = Color(tertiarySeed)
    return PaletteOption(
        id = id,
        title = title,
        seed = primary,
        lightScheme = schemeFromSeeds(primary, secondary, tertiary, dark = false),
        darkScheme = schemeFromSeeds(primary, secondary, tertiary, dark = true),
    )
}

object PaletteCatalog {
    val featured: List<PaletteOption> = curatedPaletteOptions
    val all: List<PaletteOption> = curatedPaletteOptions

    fun byId(id: String?): PaletteOption = all.firstOrNull { it.id == id } ?: featured.first()
}

fun palettePreviewTones(option: PaletteOption): List<Color> = listOf(
    tone(option.seed, 92f),
    tone(option.seed, 68f),
    rotateHue(tone(option.seed, 78f), 18f),
    rotateHue(tone(option.seed, 58f), -18f),
)

fun lightSchemeForPalette(palette: PaletteOption): ColorScheme = palette.lightScheme

fun darkSchemeForPalette(palette: PaletteOption): ColorScheme = palette.darkScheme

fun lightSchemeForCustom(palette: CustomPalette): ColorScheme = schemeFromSeeds(
    palette.primary,
    palette.secondary,
    palette.tertiary,
    dark = false,
)

fun darkSchemeForCustom(palette: CustomPalette): ColorScheme = schemeFromSeeds(
    palette.primary,
    palette.secondary,
    palette.tertiary,
    dark = true,
)

fun contrastRatio(foreground: Color, background: Color): Double {
    val first = relativeLuminance(foreground)
    val second = relativeLuminance(background)
    return (max(first, second) + 0.05) / (min(first, second) + 0.05)
}

fun isContrastAccessible(foreground: Color, background: Color, minimum: Double = 4.5): Boolean =
    contrastRatio(foreground, background) >= minimum

fun hsvColorForThemeEditor(hue: Float, saturation: Float, value: Float): Color =
    hsvToColor(hue, saturation, value)

fun previewOnColor(background: Color): Color = bestOnColor(background)

fun ColorScheme.hasAccessiblePrimaryRoles(): Boolean =
    isContrastAccessible(onPrimary, primary) &&
        isContrastAccessible(onPrimaryContainer, primaryContainer) &&
        isContrastAccessible(onSurface, surface) &&
        contrastRatio(onSurfaceVariant, surfaceVariant) >= 3.0

private fun schemeFromSeeds(
    primarySeed: Color,
    secondarySeed: Color,
    tertiarySeed: Color,
    dark: Boolean,
): ColorScheme {
    val primary = accessibleTone(primarySeed, if (dark) 68f else 40f)
    val secondary = accessibleTone(secondarySeed, if (dark) 66f else 42f)
    val tertiary = accessibleTone(tertiarySeed, if (dark) 70f else 42f)
    val primaryContainer = accessibleTone(primarySeed, if (dark) 28f else 90f)
    val secondaryContainer = accessibleTone(secondarySeed, if (dark) 27f else 90f)
    val tertiaryContainer = accessibleTone(tertiarySeed, if (dark) 30f else 90f)
    val darkBackground = tintSurface(Color(0xFF111216), primarySeed, 0.06f)
    val darkSurface = tintSurface(Color(0xFF141319), primarySeed, 0.08f)
    val darkSurfaceLow = tintSurface(Color(0xFF1B191F), primarySeed, 0.10f)
    val darkSurfaceContainer = tintSurface(Color(0xFF211F26), primarySeed, 0.12f)
    val darkSurfaceHigh = tintSurface(Color(0xFF2A2730), primarySeed, 0.14f)
    val darkSurfaceHighest = tintSurface(Color(0xFF34303A), primarySeed, 0.16f)

    return if (dark) {
        darkColorScheme(
            primary = primary,
            onPrimary = bestOnColor(primary),
            primaryContainer = primaryContainer,
            onPrimaryContainer = bestOnColor(primaryContainer),
            secondary = secondary,
            onSecondary = bestOnColor(secondary),
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = bestOnColor(secondaryContainer),
            tertiary = tertiary,
            onTertiary = bestOnColor(tertiary),
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = bestOnColor(tertiaryContainer),
            background = darkBackground,
            onBackground = Color(0xFFE6E2E9),
            surface = darkSurface,
            onSurface = Color(0xFFE6E2E9),
            surfaceVariant = darkSurfaceHigh,
            onSurfaceVariant = Color(0xFFC7C5CE),
            outline = Color(0xFFB0AAB5),
            outlineVariant = darkSurfaceHigh,
            surfaceDim = darkBackground,
            surfaceBright = darkSurfaceHighest,
            surfaceContainerLowest = Color(0xFF0B0A0D),
            surfaceContainerLow = darkSurfaceLow,
            surfaceContainer = darkSurfaceContainer,
            surfaceContainerHigh = darkSurfaceHigh,
            surfaceContainerHighest = darkSurfaceHighest,
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = bestOnColor(primary),
            primaryContainer = primaryContainer,
            onPrimaryContainer = bestOnColor(primaryContainer),
            secondary = secondary,
            onSecondary = bestOnColor(secondary),
            secondaryContainer = secondaryContainer,
            onSecondaryContainer = bestOnColor(secondaryContainer),
            tertiary = tertiary,
            onTertiary = bestOnColor(tertiary),
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = bestOnColor(tertiaryContainer),
            background = Color(0xFFFBF9FE),
            onBackground = Color(0xFF1B1B20),
            surface = Color(0xFFFBF9FE),
            onSurface = Color(0xFF1B1B20),
            surfaceVariant = Color(0xFFE5E1E8),
            onSurfaceVariant = Color(0xFF47464D),
            outline = Color(0xFF77767D),
            outlineVariant = Color(0xFFC8C5CC),
        )
    }
}

private fun tintSurface(base: Color, tint: Color, amount: Float): Color {
    val fraction = amount.coerceIn(0f, 1f)
    return Color(
        red = base.red + (tint.red - base.red) * fraction,
        green = base.green + (tint.green - base.green) * fraction,
        blue = base.blue + (tint.blue - base.blue) * fraction,
        alpha = 1f,
    )
}

private fun bestOnColor(background: Color): Color =
    if (contrastRatio(Color.White, background) >= contrastRatio(Color(0xFF111216), background)) {
        Color.White
    } else {
        Color(0xFF111216)
    }

private fun accessibleTone(color: Color, preferred: Float): Color {
    val candidates = (0..100)
        .map { it.toFloat() }
        .sortedBy { abs(it - preferred) }
    return candidates
        .map { tone(color, it) }
        .firstOrNull { contrastRatio(bestOnColor(it), it) >= 4.5 }
        ?: tone(color, preferred)
}

private fun tone(color: Color, valuePercent: Float): Color {
    val hsv = colorToHsv(color)
    hsv[2] = (valuePercent / 100f).coerceIn(0f, 1f)
    return hsvToColor(hsv[0], hsv[1], hsv[2])
}

private fun rotateHue(color: Color, degrees: Float): Color {
    val hsv = colorToHsv(color)
    hsv[0] = (hsv[0] + degrees).mod(360f)
    return hsvToColor(hsv[0], hsv[1], hsv[2])
}

private fun colorToHsv(color: Color): FloatArray {
    val red = color.red
    val green = color.green
    val blue = color.blue
    val maximum = maxOf(red, green, blue)
    val minimum = minOf(red, green, blue)
    val delta = maximum - minimum
    val hue = when {
        delta == 0f -> 0f
        maximum == red -> (60f * ((green - blue) / delta)).mod(360f)
        maximum == green -> 60f * ((blue - red) / delta + 2f)
        else -> 60f * ((red - green) / delta + 4f)
    }
    val saturation = if (maximum == 0f) 0f else delta / maximum
    return floatArrayOf(if (hue < 0f) hue + 360f else hue, saturation, maximum)
}

private fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val normalizedHue = hue.coerceIn(0f, 360f) / 60f
    val chroma = value * saturation
    val x = chroma * (1f - abs(normalizedHue % 2f - 1f))
    val match = value - chroma
    val rgb = when (normalizedHue.toInt()) {
        0 -> floatArrayOf(chroma, x, 0f)
        1 -> floatArrayOf(x, chroma, 0f)
        2 -> floatArrayOf(0f, chroma, x)
        3 -> floatArrayOf(0f, x, chroma)
        4 -> floatArrayOf(x, 0f, chroma)
        else -> floatArrayOf(chroma, 0f, x)
    }
    return Color(rgb[0] + match, rgb[1] + match, rgb[2] + match, 1f)
}

private fun relativeLuminance(color: Color): Double {
    fun channel(value: Float): Double = if (value <= 0.04045f) {
        value / 12.92
    } else {
        ((value + 0.055) / 1.055).toDouble().pow(2.4)
    }
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}
