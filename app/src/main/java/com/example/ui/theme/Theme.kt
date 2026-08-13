package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * App-wide Material 3 theme with deliberate expressive use of the stable color,
 * typography, shape, containment, and motion primitives available in Compose.
 * The palette is intentionally deterministic so the app keeps its warm Durak identity across devices.
 */
@Composable
fun CardGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DeepForestDarkColors else DeepForestLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CardGameTypography,
        shapes = CardGameShapes,
        content = content,
    )
}

object CardGameThemeTokens {
    val trumpGold: androidx.compose.ui.graphics.Color
        @Composable get() = TrumpGold
}
