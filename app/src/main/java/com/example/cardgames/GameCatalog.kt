package com.example.cardgames

enum class CardGameId(
    val title: String,
    val description: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val status: GameAvailability,
) {
    DURAK(
        title = "Durak",
        description = "Classic throw-in card game.",
        minPlayers = 2,
        maxPlayers = 4,
        status = GameAvailability.PLAYABLE_EXISTING,
    ),
    CRAZY_EIGHTS(
        title = "Crazy Eights",
        description = "Match the suit or rank and play every card from your hand.",
        minPlayers = 2,
        maxPlayers = 4,
        status = GameAvailability.PLAYABLE_NEW,
    ),
    WAR(
        title = "War",
        description = "Reveal cards and win the highest-ranked battle.",
        minPlayers = 2,
        maxPlayers = 4,
        status = GameAvailability.PLAYABLE_NEW,
    ),
    GO_FISH(
        title = "Go Fish",
        description = "Ask for ranks, collect books, and make the most sets.",
        minPlayers = 2,
        maxPlayers = 5,
        status = GameAvailability.PLAYABLE_NEW,
    ),
    BLACKJACK(
        title = "Blackjack",
        description = "Reach 21 without going over the dealer.",
        minPlayers = 1,
        maxPlayers = 4,
        status = GameAvailability.PLAYABLE_NEW,
    ),
    HEARTS(
        title = "Hearts",
        description = "Avoid hearts and the queen of spades while winning tricks.",
        minPlayers = 3,
        maxPlayers = 4,
        status = GameAvailability.PLANNED,
    ),
    SPADES(
        title = "Spades",
        description = "Bid tricks and use spades as the permanent trump suit.",
        minPlayers = 4,
        maxPlayers = 4,
        status = GameAvailability.PLANNED,
    ),
}

enum class GameType(val title: String, val description: String) {
    STANDARD("Standard", "The supported standard rules for this game"),
    CLASSIC("Classic", "Classic rules without optional variants"),
    TRANSFER("Transfer", "Redirect an attack to the next player with a matching rank"),
    SURVIVAL("Survival", "A score-based survival variant")
}

enum class GameAvailability {
    PLAYABLE_EXISTING,
    PLAYABLE_NEW,
    PLANNED,
}

enum class MatchMode {
    LOCAL_BOTS,
    PASS_AND_PLAY,
    NEARBY_P2P,
    INTERNET_P2P,
}

data class GameLaunchRequest(
    val gameId: CardGameId,
    val matchMode: MatchMode,
    val playerCount: Int,
) {
    fun isValid(): Boolean =
        playerCount in gameId.minPlayers..gameId.maxPlayers &&
            gameId.status != GameAvailability.PLANNED
}

object CardGameCatalog {
    val all: List<CardGameId> = CardGameId.entries

    val playable: List<CardGameId>
        get() = all.filter { it.status != GameAvailability.PLANNED }

    fun find(id: String): CardGameId? =
        all.firstOrNull { it.name.equals(id, ignoreCase = true) }

    fun typesFor(gameId: CardGameId): List<GameType> = when (gameId) {
        CardGameId.DURAK -> listOf(GameType.STANDARD, GameType.CLASSIC, GameType.TRANSFER)
        CardGameId.CRAZY_EIGHTS,
        CardGameId.WAR,
        CardGameId.GO_FISH,
        CardGameId.BLACKJACK -> listOf(GameType.STANDARD)
        CardGameId.HEARTS,
        CardGameId.SPADES -> emptyList()
    }
}
