package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.durak.model.Player

/**
 * Bot Avatar Badge with Material 3 Expressive Design
 * 
 * Features:
 * - Animated role indicators (attacker, defender, current turn)
 * - Proper semantic colors for each state
 * - Expressive elevation and shadows
 * - Smooth transitions
 * - M3E compliant shape and spacing
 * 
 * Following Material Design 3 guidelines:
 * https://m3.material.io/components/chips/overview
 */
@Composable
fun BotAvatarBadge(
    player: Player,
    isCurrentTurn: Boolean,
    isDefender: Boolean,
    isAttacker: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isCurrentTurn -> MaterialTheme.colorScheme.tertiary
        isDefender -> MaterialTheme.colorScheme.error
        isAttacker -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    
    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(220, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "botRoleBorder"
    )
    
    val elevation = if (isCurrentTurn) 4.dp else 2.dp
    
    Surface(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = 0.8f,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.16f)
            )
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = animatedBorderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = if (isCurrentTurn) 4.dp else 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (player.isHuman) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (player.isHuman) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Human",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Bot",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Info
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    // Current turn indicator
                    if (isCurrentTurn) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onTertiary),
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${player.hand.size} cards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Role badges
                    when {
                        isDefender -> {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    "Defend",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        isAttacker -> {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    "Attack",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact Bot Avatar for smaller spaces
 */
@Composable
fun CompactBotAvatar(
    player: Player,
    isCurrentTurn: Boolean,
    isDefender: Boolean,
    isAttacker: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isCurrentTurn -> MaterialTheme.colorScheme.tertiary
        isDefender -> MaterialTheme.colorScheme.error
        isAttacker -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    
    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(220),
        label = "compactBotRoleBorder"
    )
    
    Surface(
        modifier = modifier
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = animatedBorderColor,
                shape = CircleShape
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isCurrentTurn) 3.dp else 1.dp
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (player.isHuman) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Human",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Role indicator overlay
            if (isCurrentTurn || isDefender || isAttacker) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCurrentTurn -> MaterialTheme.colorScheme.tertiary
                                isDefender -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrentTurn -> MaterialTheme.colorScheme.onTertiary
                                    isDefender -> MaterialTheme.colorScheme.onError
                                    else -> MaterialTheme.colorScheme.onPrimary
                                }
                            ),
                    )
                }
            }
        }
    }
}

/**
 * Player Status Indicator
 */
@Composable
fun PlayerStatusIndicator(
    player: Player,
    modifier: Modifier = Modifier,
    showCards: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (player.isHuman) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (player.isHuman) Icons.Default.Person else Icons.Default.SmartToy,
                contentDescription = if (player.isHuman) "Human" else "Bot",
                tint = if (player.isHuman) MaterialTheme.colorScheme.onPrimaryContainer 
                      else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Column {
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (showCards) {
                Text(
                    text = "${player.hand.size} cards",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
