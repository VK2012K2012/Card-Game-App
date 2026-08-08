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
        if (this.suit == other.suit) {
            return this.rank.value > other.rank.value
        }
        if (this.suit == trumpSuit && other.suit != trumpSuit) {
            return true
        }
        return false
    }
}

data class TablePair(
    val attackCard: Card,
    var defenseCard: Card? = null
) {
    val isDefended: Boolean get() = defenseCard != null
}

enum class GameMode(val title: String, val description: String) {
    PODKIDNOY("Throw-in (Podkidnoy)", "Players can throw in cards matching ranks on the table"),
    PEREVODNOY("Transfer (Perevodnoy)", "Defender can transfer attack to next player with matching rank card"),
    CLASSIC("Classic Durak", "Standard 1-v-1 or 4-player classic rules")
}

enum class BotDifficulty(val displayName: String, val description: String) {
    EASY("Base Bot (Rule-based)", "Classic offline rule-based bot with fast tactical decisions"),
    MEDIUM("Standard Heuristic Bot", "Conserves trumps and tracks table card counts"),
    HARD("Master Card Counter", "Strategic card counter, saves high trumps for endgame"),
    LOCAL_NEURAL_AI("Local AI (Gemma 3B)", "100% Offline neural AI engine running locally on device")
}

enum class ThemePalette(val displayName: String, val primaryHex: Long, val seedColor: Long) {
    DEEP_FOREST("Deep Forest M3E", 0xFF19241F, 0xFFAFD43E),
    EMERALD("Emerald Felt", 0xFF0F5A47, 0xFF10B981),
    CRIMSON("Ruby Velvet", 0xFF881337, 0xFFE11D48),
    SAPPHIRE("Midnight Club", 0xFF1E3A8A, 0xFF3B82F6),
    VEGAS_GOLD("Vegas Gold", 0xFF78350F, 0xFFF59E0B),
    CYBER_PURPLE("Cyber Royale", 0xFF581C87, 0xFFA855F7)
}

enum class FeltStyle(val displayName: String) {
    CLASSIC_FELT("Classic Felt"),
    ROYAL_VELVET("Royal Velvet"),
    DARK_WOOD("Dark Mahogany"),
    NEON_GRID("Cyber Grid")
}

enum class CardBackStyle(val displayName: String) {
    RED_SCROLL("Classic Red Scroll"),
    GOLD_LATTICE("Royal Gold Lattice"),
    EMERALD_FEATHER("Emerald Feather"),
    CYBER_HEX("Cyber Hex Pattern"),
    NOIR("Midnight Noir")
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
