package com.example.durak.ai

import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.Player
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import com.example.durak.model.TablePair
import org.junit.Assert.assertTrue
import org.junit.Test

class DurakBotAITest {
    private val engine = DurakEngine()

    @Test
    fun `Expert saves a high trump in a throw-in while Standard spends it`() {
        val attack = Card("table", Suit.HEARTS, Rank.QUEEN)
        val trumpQueen = Card("expert-queen", Suit.SPADES, Rank.QUEEN, isTrump = true)
        val deck = List(11) { index -> Card("deck-$index", Suit.CLUBS, Rank.TWO) }

        fun state(difficulty: BotDifficulty) = DurakGameState(
            players = listOf(
                Player("bot", "Bot", false, difficulty, hand = listOf(trumpQueen)),
                Player("defender", "Defender", true)
            ),
            deck = deck,
            trumpCard = deck.first(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 0,
            defenderIndex = 1,
            currentTurnPlayerIndex = 0,
            tablePairs = listOf(TablePair(attack, Card("defense", Suit.HEARTS, Rank.KING))),
            defenderHandSizeAtRoundStart = 6,
            gamePhase = GamePhase.WAITING_FOR_THROW_IN
        )

        val standard = DurakBotAI.decideBotMove(state(BotDifficulty.MEDIUM), 0, engine)
        val expert = DurakBotAI.decideBotMove(state(BotDifficulty.HARD), 0, engine)

        assertTrue("Standard should use the legal throw-in", standard is BotMoveDecision.Attack)
        assertTrue("Expert should preserve its Queen trump", expert === BotMoveDecision.PassOrDone)
    }
}
