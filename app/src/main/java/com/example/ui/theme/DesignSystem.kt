package com.example.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Card Game Hub Design System
 * Material 3 Expressive (M3E) Compliant
 * 
 * This file contains the complete design system following official Material Design 3 guidelines
 * from https://m3.material.io/
 */

/**
 * M3E Motion System
 * All animations follow Material 3 Expressive motion principles
 */
object MotionTokens {
    // Easing curves
    val StandardEasing = FastOutSlowInEasing
    val EmphasizedEasing = FastOutSlowInEasing
    val LinearEasing = androidx.compose.animation.core.LinearEasing
    
    // Duration tokens (ms)
    val Instant = 0
    val Immediate = 50
    val Express = 100
    val Quick = 150
    val Responsive = 180
    val Natural = 220
    val Relaxed = 260
    val Leisurely = 300
    val Slow = 400
    val Extended = 500
    val Deliberate = 600
    val ExtendedDeliberate = 800
    
    // Spring configurations
    val StandardSpring = Spring<Float>(
        dampingRatio = 0.8f,
        stiffness = Spring.StiffnessMedium
    )
    
    val EmphasizedSpring = Spring<Float>(
        dampingRatio = 0.7f,
        stiffness = Spring.StiffnessLow
    )
    
    val ResponsiveSpring = Spring<Float>(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessMediumLow
    )
    
    // Animation specs
    fun standard(duration: Int = Responsive): AnimationSpec<Float> = tween(duration, easing = StandardEasing)
    fun spring(duration: Int = Responsive): AnimationSpec<Float> = tween(duration, easing = StandardEasing)
    fun emphasis(duration: Int = Natural): AnimationSpec<Float> = tween(duration, easing = EmphasizedEasing)
    fun enter(duration: Int = Natural): AnimationSpec<Float> = tween(duration, easing = EmphasizedEasing)
    fun exit(duration: Int = Quick): AnimationSpec<Float> = tween(duration, easing = StandardEasing)
}

/**
 * M3E Shape System
 * Expressive shape scale with proper corner radii
 */
object ShapeTokens {
    // M3 Shape scale
    val None = 0.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 24.dp
    val ExtraExtraLarge = 28.dp
    val ExtraExtraExtraLarge = 32.dp
    val Full = 50.dp
    
    // Custom expressive radii
    val CardCorner = 14.dp
    val SurfaceCorner = 16.dp
    val ContainerCorner = 20.dp
    val HeroCorner = 28.dp
    val DialogCorner = 28.dp
    val SheetCorner = 32.dp
    val ButtonCorner = 12.dp
    val PillCorner = 50.dp
}

/**
 * Updated CardGameShapes with M3E compliance
 */
val CardGameShapes = Shapes(
    extraSmall = RoundedCornerShape(ShapeTokens.ExtraSmall),
    small = RoundedCornerShape(ShapeTokens.Small),
    medium = RoundedCornerShape(ShapeTokens.Medium),
    large = RoundedCornerShape(ShapeTokens.Large),
    extraLarge = RoundedCornerShape(ShapeTokens.ExtraLarge),
)

/**
 * Expressive Corners for M3E hero surfaces
 */
object ExpressiveCorners {
    val None = RoundedCornerShape(0.dp)
    val ExtraSmall = RoundedCornerShape(4.dp)
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(24.dp)
    val ExtraExtraLarge = RoundedCornerShape(28.dp)
    val ExtraExtraExtraLarge = RoundedCornerShape(36.dp)
    val Full = RoundedCornerShape(50)
}

/**
 * Elevation System for M3E
 * Proper shadow and overlay usage
 */
object ElevationTokens {
    val Level0 = 0.dp
    val Level1 = 1.dp
    val Level2 = 3.dp
    val Level3 = 6.dp
    val Level4 = 8.dp
    val Level5 = 12.dp
    
    // Overlay colors for different elevation levels
    fun overlayColor(elevation: Int, color: Color): Color {
        return when (elevation) {
            0 -> color
            1 -> color.copy(alpha = 0.98f)
            2 -> color.copy(alpha = 0.96f)
            3 -> color.copy(alpha = 0.94f)
            4 -> color.copy(alpha = 0.92f)
            else -> color.copy(alpha = 0.90f)
        }
    }
}

/**
 * Spacing System for M3E
 * Consistent spacing tokens
 */
object SpacingTokens {
    val None = 0.dp
    val ExtraSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val MediumLarge = 14.dp
    val Large = 16.dp
    val ExtraLarge = 20.dp
    val ExtraExtraLarge = 24.dp
    val Huge = 28.dp
    val ExtraHuge = 32.dp
    val Massive = 36.dp
    val ExtraMassive = 40.dp
    val Gigantic = 48.dp
}

/**
 * Blur System for M3E
 * Modern blur effects for glassmorphism and depth
 */
object BlurTokens {
    val None = 0f
    val Subtle = 4f
    val Light = 8f
    val Medium = 12f
    val Strong = 16f
    val ExtraStrong = 24f
    val Intense = 32f
}

/**
 * Opacity System for M3E
 */
object OpacityTokens {
    val Full = 1.0f
    val High = 0.92f
    val MediumHigh = 0.84f
    val Medium = 0.72f
    val MediumLow = 0.60f
    val Low = 0.48f
    val ExtraLow = 0.36f
    val Minimal = 0.24f
    val Slight = 0.12f
    val None = 0.0f
}

/**
 * Color tokens for semantic usage
 */
object SemanticColors {
    // Success states
    val Success = Color(0xFF2E7D32)
    val SuccessContainer = Color(0xFF81C98A)
    val OnSuccess = Color(0xFFFFFFFF)
    val OnSuccessContainer = Color(0xFF062614)
    
    // Warning states
    val Warning = Color(0xFF9E6900)
    val WarningContainer = Color(0xFFFFB59D)
    val OnWarning = Color(0xFFFFFFFF)
    val OnWarningContainer = Color(0xFF331A00)
    
    // Info states
    val Info = Color(0xFF0277BD)
    val InfoContainer = Color(0xFFB3E5FC)
    val OnInfo = Color(0xFFFFFFFF)
    val OnInfoContainer = Color(0xFF001D35)
}

/**
 * Animation presets for common use cases
 */
object AnimationPresets {
    // Button press
    val ButtonPress = tween<Float>(170, easing = FastOutSlowInEasing)
    
    // Card elevation
    val CardElevate = tween<Float>(220, easing = FastOutSlowInEasing)
    
    // Modal enter
    val ModalEnter = tween<Float>(260, easing = FastOutSlowInEasing)
    
    // Modal exit
    val ModalExit = tween<Float>(180, easing = FastOutSlowInEasing)
    
    // Page transition
    val PageTransition = tween<Float>(300, easing = FastOutSlowInEasing)
    
    // Spring-based card selection
    val CardSelectSpring = Spring<Float>(
        dampingRatio = 0.82f,
        stiffness = Spring.StiffnessMediumLow
    )
    
    // Quick feedback
    val QuickFeedback = tween<Float>(120, easing = FastOutSlowInEasing)
}
