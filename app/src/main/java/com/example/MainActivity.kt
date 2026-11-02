package com.example

import android.app.Activity
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.ui.screens.AboutAppScreen
import com.example.ui.screens.CardGameOnboardingDialog
import com.example.ui.screens.DesignCustomizationScreen
import com.example.ui.screens.DurakGameScreen
import com.example.ui.screens.HomeHubScreen
import com.example.ui.screens.SettingsCustomizerScreen
import com.example.ui.screens.StatisticsScreen
import com.example.cardgames.CardGameId
import com.example.ui.screens.CardGameSessionScreen
import com.example.ui.theme.AppVisualStyle
import com.example.ui.theme.CardGameTheme
import com.example.ui.theme.CustomPalette
import com.example.ui.theme.PaletteCatalog
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ThemeColorPreset
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemePreferences
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
            val savedThemeMode = remember {
                ThemePreferences.readMode(appearancePreferences).let { mode ->
                    if (mode == ThemeMode.SYSTEM) ThemeMode.LIGHT else mode
                }
            }
            val savedSystemTheme = remember { ThemePreferences.readSystemTheme(appearancePreferences) }
            val savedAmoledBlack = remember { ThemePreferences.readAmoledBlack(appearancePreferences) }
            val savedColorPreset = remember { ThemePreferences.readPreset(appearancePreferences) }
            val savedPalette = remember { ThemePreferences.readPalette(appearancePreferences) }
            val savedCustomPalette = remember { ThemePreferences.readCustom(appearancePreferences) }
            val savedGoogleSansFlex = remember { appearancePreferences.getBoolean(GOOGLE_SANS_FLEX_KEY, true) }
            val savedVisualStyle = remember { ThemePreferences.readVisualStyle(appearancePreferences) }
            val stats by viewModel.statsFlow.collectAsState()
            val history by viewModel.historyFlow.collectAsState()
            val gameState by viewModel.gameState.collectAsState()
            val hasRestoredMatch by viewModel.hasRestoredMatch.collectAsState()
            val selectedCard by viewModel.selectedCard.collectAsState()
            var destination by remember { mutableStateOf(RootDestination.PLAY) }
            var isInMatch by remember { mutableStateOf(false) }
            var activeCardGame by remember { mutableStateOf<CardGameId?>(null) }
            var activeCardGamePlayerCount by remember { mutableIntStateOf(2) }
            var settingsPage by remember { mutableStateOf(SettingsPage.DIRECTORY) }
            var navigationAppearance by remember { mutableStateOf(savedAppearance) }
            var themeMode by remember { mutableStateOf(savedThemeMode) }
            var systemTheme by remember { mutableStateOf(savedSystemTheme) }
            var amoledBlack by remember { mutableStateOf(savedAmoledBlack) }
            var themeColorPreset by remember { mutableStateOf(savedColorPreset) }
            var paletteOption by remember { mutableStateOf(savedPalette) }
            var customPalette by remember { mutableStateOf(savedCustomPalette) }
            var useGoogleSansFlex by remember { mutableStateOf(savedGoogleSansFlex) }
            var visualStyle by remember { mutableStateOf(savedVisualStyle) }
            var showExitMatchDialog by remember { mutableStateOf(false) }
            var showOnboarding by remember {
                mutableStateOf(!appearancePreferences.getBoolean(ONBOARDING_SEEN_KEY, false))
            }

            LaunchedEffect(hasRestoredMatch) {
                if (hasRestoredMatch) isInMatch = true
            }

            fun returnToLobby() {
                showExitMatchDialog = false
                viewModel.abandonMatch()
                isInMatch = false
                destination = RootDestination.PLAY
                settingsPage = SettingsPage.DIRECTORY
            }

            fun updateAppearance(appearance: NavigationAppearance) {
                navigationAppearance = appearance
                appearancePreferences.edit().putString(NAVIGATION_STYLE_KEY, appearance.name).apply()
            }

            fun updateThemeMode(mode: ThemeMode) {
                val manualMode = if (mode == ThemeMode.DARK) ThemeMode.DARK else ThemeMode.LIGHT
                themeMode = manualMode
                systemTheme = false
                if (manualMode == ThemeMode.LIGHT) amoledBlack = false
                ThemePreferences.writeMode(appearancePreferences, manualMode)
                ThemePreferences.writeSystemTheme(appearancePreferences, false)
                ThemePreferences.writeAmoledBlack(appearancePreferences, amoledBlack)
            }

            fun updateSystemTheme(enabled: Boolean) {
                systemTheme = enabled
                if (enabled) amoledBlack = false
                ThemePreferences.writeSystemTheme(appearancePreferences, enabled)
                ThemePreferences.writeAmoledBlack(appearancePreferences, amoledBlack)
            }

            fun updateAmoledBlack(enabled: Boolean) {
                amoledBlack = enabled
                if (enabled) {
                    themeMode = ThemeMode.DARK
                    systemTheme = false
                    ThemePreferences.writeMode(appearancePreferences, ThemeMode.DARK)
                    ThemePreferences.writeSystemTheme(appearancePreferences, false)
                }
                ThemePreferences.writeAmoledBlack(appearancePreferences, enabled)
            }

            fun updateColorPreset(preset: ThemeColorPreset) {
                themeColorPreset = preset
                paletteOption = null
                customPalette = null
                ThemePreferences.writePreset(appearancePreferences, preset)
            }

            fun updatePalette(option: PaletteOption) {
                paletteOption = option
                customPalette = null
                ThemePreferences.writePalette(appearancePreferences, option)
            }

            fun updateCustomPalette(palette: CustomPalette) {
                customPalette = palette
                paletteOption = null
                ThemePreferences.writeCustom(appearancePreferences, palette)
            }

            fun updateGoogleSansFlex(enabled: Boolean) {
                useGoogleSansFlex = enabled
                appearancePreferences.edit().putBoolean(GOOGLE_SANS_FLEX_KEY, enabled).apply()
            }

            fun updateVisualStyle(style: AppVisualStyle) {
                visualStyle = style
                ThemePreferences.writeVisualStyle(appearancePreferences, style)
            }

            BackHandler(enabled = isInMatch) {
                if (gameState.isGameOver) returnToLobby() else showExitMatchDialog = true
            }

            BackHandler(enabled = !isInMatch && (destination != RootDestination.PLAY || settingsPage != SettingsPage.DIRECTORY)) {
                if (destination == RootDestination.SETTINGS && settingsPage != SettingsPage.DIRECTORY) {
                    settingsPage = SettingsPage.DIRECTORY
                } else {
                    destination = RootDestination.PLAY
                    settingsPage = SettingsPage.DIRECTORY
                }
            }

            BackHandler(enabled = activeCardGame != null) {
                activeCardGame = null
            }

            CardGameTheme(
                visualStyle = visualStyle,
                themeMode = if (systemTheme) ThemeMode.SYSTEM else themeMode,
                colorPreset = themeColorPreset,
                paletteOption = paletteOption,
                customPalette = customPalette,
                amoledBlack = amoledBlack,
                useRobotoFlex = useGoogleSansFlex,
            ) {
                val view = LocalView.current
                val backgroundColor = MaterialTheme.colorScheme.background
                SideEffect {
                    val window = (view.context as? Activity)?.window ?: return@SideEffect
                    val lightBars = backgroundColor.luminance() > 0.5f
                    val controller = WindowCompat.getInsetsController(window, view)
                    controller.isAppearanceLightStatusBars = lightBars
                    controller.isAppearanceLightNavigationBars = lightBars
                }
                if (activeCardGame != null) {
                    CardGameSessionScreen(
                        gameId = activeCardGame!!,
                        playerCount = activeCardGamePlayerCount,
                        onBack = { activeCardGame = null }
                    )
                } else if (isInMatch) {
                    DurakGameScreen(
                        gameState = gameState,
                        selectedCard = selectedCard,
                        onSelectCard = viewModel::selectCard,
                        onPlayAttack = viewModel::playHumanAttack,
                        onPlayDefend = viewModel::playHumanDefend,
                        onTransferCard = viewModel::playHumanTransfer,
                        onFinishRound = viewModel::humanFinishRound,
                        onPassThrowIn = viewModel::humanPassThrowIn,
                        onTakeTable = viewModel::humanTakeTable,
                        onExitGame = {
                            if (gameState.isGameOver) returnToLobby() else showExitMatchDialog = true
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
                        AnimatedContent(
                            targetState = destination to settingsPage,
                            transitionSpec = {
                                val isBackNavigation =
                                    (initialState.first == RootDestination.SETTINGS &&
                                        targetState.first == RootDestination.SETTINGS &&
                                        initialState.second != SettingsPage.DIRECTORY &&
                                        targetState.second == SettingsPage.DIRECTORY) ||
                                        (targetState.first == RootDestination.PLAY && initialState.first != RootDestination.PLAY)
                                if (isBackNavigation) {
                                    (slideInHorizontally(
                                        animationSpec = tween(240, easing = FastOutSlowInEasing),
                                        initialOffsetX = { -it / 12 },
                                    ) + fadeIn(tween(220, easing = FastOutSlowInEasing))).togetherWith(
                                        slideOutHorizontally(
                                            animationSpec = tween(200, easing = FastOutSlowInEasing),
                                            targetOffsetX = { it / 12 },
                                        ) + fadeOut(tween(180))
                                    )
                                } else {
                                    fadeIn(tween(220, easing = FastOutSlowInEasing)).togetherWith(fadeOut(tween(160)))
                                }
                            },
                            label = "rootDestination"
                        ) { (currentDestination, currentSettingsPage) ->
                            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                                when (currentDestination) {
                                    RootDestination.PLAY -> HomeHubScreen(
                                        stats = stats,
                                        onStartDurak = { setup ->
                                            viewModel.startNewGame(setup)
                                            isInMatch = true
                                        },
                                        onStartCardGame = { id, count ->
                                            activeCardGame = id
                                            activeCardGamePlayerCount = count
                                        },
                                    )
                                    RootDestination.STATS -> StatisticsScreen(stats, history)
                                    RootDestination.SETTINGS -> when (currentSettingsPage) {
                                        SettingsPage.DIRECTORY -> SettingsCustomizerScreen(
                                            onOpenDesign = { settingsPage = SettingsPage.DESIGN },
                                            onOpenAbout = { settingsPage = SettingsPage.ABOUT }
                                        )
                                        SettingsPage.DESIGN -> DesignCustomizationScreen(
                                            currentMode = themeMode,
                                            systemTheme = systemTheme,
                                            amoledBlack = amoledBlack,
                                            currentPalette = paletteOption ?: PaletteCatalog.featured.first(),
                                            currentCustom = customPalette,
                                            currentAppearance = navigationAppearance,
                                            currentVisualStyle = visualStyle,
                                            useGoogleSansFlex = useGoogleSansFlex,
                                            onModeChange = ::updateThemeMode,
                                            onSystemThemeChange = ::updateSystemTheme,
                                            onAmoledBlackChange = ::updateAmoledBlack,
                                            onPaletteChange = ::updatePalette,
                                            onCustomApply = ::updateCustomPalette,
                                            onAppearanceChange = ::updateAppearance,
                                            onVisualStyleChange = ::updateVisualStyle,
                                            onGoogleSansFlexChange = ::updateGoogleSansFlex,
                                            onBack = { settingsPage = SettingsPage.DIRECTORY },
                                        )
                                        SettingsPage.ABOUT -> AboutAppScreen(onBack = { settingsPage = SettingsPage.DIRECTORY })
                                    }
                                }
                            }
                        }
                    }
                }

            if (showOnboarding && !isInMatch && activeCardGame == null) {
                    CardGameOnboardingDialog(
                        initialMode = themeMode,
                        initialSystemTheme = systemTheme,
                        initialAmoledBlack = amoledBlack,
                        initialPalette = paletteOption ?: PaletteCatalog.featured.first(),
                        onModeChange = ::updateThemeMode,
                        onSystemThemeChange = ::updateSystemTheme,
                        onAmoledBlackChange = ::updateAmoledBlack,
                        onPaletteChange = ::updatePalette,
                        onFinish = {
                            appearancePreferences.edit().putBoolean(ONBOARDING_SEEN_KEY, true).apply()
                            showOnboarding = false
                        },
                    )
                }
                if (showExitMatchDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitMatchDialog = false },
                        title = { Text("Leave this match?") },
                        text = {
                            Text("Your current table will be abandoned and will not be added to statistics.")
                        },
                        confirmButton = {
                            TextButton(onClick = ::returnToLobby) { Text("Leave match") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showExitMatchDialog = false }) { Text("Keep playing") }
                        },
                    )
                }
            }
        }
    }

    private companion object {
        const val NAVIGATION_PREFERENCES = "navigation_preferences"
        const val NAVIGATION_STYLE_KEY = "navigation_style_v2"
        const val ONBOARDING_SEEN_KEY = "onboarding_seen_v2"
        const val GOOGLE_SANS_FLEX_KEY = "google_sans_flex"
    }
}

@Composable
private fun AppRootScaffold(
    destination: RootDestination,
    appearance: NavigationAppearance,
    onDestinationChange: (RootDestination) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!useNavigationRail) {
                    Material3ExpressiveNavigationBar(destination, appearance, onDestinationChange)
                }
            },
            content = { innerPadding ->
                if (useNavigationRail) {
                    Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Material3ExpressiveNavigationRail(destination, appearance, onDestinationChange)
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 960.dp)
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                            ) {
                                content(PaddingValues(0.dp))
                            }
                        }
                    }
                } else {
                    content(innerPadding)
                }
            },
        )
    }
}

@Composable
private fun Material3ExpressiveNavigationRail(
    destination: RootDestination,
    appearance: NavigationAppearance,
    onDestinationChange: (RootDestination) -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        windowInsets = NavigationRailDefaults.windowInsets,
    ) {
        RootDestination.entries.forEach { item ->
            NavigationRailItem(
                selected = destination == item,
                onClick = { onDestinationChange(item) },
                icon = { Icon(imageVector = iconFor(item), contentDescription = item.label) },
                label = if (appearance == NavigationAppearance.STANDARD) ({ Text(item.label) }) else null,
                alwaysShowLabel = appearance == NavigationAppearance.STANDARD,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun Material3ExpressiveNavigationBar(
    destination: RootDestination,
    appearance: NavigationAppearance,
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
                label = if (appearance == NavigationAppearance.STANDARD) ({ Text(item.label) }) else null,
                alwaysShowLabel = appearance == NavigationAppearance.STANDARD,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun iconFor(item: RootDestination): ImageVector = when (item) {
    RootDestination.PLAY -> Icons.Default.Style
    RootDestination.STATS -> Icons.Default.BarChart
    RootDestination.SETTINGS -> Icons.Default.Settings
}
