package com.example.durak.model

import androidx.compose.ui.graphics.Color

enum class Suit(val symbol: String, val suitName: String, val isRed: Boolean) {
    HEARTS("♥", "Hearts", true),
    DIAMONDS("♦", "Diamonds", true),
    CLUBS("♣", "Clubs", false),
    SPADES("♠", "Spades", false)
}

enum class Rank(val label: String, val value: Int) {
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13),
    ACE("A", 14)
}

data class Card(
    val id: String,
    val suit: Suit,
    val rank: Rank,
    val isTrump: Boolean = false,
    val isFaceUp: Boolean = true
) {
    fun beats(other: Card, trumpSuit: Suit): Boolean {
        if (suit == other.suit) return rank.value > other.rank.value
        return suit == trumpSuit && other.suit != trumpSuit
    }
}

data class TablePair(
    val attackCard: Card,
    val defenseCard: Card? = null
) {
    val isDefended: Boolean get() = defenseCard != null
}

enum class GameMode(val title: String, val description: String) {
    PODKIDNOY(
        "Standard (Throw-in)",
        "Any active non-defender may add matching ranks. Attacks are capped at six cards or the defender's starting hand size, whichever is lower."
    ),
    PEREVODNOY(
        "Transfer",
        "A distinct variant where the defender can pass an unbeaten attack to the next player using a card of the same rank."
    ),
    CLASSIC("Standard (Throw-in)", "Legacy alias of the standard throw-in ruleset.")
}

enum class BotDifficulty(val displayName: String, val description: String) {
    EASY("Beginner", "Relaxed pace with simple, occasionally random choices"),
    MEDIUM("Standard", "Balanced tactics for an everyday match"),
    HARD("Expert", "Preserves strong trumps and plays a sharper endgame")
}

/**
 * The on-device option is deliberately local-only. Until a bundled model strategy is installed,
 * it transparently falls back to the Classic strategy instead of ever making a network request.
 */
enum class OpponentEngine(val displayName: String, val description: String, val isAvailable: Boolean) {
    CLASSIC("Classic bot", "Reliable offline tactical bot", true),
    SMART_ON_DEVICE("Classic", "Balanced play", false)
}

/** A validated, serializable representation of a local Durak match. */
data class LocalMatchSetup(
    val playerCount: Int = 2,
    val gameMode: GameMode = GameMode.PODKIDNOY,
    val deckSize: Int = 36,
    val botDifficulty: BotDifficulty = BotDifficulty.MEDIUM,
    val opponentEngine: OpponentEngine = OpponentEngine.CLASSIC
) {
    fun normalized(): LocalMatchSetup {
        val normalizedPlayerCount = playerCount.coerceIn(2, 4)
        val supportedSizes = availableDeckSizes(normalizedPlayerCount)
        val requestedSize = deckSize.takeIf { it in STANDARD_DECK_SIZES } ?: 36
        val normalizedDeckSize = supportedSizes.firstOrNull { it >= requestedSize } ?: supportedSizes.last()

        return copy(
            playerCount = normalizedPlayerCount,
            deckSize = normalizedDeckSize,
            // Transfer requires its own state transition; Classic is an alias, not a separate ruleset.
            gameMode = GameMode.PODKIDNOY
        )
    }

    companion object {
        private val STANDARD_DECK_SIZES = listOf(24, 36, 52)

        /** A trump card must remain in the draw pile after the opening six-card deal. */
        fun availableDeckSizes(playerCount: Int): List<Int> {
            val normalizedCount = playerCount.coerceIn(2, 4)
            return STANDARD_DECK_SIZES.filter { it > normalizedCount * 6 }
        }
    }

    fun botDifficulties(): List<BotDifficulty> =
        List((normalized().playerCount - 1).coerceAtLeast(1)) { botDifficulty }
}

data class Player(
    val id: String,
    val name: String,
    val isHuman: Boolean,
    val difficulty: BotDifficulty = BotDifficulty.MEDIUM,
    val hand: MutableList<Card> = mutableListOf(),
    val avatarId: Int = 0,
    var winsCount: Int = 0,
    var isOut: Boolean = false
)
