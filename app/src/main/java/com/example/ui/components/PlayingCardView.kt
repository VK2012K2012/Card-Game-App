package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import com.example.ui.theme.TrumpGold

/**
 * A single playing card, front or back.
 *
 * Uses a stable identity ([card.id] is expected to be a stable key wherever
 * this is placed inside a `LazyRow`/`LazyColumn`) and animates selection with
 * a spring so re-selecting doesn't cause a visual "pop"/flicker.
 */
@Composable
fun PlayingCardView(
    card: Card,
    modifier: Modifier = Modifier,
    width: Dp = 68.dp,
    height: Dp = 100.dp,
    isSelected: Boolean = false,
    isSelectable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 14f else 3f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "cardElevation"
    )

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
            containerColor = if (card.isFaceUp) Color(0xFFFFFFFF) else Color(0xFF14202B)
        )
    ) {
        if (!card.isFaceUp) {
            CardBackGraphic(modifier = Modifier.fillMaxSize())
        } else {
            CardFaceGraphic(card = card, width = width, height = height)
        }
    }
}

@Composable
fun CardFaceGraphic(card: Card, width: Dp, height: Dp) {
    val suitColor = if (card.suit.isRed) Color(0xFFD3273E) else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        // Top Left Rank & Suit
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

        // Trump Badge
        if (card.isTrump) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(TrumpGold, shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Trump",
                    tint = Color.Black,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        // Center emblem
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

/** Single, polished card-back motif — deep navy with a fine gold lattice and center emblem. */
@Composable
fun CardBackGraphic(modifier: Modifier = Modifier) {
    val primaryColor = Color(0xFF14202B)
    val accentColor = TrumpGold

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(color = primaryColor)

        drawRoundRect(
            color = accentColor.copy(alpha = 0.65f),
            topLeft = Offset(w * 0.08f, h * 0.08f),
            size = Size(w * 0.84f, h * 0.84f),
            cornerRadius = CornerRadius(6f, 6f),
            style = Stroke(width = 1.6f)
        )

        val step = 14f
        var x = 0f
        while (x < w) {
            drawLine(
                color = accentColor.copy(alpha = 0.16f),
                start = Offset(x, 0f),
                end = Offset(x + h, h),
                strokeWidth = 1f
            )
            drawLine(
                color = accentColor.copy(alpha = 0.16f),
                start = Offset(x, h),
                end = Offset(x + h, 0f),
                strokeWidth = 1f
            )
            x += step
        }

        drawCircle(
            color = accentColor.copy(alpha = 0.85f),
            radius = w * 0.16f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 2f)
        )
    }
}
