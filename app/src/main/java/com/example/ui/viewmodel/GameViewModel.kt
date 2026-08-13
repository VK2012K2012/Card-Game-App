package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.GameStatsEntity
import com.example.data.MatchHistoryEntity
import com.example.durak.ai.BotMoveDecision
import com.example.durak.ai.DurakBotAI
import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.Card
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.OpponentEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(AppDatabase.getDatabase(application).gameDao())
    private val engine = DurakEngine()

    val statsFlow: StateFlow<GameStatsEntity> = repository.stats
        .map { it ?: GameStatsEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GameStatsEntity())

    val historyFlow: StateFlow<List<MatchHistoryEntity>> = repository.matchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _gameState = MutableStateFlow(DurakGameState())
    val gameState: StateFlow<DurakGameState> = _gameState.asStateFlow()

    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    private var botJob: Job? = null
    private var activeSessionId = 0L
    private var recordedSessionId: Long? = null
    private var activeSetup = LocalMatchSetup()

    fun startNewGame(setup: LocalMatchSetup) {
        botJob?.cancel()
        activeSessionId += 1
        recordedSessionId = null
        _selectedCard.value = null

        activeSetup = setup.normalized()
        val playerNames = buildList {
            add("You")
            repeat(activeSetup.playerCount - 1) { index -> add("Bot ${index + 1}") }
        }

        val initial = engine.startNewGame(
            playerNames = playerNames,
            humanIsFirst = true,
            botDifficulties = activeSetup.botDifficulties(),
            deckSize = activeSetup.deckSize,
            gameMode = activeSetup.gameMode
        )
        _gameState.value = if (activeSetup.opponentEngine == OpponentEngine.SMART_ON_DEVICE) {
            initial.copy(
                gameLog = initial.gameLog + "Smart on-device bot is not installed yet; Classic bot is playing locally.",
                lastActionMessage = "Classic bot is active locally while Smart bot is in preview."
            )
        } else {
            initial
        }
        checkTriggerBotMove(activeSessionId)
    }

    fun abandonMatch() {
        botJob?.cancel()
        _selectedCard.value = null
        activeSessionId += 1
    }

    fun selectCard(card: Card) {
        val state = _gameState.value
        val isHumanTurn = state.currentTurnPlayerIndex == HUMAN_INDEX
        val cardInHand = state.players.getOrNull(HUMAN_INDEX)?.hand?.any { it.id == card.id } == true
        if (state.isGameOver || !isHumanTurn || !cardInHand) return
        _selectedCard.value = if (_selectedCard.value?.id == card.id) null else card
    }

    fun playHumanAttack(card: Card) {
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != HUMAN_INDEX ||
            state.attackerIndex != HUMAN_INDEX ||
            state.gamePhase !in setOf(GamePhase.ATTACKING, GamePhase.WAITING_FOR_THROW_IN)
        ) return

        _gameState.value = engine.playAttackCard(state, HUMAN_INDEX, card)
        _selectedCard.value = null
        checkTriggerBotMove(activeSessionId)
    }

    fun playHumanDefend(defendingCard: Card, pairIndexToDefend: Int) {
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != HUMAN_INDEX ||
            state.defenderIndex != HUMAN_INDEX || state.gamePhase != GamePhase.DEFENDING ||
            pairIndexToDefend !in state.tablePairs.indices
        ) return

        _gameState.value = engine.playDefendCard(state, defendingCard, pairIndexToDefend)
        _selectedCard.value = null
        checkTriggerBotMove(activeSessionId)
    }

    fun humanFinishRound() {
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != HUMAN_INDEX ||
            state.attackerIndex != HUMAN_INDEX || state.gamePhase != GamePhase.WAITING_FOR_THROW_IN ||
            state.tablePairs.isEmpty() || state.tablePairs.any { !it.isDefended } || !engine.isBitoReady(state)
        ) return

        _gameState.value = engine.executeBitoClear(state)
        _selectedCard.value = null
        checkTriggerBotMove(activeSessionId)
    }

    fun humanPassThrowIn() {
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != HUMAN_INDEX ||
            state.defenderIndex == HUMAN_INDEX || state.gamePhase != GamePhase.WAITING_FOR_THROW_IN
        ) return

        _gameState.value = engine.passThrowIn(state, HUMAN_INDEX)
        _selectedCard.value = null
        checkTriggerBotMove(activeSessionId)
    }

    fun humanTakeTable() {
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != HUMAN_INDEX ||
            state.defenderIndex != HUMAN_INDEX || state.gamePhase != GamePhase.DEFENDING ||
            state.tablePairs.isEmpty()
        ) return

        _gameState.value = engine.executeDefenderTake(state)
        _selectedCard.value = null
        checkTriggerBotMove(activeSessionId)
    }

    private fun checkTriggerBotMove(sessionId: Long) {
        if (sessionId != activeSessionId) return
        val state = _gameState.value
        if (state.isGameOver) {
            recordGameFinishOnce(state, sessionId)
            return
        }

        val player = state.players.getOrNull(state.currentTurnPlayerIndex)
        if (player != null && !player.isHuman && !player.isOut) {
            botJob?.cancel()
            botJob = viewModelScope.launch {
                delay(BOT_TURN_DELAY_MS)
                if (sessionId == activeSessionId) executeBotTurn(state.currentTurnPlayerIndex, sessionId)
            }
        }
    }

    private fun executeBotTurn(botIndex: Int, sessionId: Long) {
        if (sessionId != activeSessionId) return
        val state = _gameState.value
        if (state.isGameOver || state.currentTurnPlayerIndex != botIndex || state.players.getOrNull(botIndex)?.isHuman != false) return

        // Both available and preview engines are local. Smart-on-device deliberately falls back
        // to this deterministic strategy until a bundled model strategy is packaged.
        val decision = DurakBotAI.decideBotMove(state, botIndex, engine)
        _gameState.value = when (decision) {
            is BotMoveDecision.Attack -> engine.playAttackCard(state, botIndex, decision.card)
            is BotMoveDecision.Defend -> engine.playDefendCard(state, decision.card, decision.pairIndex)
            BotMoveDecision.PassOrDone -> {
                if (state.gamePhase == GamePhase.WAITING_FOR_THROW_IN && state.tablePairs.all { it.isDefended }) {
                    if (botIndex == state.attackerIndex && engine.isBitoReady(state)) {
                        engine.executeBitoClear(state)
                    } else {
                        engine.passThrowIn(state, botIndex)
                    }
                } else {
                    state
                }
            }
            BotMoveDecision.TakeTable -> engine.executeDefenderTake(state)
        }
        checkTriggerBotMove(sessionId)
    }

    private fun recordGameFinishOnce(state: DurakGameState, sessionId: Long) {
        if (recordedSessionId == sessionId) return
        recordedSessionId = sessionId

        viewModelScope.launch {
            val humanIsDurak = state.durakPlayerName == "You"
            val humanWon = !state.isDraw && state.winnerPlayerNames.contains("You")
            val position = when {
                state.isDraw -> "Draw"
                humanWon -> "Winner"
                humanIsDurak -> "Durak"
                else -> "Safe"
            }
            repository.recordFinishedMatch(
                match = MatchHistoryEntity(
                    playerPosition = position,
                    opponentCount = (state.players.size - 1).coerceAtLeast(1),
                    botDifficulty = activeSetup.botDifficulty.name,
                    isWin = humanWon,
                    roundsPlayed = state.roundCount
                ),
                humanWon = humanWon,
                humanWasDurak = humanIsDurak
            )
        }
    }

    private companion object {
        const val HUMAN_INDEX = 0
        const val BOT_TURN_DELAY_MS = 650L
    }
}
