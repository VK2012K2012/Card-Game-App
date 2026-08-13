package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

/**
 * Soft radial felt-table backdrop that always derives from the live
 * MaterialTheme color scheme (dynamic color), so it re-themes with the
 * rest of the app automatically — no separate felt style setting.
 */
@Composable
fun TableFeltBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceContainerLow
    val backgroundBg = MaterialTheme.colorScheme.background

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radialGradient = Brush.radialGradient(
                colors = listOf(surfaceBg, backgroundBg),
                center = Offset(w / 2f, h * 0.4f),
                radius = h * 0.9f
            )
            drawRect(brush = radialGradient)
        }
        content()
    }
}
