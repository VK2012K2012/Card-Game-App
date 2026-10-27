package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * Enhanced Table Felt Background with Material 3 Expressive Design
 * 
 * Features:
 * - Radial gradient from surface container to background
 * - Subtle blur overlay for depth (glassmorphism effect)
 * - Primary color tint overlay
 * - M3E compliant color usage
 * - Optimized for performance
 * 
 * Following Material Design 3 guidelines:
 * https://m3.material.io/styles/effects/overview
 */
@Composable
fun TableFeltBackground(
    modifier: Modifier = Modifier,
    blurRadius: Float = 16f,
    content: @Composable () -> Unit
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceContainerLow
    val backgroundBg = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        // Base radial gradient - creates the felt texture
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radialGradient = Brush.radialGradient(
                colors = listOf(
                    surfaceBg,
                    surfaceBg.copy(alpha = 0.85f),
                    surfaceBg.copy(alpha = 0.70f),
                    backgroundBg
                ),
                center = Offset(w / 2f, h * 0.4f),
                radius = h * 0.9f
            )
            drawRect(brush = radialGradient)
        }
        
        // Blur overlay with primary tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.08f),
                            primaryColor.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.6f,
                        tileMode = TileMode.Clamp
                    )
                )
                .blur(blurRadius, blurRadius)
        ) {
            content()
        }
    }
}

/**
 * Alternative table felt with stronger blur for modal dialogs
 */
@Composable
fun ModalTableFeltBackground(
    modifier: Modifier = Modifier,
    blurRadius: Float = 24f,
    content: @Composable () -> Unit
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceContainer
    val backgroundBg = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radialGradient = Brush.radialGradient(
                colors = listOf(
                    surfaceBg.copy(alpha = 0.95f),
                    surfaceBg.copy(alpha = 0.80f),
                    backgroundBg
                ),
                center = Offset(w / 2f, h / 2f),
                radius = h * 0.8f
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
                            primaryColor.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.5f
                    )
                )
                .blur(blurRadius, blurRadius)
        ) {
            content()
        }
    }
}
