package com.example.cardgames

import com.example.durak.model.Card
import com.example.durak.model.Rank

data class GoFishPlayer(
    val name: String,
    val hand: List<Card>,
    val books: Int = 0,
)

data class GoFishState(
    val players: List<GoFishPlayer>,
    val drawPile: List<Card>,
    val currentPlayerIndex: Int,
    val winnerIndex: Int? = null,
    val winnerIndices: List<Int> = emptyList(),
    val message: String = "",
) {
    val isGameOver: Boolean get() = winnerIndex != null
}

class GoFishEngine {
    fun startGame(playerNames: List<String>, seed: Long? = null): GoFishState {
        require(playerNames.size in 2..5) { "Go Fish supports 2 to 5 players." }
        val deck = GameDeck.standard52(seed).toMutableList()
        val cardsPerPlayer = if (playerNames.size == 2) 7 else 5
        val players = playerNames.map { GoFishPlayer(it, emptyList()) }.toMutableList()
        repeat(cardsPerPlayer) {
            players.indices.forEach { index ->
                players[index] = players[index].copy(hand = players[index].hand + deck.removeAt(0))
            }
        }
        return refillEmptyHandOnTurn(
            settleBooks(
                GoFishState(players, deck, currentPlayerIndex = 0, message = "${players.first().name} asks first")
            )
        )
    }

    fun ask(state: GoFishState, targetPlayerIndex: Int, rank: Rank): GoFishState {
        if (state.isGameOver) return state
        val askerIndex = state.currentPlayerIndex
        if (targetPlayerIndex !in state.players.indices || targetPlayerIndex == askerIndex) {
            return state.copy(message = "Choose another player")
        }
        if (state.players[askerIndex].hand.none { it.rank == rank }) {
            return state.copy(message = "You must hold that rank to ask for it")
        }
        val target = state.players[targetPlayerIndex]
        val matching = target.hand.filter { it.rank == rank }
        val updatedPlayers = state.players.map { it.hand.toMutableList() }.toMutableList()
        var nextPlayer = (askerIndex + 1) % state.players.size
        var message = "Go fish"
        if (matching.isNotEmpty()) {
            updatedPlayers[targetPlayerIndex].removeAll(matching.toSet())
            updatedPlayers[askerIndex].addAll(matching)
            nextPlayer = askerIndex
            message = "${target.name} gave ${matching.size} card(s)"
        } else if (state.drawPile.isNotEmpty()) {
            val drawn = state.drawPile.first()
            updatedPlayers[askerIndex].add(drawn)
            if (drawn.rank == rank) {
                nextPlayer = askerIndex
                message = "Go fish worked: draw again"
            }
        }
        val updated = settleBooks(
            state.copy(
                players = updatedPlayers.mapIndexed { index, hand -> state.players[index].copy(hand = hand) },
                drawPile = if (matching.isEmpty() && state.drawPile.isNotEmpty()) state.drawPile.drop(1) else state.drawPile,
                currentPlayerIndex = nextPlayer,
                message = message,
            )
        )
        return if (updated.isGameOver) updated else refillEmptyHandOnTurn(updated)
    }

    /**
     * Standard Go Fish rule: a player whose hand has emptied (their last
     * cards just became a book) draws back up before it's their turn to ask
     * again, as long as the draw pile still has cards. Without this, an
     * empty-handed player got stuck with nothing to ask for.
     */
    private fun refillEmptyHandOnTurn(state: GoFishState): GoFishState {
        val current = state.players[state.currentPlayerIndex]
        if (current.hand.isNotEmpty() || state.drawPile.isEmpty()) {
            return state.copy(message = "${current.name}'s turn · ${state.message}")
        }
        val drawn = state.drawPile.first()
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = current.copy(hand = listOf(drawn))
        return settleBooks(
            state.copy(
                players = updatedPlayers,
                drawPile = state.drawPile.drop(1),
                message = "${current.name} drew back in · ${state.message}",
            )
        )
    }

    private fun settleBooks(state: GoFishState): GoFishState {
        val players = state.players.map { player ->
            val grouped = player.hand.groupBy { it.rank }
            val completedRanks = grouped.filterValues { it.size >= 4 }.keys
            player.copy(
                hand = player.hand.filterNot { it.rank in completedRanks },
                books = player.books + completedRanks.size,
            )
        }
        // The game ends as soon as the draw pile is exhausted.
        // Completed-book totals determine the winner or tie.
        val gameOver = state.drawPile.isEmpty()
        if (!gameOver) return state.copy(players = players)

        val maxBooks = players.maxOfOrNull { it.books } ?: 0
        val leaders = players.indices.filter { players[it].books == maxBooks }
        val winner = leaders.firstOrNull()
        val resultMessage = if (leaders.size == 1) {
            "${players[winner!!].name} wins with $maxBooks book(s)"
        } else {
            "Tie: ${leaders.joinToString { players[it].name }} with $maxBooks book(s)"
        }
        return state.copy(
            players = players,
            winnerIndex = winner,
            winnerIndices = leaders,
            message = resultMessage,
        )
    }
}
