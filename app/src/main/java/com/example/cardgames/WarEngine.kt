package com.example.cardgames

import com.example.durak.model.Card

data class WarPlayer(
    val name: String,
    val cards: List<Card>,
)

data class WarState(
    val players: List<WarPlayer>,
    val round: Int = 0,
    val battleCards: List<Card> = emptyList(),
    val winnerIndex: Int? = null,
    val message: String = "",
) {
    val isGameOver: Boolean get() = winnerIndex != null
}

class WarEngine {
    fun startGame(playerNames: List<String>, seed: Long? = null): WarState {
        require(playerNames.size in 2..4) { "War supports 2 to 4 players." }
        val deck = GameDeck.standard52(seed)
        val hands = List(playerNames.size) { mutableListOf<Card>() }
        val cardsToDeal = deck.size - (deck.size % playerNames.size)
        deck.take(cardsToDeal).forEachIndexed { index, card -> hands[index % hands.size].add(card) }
        return WarState(
            players = playerNames.mapIndexed { index, name -> WarPlayer(name, hands[index]) },
            message = if (cardsToDeal == deck.size) {
                "Reveal cards to start the battle"
            } else {
                "Reveal cards to start the battle · ${deck.size - cardsToDeal} card(s) set aside"
            },
        )
    }

    fun playBattle(state: WarState): WarState {
        if (state.isGameOver) return state
        val active = state.players.mapIndexedNotNull { index, player ->
            index.takeIf { player.cards.isNotEmpty() }
        }
        if (active.size <= 1) return state.copy(winnerIndex = active.firstOrNull(), message = "${state.players[active.firstOrNull() ?: 0].name} wins")

        val workingPlayers = state.players.map { it.cards.toMutableList() }.toMutableList()
        val battle = mutableListOf<Card>()
        var contenders = active
        var winner = active.first()
        var warResolvedByInsufficientCards = false

        while (true) {
            val revealed = contenders.mapNotNull { index ->
                workingPlayers[index].removeFirstOrNull()?.let { index to it }
            }
            if (revealed.isEmpty()) break
            battle.addAll(revealed.map { it.second })
            val highest = revealed.maxOf { it.second.rank.value }
            val winningContenders = revealed.filter { it.second.rank.value == highest }.map { it.first }
            if (winningContenders.size == 1) {
                winner = winningContenders.first()
                break
            }

            val eligible = winningContenders.filter { workingPlayers[it].size >= 2 }
            val unableToContinue = winningContenders.filterNot { it in eligible }
            unableToContinue.forEach { index ->
                battle.addAll(workingPlayers[index])
                workingPlayers[index].clear()
            }
            if (eligible.size <= 1) {
                winner = eligible.firstOrNull() ?: winningContenders.first()
                warResolvedByInsufficientCards = true
                break
            }

            eligible.forEach { index -> battle.add(workingPlayers[index].removeAt(0)) }
            contenders = eligible
        }

        val collected = battle
        workingPlayers[winner].addAll(collected)
        val updated = workingPlayers.mapIndexed { index, cards -> WarPlayer(state.players[index].name, cards) }
        val remaining = updated.mapIndexedNotNull { index, player -> index.takeIf { player.cards.isNotEmpty() } }
        val gameWinner = remaining.singleOrNull()
        return state.copy(
            players = updated,
            round = state.round + 1,
            battleCards = battle,
            winnerIndex = gameWinner,
            message = gameWinner?.let { "${updated[it].name} wins the war" }
                ?: if (warResolvedByInsufficientCards) {
                    "${updated[winner].name} wins the battle · opponent could not continue the war"
                } else {
                    "${updated[winner].name} wins the battle"
                },
        )
    }
}
