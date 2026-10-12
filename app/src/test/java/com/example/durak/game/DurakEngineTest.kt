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

    @Test(expected = IllegalArgumentException::class)
    fun `start game rejects a deck that cannot keep the trump card in the draw pile`() {
        engine.startNewGame(
            playerNames = listOf("You", "Bot 1", "Bot 2", "Bot 3"),
            humanIsFirst = true,
            botDifficulties = List(3) { BotDifficulty.MEDIUM },
            deckSize = 24
        )
    }

    @Test
    fun `four player setup moves to a deck size that leaves a trump card in the draw pile`() {
        val setup = com.example.durak.model.LocalMatchSetup(playerCount = 4, deckSize = 24).normalized()

        assertEquals(36, setup.deckSize)
        assertEquals(listOf(36, 52), com.example.durak.model.LocalMatchSetup.availableDeckSizes(4))
    }

    @Test
    fun `every supported player and deck pairing keeps the trump card in the draw pile`() {
        for (playerCount in 2..4) {
            com.example.durak.model.LocalMatchSetup.availableDeckSizes(playerCount).forEach { deckSize ->
                val state = engine.startNewGame(
                    playerNames = List(playerCount) { index -> if (index == 0) "You" else "Bot $index" },
                    humanIsFirst = true,
                    botDifficulties = List(playerCount - 1) { BotDifficulty.MEDIUM },
                    deckSize = deckSize,
                )
                val trumpId = state.trumpCard!!.id

                assertTrue("$playerCount players with $deckSize cards retain trump in deck", state.deck.any { it.id == trumpId })
                assertTrue("$playerCount players with $deckSize cards do not receive trump in a hand", state.players.none { player ->
                    player.hand.any { it.id == trumpId }
                })
            }
        }
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
    fun `three player round gives human a throw-in turn before bito`() {
        val initialAttack = card("initial_attack", Suit.HEARTS, Rank.SIX)
        val defense = card("defense", Suit.HEARTS, Rank.SEVEN)
        val humanThrowIn = card("human_throw_in", Suit.CLUBS, Rank.SIX)
        val human = Player("human", "You", true, hand = mutableListOf(humanThrowIn))
        val attacker = Player("attacker", "Bot 1", false, hand = mutableListOf())
        val defender = Player("defender", "Bot 2", false, hand = mutableListOf(defense))
        val defendedStart = DurakGameState(
            players = listOf(human, attacker, defender),
            deck = emptyList(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 1,
            defenderIndex = 2,
            currentTurnPlayerIndex = 2,
            tablePairs = listOf(com.example.durak.model.TablePair(initialAttack)),
            defenderHandSizeAtRoundStart = 6,
            gamePhase = GamePhase.DEFENDING
        )

        val afterDefense = engine.playDefendCard(defendedStart, defense, pairIndexToDefend = 0)
        val afterThrowIn = engine.playAttackCard(afterDefense, playerIndex = 0, card = humanThrowIn)

        assertEquals(GamePhase.WAITING_FOR_THROW_IN, afterDefense.gamePhase)
        assertEquals(0, afterDefense.currentTurnPlayerIndex)
        assertEquals(2, afterThrowIn.tablePairs.size)
        assertEquals(2, afterThrowIn.currentTurnPlayerIndex)
    }

    @Test
    fun `round cannot clear until each active attacker has passed or thrown in`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val defense = card("defense", Suit.HEARTS, Rank.SEVEN)
        val human = Player("human", "You", true, hand = mutableListOf())
        val attacker = Player("attacker", "Bot 1", false, hand = mutableListOf())
        val defender = Player("defender", "Bot 2", false, hand = mutableListOf())
        val state = DurakGameState(
            players = listOf(human, attacker, defender),
            deck = emptyList(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 1,
            defenderIndex = 2,
            currentTurnPlayerIndex = 0,
            tablePairs = listOf(com.example.durak.model.TablePair(attack, defense)),
            defenderHandSizeAtRoundStart = 6,
            gamePhase = GamePhase.WAITING_FOR_THROW_IN
        )

        val humanPassed = engine.passThrowIn(state, 0)
        val allPassed = engine.passThrowIn(humanPassed, 1)

        assertEquals(1, humanPassed.currentTurnPlayerIndex)
        assertTrue(!engine.isBitoReady(humanPassed))
        assertTrue(engine.isBitoReady(allPassed))
        assertEquals(1, allPassed.currentTurnPlayerIndex)
    }

    @Test
    fun `bot keeps throw-in turn after bot defender answers before human is asked`() {
        val openingAttack = card("opening_attack", Suit.HEARTS, Rank.SIX)
        val followUp = card("follow_up", Suit.CLUBS, Rank.SIX)
        val openingDefense = card("opening_defense", Suit.HEARTS, Rank.SEVEN)
        val human = Player("human", "You", true, hand = mutableListOf())
        val attacker = Player("attacker", "Bot 1", false, hand = mutableListOf(openingAttack, followUp))
        val defender = Player("defender", "Bot 2", false, hand = mutableListOf(openingDefense))
        val start = DurakGameState(
            players = listOf(human, attacker, defender),
            deck = emptyList(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 1,
            defenderIndex = 2,
            currentTurnPlayerIndex = 1,
            defenderHandSizeAtRoundStart = 6,
            gamePhase = GamePhase.ATTACKING
        )

        val afterAttack = engine.playAttackCard(start, 1, openingAttack)
        val afterDefense = engine.playDefendCard(afterAttack, openingDefense, pairIndexToDefend = 0)

        assertEquals(GamePhase.WAITING_FOR_THROW_IN, afterDefense.gamePhase)
        assertEquals(1, afterDefense.currentTurnPlayerIndex)
        assertTrue(afterDefense.lastThrowInActorIndex == 1)
    }

    @Test
    fun `defender cannot take a fully defended table`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val defense = card("defense", Suit.HEARTS, Rank.SEVEN)
        val human = Player("human", "You", true, hand = mutableListOf())
        val bot = Player("bot", "Bot 1", false, hand = mutableListOf())
        val defended = state(players = listOf(human, bot), currentTurn = 1, phase = GamePhase.DEFENDING)
            .copy(defenderIndex = 1, tablePairs = listOf(com.example.durak.model.TablePair(attack, defense)))

        val result = engine.executeDefenderTake(defended)

        assertSame(defended, result)
        assertTrue(defended.tablePairs.single().isDefended)
    }

    @Test
    fun `empty player state is never considered ready for bito`() {
        assertTrue(!engine.isBitoReady(DurakGameState()))
    }

    @Test
    fun `match ends when a player is eliminated after an earlier player was already out`() {
        val attack = card("final_attack", Suit.HEARTS, Rank.SIX)
        val defense = card("final_defense", Suit.HEARTS, Rank.SEVEN)
        val previouslyOut = Player("p0", "Bot 1", false, hand = mutableListOf(), isOut = true)
        val newlyOut = Player("p1", "Bot 2", false, hand = mutableListOf())
        val lastPlayer = Player("p2", "You", true, hand = mutableListOf(card("last_card", Suit.CLUBS, Rank.EIGHT)))
        val state = DurakGameState(
            players = listOf(previouslyOut, newlyOut, lastPlayer),
            deck = emptyList(),
            trumpSuit = Suit.SPADES,
            attackerIndex = 1,
            defenderIndex = 2,
            currentTurnPlayerIndex = 1,
            tablePairs = listOf(com.example.durak.model.TablePair(attack, defense)),
            defenderHandSizeAtRoundStart = 6,
            throwInPasses = setOf(1),
            gamePhase = GamePhase.WAITING_FOR_THROW_IN
        )

        val result = engine.executeBitoClear(state)

        assertTrue(result.isGameOver)
        assertEquals("You", result.durakPlayerName)
        assertEquals(listOf("Bot 1", "Bot 2"), result.winnerPlayerNames)
    }

    @Test
    fun `stalled bot turn completes bito when every attacker already passed`() {
        val attack = card("attack", Suit.HEARTS, Rank.SIX)
        val defense = card("defense", Suit.HEARTS, Rank.SEVEN)
        val spare = card("spare", Suit.CLUBS, Rank.EIGHT)
        val human = Player("human", "You", true, hand = mutableListOf(spare))
        val attacker = Player("attacker", "Bot 1", false, hand = mutableListOf(spare.copy(id = "spare2")))
        val defender = Player("defender", "Bot 2", false, hand = mutableListOf(spare.copy(id = "spare3")))
        val stalled = DurakGameState(
            players = listOf(human, attacker, defender),
            deck = listOf(spare.copy(id = "deck")),
            trumpSuit = Suit.SPADES,
            attackerIndex = 1,
            defenderIndex = 2,
            currentTurnPlayerIndex = 1,
            tablePairs = listOf(com.example.durak.model.TablePair(attack, defense)),
            defenderHandSizeAtRoundStart = 6,
            throwInPasses = setOf(0, 1),
            gamePhase = GamePhase.WAITING_FOR_THROW_IN
        )

        val recovered = engine.recoverStalledBotTurn(stalled, botIndex = 1)

        assertTrue(recovered.tablePairs.isEmpty())
        assertEquals(GamePhase.ATTACKING, recovered.gamePhase)
        assertEquals(2, recovered.currentTurnPlayerIndex)
    }

    @Test
    fun `only players who joined the attack replenish and they draw before the defender`() {
        fun fiveCardHand(prefix: String, suit: Suit) =
            MutableList(5) { index -> card("$prefix-$index", suit, Rank.SIX) }

        val attacker = Player("p0", "Attacker", false, hand = fiveCardHand("a", Suit.HEARTS))
        val observer = Player("p1", "Observer", false, hand = fiveCardHand("o", Suit.CLUBS))
        val helper = Player("p2", "Helper", false, hand = fiveCardHand("h", Suit.DIAMONDS))
        val defender = Player("p3", "Defender", false, hand = fiveCardHand("d", Suit.CLUBS))
        val drawForAttacker = card("draw-attacker", Suit.HEARTS, Rank.TEN)
        val drawForHelper = card("draw-helper", Suit.CLUBS, Rank.JACK)
        val drawForDefender = card("draw-defender", Suit.DIAMONDS, Rank.QUEEN)
        val state = DurakGameState(
            players = listOf(attacker, observer, helper, defender),
            deck = listOf(drawForAttacker, drawForHelper, drawForDefender),
            trumpSuit = Suit.SPADES,
            attackerIndex = 0,
            defenderIndex = 3,
            currentTurnPlayerIndex = 0,
            tablePairs = listOf(
                com.example.durak.model.TablePair(
                    card("table-attack", Suit.HEARTS, Rank.KING),
                    card("table-defense", Suit.HEARTS, Rank.ACE),
                )
            ),
            defenderHandSizeAtRoundStart = 6,
            throwInPasses = setOf(0, 1, 2),
            attackParticipants = setOf(0, 2),
            gamePhase = GamePhase.WAITING_FOR_THROW_IN,
        )

        val nextRound = engine.executeBitoClear(state)

        assertEquals(6, nextRound.players[0].hand.size)
        assertEquals(5, nextRound.players[1].hand.size)
        assertEquals(6, nextRound.players[2].hand.size)
        assertEquals(6, nextRound.players[3].hand.size)
        assertTrue(nextRound.players[0].hand.any { it.id == drawForAttacker.id })
        assertTrue(nextRound.players[2].hand.any { it.id == drawForHelper.id })
        assertTrue(nextRound.players[3].hand.any { it.id == drawForDefender.id })
        assertTrue(nextRound.attackParticipants.isEmpty())
    }

    @Test
    fun `card beating follows same suit and trump hierarchy`() {
        val sixHearts = card("six_hearts", Suit.HEARTS, Rank.SIX)
        val sevenHearts = card("seven_hearts", Suit.HEARTS, Rank.SEVEN)
        val twoSpades = card("two_spades", Suit.SPADES, Rank.TWO)
        val aceSpades = card("ace_spades", Suit.SPADES, Rank.ACE)

        assertTrue(sevenHearts.beats(sixHearts, Suit.SPADES))
        assertTrue(twoSpades.beats(sixHearts, Suit.SPADES))
        assertTrue(!sixHearts.beats(twoSpades, Suit.SPADES))
        assertTrue(aceSpades.beats(twoSpades, Suit.SPADES))
    }

    @Test
    fun `legacy classic and unsupported transfer labels normalize to standard throw-in`() {
        assertEquals(
            GameMode.PODKIDNOY,
            com.example.durak.model.LocalMatchSetup(gameMode = GameMode.PEREVODNOY).normalized().gameMode,
        )
        assertEquals(
            GameMode.PODKIDNOY,
            com.example.durak.model.LocalMatchSetup(gameMode = GameMode.CLASSIC).normalized().gameMode,
        )
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
        assertEquals(setOf(0), afterAttack.attackParticipants)
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
