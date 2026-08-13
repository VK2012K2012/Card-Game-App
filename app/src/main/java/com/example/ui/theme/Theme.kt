package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * App-wide Material 3 theme with deliberate expressive use of the stable color,
 * typography, shape, containment, and motion primitives available in Compose.
 * Dynamic system color is used on Android 12+; Deep Forest remains the fallback.
 */
@Composable
fun CardGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> dynamicLightColorScheme(context)
        darkTheme -> DeepForestDarkColors
        else -> DeepForestLightColors
    }

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
