package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val DeepForestDarkColors = darkColorScheme(
    primary = Color(0xFFA5D8B8),
    onPrimary = Color(0xFF073923),
    primaryContainer = Color(0xFF22513A),
    onPrimaryContainer = Color(0xFFC1EED2),
    inversePrimary = Color(0xFF356B4F),

    secondary = Color(0xFFDED18C),
    onSecondary = Color(0xFF38320A),
    secondaryContainer = Color(0xFF51491C),
    onSecondaryContainer = Color(0xFFF5E7A5),

    tertiary = Color(0xFFE7BE63),
    onTertiary = Color(0xFF3C2E00),
    tertiaryContainer = Color(0xFF5A4600),
    onTertiaryContainer = Color(0xFFF7D984),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF101510),
    onBackground = Color(0xFFE1E5DC),

    surface = Color(0xFF101510),
    onSurface = Color(0xFFE1E5DC),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC2C9BD),

    surfaceDim = Color(0xFF101510),
    surfaceBright = Color(0xFF353B33),
    surfaceContainerLowest = Color(0xFF0A0F0A),
    surfaceContainerLow = Color(0xFF181E18),
    surfaceContainer = Color(0xFF1C231C),
    surfaceContainerHigh = Color(0xFF272D26),
    surfaceContainerHighest = Color(0xFF31382F),

    outline = Color(0xFF8C9387),
    outlineVariant = Color(0xFF424940),
    inverseSurface = Color(0xFFE1E5DC),
    inverseOnSurface = Color(0xFF2D332C),
    scrim = Color(0xFF000000),
)

val DeepForestLightColors = lightColorScheme(
    primary = Color(0xFF176044),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCBEED8),
    onPrimaryContainer = Color(0xFF002112),
    inversePrimary = Color(0xFFA5D8B8),

    secondary = Color(0xFF625A27),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF1E8B5),
    onSecondaryContainer = Color(0xFF201B00),

    tertiary = Color(0xFF765900),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF6D878),
    onTertiaryContainer = Color(0xFF251A00),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFF8F8F1),
    onBackground = Color(0xFF191D18),

    surface = Color(0xFFF8F8F1),
    onSurface = Color(0xFF191D18),
    surfaceVariant = Color(0xFFE1E6DA),
    onSurfaceVariant = Color(0xFF424940),

    surfaceDim = Color(0xFFD9DDD3),
    surfaceBright = Color(0xFFF8F8F1),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F3EA),
    surfaceContainer = Color(0xFFEBEEE4),
    surfaceContainerHigh = Color(0xFFE5E9DE),
    surfaceContainerHighest = Color(0xFFDDE2D6),

    outline = Color(0xFF72786D),
    outlineVariant = Color(0xFFC2C9BD),
    inverseSurface = Color(0xFF2E342D),
    inverseOnSurface = Color(0xFFEFF2E9),
    scrim = Color(0xFF000000),
)

enum class ThemeColorPreset(val title: String, val description: String) {
    SYSTEM("System dynamic", "Use your Android wallpaper colors"),
    WARM("Game night", "Forest green, ivory, and warm gold"),
    OCEAN("Ocean", "Cool blue and teal"),
    FOREST("Forest", "Deep green and mint"),
    VIOLET("Violet", "Expressive purple and rose")
}

fun lightColorsFor(preset: ThemeColorPreset) = when (preset) {
    ThemeColorPreset.SYSTEM, ThemeColorPreset.WARM -> DeepForestLightColors
    ThemeColorPreset.OCEAN -> lightColorScheme(
        primary = Color(0xFF00639A),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC9E6FF),
        onPrimaryContainer = Color(0xFF001D32),
        secondary = Color(0xFF4F6070),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD3E5F5),
        onSecondaryContainer = Color(0xFF0B1D2A),
        tertiary = Color(0xFF006874),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFF95F0FC),
        onTertiaryContainer = Color(0xFF001F24)
    )
    ThemeColorPreset.FOREST -> lightColorScheme(
        primary = Color(0xFF416932),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC2E9A9),
        onPrimaryContainer = Color(0xFF0B2004),
        secondary = Color(0xFF56624D),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD9E7CD),
        onSecondaryContainer = Color(0xFF141E10),
        tertiary = Color(0xFF386568),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFBCEBF0),
        onTertiaryContainer = Color(0xFF002022)
    )
    ThemeColorPreset.VIOLET -> lightColorScheme(
        primary = Color(0xFF74536F),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFD7F6),
        onPrimaryContainer = Color(0xFF2B1229),
        secondary = Color(0xFF695D66),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF1DBEB),
        onSecondaryContainer = Color(0xFF241722),
        tertiary = Color(0xFF80533E),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFFDBCA),
        onTertiaryContainer = Color(0xFF321306)
    )
}

fun darkColorsFor(preset: ThemeColorPreset) = when (preset) {
    ThemeColorPreset.SYSTEM, ThemeColorPreset.WARM -> DeepForestDarkColors
    ThemeColorPreset.OCEAN -> darkColorScheme(
        primary = Color(0xFF91CDFF),
        onPrimary = Color(0xFF003350),
        primaryContainer = Color(0xFF004B73),
        onPrimaryContainer = Color(0xFFC9E6FF),
        secondary = Color(0xFFB7C9D9),
        onSecondary = Color(0xFF21333F),
        secondaryContainer = Color(0xFF384A56),
        onSecondaryContainer = Color(0xFFD3E5F5),
        tertiary = Color(0xFF4DD9E8),
        onTertiary = Color(0xFF00363C),
        tertiaryContainer = Color(0xFF004F58),
        onTertiaryContainer = Color(0xFF95F0FC)
    )
    ThemeColorPreset.FOREST -> darkColorScheme(
        primary = Color(0xFFA7D58F),
        onPrimary = Color(0xFF17370D),
        primaryContainer = Color(0xFF2A501E),
        onPrimaryContainer = Color(0xFFC2E9A9),
        secondary = Color(0xFFBDCCB2),
        onSecondary = Color(0xFF283426),
        secondaryContainer = Color(0xFF3E4A3A),
        onSecondaryContainer = Color(0xFFD9E7CD),
        tertiary = Color(0xFFA0CFD3),
        onTertiary = Color(0xFF00363A),
        tertiaryContainer = Color(0xFF1F4D51),
        onTertiaryContainer = Color(0xFFBCEBF0)
    )
    ThemeColorPreset.VIOLET -> darkColorScheme(
        primary = Color(0xFFE9B9E1),
        onPrimary = Color(0xFF42203E),
        primaryContainer = Color(0xFF5B3856),
        onPrimaryContainer = Color(0xFFFFD7F6),
        secondary = Color(0xFFD5C0D0),
        onSecondary = Color(0xFF392D37),
        secondaryContainer = Color(0xFF51444E),
        onSecondaryContainer = Color(0xFFF1DBEB),
        tertiary = Color(0xFFF4B99C),
        onTertiary = Color(0xFF4A2618),
        tertiaryContainer = Color(0xFF633D2D),
        onTertiaryContainer = Color(0xFFFFDBCA)
    )
}

val TrumpGold = Color(0xFFE7B93B)
val TrumpGoldOnGold = Color(0xFF3A2E00)
val SuitRedLight = Color(0xFFB3261E)
val SuitRedDark = Color(0xFFFF897D)
