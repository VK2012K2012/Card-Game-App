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
    primary = Color(0xFFFFB4A8),
    onPrimary = Color(0xFF5B160E),
    primaryContainer = Color(0xFF7D2B22),
    onPrimaryContainer = Color(0xFFFFDAD4),
    inversePrimary = Color(0xFF9A3D33),

    secondary = Color(0xFFE6BDB6),
    onSecondary = Color(0xFF442926),
    secondaryContainer = Color(0xFF62423D),
    onSecondaryContainer = Color(0xFFFFDAD5),

    tertiary = Color(0xFFE6C18D),
    onTertiary = Color(0xFF3E2E14),
    tertiaryContainer = Color(0xFF5A4526),
    onTertiaryContainer = Color(0xFFF8DDA8),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF170D0B),
    onBackground = Color(0xFFF7EDE9),

    surface = Color(0xFF170D0B),
    onSurface = Color(0xFFF7EDE9),
    surfaceVariant = Color(0xFF534340),
    onSurfaceVariant = Color(0xFFD8C2BD),

    surfaceDim = Color(0xFF170D0B),
    surfaceBright = Color(0xFF44322E),
    surfaceContainerLowest = Color(0xFF110807),
    surfaceContainerLow = Color(0xFF211310),
    surfaceContainer = Color(0xFF261916),
    surfaceContainerHigh = Color(0xFF30201C),
    surfaceContainerHighest = Color(0xFF3C2B27),

    outline = Color(0xFFA98F89),
    outlineVariant = Color(0xFF534340),
    inverseSurface = Color(0xFFF7EDE9),
    inverseOnSurface = Color(0xFF3E2824),
    scrim = Color(0xFF000000),
)

// ---- Light ----
val DeepForestLightColors = lightColorScheme(
    primary = Color(0xFF9A3D33),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF3B0905),
    inversePrimary = Color(0xFFFFB4A8),

    secondary = Color(0xFF765752),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD5),
    onSecondaryContainer = Color(0xFF2C1512),

    tertiary = Color(0xFF755B2F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF8DDA8),
    onTertiaryContainer = Color(0xFF281900),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFFF8F6),
    onBackground = Color(0xFF221A18),

    surface = Color(0xFFFFF8F6),
    onSurface = Color(0xFF221A18),
    surfaceVariant = Color(0xFFF4DDDA),
    onSurfaceVariant = Color(0xFF5B4541),

    surfaceDim = Color(0xFFE6D6D3),
    surfaceBright = Color(0xFFFFF8F6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF0ED),
    surfaceContainer = Color(0xFFFCE9E5),
    surfaceContainerHigh = Color(0xFFF7E2DE),
    surfaceContainerHighest = Color(0xFFF1DCD8),

    outline = Color(0xFF8D706B),
    outlineVariant = Color(0xFFD8C2BD),
    inverseSurface = Color(0xFF382A27),
    inverseOnSurface = Color(0xFFFFEDE9),
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
