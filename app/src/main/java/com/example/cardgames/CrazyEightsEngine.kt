package com.example.cardgames

import com.example.durak.model.Card
import com.example.durak.model.Rank
import com.example.durak.model.Suit

 data class CrazyEightsPlayer(
    val name: String,
    val hand: List<Card>,
)

data class CrazyEightsState(
    val players: List<CrazyEightsPlayer>,
    val drawPile: List<Card>,
    val discardPile: List<Card>,
    val currentPlayerIndex: Int,
    val activeSuit: Suit,
    val winnerIndex: Int? = null,
    val passCount: Int = 0,
    val message: String = "",
) {
    val isGameOver: Boolean get() = winnerIndex != null
    val topCard: Card get() = discardPile.last()
}

class CrazyEightsEngine {
    fun startGame(playerNames: List<String>, seed: Long? = null): CrazyEightsState {
        require(playerNames.size in 2..4) { "Crazy Eights supports 2 to 4 players." }
        val deck = GameDeck.standard52(seed).toMutableList()
        val players = playerNames.map { CrazyEightsPlayer(it, emptyList()) }.toMutableList()
        repeat(if (playerNames.size == 2) 7 else 5) {
            players.indices.forEach { index ->
                players[index] = players[index].copy(hand = players[index].hand + deck.removeAt(0))
            }
        }
        val firstCard = drawPlayableStarter(deck)
        val discard = mutableListOf(firstCard.first)
        val activeSuit = firstCard.first.suit
        return CrazyEightsState(
            players = players,
            drawPile = firstCard.second,
            discardPile = discard,
            currentPlayerIndex = 0,
            activeSuit = activeSuit,
            message = "${players.first().name}'s turn",
        )
    }

    fun canPlay(state: CrazyEightsState, card: Card): Boolean =
        !state.isGameOver &&
            state.players.getOrNull(state.currentPlayerIndex)?.hand?.any { it.id == card.id } == true &&
            (card.rank == Rank.EIGHT || card.suit == state.activeSuit || card.rank == state.topCard.rank)

    fun playCard(state: CrazyEightsState, card: Card, declaredSuit: Suit? = null): CrazyEightsState {
        if (!canPlay(state, card)) return state.copy(message = "That card cannot be played")
        if (card.rank == Rank.EIGHT && declaredSuit == null) return state.copy(message = "Choose a suit for the eight")
        val player = state.players[state.currentPlayerIndex]
        val updatedHand = player.hand.filterNot { it.id == card.id }
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = player.copy(hand = updatedHand)
        val winner = state.currentPlayerIndex.takeIf { updatedHand.isEmpty() }
        val nextSuit = if (card.rank == Rank.EIGHT) declaredSuit!! else card.suit
        val nextIndex = nextActiveIndex(state.players.size, state.currentPlayerIndex)
        return state.copy(
            players = updatedPlayers,
            discardPile = state.discardPile + card,
            currentPlayerIndex = if (winner == null) nextIndex else state.currentPlayerIndex,
            activeSuit = nextSuit,
            winnerIndex = winner,
            passCount = 0,
            message = winner?.let { "${player.name} wins" } ?: "${updatedPlayers[nextIndex].name}'s turn",
        )
    }

    fun drawCard(state: CrazyEightsState): CrazyEightsState {
        if (state.isGameOver) return state
        val replenished = replenishDrawPile(state.drawPile, state.discardPile)
        if (replenished.first.isEmpty()) return pass(state)
        val drawn = replenished.first.first()
        val player = state.players[state.currentPlayerIndex]
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = player.copy(hand = player.hand + drawn)
        return state.copy(
            players = updatedPlayers,
            drawPile = replenished.first.drop(1),
            discardPile = replenished.second,
            passCount = 0,
            message = "${player.name} drew a card",
        )
    }

    fun pass(state: CrazyEightsState): CrazyEightsState {
        if (state.isGameOver) return state
        val next = nextActiveIndex(state.players.size, state.currentPlayerIndex)
        val passes = state.passCount + 1
        val noCardsToDraw = state.drawPile.isEmpty() && state.discardPile.size <= 1
        val blockedRound = noCardsToDraw && passes >= state.players.size
        val winner = if (blockedRound) {
            state.players.indices.minByOrNull { state.players[it].hand.size }
        } else {
            null
        }
        return state.copy(
            currentPlayerIndex = next,
            passCount = passes,
            winnerIndex = winner,
            message = when {
                winner != null -> "${state.players[winner].name} wins the blocked round"
                passes >= state.players.size -> "No playable cards"
                else -> "${state.players[next].name}'s turn"
            },
        )
    }

    private fun drawPlayableStarter(deck: MutableList<Card>): Pair<Card, List<Card>> {
        val index = deck.indexOfFirst { it.rank != Rank.EIGHT }
        val starterIndex = if (index >= 0) index else 0
        val starter = deck.removeAt(starterIndex)
        return starter to deck.toList()
    }

    private fun replenishDrawPile(drawPile: List<Card>, discardPile: List<Card>): Pair<List<Card>, List<Card>> {
        if (drawPile.isNotEmpty() || discardPile.size <= 1) return drawPile to discardPile
        val top = discardPile.last()
        val reshuffled = discardPile.dropLast(1).shuffled()
        return reshuffled to listOf(top)
    }

    private fun nextActiveIndex(size: Int, current: Int): Int = (current + 1) % size
}
