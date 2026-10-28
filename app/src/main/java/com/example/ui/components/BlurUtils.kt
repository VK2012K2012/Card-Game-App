package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp

/** Blur intensity levels used by the app's surfaces and screens. */
enum class BlurIntensity(val radius: Float) {
    NONE(0f),
    SUBTLE(4f),
    LIGHT(8f),
    MEDIUM(12f),
    STRONG(16f),
    EXTRA_STRONG(24f),
    INTENSE(32f),
}

/** Applies a Compose blur using the intensity in density-independent pixels. */
fun Modifier.blurEffect(
    intensity: BlurIntensity = BlurIntensity.MEDIUM,
): Modifier = blur(
    radiusX = intensity.radius.dp,
    radiusY = intensity.radius.dp,
)

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    blurRadius: Float = 16f,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.95f),
                        backgroundColor.copy(alpha = 0.85f),
                    ),
                ),
            )
            .blur(blurRadius.dp),
    ) {
        content()
    }
}

@Composable
fun FrostedGlass(
    modifier: Modifier = Modifier,
    blurRadius: Float = 24f,
    gradientColors: List<Color> = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    ),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = gradientColors,
                    startY = 0f,
                    endY = size.height,
                    tileMode = TileMode.Clamp,
                ),
            )
        }
        // Keep foreground text crisp; this layer provides the translucent glass tint.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors)),
        ) {
            content()
        }
    }
}

@Composable
fun DepthBlur(
    modifier: Modifier = Modifier,
    intensity: BlurIntensity = BlurIntensity.STRONG,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.9f))
            .blur(intensity.radius.dp),
    ) {
        content()
    }
}

@Composable
fun RadialBlurBackground(
    modifier: Modifier = Modifier,
    center: Offset = Offset(0.5f, 0.5f),
    radius: Float = 0.8f,
    blurRadius: Float = 16f,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.background,
    ),
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = center,
                    radius = size.minDimension * radius,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors)),
        ) {
            content()
        }
    }
}

/** Layered table background; the gameplay content remains sharp above its gradients. */
@Suppress("UNUSED_PARAMETER")
@Composable
fun EnhancedTableFeltBackground(
    modifier: Modifier = Modifier,
    blurIntensity: BlurIntensity = BlurIntensity.STRONG,
    content: @Composable () -> Unit,
) {
    val surfaceBg = MaterialTheme.colorScheme.surface
    val backgroundBg = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        surfaceBg,
                        surfaceBg.copy(alpha = 0.85f),
                        backgroundBg,
                    ),
                    center = Offset(w / 2f, h * 0.4f),
                    radius = h * 0.9f,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.08f),
                            primaryColor.copy(alpha = 0.04f),
                        ),
                    ),
                ),
        ) {
            content()
        }
    }
}

/** Adds the translucent gradient used by glass-style cards. */
@Suppress("UNUSED_PARAMETER")
fun Modifier.frostedGlass(
    blurRadius: Float = 16f,
    gradientColors: List<Color> = listOf(
        Color.Transparent,
        Color.White.copy(alpha = 0.1f),
    ),
): Modifier = background(Brush.verticalGradient(gradientColors))
