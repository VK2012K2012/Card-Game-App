package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.durak.model.FeltStyle
import com.example.ui.theme.LocalCardAppTheme

@Composable
fun TableFeltBackground(
    modifier: Modifier = Modifier,
    feltStyle: FeltStyle = LocalCardAppTheme.current.feltStyle,
    content: @Composable () -> Unit
) {
    val primaryBg = MaterialTheme.colorScheme.background
    val surfaceBg = MaterialTheme.colorScheme.surface

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (feltStyle) {
                FeltStyle.CLASSIC_FELT -> {
                    val radialGradient = Brush.radialGradient(
                        colors = listOf(surfaceBg, primaryBg),
                        center = Offset(w / 2f, h / 2f),
                        radius = h * 0.75f
                    )
                    drawRect(brush = radialGradient)
                }
                FeltStyle.ROYAL_VELVET -> {
                    val linearGradient = Brush.verticalGradient(
                        colors = listOf(surfaceBg, primaryBg, Color(0xFF1E040A))
                    )
                    drawRect(brush = linearGradient)
                }
                FeltStyle.DARK_WOOD -> {
                    val linearGradient = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2E1A08), Color(0xFF1A0E04), Color(0xFF2E1A08))
                    )
                    drawRect(brush = linearGradient)
                    // Draw subtle wood grain lines
                    for (i in 0..12) {
                        val y = h * (i / 12f)
                        drawLine(
                            color = Color(0x1A000000),
                            start = Offset(0f, y),
                            end = Offset(w, y + 20f),
                            strokeWidth = 3f
                        )
                    }
                }
                FeltStyle.NEON_GRID -> {
                    drawRect(color = Color(0xFF0F051D))
                    val gridStep = 40f
                    var x = 0f
                    while (x < w) {
                        drawLine(
                            color = Color(0x1FA855F7),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f
                        )
                        x += gridStep
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(
                            color = Color(0x1FA855F7),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += gridStep
                    }
                }
            }
        }
        content()
    }
}
