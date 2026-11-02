package com.example.cardgames

import com.example.durak.model.Card
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardGameEnginesTest {
    @Test
    fun playCatalogContainsEveryRunnableGameAndExcludesPlannedGames() {
        assertEquals(
            listOf(CardGameId.DURAK, CardGameId.CRAZY_EIGHTS, CardGameId.WAR, CardGameId.GO_FISH, CardGameId.BLACKJACK),
            CardGameCatalog.playable,
        )
        assertTrue(CardGameCatalog.playable.all { it.status != GameAvailability.PLANNED })
        assertTrue(CardGameCatalog.playable.all { GameLaunchRequest(it, MatchMode.LOCAL_BOTS, it.minPlayers).isValid() })
    }

    @Test
    fun crazyEightsDealsAndAllowsMatchingSuitOrRank() {
        val engine = CrazyEightsEngine()
        val state = engine.startGame(listOf("A", "B"), seed = 4L)
        assertEquals(7, state.players.first().hand.size)
        assertEquals(7, state.players[1].hand.size)
        val legal = Card("test_legal", state.activeSuit, Rank.TWO)
        val prepared = state.copy(players = state.players.toMutableList().also { players ->
            players[0] = players[0].copy(hand = players[0].hand + legal)
        })
        assertTrue(engine.canPlay(prepared, legal))
        val played = engine.playCard(prepared, legal)
        assertEquals(7, played.players.first().hand.size)
        assertEquals(2, played.discardPile.size)
    }

    @Test
    fun crazyEightsDrawsWhenNoCardCanBePlayed() {
        val engine = CrazyEightsEngine()
        val state = engine.startGame(listOf("A", "B"), seed = 8L)
        val drawn = engine.drawCard(state)
        assertTrue(drawn.players.first().hand.size >= state.players.first().hand.size)
        assertFalse(drawn.isGameOver)
    }

    @Test
    fun crazyEightsEndsBlockedRoundWhenStockAndDiscardCannotContinue() {
        val engine = CrazyEightsEngine()
        val top = Card("top", Suit.HEARTS, Rank.ACE)
        val state = CrazyEightsState(
            players = listOf(
                CrazyEightsPlayer("A", List(3) { Card("a$it", Suit.CLUBS, Rank.TWO) }),
                CrazyEightsPlayer("B", List(2) { Card("b$it", Suit.SPADES, Rank.THREE) }),
            ),
            drawPile = emptyList(),
            discardPile = listOf(top),
            currentPlayerIndex = 0,
            activeSuit = top.suit,
        )
        val finished = engine.pass(engine.pass(state))
        assertTrue(finished.isGameOver)
        assertEquals(1, finished.winnerIndex)
    }

    @Test
    fun warDealsEqualHandsAndKeepsAllTiedPlayersInWar() {
        val engine = WarEngine()
        val state = WarState(
            players = listOf(
                WarPlayer(
                    "A",
                    listOf(
                        Card("a1", Suit.HEARTS, Rank.ACE),
                        Card("a2", Suit.HEARTS, Rank.TWO),
                        Card("a3", Suit.HEARTS, Rank.FOUR),
                    ),
                ),
                WarPlayer(
                    "B",
                    listOf(
                        Card("b1", Suit.CLUBS, Rank.ACE),
                        Card("b2", Suit.CLUBS, Rank.THREE),
                        Card("b3", Suit.CLUBS, Rank.FIVE),
                    ),
                ),
                WarPlayer(
                    "C",
                    listOf(
                        Card("c1", Suit.SPADES, Rank.ACE),
                        Card("c2", Suit.SPADES, Rank.SIX),
                        Card("c3", Suit.SPADES, Rank.SEVEN),
                    ),
                ),
            ),
        )
        val next = engine.playBattle(state)
        assertEquals(1, next.round)
        assertEquals(9, next.battleCards.size)
        assertEquals(9, next.players.sumOf { it.cards.size })
    }

    @Test
    fun goFishEndsWhenStockIsEmptyAndReportsAllLeaders() {
        val engine = GoFishEngine()
        val state = GoFishState(
            players = listOf(
                GoFishPlayer("A", listOf(Card("a", Suit.HEARTS, Rank.ACE)), books = 1),
                GoFishPlayer("B", listOf(Card("b", Suit.CLUBS, Rank.TWO)), books = 1),
            ),
            drawPile = emptyList(),
            currentPlayerIndex = 0,
        )
        val finished = engine.ask(state, targetPlayerIndex = 1, rank = Rank.ACE)
        assertTrue(finished.isGameOver)
        assertEquals(listOf(0, 1), finished.winnerIndices)
        assertTrue(finished.message.startsWith("Tie:"))
    }

    @Test
    fun goFishEndsWhenAPlayerCompletesAHandBook() {
        val engine = GoFishEngine()
        val state = GoFishState(
            players = listOf(
                GoFishPlayer("A", listOf(Card("a1", Suit.HEARTS, Rank.ACE)), books = 0),
                GoFishPlayer(
                    "B",
                    listOf(
                        Card("b1", Suit.CLUBS, Rank.ACE),
                        Card("b2", Suit.DIAMONDS, Rank.ACE),
                        Card("b3", Suit.SPADES, Rank.ACE),
                    ),
                    books = 0,
                ),
            ),
            drawPile = listOf(Card("draw", Suit.HEARTS, Rank.TWO)),
            currentPlayerIndex = 0,
        )
        val finished = engine.ask(state, targetPlayerIndex = 1, rank = Rank.ACE)
        assertTrue(finished.isGameOver)
        assertEquals(0, finished.winnerIndex)
        assertEquals(1, finished.players[0].books)
    }

    @Test
    fun blackjackAutoStandsAtTwentyOneAndMovesToNextPlayer() {
        val engine = BlackjackEngine()
        val first = Card("first", Suit.HEARTS, Rank.TEN)
        val second = Card("second", Suit.CLUBS, Rank.FIVE)
        val next = Card("next", Suit.SPADES, Rank.FIVE)
        val state = BlackjackState(
            players = listOf(
                BlackjackPlayer("A", listOf(first, second)),
                BlackjackPlayer("B", listOf(next, next.copy(id = "next2"))),
            ),
            dealerHand = listOf(first.copy(id = "dealer1"), second.copy(id = "dealer2", isFaceUp = false)),
            drawPile = listOf(Card("hit", Suit.DIAMONDS, Rank.SIX)),
            currentPlayerIndex = 0,
        )
        val updated = engine.hit(state)
        assertEquals(BlackjackStatus.STAND, updated.players[0].status)
        assertEquals(1, updated.currentPlayerIndex)
    }

    @Test
    fun blackjackNaturalBeatsOrdinaryTwentyOne() {
        val engine = BlackjackEngine()
        val natural = listOf(
            Card("naturalAce", Suit.HEARTS, Rank.ACE),
            Card("naturalTen", Suit.CLUBS, Rank.TEN),
        )
        val state = BlackjackState(
            players = listOf(BlackjackPlayer("A", natural, BlackjackStatus.STAND)),
            dealerHand = listOf(
                Card("dealerNine", Suit.SPADES, Rank.NINE),
                Card("dealerSeven", Suit.DIAMONDS, Rank.SEVEN, isFaceUp = false),
            ),
            drawPile = listOf(Card("dealerTwo", Suit.CLUBS, Rank.TWO)),
            currentPlayerIndex = 0,
            dealerTurn = true,
        )
        assertTrue(engine.isNatural(natural))
        val finished = engine.playDealer(state)
        assertEquals(BlackjackStatus.WIN, finished.players[0].status)
    }

    @Test
    fun blackjackDealerNaturalBeatsOrdinaryTwentyOne() {
        val engine = BlackjackEngine()
        val ordinaryTwentyOne = listOf(
            Card("playerTen", Suit.HEARTS, Rank.TEN),
            Card("playerFive", Suit.CLUBS, Rank.FIVE),
            Card("playerSix", Suit.SPADES, Rank.SIX),
        )
        val state = BlackjackState(
            players = listOf(BlackjackPlayer("A", ordinaryTwentyOne, BlackjackStatus.STAND)),
            dealerHand = listOf(
                Card("dealerAce", Suit.HEARTS, Rank.ACE),
                Card("dealerTen", Suit.DIAMONDS, Rank.TEN, isFaceUp = false),
            ),
            drawPile = emptyList(),
            currentPlayerIndex = 0,
            dealerTurn = true,
        )
        val finished = engine.playDealer(state)
        assertEquals(BlackjackStatus.LOSE, finished.players[0].status)
    }

    @Test
    fun blackjackDealsTwoCardsAndDealerRemainsHiddenUntilDealerTurn() {
        val engine = BlackjackEngine()
        val state = engine.startGame(listOf("A", "B"), seed = 15L)
        assertEquals(2, state.players[0].hand.size)
        assertEquals(2, state.players[1].hand.size)
        assertFalse(state.dealerHand[1].isFaceUp)
        val afterFirst = engine.stand(state)
        val afterSecond = engine.stand(afterFirst)
        assertTrue(afterSecond.dealerTurn)
        val finished = engine.playDealer(afterSecond)
        assertTrue(finished.isGameOver)
        assertTrue(finished.dealerHand.all { it.isFaceUp })
    }
}
