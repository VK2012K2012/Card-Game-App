package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import com.example.ui.theme.ExpressiveCorners
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.durak.ai.AiAdviceResult
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.Card
import com.example.durak.model.TablePair
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurakGameScreen(
    gameState: DurakGameState,
    selectedCard: Card?,
    aiAdvice: AiAdviceResult?,
    isAiLoading: Boolean,
    onSelectCard: (Card) -> Unit,
    onPlayAttack: (Card) -> Unit,
    onPlayDefend: (Card, Int) -> Unit,
    onPassOrClear: () -> Unit,
    onTakeTable: () -> Unit,
    onRequestAiAdvice: () -> Unit,
    onApplyAiMove: () -> Unit,
    onOpenCustomizer: () -> Unit,
    onExitGame: () -> Unit
) {
    var showAiSheet by remember { mutableStateOf(false) }

    val humanPlayer = gameState.players.firstOrNull { it.isHuman }
    val botPlayers = gameState.players.filter { !it.isHuman }

    val isHumanAttacker = (gameState.attackerIndex == 0)
    val isHumanDefender = (gameState.defenderIndex == 0)
    val isHumanTurn = (gameState.currentTurnPlayerIndex == 0)

    TableFeltBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Durak Table", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                                Text("R${gameState.roundCount}")
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onExitGame) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onRequestAiAdvice()
                            showAiSheet = true
                        }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Coach", tint = MaterialTheme.colorScheme.tertiary)
                        }
                        IconButton(onClick = onOpenCustomizer) {
                            Icon(Icons.Default.Palette, contentDescription = "Themes")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bot Avatars Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    botPlayers.forEach { bot ->
                        val botIdx = gameState.players.indexOf(bot)
                        BotAvatarBadge(
                            player = bot,
                            isCurrentTurn = (gameState.currentTurnPlayerIndex == botIdx),
                            isDefender = (gameState.defenderIndex == botIdx),
                            isAttacker = (gameState.attackerIndex == botIdx)
                        )
                    }
                }

                // Middle Table Area: Trump Card + Deck Pile + Table Field Pairs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Deck & Trump Widget
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Trump Card flipped face up under deck
                            gameState.trumpCard?.let { trump ->
                                PlayingCardView(
                                    card = trump,
                                    width = 46.dp,
                                    height = 68.dp,
                                    isSelectable = false,
                                    modifier = Modifier.offset(x = 12.dp, y = 12.dp)
                                )
                            }
                            // Deck Face Down Card
                            if (gameState.deck.isNotEmpty()) {
                                PlayingCardView(
                                    card = Card("deck_back", gameState.trumpSuit, com.example.durak.model.Rank.ACE, isFaceUp = false),
                                    width = 46.dp,
                                    height = 68.dp,
                                    isSelectable = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Badge(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                            Text("${gameState.deck.size} Left")
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Trump: ${gameState.trumpSuit.symbol}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (gameState.trumpSuit.isRed) Color(0xFFEF4444) else Color.White
                        )
                    }

                    // Right Side: Attack & Defense Card Table Pairs Grid
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (gameState.tablePairs.isEmpty()) {
                            Text(
                                text = if (isHumanAttacker) "Your turn to attack! Select a card below." else "Waiting for attack...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(gameState.tablePairs.indices.toList()) { pairIdx ->
                                    val pair = gameState.tablePairs[pairIdx]
                                    TablePairSlot(
                                        pair = pair,
                                        pairIndex = pairIdx,
                                        isHumanDefender = isHumanDefender,
                                        selectedCard = selectedCard,
                                        onDefendWithSelected = {
                                            selectedCard?.let { card ->
                                                onPlayDefend(card, pairIdx)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Action Message Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = gameState.lastActionMessage,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Action Control Buttons Row (Pass/Clear, Take, Attack/Play, AI Assist)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isHumanDefender) {
                        Button(
                            onClick = onTakeTable,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.CallReceived, contentDescription = "Take")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Take Cards")
                        }
                    }

                    if (isHumanAttacker || gameState.gamePhase == GamePhase.WAITING_FOR_THROW_IN) {
                        Button(
                            onClick = onPassOrClear,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Done")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (gameState.tablePairs.isEmpty()) "Pass" else "Clear (Bito)")
                        }
                    }

                    if (selectedCard != null && isHumanTurn && isHumanAttacker) {
                        Button(
                            onClick = { onPlayAttack(selectedCard) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Icon(Icons.Default.CallMade, contentDescription = "Play")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attack")
                        }
                    }
                }

                // Bottom Player Hand Area
                humanPlayer?.let { human ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Your Hand (${human.hand.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tap a card to select",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            if (isHumanTurn) {
                                Surface(
                                    shape = ExpressiveCorners.Full,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "YOUR TURN",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // Selected Card Info Callout Pill
                        selectedCard?.let { card ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Selected: ",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${card.rank.label} of ${card.suit.suitName} ${card.suit.symbol}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (card.suit.isRed) Color(0xFFE11D48) else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        if (card.isTrump) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFEAB308)
                                            ) {
                                                Text(
                                                    text = "TRUMP",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = Color.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (isHumanTurn && isHumanAttacker) {
                                        Button(
                                            onClick = { onPlayAttack(card) },
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = ExpressiveCorners.Full
                                        ) {
                                            Text("Play Card", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        val spacing = if (human.hand.size > 8) (-6).dp else 4.dp
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(spacing),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(human.hand) { card ->
                                val isSelected = (selectedCard?.id == card.id)
                                PlayingCardView(
                                    card = card,
                                    width = 72.dp,
                                    height = 106.dp,
                                    isSelected = isSelected,
                                    onClick = {
                                        onSelectCard(card)
                                    },
                                    modifier = Modifier.offset(y = if (isSelected) (-16).dp else 0.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // AI Advisor Sheet
        if (showAiSheet) {
            AiAdvisorBottomSheet(
                advice = aiAdvice,
                isLoading = isAiLoading,
                onDismiss = { showAiSheet = false },
                onApplyMove = onApplyAiMove
            )
        }

        // Game Over Dialog
        if (gameState.isGameOver) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(text = if (gameState.durakPlayerName == "You") "You are the Durak! 🃏" else "Victory! 🎉") },
                text = {
                    Column {
                        Text(
                            text = if (gameState.durakPlayerName == "You")
                                "You ran out of trumps and became the Fool."
                            else
                                "${gameState.durakPlayerName} is the Durak! Great game!"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rounds played: ${gameState.roundCount}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = onExitGame) {
                        Text("Return to Hub")
                    }
                }
            )
        }
    }
}

@Composable
fun TablePairSlot(
    pair: TablePair,
    pairIndex: Int,
    isHumanDefender: Boolean,
    selectedCard: Card?,
    onDefendWithSelected: () -> Unit
) {
    val canDefendThisSlot = isHumanDefender && !pair.isDefended
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(125.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (canDefendThisSlot && selectedCard != null) {
                    Modifier
                        .border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                        .clickable { onDefendWithSelected() }
                } else Modifier
            )
    ) {
        // Attack Card
        PlayingCardView(
            card = pair.attackCard,
            width = 68.dp,
            height = 100.dp,
            isSelectable = false
        )

        // Defense Card offset on top
        val defCard = pair.defenseCard
        if (defCard != null) {
            PlayingCardView(
                card = defCard,
                width = 68.dp,
                height = 100.dp,
                isSelectable = false,
                modifier = Modifier.offset(x = 12.dp, y = 20.dp)
            )
        } else if (canDefendThisSlot) {
            // Placeholder hint slot if not defended yet
            Surface(
                onClick = onDefendWithSelected,
                shape = RoundedCornerShape(8.dp),
                color = if (selectedCard != null) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.35f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selectedCard != null) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)),
                modifier = Modifier
                    .offset(x = 12.dp, y = 20.dp)
                    .size(width = 68.dp, height = 100.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = if (selectedCard != null) "BEAT HERE ⚡" else "BEAT ME",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = if (selectedCard != null) MaterialTheme.colorScheme.onPrimaryContainer else Color.White
                    )
                }
            }
        }
    }
}
