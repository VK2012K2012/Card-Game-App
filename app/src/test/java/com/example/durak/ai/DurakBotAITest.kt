package com.example.durak.ai

import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.GameMode
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

    @Test
    fun `Expert transfers instead of spending a trump when the next player can defend`() {
        val attack = Card("transfer-attack", Suit.HEARTS, Rank.ACE)
        val transfer = Card("transfer-card", Suit.CLUBS, Rank.ACE)
        val trump = Card("transfer-trump", Suit.SPADES, Rank.TWO, isTrump = true)
        val state = DurakGameState(
            players = listOf(
                Player("attacker", "Attacker", true),
                Player("expert", "Expert", false, BotDifficulty.HARD, hand = listOf(transfer, trump)),
                Player("next", "Next", false, hand = listOf(
                    Card("next-one", Suit.HEARTS, Rank.SEVEN),
                    Card("next-two", Suit.CLUBS, Rank.NINE),
                )),
            ),
            deck = emptyList(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 0,
            defenderIndex = 1,
            currentTurnPlayerIndex = 1,
            tablePairs = listOf(TablePair(attack)),
            defenderHandSizeAtRoundStart = 2,
            gameMode = GameMode.PEREVODNOY,
            gamePhase = GamePhase.DEFENDING,
        )

        val decision = DurakBotAI.decideBotMove(state, 1, engine)

        assertTrue("Expert should transfer rather than spend its trump", decision is BotMoveDecision.Transfer)
    }
}
