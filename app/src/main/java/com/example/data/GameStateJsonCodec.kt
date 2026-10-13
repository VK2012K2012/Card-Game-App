package com.example.data

import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.BotDifficulty
import com.example.durak.model.Card
import com.example.durak.model.GameMode
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.OpponentEngine
import com.example.durak.model.Player
import com.example.durak.model.Rank
import com.example.durak.model.Suit
import com.example.durak.model.TablePair
import org.json.JSONArray
import org.json.JSONObject

/** Small, versioned-by-database JSON codec for the local-only in-progress match. */
object GameStateJsonCodec {
    fun encodeState(state: DurakGameState): String = JSONObject().apply {
        put("deckSize", state.deckSize)
        put("gameMode", state.gameMode.name)
        put("players", JSONArray().apply { state.players.forEach { put(it.toJson()) } })
        put("deck", state.deck.toCardsJson())
        put("trumpCard", state.trumpCard?.toJson() ?: JSONObject.NULL)
        put("trumpSuit", state.trumpSuit.name)
        put("attackerIndex", state.attackerIndex)
        put("defenderIndex", state.defenderIndex)
        put("currentTurnPlayerIndex", state.currentTurnPlayerIndex)
        put("tablePairs", JSONArray().apply {
            state.tablePairs.forEach { pair ->
                put(JSONObject().apply {
                    put("attackCard", pair.attackCard.toJson())
                    put("defenseCard", pair.defenseCard?.toJson() ?: JSONObject.NULL)
                })
            }
        })
        put("defenderHandSizeAtRoundStart", state.defenderHandSizeAtRoundStart)
        put("bitoCount", state.bitoCount)
        put("gamePhase", state.gamePhase.name)
        put("gameLog", state.gameLog.toJsonArray())
        put("winnerPlayerNames", state.winnerPlayerNames.toJsonArray())
        put("durakPlayerName", state.durakPlayerName ?: JSONObject.NULL)
        put("isGameOver", state.isGameOver)
        put("isDraw", state.isDraw)
        put("roundCount", state.roundCount)
        put("throwInPasses", state.throwInPasses.toSortedSet().toJsonArray())
        put("lastThrowInActorIndex", state.lastThrowInActorIndex ?: JSONObject.NULL)
        put("attackParticipants", state.attackParticipants.toSortedSet().toJsonArray())
        put("lastActionMessage", state.lastActionMessage)
    }.toString()

    fun decodeState(json: String): DurakGameState {
        val source = JSONObject(json)
        return DurakGameState(
            deckSize = source.getInt("deckSize"),
            gameMode = GameMode.valueOf(source.getString("gameMode")),
            players = source.getJSONArray("players").let { array ->
                List(array.length()) { array.getJSONObject(it).toPlayer() }
            },
            deck = source.getJSONArray("deck").toCards(),
            trumpCard = source.optJSONObject("trumpCard")?.toCard(),
            trumpSuit = Suit.valueOf(source.getString("trumpSuit")),
            attackerIndex = source.getInt("attackerIndex"),
            defenderIndex = source.getInt("defenderIndex"),
            currentTurnPlayerIndex = source.getInt("currentTurnPlayerIndex"),
            tablePairs = source.getJSONArray("tablePairs").let { array ->
                List(array.length()) { index ->
                    array.getJSONObject(index).let { pair ->
                        TablePair(
                            attackCard = pair.getJSONObject("attackCard").toCard(),
                            defenseCard = pair.optJSONObject("defenseCard")?.toCard()
                        )
                    }
                }
            },
            defenderHandSizeAtRoundStart = source.getInt("defenderHandSizeAtRoundStart"),
            bitoCount = source.getInt("bitoCount"),
            gamePhase = GamePhase.valueOf(source.getString("gamePhase")),
            gameLog = source.getJSONArray("gameLog").toStringList(),
            winnerPlayerNames = source.getJSONArray("winnerPlayerNames").toStringList(),
            durakPlayerName = source.optString("durakPlayerName").takeUnless { it == "" || it == "null" },
            isGameOver = source.getBoolean("isGameOver"),
            isDraw = source.getBoolean("isDraw"),
            roundCount = source.getInt("roundCount"),
            throwInPasses = source.getJSONArray("throwInPasses").toIntSet(),
            lastThrowInActorIndex = source.opt("lastThrowInActorIndex")
                .takeUnless { it == null || it == JSONObject.NULL }
                ?.let { (it as Number).toInt() },
            attackParticipants = source.getJSONArray("attackParticipants").toIntSet(),
            lastActionMessage = source.getString("lastActionMessage")
        )
    }

    fun encodeSetup(setup: LocalMatchSetup): String {
        val normalized = setup.normalized()
        return JSONObject().apply {
            put("playerCount", normalized.playerCount)
            put("gameMode", normalized.gameMode.name)
            put("deckSize", normalized.deckSize)
            put("botDifficulty", normalized.botDifficulty.name)
            put("opponentEngine", normalized.opponentEngine.name)
        }.toString()
    }

    fun decodeSetup(json: String): LocalMatchSetup {
        val source = JSONObject(json)
        return LocalMatchSetup(
            playerCount = source.getInt("playerCount"),
            gameMode = GameMode.valueOf(source.getString("gameMode")),
            deckSize = source.getInt("deckSize"),
            botDifficulty = BotDifficulty.valueOf(source.getString("botDifficulty")),
            opponentEngine = OpponentEngine.valueOf(source.getString("opponentEngine"))
        ).normalized()
    }

    private fun Card.toJson() = JSONObject().apply {
        put("id", id)
        put("suit", suit.name)
        put("rank", rank.name)
        put("isTrump", isTrump)
        put("isFaceUp", isFaceUp)
    }

    private fun JSONObject.toCard() = Card(
        id = getString("id"),
        suit = Suit.valueOf(getString("suit")),
        rank = Rank.valueOf(getString("rank")),
        isTrump = getBoolean("isTrump"),
        isFaceUp = getBoolean("isFaceUp")
    )

    private fun Player.toJson() = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("isHuman", isHuman)
        put("difficulty", difficulty.name)
        put("hand", hand.toCardsJson())
        put("avatarId", avatarId)
        put("winsCount", winsCount)
        put("isOut", isOut)
    }

    private fun JSONObject.toPlayer() = Player(
        id = getString("id"),
        name = getString("name"),
        isHuman = getBoolean("isHuman"),
        difficulty = BotDifficulty.valueOf(getString("difficulty")),
        hand = getJSONArray("hand").toCards(),
        avatarId = getInt("avatarId"),
        winsCount = getInt("winsCount"),
        isOut = getBoolean("isOut")
    )

    private fun List<Card>.toCardsJson() = JSONArray().apply { forEach { put(it.toJson()) } }
    private fun List<String>.toJsonArray() = JSONArray().apply { forEach { put(it) } }
    private fun Set<Int>.toJsonArray() = JSONArray().apply { forEach { put(it) } }
    private fun JSONArray.toCards() = List(length()) { getJSONObject(it).toCard() }
    private fun JSONArray.toStringList() = List(length()) { getString(it) }
    private fun JSONArray.toIntSet() = List(length()) { getInt(it) }.toSet()
}
