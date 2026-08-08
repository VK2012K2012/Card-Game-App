package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.durak.model.BotDifficulty
import com.example.durak.model.GameMode
import com.example.ui.screens.*
import com.example.ui.theme.CardGameTheme
import com.example.ui.viewmodel.GameViewModel

enum class AppScreen {
    HOME_HUB,
    DURAK_GAME,
    MULTIPLAYER,
    CUSTOMIZER,
    STATISTICS
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeState by viewModel.themeState.collectAsState()
            val stats by viewModel.statsFlow.collectAsState()
            val history by viewModel.historyFlow.collectAsState()
            val gameState by viewModel.gameState.collectAsState()
            val selectedCard by viewModel.selectedCard.collectAsState()
            val aiAdvice by viewModel.aiAdvice.collectAsState()
            val isAiLoading by viewModel.isAiLoading.collectAsState()

            var currentScreen by remember { mutableStateOf(AppScreen.HOME_HUB) }

            CardGameTheme(themeState = themeState) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                        when (screen) {
                            AppScreen.HOME_HUB -> {
                                HomeHubScreen(
                                    stats = stats,
                                    onStartDurak = { count, mode, deckSize, botDiff ->
                                        viewModel.startNewGame(count, mode, deckSize, listOf(botDiff))
                                        currentScreen = AppScreen.DURAK_GAME
                                    },
                                    onOpenMultiplayer = { currentScreen = AppScreen.MULTIPLAYER },
                                    onOpenCustomizer = { currentScreen = AppScreen.CUSTOMIZER },
                                    onOpenStats = { currentScreen = AppScreen.STATISTICS }
                                )
                            }
                            AppScreen.DURAK_GAME -> {
                                DurakGameScreen(
                                    gameState = gameState,
                                    selectedCard = selectedCard,
                                    aiAdvice = aiAdvice,
                                    isAiLoading = isAiLoading,
                                    onSelectCard = { card -> viewModel.selectCard(card) },
                                    onPlayAttack = { card -> viewModel.playHumanAttack(card) },
                                    onPlayDefend = { card, pairIdx -> viewModel.playHumanDefend(card, pairIdx) },
                                    onPassOrClear = { viewModel.humanPassOrClear() },
                                    onTakeTable = { viewModel.humanTakeTable() },
                                    onRequestAiAdvice = { viewModel.requestAiAdvice() },
                                    onApplyAiMove = { viewModel.applyRecommendedCard() },
                                    onOpenCustomizer = { currentScreen = AppScreen.CUSTOMIZER },
                                    onExitGame = { currentScreen = AppScreen.HOME_HUB }
                                )
                            }
                            AppScreen.MULTIPLAYER -> {
                                MultiplayerLobbyScreen(
                                    onBack = { currentScreen = AppScreen.HOME_HUB },
                                    onStartMatch = {
                                        viewModel.startNewGame(2, GameMode.PODKIDNOY, 36, listOf(BotDifficulty.HARD))
                                        currentScreen = AppScreen.DURAK_GAME
                                    }
                                )
                            }
                            AppScreen.CUSTOMIZER -> {
                                SettingsCustomizerScreen(
                                    currentThemeState = themeState,
                                    onSaveTheme = { palette, felt, cardBack ->
                                        viewModel.updateTheme(palette, felt, cardBack)
                                    },
                                    onBack = { currentScreen = AppScreen.HOME_HUB }
                                )
                            }
                            AppScreen.STATISTICS -> {
                                StatisticsScreen(
                                    stats = stats,
                                    matchHistory = history,
                                    onBack = { currentScreen = AppScreen.HOME_HUB }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
