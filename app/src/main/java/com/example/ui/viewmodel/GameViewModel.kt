package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.durak.ai.AiAdviceResult
import com.example.durak.ai.BotMoveDecision
import com.example.durak.ai.DurakBotAI
import com.example.durak.ai.LocalAiAdvisor
import com.example.durak.game.DurakEngine
import com.example.durak.game.DurakGameState
import com.example.durak.game.GamePhase
import com.example.durak.model.*
import com.example.ui.theme.CardAppThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val engine = DurakEngine()

    // Persistent Room stats
    val statsFlow: StateFlow<GameStatsEntity>
    val historyFlow: StateFlow<List<MatchHistoryEntity>>

    // Game state
    private val _gameState = MutableStateFlow(DurakGameState())
    val gameState: StateFlow<DurakGameState> = _gameState.asStateFlow()

    // Selected Card by Human Player
    private val _selectedCard = MutableStateFlow<Card?>(null)
    val selectedCard: StateFlow<Card?> = _selectedCard.asStateFlow()

    // AI Coach advice
    private val _aiAdvice = MutableStateFlow<AiAdviceResult?>(null)
    val aiAdvice: StateFlow<AiAdviceResult?> = _aiAdvice.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Custom Theme state
    private val _themeState = MutableStateFlow(CardAppThemeState())
    val themeState: StateFlow<CardAppThemeState> = _themeState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = GameRepository(database.gameDao())

        statsFlow = repository.stats.map { entity ->
            entity ?: GameStatsEntity()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GameStatsEntity()
        )

        historyFlow = repository.matchHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Sync Theme state when Room loads
        viewModelScope.launch {
            statsFlow.collect { stats ->
                val palette = try { ThemePalette.valueOf(stats.selectedThemePalette) } catch (e: Exception) { ThemePalette.EMERALD }
                val felt = try { FeltStyle.valueOf(stats.selectedFeltStyle) } catch (e: Exception) { FeltStyle.CLASSIC_FELT }
                val back = try { CardBackStyle.valueOf(stats.selectedCardBack) } catch (e: Exception) { CardBackStyle.RED_SCROLL }
                _themeState.value = CardAppThemeState(
                    palette = palette,
                    feltStyle = felt,
                    cardBackStyle = back
                )
            }
        }
    }

    fun startNewGame(
        playerCount: Int = 2,
        mode: GameMode = GameMode.PODKIDNOY,
        deckSize: Int = 36,
        botDifficulties: List<BotDifficulty> = listOf(BotDifficulty.MEDIUM)
    ) {
        val playerNames = mutableListOf("You")
        for (i in 1 until playerCount) {
            val diff = botDifficulties.getOrElse(i - 1) { BotDifficulty.MEDIUM }
            playerNames.add(if (diff == BotDifficulty.LOCAL_NEURAL_AI) "Gemma 3B AI" else "Bot $i")
        }

        val initial = engine.startNewGame(
            playerNames = playerNames,
            humanIsFirst = true,
            botDifficulties = botDifficulties,
            deckSize = deckSize,
            gameMode = mode
        )
        _gameState.value = initial
        _selectedCard.value = null
        _aiAdvice.value = null

        checkTriggerBotMove()
    }

    fun selectCard(card: Card) {
        if (_selectedCard.value?.id == card.id) {
            _selectedCard.value = null
        } else {
            _selectedCard.value = card
        }
    }

    fun playHumanAttack(card: Card) {
        val currentState = _gameState.value
        val humanIndex = 0
        if (currentState.currentTurnPlayerIndex != humanIndex) return

        val newState = engine.playAttackCard(currentState, humanIndex, card)
        _gameState.value = newState
        _selectedCard.value = null

        checkTriggerBotMove()
    }

    fun playHumanDefend(defendingCard: Card, pairIndexToDefend: Int) {
        val currentState = _gameState.value
        val humanIndex = 0
        if (currentState.defenderIndex != humanIndex) return

        val newState = engine.playDefendCard(currentState, defendingCard, pairIndexToDefend)
        _gameState.value = newState
        _selectedCard.value = null

        checkTriggerBotMove()
    }

    fun humanPassOrClear() {
        val currentState = _gameState.value
        if (currentState.gamePhase == GamePhase.WAITING_FOR_THROW_IN || currentState.gamePhase == GamePhase.ATTACKING) {
            val newState = engine.executeBitoClear(currentState)
            _gameState.value = newState
            _selectedCard.value = null
            checkTriggerBotMove()
        }
    }

    fun humanTakeTable() {
        val currentState = _gameState.value
        if (currentState.defenderIndex == 0) {
            val newState = engine.executeDefenderTake(currentState)
            _gameState.value = newState
            _selectedCard.value = null
            checkTriggerBotMove()
        }
    }

    fun requestAiAdvice() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val currentState = _gameState.value
            val humanHand = currentState.players.firstOrNull { it.isHuman }?.hand ?: emptyList()
            val result = LocalAiAdvisor.getStrategicAdvice(currentState, humanHand)
            _aiAdvice.value = result
            _isAiLoading.value = false
        }
    }

    fun applyRecommendedCard() {
        val adviceCard = _aiAdvice.value?.recommendedCard ?: return
        val currentState = _gameState.value
        if (currentState.currentTurnPlayerIndex == 0) {
            if (currentState.defenderIndex == 0) {
                val undefIdx = currentState.tablePairs.indexOfFirst { !it.isDefended }
                if (undefIdx != -1) {
                    playHumanDefend(adviceCard, undefIdx)
                }
            } else {
                playHumanAttack(adviceCard)
            }
        }
    }

    private fun checkTriggerBotMove() {
        val currentState = _gameState.value
        if (currentState.isGameOver) {
            recordGameFinish(currentState)
            return
        }

        val currentPlayer = currentState.players.getOrNull(currentState.currentTurnPlayerIndex)
        if (currentPlayer != null && !currentPlayer.isHuman && !currentPlayer.isOut) {
            viewModelScope.launch {
                delay(800) // Realistic bot delay
                executeBotTurn(currentState.currentTurnPlayerIndex)
            }
        }
    }

    private fun executeBotTurn(botIndex: Int) {
        val currentState = _gameState.value
        val decision = DurakBotAI.decideBotMove(currentState, botIndex, engine)

        val newState = when (decision) {
            is BotMoveDecision.Attack -> engine.playAttackCard(currentState, botIndex, decision.card)
            is BotMoveDecision.Defend -> engine.playDefendCard(currentState, decision.card, decision.pairIndex)
            is BotMoveDecision.PassOrDone -> {
                if (currentState.gamePhase == GamePhase.WAITING_FOR_THROW_IN || currentState.gamePhase == GamePhase.ATTACKING) {
                    engine.executeBitoClear(currentState)
                } else {
                    currentState
                }
            }
            is BotMoveDecision.TakeTable -> engine.executeDefenderTake(currentState)
        }

        _gameState.value = newState
        checkTriggerBotMove()
    }

    private fun recordGameFinish(state: DurakGameState) {
        viewModelScope.launch {
            val humanIsDurak = (state.durakPlayerName == "You")
            val humanIsWinner = state.winnerPlayerNames.contains("You")

            val currentStats = statsFlow.value
            val updatedStats = currentStats.copy(
                totalGamesPlayed = currentStats.totalGamesPlayed + 1,
                totalWins = if (humanIsWinner) currentStats.totalWins + 1 else currentStats.totalWins,
                totalLossesDurak = if (humanIsDurak) currentStats.totalLossesDurak + 1 else currentStats.totalLossesDurak
            )
            repository.saveStats(updatedStats)

            val match = MatchHistoryEntity(
                playerPosition = if (humanIsWinner) "1st Place (Winner)" else if (humanIsDurak) "Durak (Fool)" else "Safe",
                opponentCount = state.players.size - 1,
                botDifficulty = state.players.getOrNull(1)?.difficulty?.name ?: "MEDIUM",
                isWin = humanIsWinner,
                roundsPlayed = state.roundCount
            )
            repository.recordMatch(match, humanIsWinner, humanIsDurak)
        }
    }

    fun updateTheme(palette: ThemePalette, feltStyle: FeltStyle, cardBackStyle: CardBackStyle) {
        val newTheme = CardAppThemeState(palette = palette, feltStyle = feltStyle, cardBackStyle = cardBackStyle)
        _themeState.value = newTheme
        viewModelScope.launch {
            val current = statsFlow.value
            repository.saveStats(
                current.copy(
                    selectedThemePalette = palette.name,
                    selectedFeltStyle = feltStyle.name,
                    selectedCardBack = cardBackStyle.name
                )
            )
        }
    }
}
