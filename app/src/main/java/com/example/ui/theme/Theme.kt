package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * App-wide Material 3 theme with adaptive Dynamic Color on Android 12+.
 * The deep forest/warm Durak palette remains the accessible fallback for older
 * devices and for callers that intentionally opt out of system colors.
 */
@Composable
fun CardGameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme -> {
            dynamicDarkColorScheme(context)
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicLightColorScheme(context)
        }
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
