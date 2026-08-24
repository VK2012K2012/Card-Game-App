package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/** Persistable theme mode independent of the selected color seed. */
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
    PaletteOption(
        id = "baseline",
        title = "Baseline Purple",
        seed = Color(0xFF6750A4),
        lightScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE9DDFF),
            onPrimaryContainer = Color(0xFF22005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1E192B),
            tertiary = Color(0xFF7E5260),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD9E3),
            onTertiaryContainer = Color(0xFF31101D),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFF),
            onBackground = Color(0xFF1C1B1E),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF1C1B1E),
            surfaceVariant = Color(0xFFE7E0EB),
            onSurfaceVariant = Color(0xFF49454E),
            outline = Color(0xFF7A757F),
            outlineVariant = Color(0xFFCAC4CF),
            inverseSurface = Color(0xFF313033),
            inverseOnSurface = Color(0xFFF4EFF4),
            inversePrimary = Color(0xFFCFBCFF),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFCFBCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378A),
            onPrimaryContainer = Color(0xFFE9DDFF),
            secondary = Color(0xFFCBC2DB),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF4A2532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD9E3),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF1C1B1E),
            onBackground = Color(0xFFE6E1E6),
            surface = Color(0xFF1C1B1E),
            onSurface = Color(0xFFE6E1E6),
            surfaceVariant = Color(0xFF49454E),
            onSurfaceVariant = Color(0xFFCAC4CF),
            outline = Color(0xFF948F99),
            outlineVariant = Color(0xFF49454E),
            inverseSurface = Color(0xFFE6E1E6),
            inverseOnSurface = Color(0xFF313033),
            inversePrimary = Color(0xFF6750A4),
            scrim = Color(0xFF000000),
        ),
    ),
    PaletteOption(
        id = "rose",
        title = "Rose",
        seed = Color(0xFFB3261E),
        lightScheme = lightColorScheme(
            primary = Color(0xFFB4271F),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDAD5),
            onPrimaryContainer = Color(0xFF410001),
            secondary = Color(0xFF775652),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFDAD5),
            onSecondaryContainer = Color(0xFF2C1512),
            tertiary = Color(0xFF705C2E),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFCDFA6),
            onTertiaryContainer = Color(0xFF261A00),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFF),
            onBackground = Color(0xFF201A19),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF201A19),
            surfaceVariant = Color(0xFFF5DDDA),
            onSurfaceVariant = Color(0xFF534341),
            outline = Color(0xFF857370),
            outlineVariant = Color(0xFFD8C2BE),
            inverseSurface = Color(0xFF362F2E),
            inverseOnSurface = Color(0xFFFBEEEC),
            inversePrimary = Color(0xFFFFB4AA),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFFFB4AA),
            onPrimary = Color(0xFF690003),
            primaryContainer = Color(0xFF910809),
            onPrimaryContainer = Color(0xFFFFDAD5),
            secondary = Color(0xFFE7BDB7),
            onSecondary = Color(0xFF442926),
            secondaryContainer = Color(0xFF5D3F3B),
            onSecondaryContainer = Color(0xFFFFDAD5),
            tertiary = Color(0xFFDFC38C),
            onTertiary = Color(0xFF3E2E04),
            tertiaryContainer = Color(0xFF574419),
            onTertiaryContainer = Color(0xFFFCDFA6),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF201A19),
            onBackground = Color(0xFFEDE0DE),
            surface = Color(0xFF201A19),
            onSurface = Color(0xFFEDE0DE),
            surfaceVariant = Color(0xFF534341),
            onSurfaceVariant = Color(0xFFD8C2BE),
            outline = Color(0xFFA08C89),
            outlineVariant = Color(0xFF534341),
            inverseSurface = Color(0xFFEDE0DE),
            inverseOnSurface = Color(0xFF362F2E),
            inversePrimary = Color(0xFFB4271F),
            scrim = Color(0xFF000000),
        ),
    ),
    PaletteOption(
        id = "ocean",
        title = "Ocean Blue",
        seed = Color(0xFF0061A4),
        lightScheme = lightColorScheme(
            primary = Color(0xFF0061A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD1E4FF),
            onPrimaryContainer = Color(0xFF001D36),
            secondary = Color(0xFF535F70),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD7E3F8),
            onSecondaryContainer = Color(0xFF101C2B),
            tertiary = Color(0xFF6B5778),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFF3DAFF),
            onTertiaryContainer = Color(0xFF251431),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFDFCFF),
            onBackground = Color(0xFF1A1C1E),
            surface = Color(0xFFFDFCFF),
            onSurface = Color(0xFF1A1C1E),
            surfaceVariant = Color(0xFFDFE2EB),
            onSurfaceVariant = Color(0xFF43474E),
            outline = Color(0xFF73777F),
            outlineVariant = Color(0xFFC3C6CF),
            inverseSurface = Color(0xFF2F3033),
            inverseOnSurface = Color(0xFFF1F0F4),
            inversePrimary = Color(0xFF9FCAFF),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF9FCAFF),
            onPrimary = Color(0xFF003258),
            primaryContainer = Color(0xFF00497D),
            onPrimaryContainer = Color(0xFFD1E4FF),
            secondary = Color(0xFFBBC7DB),
            onSecondary = Color(0xFF253140),
            secondaryContainer = Color(0xFF3B4858),
            onSecondaryContainer = Color(0xFFD7E3F8),
            tertiary = Color(0xFFD7BEE4),
            onTertiary = Color(0xFF3B2948),
            tertiaryContainer = Color(0xFF523F5F),
            onTertiaryContainer = Color(0xFFF3DAFF),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF1A1C1E),
            onBackground = Color(0xFFE2E2E6),
            surface = Color(0xFF1A1C1E),
            onSurface = Color(0xFFE2E2E6),
            surfaceVariant = Color(0xFF43474E),
            onSurfaceVariant = Color(0xFFC3C6CF),
            outline = Color(0xFF8D9199),
            outlineVariant = Color(0xFF43474E),
            inverseSurface = Color(0xFFE2E2E6),
            inverseOnSurface = Color(0xFF2F3033),
            inversePrimary = Color(0xFF0061A4),
            scrim = Color(0xFF000000),
        ),
    ),
    PaletteOption(
        id = "teal",
        title = "Teal",
        seed = Color(0xFF006A60),
        lightScheme = lightColorScheme(
            primary = Color(0xFF006A60),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF74F8E6),
            onPrimaryContainer = Color(0xFF00201C),
            secondary = Color(0xFF4A635F),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFCCE8E2),
            onSecondaryContainer = Color(0xFF05201C),
            tertiary = Color(0xFF466179),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFCCE5FF),
            onTertiaryContainer = Color(0xFF001D31),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFAFDFB),
            onBackground = Color(0xFF191C1B),
            surface = Color(0xFFFAFDFB),
            onSurface = Color(0xFF191C1B),
            surfaceVariant = Color(0xFFDAE5E2),
            onSurfaceVariant = Color(0xFF3F4947),
            outline = Color(0xFF6F7977),
            outlineVariant = Color(0xFFBEC9C6),
            inverseSurface = Color(0xFF2D3130),
            inverseOnSurface = Color(0xFFEFF1EF),
            inversePrimary = Color(0xFF53DBCA),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF53DBCA),
            onPrimary = Color(0xFF003732),
            primaryContainer = Color(0xFF005048),
            onPrimaryContainer = Color(0xFF74F8E6),
            secondary = Color(0xFFB1CCC6),
            onSecondary = Color(0xFF1C3531),
            secondaryContainer = Color(0xFF334B47),
            onSecondaryContainer = Color(0xFFCCE8E2),
            tertiary = Color(0xFFADCAE6),
            onTertiary = Color(0xFF153349),
            tertiaryContainer = Color(0xFF2D4961),
            onTertiaryContainer = Color(0xFFCCE5FF),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF191C1B),
            onBackground = Color(0xFFE0E3E1),
            surface = Color(0xFF191C1B),
            onSurface = Color(0xFFE0E3E1),
            surfaceVariant = Color(0xFF3F4947),
            onSurfaceVariant = Color(0xFFBEC9C6),
            outline = Color(0xFF899390),
            outlineVariant = Color(0xFF3F4947),
            inverseSurface = Color(0xFFE0E3E1),
            inverseOnSurface = Color(0xFF2D3130),
            inversePrimary = Color(0xFF006A60),
            scrim = Color(0xFF000000),
        ),
    ),
    PaletteOption(
        id = "forest",
        title = "Forest Green",
        seed = Color(0xFF386A20),
        lightScheme = lightColorScheme(
            primary = Color(0xFF386A1F),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFB8F397),
            onPrimaryContainer = Color(0xFF072100),
            secondary = Color(0xFF55624C),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD8E7CB),
            onSecondaryContainer = Color(0xFF131F0D),
            tertiary = Color(0xFF386666),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFBBEBEC),
            onTertiaryContainer = Color(0xFF002020),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFDFDF6),
            onBackground = Color(0xFF1A1C18),
            surface = Color(0xFFFDFDF6),
            onSurface = Color(0xFF1A1C18),
            surfaceVariant = Color(0xFFDFE4D7),
            onSurfaceVariant = Color(0xFF43483E),
            outline = Color(0xFF74796D),
            outlineVariant = Color(0xFFC3C8BB),
            inverseSurface = Color(0xFF2F312D),
            inverseOnSurface = Color(0xFFF1F1EA),
            inversePrimary = Color(0xFF9CD67E),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF9CD67E),
            onPrimary = Color(0xFF113800),
            primaryContainer = Color(0xFF205107),
            onPrimaryContainer = Color(0xFFB8F397),
            secondary = Color(0xFFBDCBB0),
            onSecondary = Color(0xFF283420),
            secondaryContainer = Color(0xFF3E4A35),
            onSecondaryContainer = Color(0xFFD8E7CB),
            tertiary = Color(0xFFA0CFD0),
            onTertiary = Color(0xFF003738),
            tertiaryContainer = Color(0xFF1E4E4F),
            onTertiaryContainer = Color(0xFFBBEBEC),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF1A1C18),
            onBackground = Color(0xFFE3E3DC),
            surface = Color(0xFF1A1C18),
            onSurface = Color(0xFFE3E3DC),
            surfaceVariant = Color(0xFF43483E),
            onSurfaceVariant = Color(0xFFC3C8BB),
            outline = Color(0xFF8D9286),
            outlineVariant = Color(0xFF43483E),
            inverseSurface = Color(0xFFE3E3DC),
            inverseOnSurface = Color(0xFF2F312D),
            inversePrimary = Color(0xFF386A1F),
            scrim = Color(0xFF000000),
        ),
    ),
    PaletteOption(
        id = "amber",
        title = "Amber",
        seed = Color(0xFF805600),
        lightScheme = lightColorScheme(
            primary = Color(0xFF805600),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDDB0),
            onPrimaryContainer = Color(0xFF281800),
            secondary = Color(0xFF6F5B40),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFF9DEBB),
            onSecondaryContainer = Color(0xFF261904),
            tertiary = Color(0xFF506441),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFD2EABD),
            onTertiaryContainer = Color(0xFF0E2004),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFF),
            onBackground = Color(0xFF1F1B16),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF1F1B16),
            surfaceVariant = Color(0xFFEFE0CF),
            onSurfaceVariant = Color(0xFF4F4539),
            outline = Color(0xFF817567),
            outlineVariant = Color(0xFFD2C4B4),
            inverseSurface = Color(0xFF34302A),
            inverseOnSurface = Color(0xFFF9EFE7),
            inversePrimary = Color(0xFFFDBA4B),
            scrim = Color(0xFF000000),
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFFDBA4B),
            onPrimary = Color(0xFF442C00),
            primaryContainer = Color(0xFF614000),
            onPrimaryContainer = Color(0xFFFFDDB0),
            secondary = Color(0xFFDCC3A1),
            onSecondary = Color(0xFF3D2E16),
            secondaryContainer = Color(0xFF55442A),
            onSecondaryContainer = Color(0xFFF9DEBB),
            tertiary = Color(0xFFB6CEA3),
            onTertiary = Color(0xFF233517),
            tertiaryContainer = Color(0xFF394C2B),
            onTertiaryContainer = Color(0xFFD2EABD),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFB4AB),
            background = Color(0xFF1F1B16),
            onBackground = Color(0xFFEAE1D9),
            surface = Color(0xFF1F1B16),
            onSurface = Color(0xFFEAE1D9),
            surfaceVariant = Color(0xFF4F4539),
            onSurfaceVariant = Color(0xFFD2C4B4),
            outline = Color(0xFF9B8F80),
            outlineVariant = Color(0xFF4F4539),
            inverseSurface = Color(0xFFEAE1D9),
            inverseOnSurface = Color(0xFF34302A),
            inversePrimary = Color(0xFF805600),
            scrim = Color(0xFF000000),
        ),
    ),
)

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
    val primary = tone(primarySeed, if (dark) 68f else 40f)
    val secondary = tone(secondarySeed, if (dark) 66f else 42f)
    val tertiary = tone(tertiarySeed, if (dark) 70f else 42f)
    val primaryContainer = tone(primarySeed, if (dark) 28f else 90f)
    val secondaryContainer = tone(secondarySeed, if (dark) 27f else 90f)
    val tertiaryContainer = tone(tertiarySeed, if (dark) 30f else 90f)

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
            background = Color(0xFF111216),
            onBackground = Color(0xFFE6E2E9),
            surface = Color(0xFF111216),
            onSurface = Color(0xFFE6E2E9),
            surfaceVariant = Color(0xFF46464E),
            onSurfaceVariant = Color(0xFFC7C5CE),
            outline = Color(0xFF919098),
            outlineVariant = Color(0xFF46464E),
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

private fun bestOnColor(background: Color): Color =
    if (contrastRatio(Color.White, background) >= contrastRatio(Color(0xFF111216), background)) {
        Color.White
    } else {
        Color(0xFF111216)
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
