package com.example.durak.game

import com.example.durak.model.*
import java.util.UUID

data class DurakGameState(
    val deckSize: Int = 36,
    val gameMode: GameMode = GameMode.PODKIDNOY,
    val players: List<Player> = emptyList(),
    val deck: List<Card> = emptyList(),
    val trumpCard: Card? = null,
    val trumpSuit: Suit = Suit.HEARTS,
    val attackerIndex: Int = 0,
    val defenderIndex: Int = 1,
    val currentTurnPlayerIndex: Int = 0,
    val tablePairs: List<TablePair> = emptyList(),
    val defenderHandSizeAtRoundStart: Int = 6,
    val bitoCount: Int = 0,
    val gamePhase: GamePhase = GamePhase.SETUP,
    val gameLog: List<String> = emptyList(),
    val winnerPlayerNames: List<String> = emptyList(),
    val durakPlayerName: String? = null,
    val isGameOver: Boolean = false,
    val isDraw: Boolean = false,
    val roundCount: Int = 1,
    /** Active non-defenders who have explicitly declined another throw-in this round. */
    val throwInPasses: Set<Int> = emptySet(),
    val lastActionMessage: String = ""
)

enum class GamePhase {
    SETUP,
    ATTACKING,
    DEFENDING,
    WAITING_FOR_THROW_IN,
    DEFENDER_TAKING,
    ROUND_ENDED,
    GAME_OVER
}

class DurakEngine {

    fun createInitialDeck(deckSize: Int): MutableList<Card> {
        val ranks = when (deckSize) {
            24 -> Rank.values().filter { it.value >= 9 }
            36 -> Rank.values().filter { it.value >= 6 }
            else -> Rank.values().toList()
        }
        val cards = mutableListOf<Card>()
        for (suit in Suit.values()) {
            for (rank in ranks) {
                cards.add(Card(id = "${suit.name}_${rank.name}_${UUID.randomUUID().toString().take(4)}", suit = suit, rank = rank))
            }
        }
        cards.shuffle()
        return cards
    }

    fun startNewGame(
        playerNames: List<String>,
        humanIsFirst: Boolean,
        botDifficulties: List<BotDifficulty>,
        deckSize: Int = 36,
        gameMode: GameMode = GameMode.PODKIDNOY
    ): DurakGameState {
        require(playerNames.size in 2..4) { "Durak supports 2 to 4 local players." }
        require(deckSize in setOf(24, 36, 52)) { "Use a 24, 36, or 52-card deck." }
        val rawDeck = createInitialDeck(deckSize)
        val trump = rawDeck.last()
        val trumpSuit = trump.suit

        val players = mutableListOf<Player>()
        for (i in playerNames.indices) {
            val isHuman = (i == 0)
            val difficulty = if (isHuman) BotDifficulty.EASY else botDifficulties.getOrElse(i - 1) { BotDifficulty.MEDIUM }
            players.add(
                Player(
                    id = "player_$i",
                    name = playerNames[i],
                    isHuman = isHuman,
                    difficulty = difficulty,
                    hand = mutableListOf(),
                    avatarId = i
                )
            )
        }

        // Mark cards with trump status & Deal 6 cards to each player
        val updatedDeck = rawDeck.map { it.copy(isTrump = (it.suit == trumpSuit)) }.toMutableList()

        for (round in 0 until 6) {
            for (p in players) {
                if (updatedDeck.isNotEmpty()) {
                    p.hand.add(updatedDeck.removeAt(0))
                }
            }
        }

        // Sort player hands for nice display
        for (p in players) {
            sortHand(p.hand, trumpSuit)
        }

        // Determine who has the lowest trump card to start attack
        var firstAttackerIndex = 0
        var lowestTrumpRank = Int.MAX_VALUE

        for (i in players.indices) {
            val minTrump = players[i].hand.filter { it.suit == trumpSuit }.minByOrNull { it.rank.value }
            if (minTrump != null && minTrump.rank.value < lowestTrumpRank) {
                lowestTrumpRank = minTrump.rank.value
                firstAttackerIndex = i
            }
        }

        val firstDefenderIndex = (firstAttackerIndex + 1) % players.size

        val log = mutableListOf("Game started! Trump suit is ${trumpSuit.symbol} ${trumpSuit.suitName}.")
        val starterName = players[firstAttackerIndex].name
        log.add("$starterName has the lowest trump card and attacks first!")

        return DurakGameState(
            deckSize = deckSize,
            gameMode = gameMode,
            players = players,
            deck = updatedDeck,
            trumpCard = trump.copy(isTrump = true),
            trumpSuit = trumpSuit,
            attackerIndex = firstAttackerIndex,
            defenderIndex = firstDefenderIndex,
            currentTurnPlayerIndex = firstAttackerIndex,
            tablePairs = emptyList(),
            defenderHandSizeAtRoundStart = players[firstDefenderIndex].hand.size,
            bitoCount = 0,
            gamePhase = GamePhase.ATTACKING,
            gameLog = log,
            roundCount = 1,
            lastActionMessage = "$starterName's turn to attack"
        )
    }

    fun sortHand(hand: MutableList<Card>, trumpSuit: Suit) {
        hand.sortWith(Comparator { c1, c2 ->
            if (c1.isTrump != c2.isTrump) {
                if (c1.isTrump) 1 else -1
            } else if (c1.suit != c2.suit) {
                c1.suit.ordinal.compareTo(c2.suit.ordinal)
            } else {
                c1.rank.value.compareTo(c2.rank.value)
            }
        })
    }

    /**
     * Standard Durak throw-in rule: the table may never hold more than 6 attack
     * pairs total, AND it may never hold more pairs than the defender started
     * this round with in hand (the defender's *original* hand size before any
     * cards were removed this round) — otherwise the defender could be forced
     * to take more cards than they physically hold. [defenderOriginalHandSize]
     * must be captured once at the start of the round, not read live, since the
     * defender's hand shrinks as they successfully beat attacks.
     */
    fun canAttackWith(card: Card, tablePairs: List<TablePair>, defenderOriginalHandSize: Int): Boolean {
        val maxPairs = minOf(6, defenderOriginalHandSize)
        if (tablePairs.isEmpty()) return true
        if (tablePairs.size >= maxPairs) return false
        val ranksOnTable = mutableSetOf<Rank>()
        for (pair in tablePairs) {
            ranksOnTable.add(pair.attackCard.rank)
            pair.defenseCard?.let { ranksOnTable.add(it.rank) }
        }
        return ranksOnTable.contains(card.rank)
    }

    fun canDefendWith(defCard: Card, attackCard: Card, trumpSuit: Suit): Boolean {
        return defCard.beats(attackCard, trumpSuit)
    }

    fun canTransferWith(defCard: Card, tablePairs: List<TablePair>): Boolean {
        if (tablePairs.isEmpty()) return false
        if (tablePairs.any { it.isDefended }) return false // Cannot transfer if already defended some
        return tablePairs.all { it.attackCard.rank == defCard.rank }
    }

    fun playAttackCard(
        state: DurakGameState,
        playerIndex: Int,
        card: Card
    ): DurakGameState {
        if (state.isGameOver || playerIndex !in state.players.indices ||
            state.currentTurnPlayerIndex != playerIndex ||
            state.gamePhase !in setOf(GamePhase.ATTACKING, GamePhase.WAITING_FOR_THROW_IN)
        ) return state

        // In a multi-player round each non-defender gets a deliberate throw-in turn.
        val isOpeningAttacker = playerIndex == state.attackerIndex && state.gamePhase == GamePhase.ATTACKING
        val isEligibleThrowIn = playerIndex != state.defenderIndex &&
            playerIndex !in state.throwInPasses &&
            state.gamePhase == GamePhase.WAITING_FOR_THROW_IN
        if (!isOpeningAttacker && !isEligibleThrowIn) return state

        val player = state.players[playerIndex]
        val defender = state.players[state.defenderIndex]

        if (player.hand.none { it.id == card.id } || !canAttackWith(card, state.tablePairs, state.defenderHandSizeAtRoundStart)) {
            return state.copy(lastActionMessage = "Cannot attack with ${card.rank.label} ${card.suit.symbol}!")
        }

        val updatedHand = player.hand.toMutableList()
        updatedHand.removeIf { it.id == card.id }
        player.hand.clear()
        player.hand.addAll(updatedHand)

        val updatedPairs = state.tablePairs.toMutableList()
        updatedPairs.add(TablePair(attackCard = card))

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${player.name} attacks with ${card.rank.label}${card.suit.symbol}")

        return state.copy(
            tablePairs = updatedPairs,
            currentTurnPlayerIndex = state.defenderIndex,
            gamePhase = GamePhase.DEFENDING,
            throwInPasses = if (state.gamePhase == GamePhase.WAITING_FOR_THROW_IN) state.throwInPasses + playerIndex else emptySet(),
            gameLog = newLogs,
            lastActionMessage = "${defender.name} must defend against ${card.rank.label}${card.suit.symbol}"
        )
    }

    fun playDefendCard(
        state: DurakGameState,
        defendingCard: Card,
        pairIndexToDefend: Int
    ): DurakGameState {
        if (state.isGameOver || state.currentTurnPlayerIndex != state.defenderIndex ||
            state.gamePhase != GamePhase.DEFENDING || pairIndexToDefend !in state.tablePairs.indices
        ) return state
        val targetPair = state.tablePairs[pairIndexToDefend]
        if (targetPair.isDefended) return state

        if (!canDefendWith(defendingCard, targetPair.attackCard, state.trumpSuit)) {
            return state.copy(lastActionMessage = "Card cannot beat ${targetPair.attackCard.rank.label}${targetPair.attackCard.suit.symbol}")
        }

        val defender = state.players[state.defenderIndex]
        if (defender.hand.none { it.id == defendingCard.id }) return state
        defender.hand.removeIf { it.id == defendingCard.id }

        val updatedPairs = state.tablePairs.toMutableList()
        updatedPairs[pairIndexToDefend] = targetPair.copy(defenseCard = defendingCard)

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${defender.name} defends with ${defendingCard.rank.label}${defendingCard.suit.symbol}")

        val allDefended = updatedPairs.all { it.isDefended }
        val defendedState = state.copy(
            tablePairs = updatedPairs,
            currentTurnPlayerIndex = state.defenderIndex,
            gamePhase = if (allDefended) GamePhase.WAITING_FOR_THROW_IN else GamePhase.DEFENDING,
            throwInPasses = state.throwInPasses,
            gameLog = newLogs,
            lastActionMessage = if (allDefended) "All attacks beaten. Each attacker may throw in or pass." else "${defender.name} needs to defend remaining attacks."
        )
        return if (allDefended) advanceToNextThrowIn(defendedState, state.defenderIndex) else defendedState
    }

    fun executeBitoClear(state: DurakGameState): DurakGameState {
        if (state.isGameOver || state.tablePairs.isEmpty() || state.tablePairs.any { !it.isDefended } ||
            state.currentTurnPlayerIndex != state.attackerIndex ||
            state.gamePhase != GamePhase.WAITING_FOR_THROW_IN || !isBitoReady(state)
        ) return state
        val cardsCleared = state.tablePairs.size * 2
        val newBitoCount = state.bitoCount + cardsCleared
        val newLogs = state.gameLog.toMutableList()
        newLogs.add("Bito! ${state.tablePairs.size} pairs cleared.")

        val (nextPlayers, nextDeck) = replenishHands(state.players, state.deck, state.attackerIndex, state.defenderIndex, state.trumpSuit)

        // Winner check
        val winners = checkWinners(nextPlayers, nextDeck)
        val isOver = winners.size >= nextPlayers.size - 1
        val isDraw = isOver && winners.size == nextPlayers.size
        val durak = if (isOver && !isDraw) nextPlayers.firstOrNull { !it.isOut }?.name else null

        // Next turn: defender becomes attacker if they successfully defended!
        val activeIndices = nextPlayers.indices.filter { !nextPlayers[it].isOut }
        val oldDefIndex = state.defenderIndex
        val nextAttackerIndex = if (nextPlayers[oldDefIndex].isOut) {
            getNextActivePlayerIndex(nextPlayers, oldDefIndex)
        } else {
            oldDefIndex
        }
        val nextDefenderIndex = getNextActivePlayerIndex(nextPlayers, nextAttackerIndex)

        return state.copy(
            players = nextPlayers,
            deck = nextDeck,
            tablePairs = emptyList(),
            defenderHandSizeAtRoundStart = nextPlayers.getOrNull(nextDefenderIndex)?.hand?.size ?: 6,
            bitoCount = newBitoCount,
            attackerIndex = nextAttackerIndex,
            defenderIndex = nextDefenderIndex,
            currentTurnPlayerIndex = nextAttackerIndex,
            gamePhase = if (isOver) GamePhase.GAME_OVER else GamePhase.ATTACKING,
            gameLog = newLogs,
            winnerPlayerNames = winners,
            durakPlayerName = durak,
            isGameOver = isOver,
            isDraw = isDraw,
            roundCount = state.roundCount + 1,
            throwInPasses = emptySet(),
            lastActionMessage = when {
                isDraw -> "Game over — everyone finished their cards."
                isOver -> "Game over — $durak is the Durak."
                else -> "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
            }
        )
    }

    fun executeDefenderTake(state: DurakGameState): DurakGameState {
        val defender = state.players[state.defenderIndex]
        val cardsTaken = mutableListOf<Card>()
        for (pair in state.tablePairs) {
            cardsTaken.add(pair.attackCard)
            pair.defenseCard?.let { cardsTaken.add(it) }
        }

        defender.hand.addAll(cardsTaken)
        sortHand(defender.hand, state.trumpSuit)

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${defender.name} takes ${cardsTaken.size} cards from table!")

        val (nextPlayers, nextDeck) = replenishHands(state.players, state.deck, state.attackerIndex, state.defenderIndex, state.trumpSuit)

        val winners = checkWinners(nextPlayers, nextDeck)
        val isOver = winners.size >= nextPlayers.size - 1
        val isDraw = isOver && winners.size == nextPlayers.size
        val durak = if (isOver && !isDraw) nextPlayers.firstOrNull { !it.isOut }?.name else null

        // Defender took cards, so they skip their turn to attack. The player AFTER defender attacks next!
        val nextAttackerIndex = getNextActivePlayerIndex(nextPlayers, state.defenderIndex)
        val nextDefenderIndex = getNextActivePlayerIndex(nextPlayers, nextAttackerIndex)

        return state.copy(
            players = nextPlayers,
            deck = nextDeck,
            tablePairs = emptyList(),
            defenderHandSizeAtRoundStart = nextPlayers.getOrNull(nextDefenderIndex)?.hand?.size ?: 6,
            attackerIndex = nextAttackerIndex,
            defenderIndex = nextDefenderIndex,
            currentTurnPlayerIndex = nextAttackerIndex,
            gamePhase = if (isOver) GamePhase.GAME_OVER else GamePhase.ATTACKING,
            gameLog = newLogs,
            winnerPlayerNames = winners,
            durakPlayerName = durak,
            isGameOver = isOver,
            isDraw = isDraw,
            roundCount = state.roundCount + 1,
            throwInPasses = emptySet(),
            lastActionMessage = when {
                isDraw -> "Game over — everyone finished their cards."
                isOver -> "Game over — $durak is the Durak."
                else -> "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
            }
        )
    }

    /** True only after every active non-defender has had a chance to throw in. */
    fun isBitoReady(state: DurakGameState): Boolean = eligibleThrowInPlayers(state).all { it in state.throwInPasses }

    /** Records a deliberate pass and gives the next eligible attacker one clear turn. */
    fun passThrowIn(state: DurakGameState, playerIndex: Int): DurakGameState {
        if (state.isGameOver || state.gamePhase != GamePhase.WAITING_FOR_THROW_IN ||
            state.currentTurnPlayerIndex != playerIndex || playerIndex == state.defenderIndex ||
            playerIndex !in eligibleThrowInPlayers(state) || playerIndex in state.throwInPasses
        ) return state

        val passed = state.copy(
            throwInPasses = state.throwInPasses + playerIndex,
            gameLog = state.gameLog + "${state.players[playerIndex].name} passes the throw-in.",
            lastActionMessage = "${state.players[playerIndex].name} passes."
        )
        return if (isBitoReady(passed)) {
            passed.copy(
                currentTurnPlayerIndex = passed.attackerIndex,
                lastActionMessage = "All attackers passed. ${passed.players[passed.attackerIndex].name} can clear Bito."
            )
        } else {
            advanceToNextThrowIn(passed, playerIndex)
        }
    }

    private fun advanceToNextThrowIn(state: DurakGameState, afterPlayerIndex: Int): DurakGameState {
        repeat(state.players.size) { offset ->
            val candidate = (afterPlayerIndex + 1 + offset) % state.players.size
            if (candidate in eligibleThrowInPlayers(state) && candidate !in state.throwInPasses) {
                return state.copy(
                    currentTurnPlayerIndex = candidate,
                    gamePhase = GamePhase.WAITING_FOR_THROW_IN,
                    lastActionMessage = "${state.players[candidate].name}: throw in a matching rank or pass."
                )
            }
        }
        return state.copy(currentTurnPlayerIndex = state.attackerIndex)
    }

    private fun eligibleThrowInPlayers(state: DurakGameState): List<Int> = state.players.indices.filter { index ->
        index != state.defenderIndex && !state.players[index].isOut
    }

    private fun replenishHands(
        players: List<Player>,
        deck: List<Card>,
        attackerIdx: Int,
        defenderIdx: Int,
        trumpSuit: Suit
    ): Pair<List<Player>, List<Card>> {
        val mutableDeck = deck.toMutableList()

        // Replenish in order: Attacker first, then secondary players, then defender last!
        val order = mutableListOf<Int>()
        order.add(attackerIdx)
        for (i in players.indices) {
            if (i != attackerIdx && i != defenderIdx) order.add(i)
        }
        order.add(defenderIdx)

        for (idx in order) {
            val p = players[idx]
            if (p.isOut) continue
            while (p.hand.size < 6 && mutableDeck.isNotEmpty()) {
                p.hand.add(mutableDeck.removeAt(0))
            }
            sortHand(p.hand, trumpSuit)
        }

        return Pair(players, mutableDeck)
    }

    private fun checkWinners(players: List<Player>, deck: List<Card>): List<String> {
        val winners = mutableListOf<String>()
        if (deck.isEmpty()) {
            for (p in players) {
                if (p.hand.isEmpty()) {
                    p.isOut = true
                    if (!winners.contains(p.name)) {
                        winners.add(p.name)
                    }
                }
            }
        }
        return winners
    }

    private fun getNextActivePlayerIndex(players: List<Player>, currentIndex: Int): Int {
        var next = (currentIndex + 1) % players.size
        var attempts = 0
        while (players[next].isOut && attempts < players.size) {
            next = (next + 1) % players.size
            attempts++
        }
        return if (attempts >= players.size) currentIndex else next
    }
}
