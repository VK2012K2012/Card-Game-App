package com.example.cardgames

import com.example.durak.model.Card

enum class BlackjackStatus {
    ACTIVE,
    STAND,
    BUST,
    WIN,
    LOSE,
    PUSH,
}

data class BlackjackPlayer(
    val name: String,
    val hand: List<Card>,
    val status: BlackjackStatus = BlackjackStatus.ACTIVE,
)

data class BlackjackState(
    val players: List<BlackjackPlayer>,
    val dealerHand: List<Card>,
    val drawPile: List<Card>,
    val currentPlayerIndex: Int = 0,
    val dealerTurn: Boolean = false,
    val isGameOver: Boolean = false,
    val message: String = "",
)

class BlackjackEngine {
    fun startGame(playerNames: List<String>, seed: Long? = null): BlackjackState {
        require(playerNames.size in 1..4) { "Blackjack supports 1 to 4 players." }
        val deck = GameDeck.standard52(seed).toMutableList()
        val players = playerNames.map { BlackjackPlayer(it, emptyList()) }.toMutableList()
        repeat(2) {
            players.indices.forEach { index ->
                players[index] = players[index].copy(hand = players[index].hand + deck.removeAt(0))
            }
        }
        for (index in players.indices) {
            if (isNatural(players[index].hand)) {
                players[index] = players[index].copy(status = BlackjackStatus.STAND)
            }
        }
        val dealer = listOf(deck.removeAt(0), deck.removeAt(0)).mapIndexed { index, card ->
            if (index == 0) card else card.copy(isFaceUp = false)
        }
        val firstActive = players.indexOfFirst { it.status == BlackjackStatus.ACTIVE }
        val dealerTurn = firstActive == -1
        return BlackjackState(
            players = players,
            dealerHand = dealer,
            drawPile = deck,
            currentPlayerIndex = firstActive.takeIf { it >= 0 } ?: 0,
            dealerTurn = dealerTurn,
            message = if (dealerTurn) "Dealer's turn" else "${players[firstActive].name}'s turn",
        )
    }

    fun hit(state: BlackjackState): BlackjackState {
        if (state.isGameOver || state.dealerTurn || state.currentPlayerIndex !in state.players.indices || state.drawPile.isEmpty()) return state
        val player = state.players[state.currentPlayerIndex]
        if (player.status != BlackjackStatus.ACTIVE) return advancePlayer(state)
        val updatedHand = player.hand + state.drawPile.first()
        val total = GameDeck.handValue(updatedHand)
        val updatedStatus = when {
            total > 21 -> BlackjackStatus.BUST
            total == 21 -> BlackjackStatus.STAND
            else -> BlackjackStatus.ACTIVE
        }
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = player.copy(hand = updatedHand, status = updatedStatus)
        val updated = state.copy(players = updatedPlayers, drawPile = state.drawPile.drop(1))
        return if (updatedStatus != BlackjackStatus.ACTIVE) {
            advancePlayer(updated)
        } else {
            updated.copy(message = "${player.name}'s total is $total")
        }
    }

    fun stand(state: BlackjackState): BlackjackState {
        if (state.isGameOver || state.dealerTurn || state.currentPlayerIndex !in state.players.indices) return state
        val player = state.players[state.currentPlayerIndex]
        if (player.status != BlackjackStatus.ACTIVE) return advancePlayer(state)
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[state.currentPlayerIndex] = player.copy(status = BlackjackStatus.STAND)
        return advancePlayer(state.copy(players = updatedPlayers))
    }

    fun playDealer(state: BlackjackState): BlackjackState {
        if (state.isGameOver || !state.dealerTurn) return state
        var hand = state.dealerHand.map { it.copy(isFaceUp = true) }
        var pile = state.drawPile
        while (GameDeck.handValue(hand) < 17 && pile.isNotEmpty()) {
            hand = hand + pile.first()
            pile = pile.drop(1)
        }
        val dealerValue = GameDeck.handValue(hand)
        val dealerBust = dealerValue > 21
        val dealerNatural = isNatural(hand)
        val settled = state.players.map { player ->
            val playerValue = GameDeck.handValue(player.hand)
            val playerNatural = isNatural(player.hand)
            val status = when {
                player.status == BlackjackStatus.BUST -> BlackjackStatus.LOSE
                playerNatural && !dealerNatural -> BlackjackStatus.WIN
                dealerNatural && !playerNatural -> BlackjackStatus.LOSE
                dealerBust -> BlackjackStatus.WIN
                playerValue > dealerValue -> BlackjackStatus.WIN
                playerValue < dealerValue -> BlackjackStatus.LOSE
                else -> BlackjackStatus.PUSH
            }
            player.copy(status = status)
        }
        return state.copy(
            players = settled,
            dealerHand = hand,
            drawPile = pile,
            isGameOver = true,
            message = if (dealerBust) "Dealer busts" else "Dealer stands on $dealerValue",
        )
    }

    fun isNatural(hand: List<com.example.durak.model.Card>): Boolean =
        hand.size == 2 && hand.any { it.rank == com.example.durak.model.Rank.ACE } &&
            hand.any { it.rank.value >= 10 }

    private fun advancePlayer(state: BlackjackState): BlackjackState {
        val next = (state.currentPlayerIndex + 1 until state.players.size)
            .firstOrNull { state.players[it].status == BlackjackStatus.ACTIVE }
        if (next != null) return state.copy(currentPlayerIndex = next, message = "${state.players[next].name}'s turn")
        return state.copy(dealerTurn = true, message = "Dealer's turn")
    }
}
