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
    val bitoCount: Int = 0,
    val gamePhase: GamePhase = GamePhase.SETUP,
    val gameLog: List<String> = emptyList(),
    val winnerPlayerNames: List<String> = emptyList(),
    val durakPlayerName: String? = null,
    val isGameOver: Boolean = false,
    val roundCount: Int = 1,
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

    fun canAttackWith(card: Card, tablePairs: List<TablePair>, defenderHandSize: Int): Boolean {
        if (tablePairs.isEmpty()) return true
        if (tablePairs.size >= 6 || tablePairs.size >= defenderHandSize) return false
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
        val player = state.players[playerIndex]
        val defender = state.players[state.defenderIndex]

        if (!canAttackWith(card, state.tablePairs, defender.hand.size)) {
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
            gameLog = newLogs,
            lastActionMessage = "${defender.name} must defend against ${card.rank.label}${card.suit.symbol}"
        )
    }

    fun playDefendCard(
        state: DurakGameState,
        defendingCard: Card,
        pairIndexToDefend: Int
    ): DurakGameState {
        if (pairIndexToDefend !in state.tablePairs.indices) return state
        val targetPair = state.tablePairs[pairIndexToDefend]
        if (targetPair.isDefended) return state

        if (!canDefendWith(defendingCard, targetPair.attackCard, state.trumpSuit)) {
            return state.copy(lastActionMessage = "Card cannot beat ${targetPair.attackCard.rank.label}${targetPair.attackCard.suit.symbol}")
        }

        val defender = state.players[state.defenderIndex]
        defender.hand.removeIf { it.id == defendingCard.id }

        val updatedPairs = state.tablePairs.toMutableList()
        updatedPairs[pairIndexToDefend] = targetPair.copy(defenseCard = defendingCard)

        val newLogs = state.gameLog.toMutableList()
        newLogs.add("${defender.name} defends with ${defendingCard.rank.label}${defendingCard.suit.symbol}")

        val allDefended = updatedPairs.all { it.isDefended }
        val nextPhase = if (allDefended) GamePhase.WAITING_FOR_THROW_IN else GamePhase.DEFENDING
        val nextTurnIndex = if (allDefended) state.attackerIndex else state.defenderIndex

        return state.copy(
            tablePairs = updatedPairs,
            currentTurnPlayerIndex = nextTurnIndex,
            gamePhase = nextPhase,
            gameLog = newLogs,
            lastActionMessage = if (allDefended) "All attacks beaten! Throw in or Clear (Bito)." else "${defender.name} needs to defend remaining attacks."
        )
    }

    fun executeBitoClear(state: DurakGameState): DurakGameState {
        val cardsCleared = state.tablePairs.size * 2
        val newBitoCount = state.bitoCount + cardsCleared
        val newLogs = state.gameLog.toMutableList()
        newLogs.add("Bito! ${state.tablePairs.size} pairs cleared.")

        val (nextPlayers, nextDeck) = replenishHands(state.players, state.deck, state.attackerIndex, state.defenderIndex, state.trumpSuit)

        // Winner check
        val winners = checkWinners(nextPlayers, nextDeck)
        val isOver = winners.size >= nextPlayers.size - 1
        val durak = if (isOver) nextPlayers.firstOrNull { !it.isOut }?.name else null

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
            bitoCount = newBitoCount,
            attackerIndex = nextAttackerIndex,
            defenderIndex = nextDefenderIndex,
            currentTurnPlayerIndex = nextAttackerIndex,
            gamePhase = if (isOver) GamePhase.GAME_OVER else GamePhase.ATTACKING,
            gameLog = newLogs,
            winnerPlayerNames = winners,
            durakPlayerName = durak,
            isGameOver = isOver,
            roundCount = state.roundCount + 1,
            lastActionMessage = if (isOver) "Game Over! $durak is the Durak!" else "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
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
        val durak = if (isOver) nextPlayers.firstOrNull { !it.isOut }?.name else null

        // Defender took cards, so they skip their turn to attack. The player AFTER defender attacks next!
        val nextAttackerIndex = getNextActivePlayerIndex(nextPlayers, state.defenderIndex)
        val nextDefenderIndex = getNextActivePlayerIndex(nextPlayers, nextAttackerIndex)

        return state.copy(
            players = nextPlayers,
            deck = nextDeck,
            tablePairs = emptyList(),
            attackerIndex = nextAttackerIndex,
            defenderIndex = nextDefenderIndex,
            currentTurnPlayerIndex = nextAttackerIndex,
            gamePhase = if (isOver) GamePhase.GAME_OVER else GamePhase.ATTACKING,
            gameLog = newLogs,
            winnerPlayerNames = winners,
            durakPlayerName = durak,
            isGameOver = isOver,
            roundCount = state.roundCount + 1,
            lastActionMessage = if (isOver) "Game Over! $durak is the Durak!" else "${nextPlayers[nextAttackerIndex].name}'s turn to attack"
        )
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
        return next
    }
}
