package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive shape scale.
 *
 * Expressive extends the classic 5-step M3 shape scale with larger,
 * softer "extraLarge"/"extraExtraLarge" tiers intended for hero
 * surfaces, sheets, and FABs — used throughout the hub's bento cards
 * and the quick-setup bottom sheet for a friendlier, more tactile feel
 * than the tighter classic M3 radii.
 */
val CardGameShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Restrained shape scale for the One UI-inspired option, built with standard Compose shapes. */
val OneUiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

/** Extra expressive radii beyond the standard M3 [Shapes] container (M3E convention). */
object ExpressiveCorners {
    val ExtraExtraLarge: RoundedCornerShape
        @Composable get() = if (LocalAppVisualStyle.current == AppVisualStyle.MATERIAL_3_EXPRESSIVE) {
            RoundedCornerShape(36.dp)
        } else {
            RoundedCornerShape(24.dp)
        }
    val Full = RoundedCornerShape(percent = 50)
}
