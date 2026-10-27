package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Blur utilities for Material 3 Expressive design
 * 
 * Provides glassmorphism effects, depth blurs, and modern blur treatments
 * following M3E guidelines: https://m3.material.io/styles/effects/overview
 */

/**
 * Blur intensity levels for different use cases
 */
enum class BlurIntensity(val radius: Float) {
    NONE(0f),
    SUBTLE(4f),
    LIGHT(8f),
    MEDIUM(12f),
    STRONG(16f),
    EXTRA_STRONG(24f),
    INTENSE(32f)
}

/**
 * Apply blur with proper edge treatment
 */
fun Modifier.blurEffect(
    intensity: BlurIntensity = BlurIntensity.MEDIUM
): Modifier = this.blur(
    radiusX = intensity.radius,
    radiusY = intensity.radius
)

/**
 * Glassmorphism surface with blur
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    blurRadius: Float = 16f,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val blurPx = with(density) { blurRadius.toDp().toPx() }
    
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.95f),
                        backgroundColor.copy(alpha = 0.85f)
                    )
                )
            )
            .blur(blurPx, blurPx)
    ) {
        content()
    }
}

/**
 * Frosted glass effect with gradient
 */
@Composable
fun FrostedGlass(
    modifier: Modifier = Modifier,
    blurRadius: Float = 24f,
    gradientColors: List<Color> = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    ),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.verticalGradient(
                colors = gradientColors,
                startY = 0f,
                endY = size.height,
                tileMode = TileMode.Clamp
            )
            drawRect(brush = gradient)
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius, blurRadius)
        ) {
            content()
        }
    }
}

/**
 * Depth blur for layered surfaces
 */
@Composable
fun DepthBlur(
    modifier: Modifier = Modifier,
    intensity: BlurIntensity = BlurIntensity.STRONG,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.9f))
            .blur(intensity.radius, intensity.radius)
    ) {
        content()
    }
}

/**
 * Radial blur effect
 */
@Composable
fun RadialBlurBackground(
    modifier: Modifier = Modifier,
    center: Offset = Offset(0.5f, 0.5f),
    radius: Float = 0.8f,
    blurRadius: Float = 16f,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.surfaceContainerLow,
        MaterialTheme.colorScheme.background
    ),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.radialGradient(
                colors = colors,
                center = center,
                radius = size.minDimension * radius
            )
            drawRect(brush = gradient)
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius, blurRadius)
        ) {
            content()
        }
    }
}

/**
 * Table felt with blur and depth
 */
@Composable
fun EnhancedTableFeltBackground(
    modifier: Modifier = Modifier,
    blurIntensity: BlurIntensity = BlurIntensity.STRONG,
    content: @Composable () -> Unit
) {
    val surfaceBg = MaterialTheme.colorScheme.surfaceContainerLow
    val backgroundBg = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(modifier = modifier.fillMaxSize()) {
        // Base gradient
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val radialGradient = Brush.radialGradient(
                colors = listOf(
                    surfaceBg,
                    surfaceBg.copy(alpha = 0.85f),
                    backgroundBg
                ),
                center = Offset(w / 2f, h * 0.4f),
                radius = h * 0.9f
            )
            drawRect(brush = radialGradient)
        }
        
        // Blur overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.08f),
                            primaryColor.copy(alpha = 0.04f)
                        ),
                        startY = 0f,
                        endY = size.height * 0.5f,
                        tileMode = TileMode.Clamp
                    )
                )
                .blur(blurIntensity.radius, blurIntensity.radius)
        ) {
            content()
        }
    }
}


