package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive shape scale for Card Game Hub
 * 
 * Expressive extends the classic 5-step M3 shape scale with larger,
 * softer "extraLarge"/"extraExtraLarge" tiers intended for hero
 * surfaces, sheets, and FABs — used throughout the hub's bento cards
 * and the quick-setup bottom sheet for a friendlier, more tactile feel
 * than the tighter classic M3 radii.
 * 
 * Following Material Design 3 guidelines:
 * https://m3.material.io/styles/shape/overview
 */
val CardGameShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

/**
 * Extra expressive radii beyond the standard M3 [Shapes] container
 * Used for hero surfaces, dialogs, and feature cards
 * 
 * M3E Convention: Use larger corners for prominent surfaces
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
 * Shape tokens for specific use cases
 */
object AppShapes {
    // Cards
    val cardSmall = RoundedCornerShape(12.dp)
    val cardMedium = RoundedCornerShape(16.dp)
    val cardLarge = RoundedCornerShape(20.dp)
    val cardHero = RoundedCornerShape(28.dp)
    
    // Buttons
    val buttonSmall = RoundedCornerShape(8.dp)
    val buttonMedium = RoundedCornerShape(12.dp)
    val buttonLarge = RoundedCornerShape(16.dp)
    val buttonPill = RoundedCornerShape(50.dp)
    
    // Surfaces
    val surfaceSmall = RoundedCornerShape(8.dp)
    val surfaceMedium = RoundedCornerShape(12.dp)
    val surfaceLarge = RoundedCornerShape(16.dp)
    val surfaceHero = RoundedCornerShape(28.dp)
    
    // Dialogs and Sheets
    val dialog = RoundedCornerShape(28.dp)
    val sheet = RoundedCornerShape(32.dp)
    val bottomSheet = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    
    // Special
    val circle = RoundedCornerShape(50)
    val pill = RoundedCornerShape(50)
    
    // Settings
    val settingsCard = RoundedCornerShape(16.dp)
}
