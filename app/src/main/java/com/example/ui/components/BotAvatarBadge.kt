package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.durak.model.Player

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
        isDefender -> Color(0xFFEF4444)
        isAttacker -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        modifier = modifier
            .border(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
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
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Bot",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isCurrentTurn) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${player.hand.size} cards",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isDefender) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(containerColor = Color(0xFFEF4444), contentColor = Color.White) {
                            Text("Defend", style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (isAttacker) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(containerColor = Color(0xFF3B82F6), contentColor = Color.White) {
                            Text("Attack", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
