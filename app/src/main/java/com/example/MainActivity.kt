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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
import com.example.ui.screens.AboutAppScreen
import com.example.ui.screens.CardGameOnboardingDialog
import com.example.ui.screens.DesignCustomizationScreen
import com.example.ui.screens.DurakGameScreen
import com.example.ui.screens.HomeHubScreen
import com.example.ui.screens.SettingsCustomizerScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.CardGameTheme
import com.example.ui.theme.CustomPalette
import com.example.ui.theme.PaletteCatalog
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ThemeColorPreset
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.ThemePreferences
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.viewmodel.GameViewModel
import com.example.update.GitHubUpdateChecker

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
            val stats by viewModel.statsFlow.collectAsState()
            val history by viewModel.historyFlow.collectAsState()
            val gameState by viewModel.gameState.collectAsState()
            val selectedCard by viewModel.selectedCard.collectAsState()
            var destination by remember { mutableStateOf(RootDestination.PLAY) }
            var isInMatch by remember { mutableStateOf(false) }
            var settingsPage by remember { mutableStateOf(SettingsPage.DIRECTORY) }
            var navigationAppearance by remember { mutableStateOf(savedAppearance) }
            var themeMode by remember { mutableStateOf(savedThemeMode) }
            var systemTheme by remember { mutableStateOf(savedSystemTheme) }
            var amoledBlack by remember { mutableStateOf(savedAmoledBlack) }
            var themeColorPreset by remember { mutableStateOf(savedColorPreset) }
            var paletteOption by remember { mutableStateOf(savedPalette) }
            var customPalette by remember { mutableStateOf(savedCustomPalette) }
            var useGoogleSansFlex by remember { mutableStateOf(savedGoogleSansFlex) }
            var showExitMatchDialog by remember { mutableStateOf(false) }
            var showOnboarding by remember {
                mutableStateOf(!appearancePreferences.getBoolean(ONBOARDING_SEEN_KEY, false))
            }
            var startupCheckStarted by remember { mutableStateOf(false) }
            val startupSnackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(showOnboarding) {
                if (showOnboarding || startupCheckStarted) return@LaunchedEffect
                startupCheckStarted = true
                runCatching { GitHubUpdateChecker.fetchLatestRelease() }
                    .onSuccess { release ->
                        if (GitHubUpdateChecker.isNewerVersion(release.tagName, BuildConfig.VERSION_NAME)) {
                            val result = startupSnackbarHostState.showSnackbar(
                                message = "Update ${release.tagName} is available.",
                                actionLabel = "Open About",
                                withDismissAction = true,
                                duration = SnackbarDuration.Long,
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                destination = RootDestination.SETTINGS
                                settingsPage = SettingsPage.ABOUT
                            }
                        }
                    }
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

            CardGameTheme(
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
                            if (gameState.isGameOver) returnToLobby() else showExitMatchDialog = true
                        }

                    )
                } else {
                    AppRootScaffold(
                        destination = destination,
                        appearance = navigationAppearance,
                        snackbarHostState = startupSnackbarHostState,
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
                                            currentMode = themeMode,
                                            systemTheme = systemTheme,
                                            amoledBlack = amoledBlack,
                                            currentPalette = paletteOption ?: PaletteCatalog.featured.first(),
                                            currentCustom = customPalette,
                                            currentAppearance = navigationAppearance,
                                            useGoogleSansFlex = useGoogleSansFlex,
                                            onModeChange = ::updateThemeMode,
                                            onSystemThemeChange = ::updateSystemTheme,
                                            onAmoledBlackChange = ::updateAmoledBlack,
                                            onPaletteChange = ::updatePalette,
                                            onCustomApply = ::updateCustomPalette,
                                            onAppearanceChange = ::updateAppearance,
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

            if (showOnboarding && !isInMatch) {
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
                Dialog(onDismissRequest = { showExitMatchDialog = false }) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(180)) +
                            androidx.compose.animation.scaleIn(
                                initialScale = 0.92f,
                                animationSpec = spring(
                                    dampingRatio = 0.82f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) +
                            androidx.compose.animation.slideInVertically(
                                initialOffsetY = { it / 5 },
                                animationSpec = spring(
                                    dampingRatio = 0.86f,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .widthIn(max = 560.dp),
                            shape = RoundedCornerShape(28.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 6.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                Text(
                                    text = "Leave this match?",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Your current table will be abandoned and will not be added to statistics.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = { showExitMatchDialog = false },
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text("Keep playing")
                                    }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = ::returnToLobby,
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        Text("Leave match")
                                    }
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
        const val NAVIGATION_STYLE_KEY = "navigation_style_v2"
        const val ONBOARDING_SEEN_KEY = "onboarding_seen_v2"
        const val GOOGLE_SANS_FLEX_KEY = "google_sans_flex"
    }
}

@Composable
private fun AppRootScaffold(
    destination: RootDestination,
    appearance: NavigationAppearance,
    snackbarHostState: SnackbarHostState,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = content
    )
}

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
