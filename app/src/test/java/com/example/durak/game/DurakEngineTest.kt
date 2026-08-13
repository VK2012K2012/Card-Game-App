package com.example.durak.game

import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.GameMode
import com.example.durak.model.Player
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DurakEngineTest {
    private val engine = DurakEngine()

    @Test
    fun `start game distributes each configured bot difficulty`() {
        val state = engine.startNewGame(
            playerNames = listOf("You", "Bot 1", "Bot 2"),
            humanIsFirst = true,
            botDifficulties = listOf(BotDifficulty.HARD, BotDifficulty.EASY),
            deckSize = 36,
            gameMode = GameMode.PODKIDNOY
        )

        assertEquals(BotDifficulty.HARD, state.players[1].difficulty)
        assertEquals(BotDifficulty.EASY, state.players[2].difficulty)
        assertEquals(3, state.players.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `start game rejects unsupported player count`() {
        engine.startNewGame(
            playerNames = listOf("You"),
            humanIsFirst = true,
            botDifficulties = emptyList(),
            deckSize = 36
        )
    }

    @Test
    fun `attack is ignored when it is not the attackers turn`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val human = Player("human", "You", true, hand = mutableListOf(attack))
        val bot = Player("bot", "Bot 1", false, hand = mutableListOf())
        val state = state(players = listOf(human, bot), currentTurn = 1)

        val result = engine.playAttackCard(state, playerIndex = 0, card = attack)

        assertSame(state, result)
        assertEquals(1, human.hand.size)
    }

    @Test
    fun `empty or unfinished table cannot be cleared`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val human = Player("human", "You", true, hand = mutableListOf())
        val bot = Player("bot", "Bot 1", false, hand = mutableListOf())
        val emptyRound = state(players = listOf(human, bot), phase = GamePhase.WAITING_FOR_THROW_IN)
        val unfinishedRound = emptyRound.copy(tablePairs = listOf(com.example.durak.model.TablePair(attack)))

        assertSame(emptyRound, engine.executeBitoClear(emptyRound))
        assertSame(unfinishedRound, engine.executeBitoClear(unfinishedRound))
    }

    @Test
    fun `legal attack and defense move the round through expected phases`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val defense = card("defense", Suit.HEARTS, Rank.SEVEN)
        val human = Player("human", "You", true, hand = mutableListOf(attack))
        val bot = Player("bot", "Bot 1", false, hand = mutableListOf(defense))
        val start = state(players = listOf(human, bot))

        val afterAttack = engine.playAttackCard(start, playerIndex = 0, card = attack)
        val afterDefense = engine.playDefendCard(afterAttack, defendingCard = defense, pairIndexToDefend = 0)

        assertEquals(GamePhase.DEFENDING, afterAttack.gamePhase)
        assertEquals(1, afterAttack.currentTurnPlayerIndex)
        assertEquals(GamePhase.WAITING_FOR_THROW_IN, afterDefense.gamePhase)
        assertEquals(0, afterDefense.currentTurnPlayerIndex)
        assertTrue(afterDefense.tablePairs.single().isDefended)
    }

    private fun state(
        players: List<Player>,
        currentTurn: Int = 0,
        phase: GamePhase = GamePhase.ATTACKING
    ) = DurakGameState(
        players = players,
        deck = emptyList(),
        trumpSuit = Suit.SPADES,
        attackerIndex = 0,
        defenderIndex = 1,
        currentTurnPlayerIndex = currentTurn,
        defenderHandSizeAtRoundStart = 6,
        gamePhase = phase
    )

    private fun card(id: String, suit: Suit, rank: Rank) = Card(
        id = id,
        suit = suit,
        rank = rank,
        isTrump = suit == Suit.SPADES
    )
}
