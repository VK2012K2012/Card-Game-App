package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.DurakGameScreen
import com.example.ui.screens.HomeHubScreen
import com.example.ui.screens.SettingsCustomizerScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.CardGameTheme
import com.example.ui.viewmodel.GameViewModel

enum class AppScreen {
    HOME_HUB,
    DURAK_GAME,
    SETTINGS,
    STATISTICS
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val stats by viewModel.statsFlow.collectAsState()
            val history by viewModel.historyFlow.collectAsState()
            val gameState by viewModel.gameState.collectAsState()
            val selectedCard by viewModel.selectedCard.collectAsState()
            var currentScreen by remember { mutableStateOf(AppScreen.HOME_HUB) }

            CardGameTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                        when (screen) {
                            AppScreen.HOME_HUB -> HomeHubScreen(
                                stats = stats,
                                onStartDurak = { setup ->
                                    viewModel.startNewGame(setup)
                                    currentScreen = AppScreen.DURAK_GAME
                                },
                                onOpenSettings = { currentScreen = AppScreen.SETTINGS },
                                onOpenStats = { currentScreen = AppScreen.STATISTICS }
                            )

                            AppScreen.DURAK_GAME -> DurakGameScreen(
                                gameState = gameState,
                                selectedCard = selectedCard,
                                onSelectCard = viewModel::selectCard,
                                onPlayAttack = viewModel::playHumanAttack,
                                onPlayDefend = viewModel::playHumanDefend,
                                onFinishRound = viewModel::humanFinishRound,
                                onTakeTable = viewModel::humanTakeTable,
                                onExitGame = {
                                    viewModel.abandonMatch()
                                    currentScreen = AppScreen.HOME_HUB
                                }
                            )

                            AppScreen.SETTINGS -> SettingsCustomizerScreen(
                                onBack = { currentScreen = AppScreen.HOME_HUB }
                            )

                            AppScreen.STATISTICS -> StatisticsScreen(
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
