package com.example.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.durak.game.DurakEngine
import com.example.durak.game.GamePhase
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.GameMode
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import com.example.durak.model.TablePair
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameStateJsonCodecTest {
    @Test
    fun stateRoundTripRetainsLiveThrowInAndCardCollections() {
        val state = DurakEngine().startNewGame(
            playerNames = listOf("You", "Bot 1", "Bot 2"),
            humanIsFirst = true,
            botDifficulties = listOf(BotDifficulty.HARD, BotDifficulty.MEDIUM),
            deckSize = 36
        ).copy(
            gamePhase = GamePhase.WAITING_FOR_THROW_IN,
            tablePairs = listOf(
                TablePair(
                    Card("attack", Suit.HEARTS, Rank.SEVEN),
                    Card("defense", Suit.HEARTS, Rank.EIGHT)
                )
            ),
            gameLog = listOf("Game started", "All attacks beaten"),
            throwInPasses = setOf(1),
            lastThrowInActorIndex = 2,
            attackParticipants = setOf(0, 2),
            lastActionMessage = "Your turn to throw in"
        )

        assertEquals(state, GameStateJsonCodec.decodeState(GameStateJsonCodec.encodeState(state)))
    }

    @Test
    fun setupRoundTripRetainsSelectedLocalOptions() {
        val setup = LocalMatchSetup(
            playerCount = 3,
            gameMode = GameMode.PODKIDNOY,
            deckSize = 36,
            botDifficulty = BotDifficulty.HARD
        )

        assertEquals(setup, GameStateJsonCodec.decodeSetup(GameStateJsonCodec.encodeSetup(setup)))
    }
}
