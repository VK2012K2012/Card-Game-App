package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cardgames.BlackjackEngine
import com.example.cardgames.BlackjackStatus
import com.example.cardgames.CardGameId
import com.example.cardgames.CrazyEightsEngine
import com.example.cardgames.GameDeck
import com.example.cardgames.GoFishEngine
import com.example.cardgames.WarEngine
import com.example.durak.model.Card
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import com.example.ui.components.ExpressiveBackButton
import com.example.ui.components.PlayingCardView
import com.example.ui.components.TableChoiceChip
import com.example.ui.components.TableFeltBackground
import com.example.ui.components.TableFilledAction
import com.example.ui.components.TableOutlinedAction
import com.example.ui.components.tableAccentTextColor
import com.example.ui.components.tablePrimaryTextColor
import com.example.ui.components.tableSecondaryTextColor
import com.example.ui.theme.ExpressiveCorners

@Composable
fun CardGameSessionScreen(
    gameId: CardGameId,
    playerCount: Int,
    onBack: () -> Unit,
) {
    val names = remember(playerCount) { listOf("You") + (2..playerCount).map { "Player $it" } }
    when (gameId) {
        CardGameId.CRAZY_EIGHTS -> CrazyEightsSession(names, onBack)
        CardGameId.WAR -> WarSession(names, onBack)
        CardGameId.GO_FISH -> GoFishSession(names, onBack)
        CardGameId.BLACKJACK -> BlackjackSession(names, onBack)
        else -> TableFeltBackground(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().padding(24.dp)) {
                ExpressiveBackButton(onClick = onBack, contentDescription = "Back")
            }
        }
    }
}

/** Shared top bar used by every new game session, matching the Durak table's header. */
@Composable
private fun SessionHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ExpressiveBackButton(onClick = onBack, contentDescription = "Leave match")
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = tableAccentTextColor(),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleLarge,
                color = tablePrimaryTextColor(),
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

/** Shared felt table surface every session's status text and cards sit on. */
@Composable
private fun SessionTable(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun StatusLine(message: String) {
    AnimatedContent(targetState = message, label = "sessionStatus") { text ->
        Text(text, style = MaterialTheme.typography.bodyLarge, color = tableSecondaryTextColor())
    }
}

@Composable
private fun WinnerDialog(text: String, onBack: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Match complete", fontWeight = FontWeight.ExtraBold) },
        text = { Text(text) },
        confirmButton = { Button(onClick = onBack) { Text("Back to library") } }
    )
}

// ---------------------------------------------------------------------
// Crazy Eights
// ---------------------------------------------------------------------

@Composable
private fun CrazyEightsSession(names: List<String>, onBack: () -> Unit) {
    val engine = remember { CrazyEightsEngine() }
    var state by remember { mutableStateOf(engine.startGame(names)) }
    var suitPickerFor by remember { mutableStateOf<Card?>(null) }
    val player = state.players[state.currentPlayerIndex]

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SessionHeader("Crazy Eights", "${player.name}'s turn", onBack)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SessionTable {
                    StatusLine(state.message)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Top card", style = MaterialTheme.typography.labelMedium, color = tableSecondaryTextColor())
                            PlayingCardView(card = state.topCard, width = 68.dp, height = 100.dp, isSelectable = false)
                        }
                        Column {
                            Text("Active suit", style = MaterialTheme.typography.labelMedium, color = tableSecondaryTextColor())
                            Text(
                                "${state.activeSuit.symbol} ${state.activeSuit.suitName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            HandTrayForGame(
                title = "${player.name.uppercase()}'S HAND",
                cardCount = player.hand.size
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(player.hand, key = { it.id }) { card ->
                        PlayingCardView(
                            card = card,
                            width = 60.dp,
                            height = 88.dp,
                            isSelectable = engine.canPlay(state, card),
                            onClick = {
                                if (card.rank == Rank.EIGHT) suitPickerFor = card
                                else state = engine.playCard(state, card)
                            }
                        )
                    }
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TableOutlinedAction(
                        title = "Draw",
                        enabled = !state.isGameOver,
                        modifier = Modifier.weight(1f),
                        onClick = { state = engine.drawCard(state) }
                    )
                    TableOutlinedAction(
                        title = "Pass",
                        enabled = !state.isGameOver,
                        modifier = Modifier.weight(1f),
                        onClick = { state = engine.pass(state) }
                    )
                }
            }
        }

        suitPickerFor?.let { card ->
            SuitPickerDialog(
                onPick = { suit ->
                    state = engine.playCard(state, card, suit)
                    suitPickerFor = null
                },
                onDismiss = { suitPickerFor = null }
            )
        }

        if (state.isGameOver) {
            WinnerDialog("${state.players[state.winnerIndex!!].name} wins Crazy Eights.", onBack)
        }
    }
}

@Composable
private fun SuitPickerDialog(onPick: (Suit) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose a suit", fontWeight = FontWeight.ExtraBold) },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Suit.entries.forEach { suit ->
                    TableChoiceChip(title = suit.symbol, selected = false, onClick = { onPick(suit) })
                }
            }
        },
        confirmButton = {}
    )
}

// ---------------------------------------------------------------------
// War
// ---------------------------------------------------------------------

@Composable
private fun WarSession(names: List<String>, onBack: () -> Unit) {
    val engine = remember { WarEngine() }
    var state by remember { mutableStateOf(engine.startGame(names)) }

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SessionHeader("War", "Round ${state.round}", onBack)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SessionTable {
                    StatusLine(state.message)
                    AnimatedVisibility(visible = state.battleCards.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                        Column {
                            Text("Last battle", style = MaterialTheme.typography.labelMedium, color = tableSecondaryTextColor())
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(state.battleCards, key = { it.id }) { card ->
                                    PlayingCardView(card = card, width = 48.dp, height = 70.dp, isSelectable = false)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.padding(top = 4.dp))
                state.players.forEach { p ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(p.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text("${p.cards.size} cards", style = MaterialTheme.typography.bodyMedium, color = tableSecondaryTextColor())
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                TableFilledAction(
                    title = "Reveal battle",
                    enabled = !state.isGameOver,
                    onClick = { state = engine.playBattle(state) }
                )
            }
        }

        if (state.isGameOver) {
            WinnerDialog("${state.players[state.winnerIndex!!].name} wins the war.", onBack)
        }
    }
}

// ---------------------------------------------------------------------
// Go Fish
// ---------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GoFishSession(names: List<String>, onBack: () -> Unit) {
    val engine = remember { GoFishEngine() }
    var state by remember { mutableStateOf(engine.startGame(names)) }
    var selectedRank by remember { mutableStateOf<Rank?>(null) }
    var selectedTarget by remember { mutableStateOf<Int?>(null) }
    val currentPlayer = state.players[state.currentPlayerIndex]

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SessionHeader("Go Fish", "${currentPlayer.name}'s turn", onBack)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SessionTable {
                    StatusLine(state.message)
                    state.players.forEachIndexed { index, p ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(p.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(
                                "${p.hand.size} cards · ${p.books} books",
                                style = MaterialTheme.typography.bodyMedium,
                                color = tableSecondaryTextColor()
                            )
                        }
                    }
                }
                Spacer(Modifier.padding(top = 8.dp))
                Text("Choose a rank you hold", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    currentPlayer.hand.map { it.rank }.distinct().forEach { rank ->
                        TableChoiceChip(title = rank.label, selected = selectedRank == rank, onClick = { selectedRank = rank })
                    }
                }
                Spacer(Modifier.padding(top = 8.dp))
                Text("Choose a player to ask", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    state.players.indices.filter { it != state.currentPlayerIndex }.forEach { index ->
                        TableChoiceChip(
                            title = state.players[index].name,
                            selected = selectedTarget == index,
                            onClick = { selectedTarget = index }
                        )
                    }
                }
            }
            Column(Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                TableFilledAction(
                    title = "Ask",
                    enabled = selectedRank != null && selectedTarget != null && !state.isGameOver,
                    onClick = {
                        state = engine.ask(state, selectedTarget!!, selectedRank!!)
                        selectedRank = null
                        selectedTarget = null
                    }
                )
            }
        }

        if (state.isGameOver) {
            val text = if (state.winnerIndices.size > 1) {
                "Tie between ${state.winnerIndices.joinToString { state.players[it].name }}."
            } else {
                "${state.players[state.winnerIndex!!].name} wins with ${state.players[state.winnerIndex!!].books} books."
            }
            WinnerDialog(text, onBack)
        }
    }
}

// ---------------------------------------------------------------------
// Blackjack
// ---------------------------------------------------------------------

@Composable
private fun BlackjackSession(names: List<String>, onBack: () -> Unit) {
    val engine = remember { BlackjackEngine() }
    var state by remember { mutableStateOf(engine.startGame(names)) }
    val player = state.players[state.currentPlayerIndex.coerceIn(0, state.players.lastIndex)]

    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SessionHeader("Blackjack", if (state.dealerTurn) "Dealer's turn" else "${player.name}'s turn", onBack)
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SessionTable {
                    Text("Dealer", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(state.dealerHand, key = { it.id }) { card ->
                            PlayingCardView(card = card, width = 56.dp, height = 82.dp, isSelectable = false)
                        }
                    }
                    Spacer(Modifier.padding(top = 4.dp))
                    Text(
                        "${player.name} · ${GameDeck.handValue(player.hand)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(player.hand, key = { it.id }) { card ->
                            PlayingCardView(card = card, width = 56.dp, height = 82.dp, isSelectable = false)
                        }
                    }
                    StatusLine(state.message)
                }
            }
            Column(Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    TableOutlinedAction(
                        title = "Hit",
                        enabled = !state.dealerTurn && !state.isGameOver,
                        modifier = Modifier.weight(1f),
                        onClick = { state = engine.hit(state) }
                    )
                    TableOutlinedAction(
                        title = "Stand",
                        enabled = !state.dealerTurn && !state.isGameOver,
                        modifier = Modifier.weight(1f),
                        onClick = { state = engine.stand(state) }
                    )
                }
                Spacer(Modifier.padding(top = 10.dp))
                TableFilledAction(
                    title = "Dealer plays",
                    enabled = state.dealerTurn && !state.isGameOver,
                    onClick = { state = engine.playDealer(state) }
                )
            }
        }

        if (state.isGameOver) {
            WinnerDialog(state.players.joinToString { "${it.name}: ${it.status.label()}" }, onBack)
        }
    }
}

private fun BlackjackStatus.label(): String = when (this) {
    BlackjackStatus.WIN -> "Win"
    BlackjackStatus.LOSE -> "Lose"
    BlackjackStatus.PUSH -> "Push"
    BlackjackStatus.BUST -> "Bust"
    BlackjackStatus.STAND -> "Stand"
    BlackjackStatus.ACTIVE -> "Active"
}

// ---------------------------------------------------------------------
// Shared hand tray
// ---------------------------------------------------------------------

@Composable
private fun HandTrayForGame(
    title: String,
    cardCount: Int,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text("$cardCount cards", style = MaterialTheme.typography.labelMedium, color = tableAccentTextColor(), fontWeight = FontWeight.ExtraBold)
            }
            content()
        }
    }
}
