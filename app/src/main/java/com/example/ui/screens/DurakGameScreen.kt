package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.Card as GameCard
import com.example.durak.model.Rank
import com.example.durak.model.TablePair
import com.example.ui.components.BotAvatarBadge
import com.example.ui.components.PlayingCardView
import com.example.ui.components.TableFeltBackground
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.theme.TrumpGold

private const val HUMAN_INDEX = 0

@Composable
fun DurakGameScreen(
    gameState: DurakGameState,
    selectedCard: GameCard?,
    onSelectCard: (GameCard) -> Unit,
    onPlayAttack: (GameCard) -> Unit,
    onPlayDefend: (GameCard, Int) -> Unit,
    onFinishRound: () -> Unit,
    onPassThrowIn: () -> Unit,
    onTakeTable: () -> Unit,
    onExitGame: () -> Unit
) {
    val human = gameState.players.firstOrNull { it.isHuman }
    val isHumanTurn = gameState.currentTurnPlayerIndex == HUMAN_INDEX
    val isHumanDefender = gameState.defenderIndex == HUMAN_INDEX
    val isHumanAttacker = gameState.attackerIndex == HUMAN_INDEX

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            MatchHeader(gameState.roundCount, onExitGame)
            OpponentRail(gameState)
            TurnBanner(buildTurnMessage(gameState, isHumanTurn, isHumanDefender))
            GameTable(
                gameState = gameState,
                modifier = Modifier.weight(1f)
            )
            ExplicitMatchActions(
                gameState = gameState,
                selectedCard = selectedCard,
                isHumanTurn = isHumanTurn,
                isHumanAttacker = isHumanAttacker,
                isHumanDefender = isHumanDefender,
                onPlayAttack = onPlayAttack,
                onPlayDefend = onPlayDefend,
                onFinishRound = onFinishRound,
                onPassThrowIn = onPassThrowIn,
                onTakeTable = onTakeTable
            )
            HandTray(
                hand = human?.hand.orEmpty(),
                selectedCard = selectedCard,
                isHumanTurn = isHumanTurn,
                onSelectCard = onSelectCard
            )
        }
        if (gameState.isGameOver) MatchResultDialog(gameState, onExitGame)
    }
}

@Composable
private fun MatchHeader(round: Int, onExitGame: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(shape = ExpressiveCorners.Full, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)) {
            IconButton(onClick = onExitGame) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave match")
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("DURAK", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Round $round", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
        }
        Surface(shape = ExpressiveCorners.Full, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)) {
            Text("LOCAL", Modifier.padding(horizontal = 11.dp, vertical = 7.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OpponentRail(gameState: DurakGameState) {
    val bots = gameState.players.filterNot { it.isHuman }
    if (bots.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(bots, key = { _, player -> player.id }) { _, bot ->
            val index = gameState.players.indexOfFirst { it.id == bot.id }
            BotAvatarBadge(
                player = bot,
                isCurrentTurn = gameState.currentTurnPlayerIndex == index,
                isDefender = gameState.defenderIndex == index,
                isAttacker = gameState.attackerIndex == index
            )
        }
    }
}

@Composable
private fun TurnBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.83f)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun GameTable(gameState: DurakGameState, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DeckStatus(gameState)
        Surface(
            modifier = Modifier.weight(1f).fillMaxSize(),
            shape = ExpressiveCorners.ExtraExtraLarge,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
        ) {
            if (gameState.tablePairs.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("The table is ready.\nChoose a card, then use the action below.", textAlign = TextAlign.Center, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(gameState.tablePairs, key = { _, pair -> pair.attackCard.id }) { _, pair ->
                        InformationalTablePair(pair)
                    }
                }
            }
        }
    }
}

@Composable
private fun InformationalTablePair(pair: TablePair) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        PlayingCardView(pair.attackCard, width = 48.dp, height = 71.dp, isSelectable = false)
        pair.defenseCard?.let { PlayingCardView(it, width = 48.dp, height = 71.dp, isSelectable = false) }
    }
}

@Composable
private fun DeckStatus(gameState: DurakGameState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(width = 54.dp, height = 80.dp), contentAlignment = Alignment.Center) {
            gameState.trumpCard?.let { PlayingCardView(it, width = 43.dp, height = 64.dp, isSelectable = false) }
            if (gameState.deck.isNotEmpty()) PlayingCardView(GameCard("deck_back", gameState.trumpSuit, Rank.ACE, isFaceUp = false), width = 43.dp, height = 64.dp, isSelectable = false)
        }
        Text("${gameState.deck.size}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text("${gameState.trumpSuit.symbol}", style = MaterialTheme.typography.labelMedium, color = TrumpGold, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun ExplicitMatchActions(
    gameState: DurakGameState,
    selectedCard: GameCard?,
    isHumanTurn: Boolean,
    isHumanAttacker: Boolean,
    isHumanDefender: Boolean,
    onPlayAttack: (GameCard) -> Unit,
    onPlayDefend: (GameCard, Int) -> Unit,
    onFinishRound: () -> Unit,
    onPassThrowIn: () -> Unit,
    onTakeTable: () -> Unit
) {
    if (!isHumanTurn || gameState.isGameOver) return
    val isDefending = isHumanDefender && gameState.gamePhase == GamePhase.DEFENDING
    val isThrowIn = !isHumanDefender && gameState.gamePhase == GamePhase.WAITING_FOR_THROW_IN
    val isOpening = isHumanAttacker && gameState.gamePhase == GamePhase.ATTACKING
    val firstUnmatched = gameState.tablePairs.indexOfFirst { !it.isDefended }
    val cardCanDefend = selectedCard != null && firstUnmatched >= 0 && selectedCard.beats(gameState.tablePairs[firstUnmatched].attackCard, gameState.trumpSuit)
    val cardCanAttack = selectedCard != null && canAddToTable(selectedCard, gameState)
    val allAttackersPassed = gameState.players.indices.filter { it != gameState.defenderIndex && !gameState.players[it].isOut }.all { it in gameState.throwInPasses }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        when {
            isDefending -> {
                PrimaryAction(
                    title = "${if (selectedCard == null) "Choose a card to defend" else "Defend selected card"}",
                    enabled = cardCanDefend,
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    onClick = { if (cardCanDefend) onPlayDefend(selectedCard!!, firstUnmatched) }
                )
                OutlinedButton(onClick = onTakeTable, modifier = Modifier.fillMaxWidth(), shape = ExpressiveCorners.Full) {
                    Icon(Icons.Default.KeyboardDoubleArrowDown, contentDescription = null)
                    Spacer(Modifier.width(7.dp))
                    Text("Take the table", fontWeight = FontWeight.Bold)
                }
            }
            isOpening || isThrowIn -> {
                val label = when {
                    isOpening -> "${if (selectedCard == null) "Choose a card to attack" else "Play attack"}"
                    else -> "${if (selectedCard == null) "Choose a matching card" else "Throw in selected card"}"
                }
                PrimaryAction(
                    title = label,
                    enabled = cardCanAttack,
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    onClick = { if (cardCanAttack) onPlayAttack(selectedCard!!) }
                )
                if (isThrowIn && !allAttackersPassed) {
                    OutlinedButton(onClick = onPassThrowIn, modifier = Modifier.fillMaxWidth(), shape = ExpressiveCorners.Full) {
                        Icon(Icons.Default.SkipNext, contentDescription = null)
                        Spacer(Modifier.width(7.dp))
                        Text("Pass throw-in", fontWeight = FontWeight.Bold)
                    }
                }
                if (isHumanAttacker && isThrowIn && allAttackersPassed) {
                    PrimaryAction(
                        title = "Clear Bito",
                        enabled = true,
                        icon = { Icon(Icons.Default.Check, contentDescription = null) },
                        onClick = onFinishRound
                    )
                }
            }
        }
    }
}

@Composable
private fun PrimaryAction(title: String, enabled: Boolean, icon: @Composable () -> Unit, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = ExpressiveCorners.Full,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.ExtraBold)
    }
}

private fun canAddToTable(card: GameCard, state: DurakGameState): Boolean {
    if (state.tablePairs.isEmpty()) return true
    if (state.tablePairs.size >= minOf(6, state.defenderHandSizeAtRoundStart)) return false
    val ranks = state.tablePairs.flatMap { listOfNotNull(it.attackCard.rank, it.defenseCard?.rank) }
    return card.rank in ranks
}

@Composable
private fun HandTray(hand: List<GameCard>, selectedCard: GameCard?, isHumanTurn: Boolean, onSelectCard: (GameCard) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isHumanTurn) "YOUR HAND · SELECT A CARD" else "YOUR HAND · WAITING FOR THE NEXT TURN",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp), contentPadding = PaddingValues(bottom = 14.dp)) {
                itemsIndexed(hand, key = { _, card -> card.id }) { _, card ->
                    PlayingCardView(
                        card = card,
                        width = 61.dp,
                        height = 90.dp,
                        isSelectable = isHumanTurn,
                        isSelected = selectedCard?.id == card.id,
                        onClick = { onSelectCard(card) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchResultDialog(gameState: DurakGameState, onExitGame: () -> Unit) {
    val result = when {
        gameState.isDraw -> "Everyone finished their cards."
        gameState.durakPlayerName == "You" -> "You are the Durak this round."
        else -> "${gameState.durakPlayerName ?: "The last player"} is the Durak."
    }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Match complete", fontWeight = FontWeight.ExtraBold) },
        text = { Text(result) },
        confirmButton = { Button(onClick = onExitGame) { Text("Back to Play") } }
    )
}

private fun buildTurnMessage(state: DurakGameState, humanTurn: Boolean, humanDefender: Boolean): String = when {
    state.isGameOver -> "Match complete"
    humanTurn && humanDefender -> "Your turn: choose a card, then defend."
    humanTurn && state.gamePhase == GamePhase.WAITING_FOR_THROW_IN -> "Your turn: throw in a matching rank or pass."
    humanTurn -> "Your turn: choose a card, then play it."
    else -> "${state.players.getOrNull(state.currentTurnPlayerIndex)?.name ?: "Opponent"} is thinking…"
}
