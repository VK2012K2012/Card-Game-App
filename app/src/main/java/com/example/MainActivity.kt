package com.example

import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
            var showExitMatchDialog by remember { mutableStateOf(false) }

            fun updateAppearance(appearance: NavigationAppearance) {
                navigationAppearance = appearance
                appearancePreferences.edit().putString(NAVIGATION_STYLE_KEY, appearance.name).apply()
            }

            BackHandler(enabled = isInMatch) {
                showExitMatchDialog = true
            }

            BackHandler(enabled = !isInMatch && (destination != RootDestination.PLAY || settingsPage != SettingsPage.DIRECTORY)) {
                if (destination == RootDestination.SETTINGS && settingsPage != SettingsPage.DIRECTORY) {
                    settingsPage = SettingsPage.DIRECTORY
                } else {
                    destination = RootDestination.PLAY
                    settingsPage = SettingsPage.DIRECTORY
                }
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
                        onExitGame = { showExitMatchDialog = true }

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
                        AnimatedContent(
                            targetState = destination to settingsPage,
                            transitionSpec = { fadeIn(tween(220, easing = FastOutSlowInEasing)).togetherWith(fadeOut(tween(160))) },
                            label = "rootDestination"
                        ) { (currentDestination, currentSettingsPage) ->
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

            if (showExitMatchDialog) {
                AlertDialog(
                    onDismissRequest = { showExitMatchDialog = false },
                    title = { Text("Leave this match?") },
                    text = { Text("Your current table will be abandoned and will not be added to statistics.") },
                    dismissButton = {
                        Button(onClick = { showExitMatchDialog = false }) { Text("Keep playing") }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitMatchDialog = false
                                viewModel.abandonMatch()
                                isInMatch = false
                                destination = RootDestination.PLAY
                            }
                        ) { Text("Leave match") }
                    }
                )
            }
        }
    }

    private companion object {
        const val NAVIGATION_PREFERENCES = "navigation_preferences"
        const val NAVIGATION_STYLE_KEY = "navigation_style_v2"
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
                NavigationAppearance.STANDARD -> Material3ExpressiveNavigationBar(destination, onDestinationChange)
                NavigationAppearance.COMPACT -> CompactNavigationDock(destination, onDestinationChange)
            }
        },
        content = content
    )
}

/**
 * The standard option deliberately uses the framework Material 3 navigation components.
 * NavigationBarDefaults.windowInsets owns system navigation-bar padding, avoiding a custom
 * wrapper or fixed height that would make the bar taller than the Material specification.
 */
@Composable
private fun Material3ExpressiveNavigationBar(
    destination: RootDestination,
    onDestinationChange: (RootDestination) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        RootDestination.entries.forEach { item ->
            NavigationBarItem(
                selected = destination == item,
                onClick = { onDestinationChange(item) },
                icon = {
                    Icon(
                        imageVector = iconFor(item),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun iconFor(item: RootDestination): ImageVector = when (item) {
    RootDestination.PLAY -> Icons.Default.PlayArrow
    RootDestination.STATS -> Icons.Default.BarChart
    RootDestination.SETTINGS -> Icons.Default.Settings
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
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedWidth by animateDpAsState(
        targetValue = if (selected) 84.dp else 72.dp,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "compactSelectionWidth"
    )
    val animatedContainer by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "compactSelectionColor"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "compactPressScale"
    )
    Box(
        modifier = Modifier
            .size(width = animatedWidth, height = 48.dp)
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .clip(ExpressiveCorners.Full)
            .background(animatedContainer)
            .clickable(interactionSource = interactionSource, indication = null) { onDestinationChange(item) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = item.label,
            modifier = Modifier.size(28.dp),
            tint = animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                label = "compactIconColor"
            ).value
        )
    }
}
