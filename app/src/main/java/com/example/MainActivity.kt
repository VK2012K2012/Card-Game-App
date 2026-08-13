package com.example

import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AboutAppScreen
import com.example.ui.screens.DesignCustomizationScreen
import com.example.ui.screens.DurakGameScreen
import com.example.ui.screens.HomeHubScreen
import com.example.ui.screens.SettingsCustomizerScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.CardGameTheme
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.viewmodel.GameViewModel

enum class RootDestination(val label: String) {
    PLAY("Play"),
    STATS("Stats"),
    SETTINGS("Settings")
}

enum class NavigationAppearance {
    STANDARD,
    COMPACT
}

private enum class SettingsPage {
    DIRECTORY,
    DESIGN,
    ABOUT
}

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced = false

        setContent {
            val context = LocalContext.current
            val appearancePreferences = remember(context) {
                context.getSharedPreferences(NAVIGATION_PREFERENCES, Context.MODE_PRIVATE)
            }
            val savedAppearance = remember {
                appearancePreferences.getString(NAVIGATION_STYLE_KEY, NavigationAppearance.STANDARD.name)
                    ?.let { runCatching { NavigationAppearance.valueOf(it) }.getOrNull() }
                    ?: NavigationAppearance.STANDARD
            }
            val stats by viewModel.statsFlow.collectAsState()
            val history by viewModel.historyFlow.collectAsState()
            val gameState by viewModel.gameState.collectAsState()
            val selectedCard by viewModel.selectedCard.collectAsState()
            var destination by remember { mutableStateOf(RootDestination.PLAY) }
            var isInMatch by remember { mutableStateOf(false) }
            var settingsPage by remember { mutableStateOf(SettingsPage.DIRECTORY) }
            var navigationAppearance by remember { mutableStateOf(savedAppearance) }

            fun updateAppearance(appearance: NavigationAppearance) {
                navigationAppearance = appearance
                appearancePreferences.edit().putString(NAVIGATION_STYLE_KEY, appearance.name).apply()
            }

            CardGameTheme {
                if (isInMatch) {
                    DurakGameScreen(
                        gameState = gameState,
                        selectedCard = selectedCard,
                        onSelectCard = viewModel::selectCard,
                        onPlayAttack = viewModel::playHumanAttack,
                        onPlayDefend = viewModel::playHumanDefend,
                        onFinishRound = viewModel::humanFinishRound,
                        onPassThrowIn = viewModel::humanPassThrowIn,
                        onTakeTable = viewModel::humanTakeTable,
                        onExitGame = {
                            viewModel.abandonMatch()
                            isInMatch = false
                            destination = RootDestination.PLAY
                        }
                    )
                } else {
                    AppRootScaffold(
                        destination = destination,
                        appearance = navigationAppearance,
                        onDestinationChange = {
                            destination = it
                            if (it != RootDestination.SETTINGS) settingsPage = SettingsPage.DIRECTORY
                        }
                    ) { contentPadding ->
                        Crossfade(targetState = destination to settingsPage, label = "rootDestination") { (currentDestination, currentSettingsPage) ->
                            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                                when (currentDestination) {
                                    RootDestination.PLAY -> HomeHubScreen(stats, onStartDurak = {
                                        viewModel.startNewGame(it)
                                        isInMatch = true
                                    })
                                    RootDestination.STATS -> StatisticsScreen(stats, history)
                                    RootDestination.SETTINGS -> when (currentSettingsPage) {
                                        SettingsPage.DIRECTORY -> SettingsCustomizerScreen(
                                            onOpenDesign = { settingsPage = SettingsPage.DESIGN },
                                            onOpenAbout = { settingsPage = SettingsPage.ABOUT }
                                        )
                                        SettingsPage.DESIGN -> DesignCustomizationScreen(
                                            currentAppearance = navigationAppearance,
                                            onAppearanceChange = ::updateAppearance,
                                            onBack = { settingsPage = SettingsPage.DIRECTORY }
                                        )
                                        SettingsPage.ABOUT -> AboutAppScreen(onBack = { settingsPage = SettingsPage.DIRECTORY })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val NAVIGATION_PREFERENCES = "navigation_preferences"
        const val NAVIGATION_STYLE_KEY = "navigation_style"
    }
}

@Composable
private fun AppRootScaffold(
    destination: RootDestination,
    appearance: NavigationAppearance,
    onDestinationChange: (RootDestination) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            when (appearance) {
                NavigationAppearance.STANDARD -> StandardNavigationBar(destination, onDestinationChange)
                NavigationAppearance.COMPACT -> CompactNavigationDock(destination, onDestinationChange)
            }
        },
        content = content
    )
}

@Composable
private fun StandardNavigationBar(
    destination: RootDestination,
    onDestinationChange: (RootDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.98f))
            .navigationBarsPadding()
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StandardDestination(RootDestination.PLAY, Icons.Default.PlayArrow, destination, onDestinationChange)
            StandardDestination(RootDestination.STATS, Icons.Default.BarChart, destination, onDestinationChange)
            StandardDestination(RootDestination.SETTINGS, Icons.Default.Settings, destination, onDestinationChange)
        }
    }
}

@Composable
private fun StandardDestination(
    item: RootDestination,
    icon: ImageVector,
    destination: RootDestination,
    onDestinationChange: (RootDestination) -> Unit
) {
    val selected = destination == item
    val corner by animateDpAsState(
        targetValue = if (selected) 22.dp else 16.dp,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "standardNavCorner"
    )
    Column(
        modifier = Modifier
            .width(94.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(corner))
            .clickable { onDestinationChange(item) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
    ) {
        Surface(
            modifier = Modifier.size(width = 48.dp, height = 28.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(22.dp),
                    tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.sp
        )
    }
}

@Composable
private fun CompactNavigationDock(
    destination: RootDestination,
    onDestinationChange: (RootDestination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactDestination(RootDestination.PLAY, Icons.Default.PlayArrow, destination, onDestinationChange)
            CompactDestination(RootDestination.STATS, Icons.Default.BarChart, destination, onDestinationChange)
            CompactDestination(RootDestination.SETTINGS, Icons.Default.Settings, destination, onDestinationChange)
        }
    }
}

@Composable
private fun CompactDestination(
    item: RootDestination,
    icon: ImageVector,
    destination: RootDestination,
    onDestinationChange: (RootDestination) -> Unit
) {
    val selected = destination == item
    val corner by animateDpAsState(
        targetValue = if (selected) 24.dp else 20.dp,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "compactNavCorner"
    )
    Box(
        modifier = Modifier
            .size(width = 76.dp, height = 48.dp)
            .clip(RoundedCornerShape(corner))
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background)
            .clickable { onDestinationChange(item) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = item.label,
            modifier = Modifier.size(28.dp),
            tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
