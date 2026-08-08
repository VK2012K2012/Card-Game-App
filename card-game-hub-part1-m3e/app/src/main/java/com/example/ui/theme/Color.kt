package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Deep Forest M3 Expressive — fallback palette.
 *
 * Used ONLY on API < 31 (no Dynamic Color support) or if the system palette
 * can't be read for some reason. On API 31+ the app always prefers the
 * live system Dynamic Color scheme derived from the user's wallpaper.
 * See [CardGameTheme].
 *
 * Tonal roles below are hand-derived from seed 0xFFAFD43E (vivid chartreuse)
 * to sit alongside real Dynamic Color output without looking out of place.
 */

// ---- Dark ----
val DeepForestDarkColors = darkColorScheme(
    primary = Color(0xFFAFD43E),
    onPrimary = Color(0xFF1D2900),
    primaryContainer = Color(0xFF2C3D00),
    onPrimaryContainer = Color(0xFFCAF158),
    inversePrimary = Color(0xFF4C6600),

    secondary = Color(0xFFC5C9A6),
    onSecondary = Color(0xFF2C301A),
    secondaryContainer = Color(0xFF42472E),
    onSecondaryContainer = Color(0xFFE1E5BE),

    tertiary = Color(0xFF9ED4C8),
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF1F4F47),
    onTertiaryContainer = Color(0xFFBAF1E4),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF10130B),
    onBackground = Color(0xFFE2E3D5),

    surface = Color(0xFF10130B),
    onSurface = Color(0xFFE2E3D5),
    surfaceVariant = Color(0xFF44483A),
    onSurfaceVariant = Color(0xFFC5C8B4),

    surfaceDim = Color(0xFF10130B),
    surfaceBright = Color(0xFF36392F),
    surfaceContainerLowest = Color(0xFF0B0E07),
    surfaceContainerLow = Color(0xFF181B12),
    surfaceContainer = Color(0xFF1C1F16),
    surfaceContainerHigh = Color(0xFF262920),
    surfaceContainerHighest = Color(0xFF31342A),

    outline = Color(0xFF8F927F),
    outlineVariant = Color(0xFF44483A),
    inverseSurface = Color(0xFFE2E3D5),
    inverseOnSurface = Color(0xFF2F3226),
    scrim = Color(0xFF000000),
)

// ---- Light ----
val DeepForestLightColors = lightColorScheme(
    primary = Color(0xFF4C6600),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCAF158),
    onPrimaryContainer = Color(0xFF141F00),
    inversePrimary = Color(0xFFAFD43E),

    secondary = Color(0xFF5B6047),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE0E4BC),
    onSecondaryContainer = Color(0xFF181D08),

    tertiary = Color(0xFF3C6B60),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBEF0E1),
    onTertiaryContainer = Color(0xFF002019),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFBFAEE),
    onBackground = Color(0xFF1B1C13),

    surface = Color(0xFFFBFAEE),
    onSurface = Color(0xFF1B1C13),
    surfaceVariant = Color(0xFFE4E4D0),
    onSurfaceVariant = Color(0xFF46483B),

    surfaceDim = Color(0xFFDBDBCF),
    surfaceBright = Color(0xFFFBFAEE),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F4E8),
    surfaceContainer = Color(0xFFEFEEE2),
    surfaceContainerHigh = Color(0xFFE9E9DC),
    surfaceContainerHighest = Color(0xFFE3E3D7),

    outline = Color(0xFF767869),
    outlineVariant = Color(0xFFC7C7B4),
    inverseSurface = Color(0xFF303127),
    inverseOnSurface = Color(0xFFF3F2E5),
    scrim = Color(0xFF000000),
)

// Semantic, palette-independent accents used sparingly for card-game specific
// meaning (trump highlight, suit reds). Intentionally NOT part of the M3
// ColorScheme because they must stay legible against both light and dark
// dynamic surfaces regardless of the user's wallpaper hue.
val TrumpGold = Color(0xFFE7B93B)
val TrumpGoldOnGold = Color(0xFF3A2E00)
val SuitRedLight = Color(0xFFB3261E)
val SuitRedDark = Color(0xFFFF897D)
