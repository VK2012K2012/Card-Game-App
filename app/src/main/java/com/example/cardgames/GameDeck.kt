package com.example.cardgames

import com.example.durak.model.Card
import com.example.durak.model.Rank
import com.example.durak.model.Suit

object GameDeck {
    fun standard52(seed: Long? = null): List<Card> {
        val cards = Suit.entries.flatMap { suit ->
            Rank.entries.map { rank ->
                Card(
                    id = "${suit.name}_${rank.name}",
                    suit = suit,
                    rank = rank,
                )
            }
        }.toMutableList()
        if (seed == null) cards.shuffle() else cards.shuffle(java.util.Random(seed))
        return cards
    }

    fun handValue(cards: List<Card>): Int {
        var total = cards.sumOf { it.rank.value.coerceAtMost(10) }
        var aces = cards.count { it.rank == Rank.ACE }
        while (total > 21 && aces > 0) {
            total -= 10
            aces -= 1
        }
        return total
    }
}
