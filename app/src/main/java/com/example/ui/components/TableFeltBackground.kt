package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurEffect
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * Soft radial felt-table backdrop with blur effects for Material 3 Expressive design
 * Always derives from the live MaterialTheme color scheme (dynamic color), so it 
 * re-themes with the rest of the app automatically \u2014 no separate felt style setting.
 * 
 * Features:
 * - Radial gradient from surface container to background
 * - Subtle blur overlay for depth
 * - M3E compliant color usage
 */
@Composable
fun TableFeltBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceContainerLow
    val backgroundBg = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radialGradient = Brush.radialGradient(
                colors = listOf(
                    surfaceBg,
                    surfaceBg.copy(alpha = 0.8f),
                    backgroundBg
                ),
                center = Offset(w / 2f, h * 0.4f),
                radius = h * 0.9f
            )
            drawRect(brush = radialGradient)
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.05f)
                        ),
                        startY = 0f,
                        endY = size.height * 0.3f,
                        tileMode = TileMode.Clamp
                    )
                )
                .blur(
                    radiusX = 16f,
                    radiusY = 16f,
                    edgeTreatment = BlurEffect.EdgeTreatment.Unbounded
                )
        ) {
            content()
        }
    }
}
