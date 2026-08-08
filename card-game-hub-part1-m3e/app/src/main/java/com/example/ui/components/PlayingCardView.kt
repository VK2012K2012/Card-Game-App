package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durak.model.Card
import com.example.durak.model.CardBackStyle
import com.example.ui.theme.LocalCardAppTheme
import com.example.ui.theme.TrumpGold

@Composable
fun PlayingCardView(
    card: Card,
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 100.dp,
    isSelected: Boolean = false,
    isSelectable: Boolean = true,
    cardBackStyle: CardBackStyle = LocalCardAppTheme.current.cardBackStyle,
    onClick: (() -> Unit)? = null
) {
    val elevation by animateFloatAsState(targetValue = if (isSelected) 14f else 3f, label = "cardElevation")

    val shape = RoundedCornerShape(10.dp)
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        card.isTrump && card.isFaceUp -> TrumpGold
        else -> Color(0x33000000)
    }
    val borderWidth = when {
        isSelected -> 3.dp
        card.isTrump && card.isFaceUp -> 2.dp
        else -> 1.dp
    }

    Card(
        modifier = modifier
            .size(width = width, height = height)
            .shadow(elevation.dp, shape = shape)
            .clip(shape)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .then(
                if (onClick != null && isSelectable) Modifier.clickable { onClick() } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (card.isFaceUp) Color(0xFFFFFFFF) else Color(0xFF1E293B)
        )
    ) {
        if (!card.isFaceUp) {
            CardBackGraphic(cardBackStyle = cardBackStyle, modifier = Modifier.fillMaxSize())
        } else {
            CardFaceGraphic(card = card, width = width, height = height)
        }
    }
}

@Composable
fun CardFaceGraphic(card: Card, width: Dp, height: Dp) {
    val suitColor = if (card.suit.isRed) Color(0xFFE11D48) else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        // Top Left Rank & Suit (Large & Bold)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Text(
                text = card.rank.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = suitColor,
                lineHeight = 15.sp
            )
            Text(
                text = card.suit.symbol,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = suitColor,
                lineHeight = 13.sp
            )
        }

        // Trump Badge in top right if trump card
        if (card.isTrump) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(TrumpGold, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Trump",
                        tint = Color.Black,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        // Center Emblem / Large Rank + Suit combo for instant recognition
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.rank.label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = suitColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = card.suit.symbol,
                fontSize = 22.sp,
                color = suitColor,
                textAlign = TextAlign.Center
            )
        }

        // Bottom Right Rotated Rank & Suit
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Text(
                text = card.suit.symbol,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = suitColor,
                lineHeight = 13.sp
            )
            Text(
                text = card.rank.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = suitColor,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CardBackGraphic(cardBackStyle: CardBackStyle, modifier: Modifier = Modifier) {
    val primaryColor = when (cardBackStyle) {
        CardBackStyle.RED_SCROLL -> Color(0xFF991B1B)
        CardBackStyle.GOLD_LATTICE -> Color(0xFFB45309)
        CardBackStyle.EMERALD_FEATHER -> Color(0xFF047857)
        CardBackStyle.CYBER_HEX -> Color(0xFF6B21A8)
        CardBackStyle.NOIR -> Color(0xFF111827)
    }

    val accentColor = when (cardBackStyle) {
        CardBackStyle.RED_SCROLL -> Color(0xFFFEE2E2)
        CardBackStyle.GOLD_LATTICE -> Color(0xFFFEF3C7)
        CardBackStyle.EMERALD_FEATHER -> Color(0xFFD1FAE5)
        CardBackStyle.CYBER_HEX -> Color(0xFF06B6D4)
        CardBackStyle.NOIR -> Color(0xFF94A3B8)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(color = primaryColor)

        // Inner border frame
        drawRoundRect(
            color = accentColor.copy(alpha = 0.8f),
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.84f, h * 0.84f),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 2f)
        )

        // Lattice pattern / emblem
        val step = 14f
        var x = 0f
        while (x < w) {
            drawLine(
                color = accentColor.copy(alpha = 0.25f),
                start = Offset(x, 0f),
                end = Offset(x + h, h),
                strokeWidth = 1.5f
            )
            drawLine(
                color = accentColor.copy(alpha = 0.25f),
                start = Offset(x, h),
                end = Offset(x + h, 0f),
                strokeWidth = 1.5f
            )
            x += step
        }

        // Center emblem circle
        drawCircle(
            color = accentColor.copy(alpha = 0.9f),
            radius = w * 0.18f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 2.5f)
        )
    }
}
