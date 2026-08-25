package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.Card as GameCard
import com.example.durak.model.Rank
import com.example.durak.model.TablePair
import com.example.ui.components.BotAvatarBadge
import com.example.ui.components.ExpressiveBackButton
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
    onPassThrowIn: () -> Unit,
    onTakeTable: () -> Unit,
    onExitGame: () -> Unit
) {
    val humanIndex = gameState.players.indexOfFirst { it.isHuman }.let { if (it >= 0) it else 0 }
    val human = gameState.players.getOrNull(humanIndex)
    val isHumanTurn = gameState.currentTurnPlayerIndex == humanIndex
    val isHumanDefender = gameState.defenderIndex == humanIndex
    val isHumanAttacker = gameState.attackerIndex == humanIndex
    TableFeltBackground(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            MatchHeader(gameState.roundCount, onExitGame)
            OpponentRail(gameState)
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
    val primaryText = tablePrimaryTextColor()
    val accentText = tableAccentTextColor()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ExpressiveBackButton(
            onClick = onExitGame,
            contentDescription = "Leave match",
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DURAK TABLE",
                style = MaterialTheme.typography.labelMedium,
                color = accentText,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Round $round",
                style = MaterialTheme.typography.titleLarge,
                color = primaryText,
                fontWeight = FontWeight.ExtraBold
            )
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OpponentRail(gameState: DurakGameState) {
    val bots = gameState.players.filterNot { it.isHuman }
    if (bots.isEmpty()) return

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = bots.size.coerceAtLeast(1)
    ) {
        bots.forEach { bot ->
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
private fun GameTable(gameState: DurakGameState, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DeckStatus(gameState)
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            shape = ExpressiveCorners.ExtraExtraLarge,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
            tonalElevation = 1.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = gameState.tablePairs,
                    animationSpec = tween(260),
                    label = "tablePairsTransition"
                ) { pairs ->
                    if (pairs.isEmpty()) {
                        EmptyTableState()
                    } else {
                        AdaptiveTablePairs(pairs)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTableState() {
    val primaryText = tablePrimaryTextColor()
    val secondaryText = tableSecondaryTextColor()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier.padding(20.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.TableRestaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        Text(
            text = "The table is ready",
            style = MaterialTheme.typography.titleMedium,
            color = primaryText,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Choose a card from your hand to start the next move.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = secondaryText
        )
    }
}

@Composable
private fun AdaptiveTablePairs(pairs: List<TablePair>) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val pairGap = 8.dp
        val cardGap = 4.dp
        val maxCardWidth = 54.dp
        val minCardWidth = 32.dp
        val availableForCards = maxWidth - pairGap * (pairs.size - 1) - cardGap * pairs.size
        val fitCardWidth = (availableForCards / (pairs.size * 2)).coerceIn(minCardWidth, maxCardWidth)
        val canFitAll = availableForCards / (pairs.size * 2) >= minCardWidth

        if (canFitAll) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(pairGap, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pairs.forEach { pair ->
                    AnimatedTablePair(
                        pair = pair,
                        cardWidth = fitCardWidth,
                        cardHeight = fitCardWidth * (90f / 61f)
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()
            LaunchedEffect(pairs.size, pairs.lastOrNull()?.attackCard?.id, pairs.lastOrNull()?.defenseCard?.id) {
                if (pairs.isNotEmpty()) listState.animateScrollToItem(pairs.lastIndex)
            }
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(pairGap)
            ) {
                itemsIndexed(pairs, key = { _, pair -> pair.attackCard.id }) { _, pair ->
                    AnimatedTablePair(pair, cardWidth = minCardWidth, cardHeight = minCardWidth * (90f / 61f))
                }
            }
        }
    }
}

@Composable
private fun AnimatedTablePair(pair: TablePair, cardWidth: Dp, cardHeight: Dp) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .animateContentSize()
            .padding(vertical = 8.dp)
    ) {
        PlayingCardView(pair.attackCard, width = cardWidth, height = cardHeight, isSelectable = false)
        AnimatedVisibility(
            visible = pair.defenseCard != null,
            enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.82f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
            exit = fadeOut(tween(140)) + scaleOut(targetScale = 0.82f, animationSpec = tween(140))
        ) {
            pair.defenseCard?.let {
                PlayingCardView(it, width = cardWidth, height = cardHeight, isSelectable = false)
            }
        }
    }
}

@Composable
private fun DeckStatus(gameState: DurakGameState) {
    val primaryText = tablePrimaryTextColor()
    val secondaryText = tableSecondaryTextColor()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 560.dp),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(width = 92.dp, height = 62.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (gameState.deck.isNotEmpty()) {
                    PlayingCardView(
                        card = GameCard("deck_back", gameState.trumpSuit, Rank.ACE, isFaceUp = false),
                        width = 38.dp,
                        height = 56.dp,
                        isSelectable = false,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
                gameState.trumpCard?.let {
                    PlayingCardView(
                        card = it,
                        width = 38.dp,
                        height = 56.dp,
                        isSelectable = false,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "${gameState.deck.size} cards left",
                    style = MaterialTheme.typography.titleSmall,
                    color = primaryText,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Deck  ·  Trump ${gameState.trumpSuit.symbol} ${gameState.trumpSuit.suitName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = ExpressiveCorners.Full,
                color = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Text(
                    text = gameState.trumpSuit.symbol,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Black
                )
            }
        }
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
    val cardCanDefend = selectedCard != null &&
        firstUnmatched >= 0 &&
        selectedCard.beats(gameState.tablePairs[firstUnmatched].attackCard, gameState.trumpSuit)
    val cardCanAttack = selectedCard != null && canAddToTable(selectedCard, gameState)
    val allAttackersPassed = gameState.players.indices
        .filter { it != gameState.defenderIndex && !gameState.players[it].isOut }
        .all { it in gameState.throwInPasses }

    val actionAccent = tableAccentTextColor()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when {
                    isDefending -> "DEFEND THE TABLE"
                    isThrowIn -> "THROW-IN WINDOW"
                    else -> "YOUR ATTACK"
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = actionAccent
            )

            when {
                isDefending -> {
                    PrimaryAction(
                        title = if (selectedCard == null) "Choose a card to defend" else "Defend selected card",
                        enabled = cardCanDefend,
                        icon = { Icon(Icons.Default.Check, contentDescription = null) },
                        onClick = { if (cardCanDefend) onPlayDefend(selectedCard!!, firstUnmatched) }
                    )
                    AnimatedOutlinedAction(onClick = onTakeTable) {
                        Icon(Icons.Default.KeyboardDoubleArrowDown, contentDescription = null)
                        Spacer(Modifier.width(7.dp))
                        Text("Take the table", fontWeight = FontWeight.Bold)
                    }
                }
                isOpening || isThrowIn -> {
                    PrimaryAction(
                        title = when {
                            isOpening && selectedCard == null -> "Choose a card to attack"
                            isOpening -> "Play attack"
                            selectedCard == null -> "Choose a matching card"
                            else -> "Throw in selected card"
                        },
                        enabled = cardCanAttack,
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                        onClick = { if (cardCanAttack) onPlayAttack(selectedCard!!) }
                    )
                    if (isThrowIn && !allAttackersPassed) {
                        AnimatedOutlinedAction(onClick = onPassThrowIn) {
                            Icon(Icons.Default.SkipNext, contentDescription = null)
                            Spacer(Modifier.width(7.dp))
                            Text("Pass throw-in", fontWeight = FontWeight.Bold)
                        }
                    }
                    if (isHumanAttacker && isThrowIn && allAttackersPassed) {
                        PrimaryAction(
                            title = "Clear the table",
                            enabled = true,
                            icon = { Icon(Icons.Default.Check, contentDescription = null) },
                            onClick = onFinishRound
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedOutlinedAction(
    onClick: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val actionText = tablePrimaryTextColor()
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 22.dp else 34.dp,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "outlinedActionCorner"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "outlinedActionPress"
    )
    OutlinedButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(corner),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = actionText),
        content = content
    )
}

@Composable
private fun PrimaryAction(
    title: String,
    enabled: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 22.dp else 34.dp,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "matchActionCorner"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "matchActionPress"
    )
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(corner),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = if (isSystemInDarkTheme()) Color(0xFF3B332F) else MaterialTheme.colorScheme.surfaceContainerHighest,
            disabledContentColor = tableDisabledTextColor()
        )
    ) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            color = if (enabled) MaterialTheme.colorScheme.onPrimary else tableDisabledTextColor(),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun tablePrimaryTextColor(): Color = if (isSystemInDarkTheme()) {
    Color(0xFFFFF1EA)
} else {
    MaterialTheme.colorScheme.onSurface
}

@Composable
private fun tableSecondaryTextColor(): Color = if (isSystemInDarkTheme()) {
    Color(0xFFDCC9C0)
} else {
    MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun tableAccentTextColor(): Color = if (isSystemInDarkTheme()) {
    Color(0xFFFFB69F)
} else {
    MaterialTheme.colorScheme.primary
}

@Composable
private fun tableDisabledTextColor(): Color = if (isSystemInDarkTheme()) {
    Color(0xFFB9AAA4)
} else {
    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
}

private fun canAddToTable(card: GameCard, state: DurakGameState): Boolean {
    if (state.tablePairs.isEmpty()) return true
    if (state.tablePairs.size >= minOf(6, state.defenderHandSizeAtRoundStart)) return false
    val ranks = state.tablePairs.flatMap { listOfNotNull(it.attackCard.rank, it.defenseCard?.rank) }
    return card.rank in ranks
}

@Composable
private fun HandTray(
    hand: List<GameCard>,
    selectedCard: GameCard?,
    isHumanTurn: Boolean,
    onSelectCard: (GameCard) -> Unit
) {
    val primaryText = tablePrimaryTextColor()
    val secondaryText = tableSecondaryTextColor()
    val accentText = tableAccentTextColor()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 12.dp, end = 16.dp)
                .navigationBarsPadding()
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isHumanTurn) "YOUR HAND" else "YOUR HAND · WAITING",
                    style = MaterialTheme.typography.labelLarge,
                    color = primaryText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${hand.size} cards",
                    style = MaterialTheme.typography.labelMedium,
                    color = accentText,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            AnimatedContent(
                targetState = selectedCard?.id,
                label = "selectedCardHint"
            ) { selectedId ->
                Text(
                    text = if (selectedId == null) {
                        "Tap a card to select it"
                    } else {
                        "${selectedCard?.rank?.label} ${selectedCard?.suit?.symbol} selected"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }
            AdaptiveHandRow(
                hand = hand,
                selectedCard = selectedCard,
                isHumanTurn = isHumanTurn,
                onSelectCard = onSelectCard
            )
        }
    }
}

@Composable
private fun AdaptiveHandRow(
    hand: List<GameCard>,
    selectedCard: GameCard?,
    isHumanTurn: Boolean,
    onSelectCard: (GameCard) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        val cardCount = hand.size.coerceAtLeast(1)
        val gap = 6.dp
        val availableWidth = maxWidth - (gap * (cardCount - 1))
        val adaptiveWidth = (availableWidth / cardCount).coerceIn(44.dp, 61.dp)
        val adaptiveHeight = adaptiveWidth * (90f / 61f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom
        ) {
            hand.forEach { card ->
                PlayingCardView(
                    card = card,
                    width = adaptiveWidth,
                    height = adaptiveHeight,
                    isSelectable = isHumanTurn,
                    isSelected = selectedCard?.id == card.id,
                    onClick = { onSelectCard(card) }
                )
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
        confirmButton = {
            Button(onClick = onExitGame) {
                Text("Back to Play")
            }
        }
    )
}
