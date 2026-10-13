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
    /** Most recent non-defender to play or pass during the current throw-in rotation. */
    val lastThrowInActorIndex: Int? = null,
    /** Players who contributed one or more attack cards during the current bout. */
    val attackParticipants: Set<Int> = emptySet(),
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
        require(deckSize > playerNames.size * 6) { "The deck must leave the trump card in the draw pile after dealing." }
        val rawDeck = createInitialDeck(deckSize)
        val trump = rawDeck.last()
        val trumpSuit = trump.suit

        val players = playerNames.mapIndexed { i, name ->
            val isHuman = (i == 0)
            val difficulty = if (isHuman) BotDifficulty.EASY else botDifficulties.getOrElse(i - 1) { BotDifficulty.MEDIUM }
            Player(
                id = "player_$i",
                name = name,
                isHuman = isHuman,
                difficulty = difficulty,
                hand = emptyList(),
                avatarId = i
            )
        }

        // Mark cards with trump status & Deal 6 cards to each player
        val updatedDeck = rawDeck.map { it.copy(isTrump = (it.suit == trumpSuit)) }.toMutableList()

        val dealtHands = MutableList(players.size) { mutableListOf<Card>() }
        for (round in 0 until 6) {
            for (playerIndex in players.indices) {
                if (updatedDeck.isNotEmpty()) {
                    dealtHands[playerIndex].add(updatedDeck.removeAt(0))
                }
            }
        }

        val dealtPlayers = players.mapIndexed { index, player ->
            player.copy(hand = sortHand(dealtHands[index], trumpSuit))
        }

        // Determine who has the lowest trump card to start attack
        var firstAttackerIndex = 0
        var lowestTrumpRank = Int.MAX_VALUE

        for (i in dealtPlayers.indices) {
            val minTrump = dealtPlayers[i].hand.filter { it.suit == trumpSuit }.minByOrNull { it.rank.value }
            if (minTrump != null && minTrump.rank.value < lowestTrumpRank) {
                lowestTrumpRank = minTrump.rank.value
                firstAttackerIndex = i
            }
        }

        val firstDefenderIndex = (firstAttackerIndex + 1) % dealtPlayers.size

        val log = mutableListOf("Game started! Trump suit is ${trumpSuit.symbol} ${trumpSuit.suitName}.")
        val starterName = dealtPlayers[firstAttackerIndex].name
        log.add("$starterName has the lowest trump card and attacks first!")

        return DurakGameState(
            deckSize = deckSize,
            gameMode = gameMode,
            players = dealtPlayers.toList(),
            deck = updatedDeck.toList(),
            trumpCard = trump.copy(isTrump = true),
            trumpSuit = trumpSuit,
            attackerIndex = firstAttackerIndex,
            defenderIndex = firstDefenderIndex,
            currentTurnPlayerIndex = firstAttackerIndex,
            tablePairs = emptyList(),
            defenderHandSizeAtRoundStart = dealtPlayers[firstDefenderIndex].hand.size,
            bitoCount = 0,
            gamePhase = GamePhase.ATTACKING,
            gameLog = log.toList(),
            roundCount = 1,
            lastActionMessage = "$starterName's turn to attack"
        )
    }

    fun sortHand(hand: List<Card>, trumpSuit: Suit): List<Card> = hand.sortedWith(Comparator { c1, c2 ->
            if (c1.isTrump != c2.isTrump) {
                if (c1.isTrump) 1 else -1
            } else if (c1.suit != c2.suit) {
                c1.suit.ordinal.compareTo(c2.suit.ordinal)
            } else {
                c1.rank.value.compareTo(c2.rank.value)
            }
        })

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

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[playerIndex] = player.copy(hand = player.hand.filterNot { it.id == card.id })

        val updatedPairs = state.tablePairs.toMutableList()
        updatedPairs.add(TablePair(attackCard = card))

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${player.name} attacks with ${card.rank.label}${card.suit.symbol}")

        return state.copy(
            players = updatedPlayers.toList(),
            tablePairs = updatedPairs,
            currentTurnPlayerIndex = state.defenderIndex,
            gamePhase = GamePhase.DEFENDING,
            attackParticipants = state.attackParticipants + playerIndex,
            // Playing a card is not the same as passing. Keep a throw-in player eligible
            // until they explicitly select Pass throw-in after all desired legal cards.
            throwInPasses = if (state.gamePhase == GamePhase.WAITING_FOR_THROW_IN) state.throwInPasses else emptySet(),
            lastThrowInActorIndex = playerIndex,
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
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.defenderIndex] = defender.copy(
            hand = defender.hand.filterNot { it.id == defendingCard.id }
        )

        val updatedPairs = state.tablePairs.toMutableList()
        updatedPairs[pairIndexToDefend] = targetPair.copy(defenseCard = defendingCard)

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${defender.name} defends with ${defendingCard.rank.label}${defendingCard.suit.symbol}")

        val allDefended = updatedPairs.all { it.isDefended }
        val defendedState = state.copy(
            players = updatedPlayers.toList(),
            tablePairs = updatedPairs,
            currentTurnPlayerIndex = state.defenderIndex,
            gamePhase = if (allDefended) GamePhase.WAITING_FOR_THROW_IN else GamePhase.DEFENDING,
            throwInPasses = state.throwInPasses,
            gameLog = newLogs,
            lastActionMessage = if (allDefended) "All attacks beaten. Each attacker may throw in or pass." else "${defender.name} needs to defend remaining attacks."
        )
        if (!allDefended) return defendedState

        val lastThrowInActor = state.lastThrowInActorIndex
        val canContinueBotSequence = lastThrowInActor != null &&
            !state.players[lastThrowInActor].isHuman &&
            !state.players[state.defenderIndex].isHuman &&
            lastThrowInActor != state.defenderIndex &&
            lastThrowInActor !in state.throwInPasses
        return if (canContinueBotSequence) {
            // Give the bot another uninterrupted decision: it may add every legal card, then
            // explicitly pass. This keeps secondary human attackers out of bot-only turns.
            defendedState.copy(
                currentTurnPlayerIndex = lastThrowInActor,
                gamePhase = GamePhase.WAITING_FOR_THROW_IN,
                lastActionMessage = "${state.players[lastThrowInActor].name}: continue throwing in or pass."
            )
        } else {
            advanceToNextThrowIn(defendedState, lastThrowInActor ?: state.defenderIndex)
        }
    }

    fun executeBitoClear(state: DurakGameState): DurakGameState {
        if (state.isGameOver || state.players.size < 2 ||
            state.attackerIndex !in state.players.indices || state.defenderIndex !in state.players.indices ||
            state.tablePairs.isEmpty() || state.tablePairs.any { !it.isDefended } ||
            state.currentTurnPlayerIndex != state.attackerIndex ||
            state.gamePhase != GamePhase.WAITING_FOR_THROW_IN || !isBitoReady(state)
        ) return state
        val cardsCleared = state.tablePairs.size * 2
        val newBitoCount = state.bitoCount + cardsCleared
        val newLogs = state.gameLog.toMutableList()
        newLogs.add("Bito! ${state.tablePairs.size} pairs cleared.")

        val (replenishedPlayers, nextDeck) = replenishHands(
            state.players, state.deck, state.attackerIndex, state.defenderIndex, state.trumpSuit, state.attackParticipants
        )

        // Winner check
        val (nextPlayers, winners) = checkWinners(replenishedPlayers, nextDeck)
        val activePlayerCount = nextPlayers.count { !it.isOut }
        val isOver = activePlayerCount <= 1
        val isDraw = isOver && activePlayerCount == 0
        val durak = if (isOver && !isDraw) nextPlayers.firstOrNull { !it.isOut }?.name else null

        // Next turn: defender becomes attacker if they successfully defended!
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
            attackParticipants = emptySet(),
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
            lastThrowInActorIndex = null,
            lastActionMessage = when {
                isDraw -> "Game over — everyone finished their cards."
                isOver -> "Game over — $durak is the Durak."
                else -> "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
            }
        )
    }

    fun executeDefenderTake(state: DurakGameState): DurakGameState {
        if (state.isGameOver || state.defenderIndex !in state.players.indices ||
            state.currentTurnPlayerIndex != state.defenderIndex ||
            state.gamePhase != GamePhase.DEFENDING || state.tablePairs.isEmpty() ||
            state.tablePairs.all { it.isDefended }
        ) return state

        val defender = state.players[state.defenderIndex]
        val cardsTaken = mutableListOf<Card>()
        for (pair in state.tablePairs) {
            cardsTaken.add(pair.attackCard)
            pair.defenseCard?.let { cardsTaken.add(it) }
        }

        val playersWithTakenCards = state.players.toMutableList()
        playersWithTakenCards[state.defenderIndex] = defender.copy(
            hand = sortHand(defender.hand + cardsTaken, state.trumpSuit)
        )

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${defender.name} takes ${cardsTaken.size} cards from table!")

        val (replenishedPlayers, nextDeck) = replenishHands(
            playersWithTakenCards, state.deck, state.attackerIndex, state.defenderIndex, state.trumpSuit, state.attackParticipants
        )

        val (nextPlayers, winners) = checkWinners(replenishedPlayers, nextDeck)
        val activePlayerCount = nextPlayers.count { !it.isOut }
        val isOver = activePlayerCount <= 1
        val isDraw = isOver && activePlayerCount == 0
        val durak = if (isOver && !isDraw) nextPlayers.firstOrNull { !it.isOut }?.name else null

        // Defender took cards, so they skip their turn to attack. The player AFTER defender attacks next!
        val nextAttackerIndex = getNextActivePlayerIndex(nextPlayers, state.defenderIndex)
        val nextDefenderIndex = getNextActivePlayerIndex(nextPlayers, nextAttackerIndex)

        return state.copy(
            players = nextPlayers,
            deck = nextDeck,
            tablePairs = emptyList(),
            defenderHandSizeAtRoundStart = nextPlayers.getOrNull(nextDefenderIndex)?.hand?.size ?: 6,
            attackParticipants = emptySet(),
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
            lastThrowInActorIndex = null,
            lastActionMessage = when {
                isDraw -> "Game over — everyone finished their cards."
                isOver -> "Game over — $durak is the Durak."
                else -> "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
            }
        )
    }

    /** True only after every active non-defender has had a chance to throw in. */
    fun isBitoReady(state: DurakGameState): Boolean =
        state.players.isNotEmpty() && eligibleThrowInPlayers(state).all { it in state.throwInPasses }

    /**
     * Repairs a stale bot turn without inventing a move. This is only used when a bot
     * decision was rejected because the state already recorded that actor as complete.
     */
    fun recoverStalledBotTurn(state: DurakGameState, botIndex: Int): DurakGameState {
        if (state.isGameOver || botIndex !in state.players.indices) return state

        return when (state.gamePhase) {
            GamePhase.WAITING_FOR_THROW_IN -> {
                val eligible = eligibleThrowInPlayers(state)
                val canPass = botIndex in eligible && botIndex !in state.throwInPasses
                if (canPass) {
                    val passed = passThrowIn(state, botIndex)
                    if (passed !== state) return passed
                }

                val next = (1..state.players.size)
                    .map { offset -> (botIndex + offset) % state.players.size }
                    .firstOrNull { candidate ->
                        candidate in eligible && candidate !in state.throwInPasses
                    }

                if (next != null) {
                    state.copy(
                        currentTurnPlayerIndex = next,
                        lastActionMessage = "${state.players[next].name}: throw in or pass."
                    )
                } else if (isBitoReady(state) && state.attackerIndex == botIndex) {
                    executeBitoClear(state)
                } else {
                    state.copy(
                        currentTurnPlayerIndex = state.attackerIndex,
                        lastActionMessage = "All attackers have completed the throw-in window."
                    )
                }
            }
            GamePhase.DEFENDING -> {
                if (state.tablePairs.isNotEmpty() && state.tablePairs.all { it.isDefended }) {
                    state.copy(
                        currentTurnPlayerIndex = state.attackerIndex,
                        gamePhase = GamePhase.WAITING_FOR_THROW_IN,
                        lastActionMessage = "All attacks beaten. Attackers may throw in or pass."
                    )
                } else {
                    state.copy(
                        currentTurnPlayerIndex = state.defenderIndex,
                        lastActionMessage = "${state.players[state.defenderIndex].name} must defend or take the table."
                    )
                }
            }
            else -> state
        }
    }

    /** Records a deliberate pass and gives the next eligible attacker one clear turn. */
    fun passThrowIn(state: DurakGameState, playerIndex: Int): DurakGameState {
        if (state.isGameOver || playerIndex !in state.players.indices ||
            state.defenderIndex !in state.players.indices || state.gamePhase != GamePhase.WAITING_FOR_THROW_IN ||
            state.currentTurnPlayerIndex != playerIndex || playerIndex == state.defenderIndex ||
            playerIndex !in eligibleThrowInPlayers(state) || playerIndex in state.throwInPasses
        ) return state

        val passed = state.copy(
            throwInPasses = state.throwInPasses + playerIndex,
            lastThrowInActorIndex = playerIndex,
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
        trumpSuit: Suit,
        attackParticipants: Set<Int>
    ): Pair<List<Player>, List<Card>> {
        val mutableDeck = deck.toMutableList()
        val updatedPlayers = players.toMutableList()

        // Replenish the principal attacker, joined attackers clockwise, then the defender last.
        val order = mutableListOf<Int>()
        if (attackerIdx in players.indices) order.add(attackerIdx)
        for (offset in 1 until players.size) {
            val playerIndex = (attackerIdx + offset) % players.size
            if (playerIndex != defenderIdx && playerIndex in attackParticipants) {
                order.add(playerIndex)
            }
        }
        if (defenderIdx in players.indices) order.add(defenderIdx)

        for (idx in order) {
            val p = updatedPlayers[idx]
            if (p.isOut) continue
            val hand = p.hand.toMutableList()
            while (hand.size < 6 && mutableDeck.isNotEmpty()) {
                hand.add(mutableDeck.removeAt(0))
            }
            updatedPlayers[idx] = p.copy(hand = sortHand(hand, trumpSuit))
        }

        return Pair(updatedPlayers.toList(), mutableDeck.toList())
    }

    private fun checkWinners(players: List<Player>, deck: List<Card>): Pair<List<Player>, List<String>> {
        val finishedPlayers = if (deck.isEmpty()) {
            players.map { player ->
                if (!player.isOut && player.hand.isEmpty()) player.copy(isOut = true) else player
            }
        } else {
            players.toList()
        }
        return finishedPlayers to finishedPlayers.filter { it.isOut }.map { it.name }
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
