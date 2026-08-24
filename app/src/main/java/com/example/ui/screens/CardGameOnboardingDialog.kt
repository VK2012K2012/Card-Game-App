package com.example.ui.screens

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Build

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import com.example.ui.theme.PaletteCatalog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.palettePreviewTones
import com.example.ui.theme.previewOnColor

@Composable
fun CardGameOnboardingDialog(
    initialMode: ThemeMode,
    initialSystemTheme: Boolean,
    initialAmoledBlack: Boolean,
    initialPalette: PaletteOption,
    onModeChange: (ThemeMode) -> Unit,
    onSystemThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onPaletteChange: (PaletteOption) -> Unit,
    onFinish: () -> Unit,
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedMode by remember {
        mutableStateOf(if (initialMode == ThemeMode.DARK) ThemeMode.DARK else ThemeMode.LIGHT)
    }
    var selectedSystemTheme by remember { mutableStateOf(initialSystemTheme) }
    var selectedAmoledBlack by remember { mutableStateOf(initialAmoledBlack) }
    var selectedPalette by remember { mutableStateOf(initialPalette) }

    fun setMode(mode: ThemeMode) {
        selectedMode = mode
        selectedSystemTheme = false
        if (mode == ThemeMode.LIGHT) selectedAmoledBlack = false
        onModeChange(mode)
        onSystemThemeChange(false)
    }

    fun setSystemTheme(enabled: Boolean) {
        selectedSystemTheme = enabled
        if (enabled) selectedAmoledBlack = false
        onSystemThemeChange(enabled)
        if (enabled) onAmoledBlackChange(false)
    }

    fun setAmoledBlack(enabled: Boolean) {
        selectedAmoledBlack = enabled
        if (enabled) {
            selectedMode = ThemeMode.DARK
            selectedSystemTheme = false
            onModeChange(ThemeMode.DARK)
            onSystemThemeChange(false)
        }
        onAmoledBlackChange(enabled)
    }

    fun setPalette(palette: PaletteOption) {
        selectedPalette = palette
        onPaletteChange(palette)
    }

    BackHandler(enabled = step > 0) { step = 0 }

    Dialog(
        onDismissRequest = onFinish,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val dialogView = LocalView.current
        val dialogWindow = (dialogView.parent as? DialogWindowProvider)?.window
        val dialogBackground = MaterialTheme.colorScheme.background
        DisposableEffect(dialogWindow, dialogBackground) {
            dialogWindow?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
                window.statusBarColor = AndroidColor.TRANSPARENT
                window.navigationBarColor = AndroidColor.TRANSPARENT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.navigationBarDividerColor = AndroidColor.TRANSPARENT
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                val controller = WindowCompat.getInsetsController(window, dialogView)
                val lightBars = dialogBackground.luminance() > 0.5f
                controller.isAppearanceLightStatusBars = lightBars
                controller.isAppearanceLightNavigationBars = lightBars
            }
            onDispose { }
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "CARD GAME HUB",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = "${step + 1} of 2",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                AnimatedContent(
                    targetState = step,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        val forward = targetState > initialState
                        (slideInHorizontally(
                            initialOffsetX = { width -> if (forward) width / 4 else -width / 4 },
                            animationSpec = tween(160),
                        ) + fadeIn(tween(120))).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> if (forward) -width / 4 else width / 4 },
                                animationSpec = tween(130),
                            ) + fadeOut(tween(90)),
                        )
                    },
                    label = "onboarding-step",
                ) { currentStep ->
                    when (currentStep) {
                        0 -> WelcomeStep()
                        else -> CustomizeStep(
                            selectedMode = selectedMode,
                            selectedPalette = selectedPalette,
                            onModeChange = ::setMode,
                            systemTheme = selectedSystemTheme,
                            amoledBlack = selectedAmoledBlack,
                            onSystemThemeChange = ::setSystemTheme,
                            onAmoledBlackChange = ::setAmoledBlack,
                            onPaletteChange = ::setPalette,
                        )
                    }
                }

                OnboardingActions(
                    step = step,
                    onBack = { step = 0 },
                    onContinue = { if (step == 0) step = 1 else onFinish() },
                )
            }
        }
    }
}

@Composable
private fun OnboardingActions(
    step: Int,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val continueInteractions = remember { MutableInteractionSource() }
    val backInteractions = remember { MutableInteractionSource() }
    val continuePressed by continueInteractions.collectIsPressedAsState()
    val backPressed by backInteractions.collectIsPressedAsState()
    var clickPulse by remember { mutableStateOf(false) }
    val groupActive = continuePressed || backPressed || clickPulse
    val groupScale by animateFloatAsState(
        targetValue = if (groupActive) 0.965f else 1f,
        animationSpec = tween(170, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "onboarding-group-scale",
    )
    val outerCorner by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (groupActive) 32.dp else 38.dp,
        animationSpec = tween(170, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "onboarding-group-outer-corner",
    )
    val innerCorner by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (groupActive) 12.dp else 16.dp,
        animationSpec = tween(170, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "onboarding-group-inner-corner",
    )
    val scope = rememberCoroutineScope()

    fun trigger(action: () -> Unit) {
        clickPulse = true
        scope.launch {
            delay(190)
            clickPulse = false
        }
        action()
    }

    Row(
        modifier = Modifier.fillMaxWidth().height(76.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (step > 0) {
            Button(
                modifier = Modifier
                    .weight(0.82f)
                    .fillMaxHeight()
                    .graphicsLayer { scaleX = groupScale; scaleY = groupScale },
                onClick = { trigger(onBack) },
                shape = RoundedCornerShape(
                    topStart = outerCorner,
                    bottomStart = outerCorner,
                    topEnd = innerCorner,
                    bottomEnd = innerCorner,
                ),
                colors = androidx.compose.material3.ButtonDefaults.filledTonalButtonColors(),
                contentPadding = PaddingValues(0.dp),
                interactionSource = backInteractions,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Button(
            modifier = Modifier
                .weight(if (step > 0) 2f else 1f)
                .fillMaxHeight()
                .graphicsLayer { scaleX = groupScale; scaleY = groupScale },
            onClick = { trigger(onContinue) },
            shape = if (step > 0) {
                RoundedCornerShape(
                    topStart = innerCorner,
                    bottomStart = innerCorner,
                    topEnd = outerCorner,
                    bottomEnd = outerCorner,
                )
            } else {
                RoundedCornerShape(outerCorner)
            },
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
            interactionSource = continueInteractions,
        ) {
            Text(if (step == 0) "Continue" else "Start playing")
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WelcomeHero()
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Welcome to Card Game Hub",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "A clean local card table, ready to play.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun WelcomeHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .size(width = 176.dp, height = 236.dp)
                .clip(RoundedCornerShape(30.dp)),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 2.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "♠",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Surface(
            modifier = Modifier
                .size(width = 176.dp, height = 236.dp)
                .clip(RoundedCornerShape(30.dp)),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("A", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = "♥",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text("A", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
private fun CustomizeStep(
    selectedMode: ThemeMode,
    systemTheme: Boolean,
    amoledBlack: Boolean,
    selectedPalette: PaletteOption,
    onModeChange: (ThemeMode) -> Unit,
    onSystemThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onPaletteChange: (PaletteOption) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Choose your look", style = MaterialTheme.typography.headlineMedium)
            Text("You can change this later in Settings.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SegmentedListItem(
            onClick = { onSystemThemeChange(!systemTheme) },
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            supportingContent = { Text("Follow your phone appearance and colors") },
            trailingContent = { Switch(checked = systemTheme, onCheckedChange = null) },
            content = { Text("Adaptive theme") },
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            val modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK)
            modes.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = !systemTheme && selectedMode == mode,
                    onClick = { onModeChange(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                ) { Text(mode.title) }
            }
        }
        SegmentedListItem(
            onClick = { onAmoledBlackChange(!amoledBlack) },
            shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            supportingContent = { Text("Use a true black background") },
            trailingContent = { Switch(checked = amoledBlack, onCheckedChange = onAmoledBlackChange) },
            content = { Text("AMOLED black") },
        )
        if (!systemTheme) {
            Text("Palette", style = MaterialTheme.typography.titleMedium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(170.dp),
                contentPadding = PaddingValues(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(PaletteCatalog.all, key = { it.id }) { option ->
                    OnboardingPaletteTile(
                        option = option,
                        selected = selectedPalette.id == option.id,
                        onClick = { onPaletteChange(option) },
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPaletteTile(
    option: PaletteOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(48.dp)) {
                val tones = palettePreviewTones(option)
                val sweep = 360f / tones.size
                tones.forEachIndexed { index, color ->
                    drawArc(
                        color = color,
                        startAngle = index * sweep - 90f,
                        sweepAngle = sweep,
                        useCenter = true,
                    )
                }
            }
            if (selected) {
                Surface(shape = CircleShape, color = previewOnColor(option.seed)) {
                    Icon(Icons.Default.Check, contentDescription = "Selected", modifier = Modifier.padding(7.dp))
                }
            }
        }
    }
}
