package com.example.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.Card as GameCard
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import com.example.durak.model.TablePair
import com.example.ui.components.BotAvatarBadge
import com.example.ui.components.PlayingCardView
import com.example.ui.components.TableFeltBackground
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.theme.TrumpGold

@Composable
fun DurakGameScreen(
    gameState: DurakGameState,
    selectedCard: GameCard?,
    onSelectCard: (GameCard) -> Unit,
    onPlayAttack: (GameCard) -> Unit,
    onPlayDefend: (GameCard, Int) -> Unit,
    onFinishRound: () -> Unit,
    onTakeTable: () -> Unit,
    onExitGame: () -> Unit
) {
    val human = gameState.players.firstOrNull { it.isHuman }
    val bots = gameState.players.filterNot { it.isHuman }
    val isHumanTurn = gameState.currentTurnPlayerIndex == HUMAN_INDEX
    val isHumanAttacker = gameState.attackerIndex == HUMAN_INDEX
    val isHumanDefender = gameState.defenderIndex == HUMAN_INDEX

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            MatchHeader(gameState.roundCount, onExitGame)
            OpponentRail(gameState, bots)
            TurnBanner(
                message = buildTurnMessage(gameState, isHumanAttacker, isHumanDefender),
                isHumanTurn = isHumanTurn
            )
            TableZone(
                gameState = gameState,
                selectedCard = selectedCard,
                canDefend = isHumanTurn && isHumanDefender,
                onPlayDefend = onPlayDefend,
                modifier = Modifier.weight(1f)
            )
            MatchActions(
                gameState = gameState,
                selectedCard = selectedCard,
                isHumanTurn = isHumanTurn,
                isHumanAttacker = isHumanAttacker,
                isHumanDefender = isHumanDefender,
                onPlayAttack = onPlayAttack,
                onFinishRound = onFinishRound,
                onTakeTable = onTakeTable
            )
            HandTray(
                hand = human?.hand.orEmpty(),
                selectedCard = selectedCard,
                isHumanTurn = isHumanTurn,
                isHumanAttacker = isHumanAttacker,
                isHumanDefender = isHumanDefender,
                onSelectCard = onSelectCard
            )
        }

        if (gameState.isGameOver) {
            MatchResultDialog(gameState, onExitGame)
        }
    }
}

@Composable
private fun MatchHeader(round: Int, onExitGame: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = ExpressiveCorners.Full,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)
        ) {
            IconButton(onClick = onExitGame) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave match")
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("DURAK", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("The table is live", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
        Surface(
            shape = ExpressiveCorners.Full,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        ) {
            Text(
                text = "ROUND $round",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OpponentRail(gameState: DurakGameState, bots: List<com.example.durak.model.Player>) {
    if (bots.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(bots, key = { _, player -> player.id }) { _, bot ->
            val playerIndex = gameState.players.indexOfFirst { it.id == bot.id }
            BotAvatarBadge(
                player = bot,
                isCurrentTurn = gameState.currentTurnPlayerIndex == playerIndex,
                isDefender = gameState.defenderIndex == playerIndex,
                isAttacker = gameState.attackerIndex == playerIndex
            )
        }
    }
}

@Composable
private fun TurnBanner(message: String, isHumanTurn: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = if (isHumanTurn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface.copy(alpha = 0.84f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(10.dp),
                shape = ExpressiveCorners.Full,
                color = if (isHumanTurn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
            ) {}
            Text(
                message,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isHumanTurn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun TableZone(
    gameState: DurakGameState,
    selectedCard: GameCard?,
    canDefend: Boolean,
    onPlayDefend: (GameCard, Int) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeckStatus(gameState)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            shape = ExpressiveCorners.ExtraExtraLarge,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
        ) {
            if (gameState.tablePairs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "The table is ready\nfor the opening card",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(gameState.tablePairs, key = { _, pair -> pair.attackCard.id }) { index, pair ->
                        TablePairSlot(pair, index, selectedCard, gameState.trumpSuit, canDefend, onPlayDefend)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeckStatus(gameState: DurakGameState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(Modifier.size(width = 58.dp, height = 86.dp), contentAlignment = Alignment.Center) {
            gameState.trumpCard?.let { trump ->
                PlayingCardView(trump, width = 46.dp, height = 68.dp, isSelectable = false, modifier = Modifier.offset(x = 8.dp, y = 9.dp))
            }
            if (gameState.deck.isNotEmpty()) {
                PlayingCardView(GameCard("deck_back", gameState.trumpSuit, Rank.ACE, isFaceUp = false), width = 46.dp, height = 68.dp, isSelectable = false)
            }
        }
        Surface(shape = ExpressiveCorners.Full, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)) {
            Text("${gameState.deck.size}", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Text("TRUMP ${gameState.trumpSuit.symbol}", style = MaterialTheme.typography.labelSmall, color = TrumpGold, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun MatchActions(
    gameState: DurakGameState,
    selectedCard: GameCard?,
    isHumanTurn: Boolean,
    isHumanAttacker: Boolean,
    isHumanDefender: Boolean,
    onPlayAttack: (GameCard) -> Unit,
    onFinishRound: () -> Unit,
    onTakeTable: () -> Unit
) {
    val canAttack = isHumanTurn && isHumanAttacker && selectedCard != null && gameState.gamePhase in setOf(GamePhase.ATTACKING, GamePhase.WAITING_FOR_THROW_IN)
    val canFinish = isHumanTurn && isHumanAttacker && gameState.gamePhase == GamePhase.WAITING_FOR_THROW_IN && gameState.tablePairs.isNotEmpty() && gameState.tablePairs.all { it.isDefended }
    val canTake = isHumanTurn && isHumanDefender && gameState.gamePhase == GamePhase.DEFENDING && gameState.tablePairs.isNotEmpty()
    if (!canAttack && !canFinish && !canTake) return

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (canTake) {
            Button(
                onClick = onTakeTable,
                modifier = Modifier.weight(1f),
                shape = ExpressiveCorners.Full,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
            ) {
                Icon(Icons.Default.KeyboardDoubleArrowDown, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Take cards", fontWeight = FontWeight.Bold)
            }
        }
        if (canFinish) {
            Button(onClick = onFinishRound, modifier = Modifier.weight(1f), shape = ExpressiveCorners.Full) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Finish round", fontWeight = FontWeight.Bold)
            }
        }
        if (canAttack) {
            Button(
                onClick = { selectedCard?.let(onPlayAttack) },
                modifier = Modifier.weight(1f),
                shape = ExpressiveCorners.Full,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary, contentColor = MaterialTheme.colorScheme.onTertiary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(if (gameState.tablePairs.isEmpty()) "Open attack" else "Throw in", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HandTray(
    hand: List<GameCard>,
    selectedCard: GameCard?,
    isHumanTurn: Boolean,
    isHumanAttacker: Boolean,
    isHumanDefender: Boolean,
    onSelectCard: (GameCard) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 14.dp, end = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("YOUR HAND · ${hand.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    val hint = when {
                        !isHumanTurn -> "Waiting for the other player"
                        isHumanAttacker -> "Choose a card to attack"
                        isHumanDefender -> "Choose a card, then tap an attack to defend"
                        else -> "Choose a card"
                    }
                    Text(hint, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isHumanTurn) {
                    Surface(shape = ExpressiveCorners.Full, color = MaterialTheme.colorScheme.primaryContainer) {
                        Text("YOUR TURN", modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(if (hand.size > 8) (-8).dp else 4.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 4.dp)
            ) {
                itemsIndexed(hand, key = { _, card -> card.id }) { _, card ->
                    val isSelected = selectedCard?.id == card.id
                    val lift by animateDpAsState(if (isSelected) (-14).dp else 0.dp, label = "cardLift")
                    PlayingCardView(
                        card = card,
                        width = 68.dp,
                        height = 100.dp,
                        isSelected = isSelected,
                        isSelectable = isHumanTurn,
                        onClick = { onSelectCard(card) },
                        modifier = Modifier.offset(y = lift)
                    )
                }
            }
        }
    }
}

@Composable
private fun TablePairSlot(
    pair: TablePair,
    index: Int,
    selectedCard: GameCard?,
    trumpSuit: Suit,
    canDefend: Boolean,
    onPlayDefend: (GameCard, Int) -> Unit
) {
    val selectedBeatsAttack = selectedCard?.let { it.beats(pair.attackCard, trumpSuit) } == true
    val canPlaceDefense = canDefend && !pair.isDefended && selectedCard != null && selectedBeatsAttack
    val borderColor = when {
        canPlaceDefense -> MaterialTheme.colorScheme.primary
        canDefend && !pair.isDefended && selectedCard != null -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }
    Box(
        modifier = Modifier
            .width(88.dp)
            .height(128.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(2.dp, borderColor, MaterialTheme.shapes.medium)
            .then(if (canPlaceDefense) Modifier.clickable { selectedCard?.let { onPlayDefend(it, index) } } else Modifier)
    ) {
        PlayingCardView(pair.attackCard, width = 68.dp, height = 100.dp, isSelectable = false)
        if (pair.defenseCard != null) {
            PlayingCardView(pair.defenseCard, width = 68.dp, height = 100.dp, isSelectable = false, modifier = Modifier.offset(x = 16.dp, y = 24.dp))
        } else {
            Surface(
                modifier = Modifier.offset(x = 16.dp, y = 24.dp).size(width = 68.dp, height = 100.dp),
                shape = MaterialTheme.shapes.small,
                color = if (canPlaceDefense) MaterialTheme.colorScheme.primaryContainer else Color.Black.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (canPlaceDefense) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                    Text(
                        if (selectedCard == null) "DEFEND" else if (canPlaceDefense) "PLAY HERE" else "CAN'T BEAT",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (canPlaceDefense) MaterialTheme.colorScheme.onPrimaryContainer else Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchResultDialog(gameState: DurakGameState, onExit: () -> Unit) {
    val title = when {
        gameState.isDraw -> "A shared finish"
        gameState.durakPlayerName == "You" -> "You are the Durak"
        else -> "You made it out"
    }
    val message = when {
        gameState.isDraw -> "Everyone finished their cards at the same time."
        gameState.durakPlayerName == "You" -> "Your opponents emptied their hands first. Regroup and try another table."
        else -> "${gameState.durakPlayerName} is the Durak. Your hand is clear."
    }
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(message)
                Text("${gameState.roundCount} rounds played", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            Button(onClick = onExit, shape = ExpressiveCorners.Full) { Text("Back to game hub", fontWeight = FontWeight.Bold) }
        }
    )
}

private fun buildTurnMessage(state: DurakGameState, humanIsAttacker: Boolean, humanIsDefender: Boolean): String = when {
    state.isGameOver -> state.lastActionMessage
    state.currentTurnPlayerIndex == HUMAN_INDEX && humanIsAttacker && state.gamePhase == GamePhase.WAITING_FOR_THROW_IN -> "Your attack is defended. Throw in or finish the round."
    state.currentTurnPlayerIndex == HUMAN_INDEX && humanIsAttacker -> "Your attack. Lead with a card."
    state.currentTurnPlayerIndex == HUMAN_INDEX && humanIsDefender -> "Your defense. Beat each attack or take the table."
    else -> "${state.players.getOrNull(state.currentTurnPlayerIndex)?.name ?: "Opponent"} is thinking."
}

private const val HUMAN_INDEX = 0
