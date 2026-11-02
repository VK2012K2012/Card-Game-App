package com.example.ui

import android.os.SystemClock
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.MainActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DurakFlowUiTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homePresentsThePlayDropdownOnTheMainScreen() {
        dismissOnboardingIfNeeded()
        abandonMatchIfRestored()

        assertTrue(textExists("READY TO PLAY"))
        assertTrue(rule.onAllNodesWithTag("primary-play").fetchSemanticsNodes().isNotEmpty())
        assertTrue(rule.onAllNodesWithTag("play-menu-arrow").fetchSemanticsNodes().isNotEmpty())
        assertFalse(textExists("A growing game library"))
    }

    @Test
    fun playArrowLetsUserChooseTransferPlayersAndDifficultyBeforeStarting() {
        dismissOnboardingIfNeeded()
        abandonMatchIfRestored()

        rule.onNodeWithTag("play-menu-arrow").performClick()
        rule.waitUntil(5_000) { textExists("Game") && textExists("Type") }
        rule.onNodeWithContentDescription("Change Type").performClick()
        rule.onNodeWithText("Transfer").performClick()
        rule.onNodeWithContentDescription("Back to settings").performClick()
        assertTrue(textExists("3 players"))
        rule.onNodeWithContentDescription("Change Players").performClick()
        assertFalse(textExists("2 players"))
        assertTrue(textExists("4 players"))
        rule.onNodeWithContentDescription("Back to settings").performClick()
        rule.onNodeWithContentDescription("Change Bot difficulty").performClick()
        rule.onNodeWithText("Expert").performClick()
        rule.onNodeWithContentDescription("Back to settings").performClick()
        assertTrue(textExists("Transfer"))
        rule.onNodeWithContentDescription("Close game menu").performClick()
        rule.onNodeWithTag("primary-play").performClick()

        rule.waitUntil(10_000) { textExists("DURAK TABLE") }
        assertTrue(textExists("Round 1"))

        rule.onNodeWithContentDescription("Leave match").performClick()
        rule.onNodeWithText("Leave match").performClick()
        rule.waitUntil(10_000) { textExists("READY TO PLAY") }
    }

    @Test
    fun playArrowListsAndLaunchesEveryNewCardGameFromTheSameMenu() {
        dismissOnboardingIfNeeded()
        abandonMatchIfRestored()

        rule.onNodeWithTag("play-menu-arrow").performClick()
        rule.onNodeWithContentDescription("Change Game").performClick()
        listOf("Durak", "Crazy Eights", "War", "Go Fish", "Blackjack").forEach { game ->
            assertTrue("$game should be listed in the Play menu", textExists(game))
        }
        rule.onNodeWithContentDescription("Back to settings").performClick()
        rule.onNodeWithContentDescription("Close game menu").performClick()

        listOf(
            "Crazy Eights" to "CRAZY EIGHTS",
            "War" to "WAR",
            "Go Fish" to "GO FISH",
            "Blackjack" to "BLACKJACK",
        ).forEach { (game, sessionTitle) ->
            rule.onNodeWithTag("play-menu-arrow").performClick()
            rule.onNodeWithContentDescription("Change Game").performClick()
            rule.onNodeWithText(game).performClick()
            rule.onNodeWithContentDescription("Back to settings").performClick()
            rule.onNodeWithContentDescription("Close game menu").performClick()
            rule.onNodeWithTag("primary-play").performClick()

            rule.waitUntil(10_000) { textExists(sessionTitle) }
            rule.onNodeWithContentDescription("Leave match").performClick()
            rule.waitUntil(10_000) { textExists("READY TO PLAY") }
        }
    }

    @Test
    fun humanCanPlayThroughAFullMatchOnTheComposeGameScreen() {
        dismissOnboardingIfNeeded()
        abandonMatchIfRestored()
        rule.onNodeWithTag("primary-play").performClick()
        rule.waitUntil(10_000) { textExists("DURAK TABLE") }

        val deadline = SystemClock.elapsedRealtime() + MATCH_TIMEOUT_MS
        var decisions = 0
        while (!textExists("Match complete") && SystemClock.elapsedRealtime() < deadline && decisions < MAX_DECISIONS) {
            rule.waitUntil(15_000) {
                textExists("Match complete") || isHumanActionVisible()
            }
            if (textExists("Match complete")) break

            when {
                textExists("DEFEND THE TABLE") -> {
                    if (!playFirstLegalCard("Defend selected card")) clickEnabled("Take the table")
                }
                textExists("THROW-IN WINDOW") -> {
                    if (!playFirstLegalCard("Throw in selected card")) {
                        if (isEnabled("Clear the table")) clickEnabled("Clear the table")
                        else clickEnabled("Pass throw-in")
                    }
                }
                textExists("YOUR ATTACK") -> {
                    if (isEnabled("Clear the table")) clickEnabled("Clear the table")
                    else if (!playFirstLegalCard("Play attack")) {
                        throw AssertionError("Human is attacking but no legal card or clear-table action is available")
                    }
                }
            }
            decisions += 1
        }

        assertTrue("A complete match should reach its result dialog; handled $decisions human decisions", textExists("Match complete"))
        rule.onNodeWithText("Back to Play").performClick()
    }

    private fun dismissOnboardingIfNeeded() {
        if (textExists("Welcome to Card Game Hub")) {
            rule.onNodeWithText("Continue").performClick()
            rule.waitUntil(5_000) { textExists("Start playing") }
            rule.onNodeWithText("Start playing").performClick()
            rule.waitUntil(10_000) { textExists("READY TO PLAY") }
        }
    }

    private fun abandonMatchIfRestored() {
        if (textExists("DURAK TABLE")) {
            rule.onNodeWithContentDescription("Leave match").performClick()
            if (textExists("Leave this match?")) rule.onNodeWithText("Leave match").performClick()
            rule.waitUntil(10_000) { textExists("READY TO PLAY") }
        }
    }

    private fun isHumanActionVisible(): Boolean =
        textExists("YOUR ATTACK") || textExists("DEFEND THE TABLE") || textExists("THROW-IN WINDOW")

    private fun textExists(text: String): Boolean =
        rule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    private fun isEnabled(text: String): Boolean =
        rule.onAllNodes(hasText(text) and isEnabled()).fetchSemanticsNodes().isNotEmpty()

    private fun clickEnabled(text: String) {
        rule.onNode(hasText(text) and isEnabled()).performClick()
    }

    private fun playFirstLegalCard(actionText: String): Boolean {
        val cards = rule.onAllNodesWithTag("human-hand-card").fetchSemanticsNodes().size
        for (index in 0 until cards) {
            rule.onAllNodesWithTag("human-hand-card")[index].performClick()
            if (isEnabled(actionText)) {
                clickEnabled(actionText)
                return true
            }
        }
        return false
    }

    private companion object {
        const val MATCH_TIMEOUT_MS = 240_000L
        const val MAX_DECISIONS = 600
    }
}
