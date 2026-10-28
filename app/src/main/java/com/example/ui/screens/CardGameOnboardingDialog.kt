@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.ui.screens

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.ui.components.BlurIntensity
import com.example.ui.components.EnhancedTableFeltBackground
import com.example.ui.components.blurEffect
import com.example.ui.components.frostedGlass
import com.example.ui.theme.AppShapes
import com.example.ui.theme.MotionTokens
import com.example.ui.theme.PaletteCatalog
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ShapeTokens
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.palettePreviewTones
import com.example.ui.theme.previewOnColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/**
 * Card Game Onboarding Dialog
 * M3E Compliant with animated hero, blur effects, and proper transitions
 */
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
    var selectedMode by remember { mutableStateOf(initialMode) }
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

        EnhancedTableFeltBackground(
            modifier = Modifier
                .fillMaxSize()
                .blurEffect(BlurIntensity.MEDIUM)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Header
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
                    AnimatedContent(
                        targetState = step,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width / 2 } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width / 2 } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width / 2 } + fadeOut()
                                )
                            }
                        },
                        label = "onboardingStepCounter"
                    ) { currentStep ->
                        Text(
                            text = "${currentStep + 1} of 2",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }

                // Content
                AnimatedContent(
                    targetState = step,
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        val forward = targetState > initialState
                        (slideInHorizontally(
                            initialOffsetX = { width -> if (forward) width / 4 else -width / 4 },
                            animationSpec = tween(MotionTokens.Natural),
                        ) + fadeIn(tween(MotionTokens.Quick))).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> if (forward) -width / 4 else width / 4 },
                                animationSpec = tween(MotionTokens.Quick),
                            ) + fadeOut(tween(MotionTokens.Responsive)),
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

                // Actions
                OnboardingActions(
                    step = step,
                    onBack = { step = 0 },
                    onContinue = { if (step == 0) step = 1 else onFinish() },
                )
            }
        }
    }
}

/**
 * Onboarding Actions with animated buttons
 */
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
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "onboarding-group-scale",
    )
    
    val outerCorner by animateFloatAsState(
        targetValue = if (groupActive) 32f else 38f,
        animationSpec = tween(MotionTokens.Responsive, easing = FastOutSlowInEasing),
        label = "onboarding-group-outer-corner",
    )
    
    val innerCorner by animateFloatAsState(
        targetValue = if (groupActive) 12f else 16f,
        animationSpec = tween(MotionTokens.Responsive, easing = FastOutSlowInEasing),
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

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(outerCorner.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 4.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { 
                    scaleX = groupScale
                    scaleY = groupScale 
                },
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (step > 0) {
                Button(
                    modifier = Modifier
                        .weight(0.82f)
                        .fillMaxHeight(),
                    onClick = { trigger(onBack) },
                    shape = RoundedCornerShape(
                        topStart = outerCorner.dp,
                        bottomStart = outerCorner.dp,
                        topEnd = innerCorner.dp,
                        bottomEnd = innerCorner.dp,
                    ),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    contentPadding = PaddingValues(0.dp),
                    interactionSource = backInteractions,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back"
                    )
                }
            }
            Button(
                modifier = Modifier
                    .weight(if (step > 0) 2f else 1f)
                    .fillMaxHeight(),
                onClick = { trigger(onContinue) },
                shape = if (step > 0) {
                    RoundedCornerShape(
                        topStart = innerCorner.dp,
                        bottomStart = innerCorner.dp,
                        topEnd = outerCorner.dp,
                        bottomEnd = outerCorner.dp,
                    )
                } else {
                    RoundedCornerShape(outerCorner.dp)
                },
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
                interactionSource = continueInteractions,
            ) {
                Text(if (step == 0) "Continue" else "Start playing")
            }
        }
    }
}

/**
 * Welcome Step with animated hero cards
 */
@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        WelcomeHero()
        Spacer(Modifier.height(28.dp))
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { height -> height / 4 }
            ) + fadeIn(tween(MotionTokens.Natural)),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Welcome to Card Game Hub",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Text(
                    text = "A clean local card table, ready to play.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

/**
 * Welcome Hero with animated card stack
 */
@Composable
private fun WelcomeHero() {
    val scope = rememberCoroutineScope()
    var animationPhase by remember { mutableStateOf(0) }
    
    // Trigger animation sequence
    LaunchedEffect(Unit) {
        delay(200)
        animationPhase = 1
        delay(300)
        animationPhase = 2
    }
    
    val card1Scale by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "heroCard1Scale"
    )
    
    val card2Scale by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = MotionTokens.Responsive,
            delayMillis = 100,
            easing = FastOutSlowInEasing,
        ),
        label = "heroCard2Scale"
    )
    
    val card1OffsetY by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 0f else 20f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "heroCard1Offset"
    )
    
    val card2OffsetY by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 0f else 20f,
        animationSpec = tween(
            durationMillis = MotionTokens.Responsive,
            delayMillis = 100,
            easing = FastOutSlowInEasing,
        ),
        label = "heroCard2Offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Background card (Spade)
        Surface(
            modifier = Modifier
                .size(width = 176.dp, height = 236.dp)
                .graphicsLayer {
                    scaleX = card1Scale
                    scaleY = card1Scale
                    translationY = card1OffsetY
                }
                .clip(RoundedCornerShape(ShapeTokens.HeroCorner)),
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.frostedGlass(
                    blurRadius = 8f,
                    gradientColors = listOf(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    )
                )
            ) {
                Text(
                    text = "\u2660",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        
        // Foreground card (Heart)
        Surface(
            modifier = Modifier
                .size(width = 176.dp, height = 236.dp)
                .graphicsLayer {
                    scaleX = card2Scale
                    scaleY = card2Scale
                    translationY = card2OffsetY
                }
                .clip(RoundedCornerShape(ShapeTokens.HeroCorner)),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .frostedGlass(
                        blurRadius = 12f,
                        gradientColors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    ),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "A", 
                    style = MaterialTheme.typography.headlineMedium, 
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "\u2665",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    "A", 
                    style = MaterialTheme.typography.headlineMedium, 
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/**
 * Customize Step with theme selection
 */
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
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(MotionTokens.Natural)) + 
                slideInVertically(
                    initialOffsetY = { height -> height / 8 }
                ),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Choose your look",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    "You can change this later in Settings.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Adaptive theme toggle
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(MotionTokens.Responsive, delayMillis = 100)),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.settingsCard,
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSystemThemeChange(!systemTheme) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("Adaptive theme")
                        Text(
                            "Follow your phone appearance and colors",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = systemTheme,
                        onCheckedChange = null
                    )
                }
            }
        }
        
        // Theme mode selection
        if (!systemTheme) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(MotionTokens.Responsive, delayMillis = 150)),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.settingsCard,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "App theme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK)
                            modes.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = selectedMode == mode,
                                    onClick = { onModeChange(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(index, modes.size),
                                ) { 
                                    Text(
                                        mode.title,
                                        style = MaterialTheme.typography.labelLarge
                                    ) 
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // AMOLED black toggle
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(MotionTokens.Responsive, delayMillis = 200)),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.settingsCard,
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAmoledBlackChange(!amoledBlack) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("AMOLED black")
                        Text(
                            "Use a true black background",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = amoledBlack,
                        onCheckedChange = onAmoledBlackChange
                    )
                }
            }
        }
        
        // Palette selection
        if (!systemTheme) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(MotionTokens.Responsive, delayMillis = 250)),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppShapes.settingsCard,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Palette",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
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
        }
    }
}

/**
 * Onboarding Palette Tile with animations
 */
@Composable
private fun OnboardingPaletteTile(
    option: PaletteOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(MotionTokens.Responsive, easing = FastOutSlowInEasing),
        label = "onboardingPaletteScale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (selected) 4f else 2f,
        animationSpec = tween(MotionTokens.Responsive),
        label = "onboardingPaletteElevation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(AppShapes.paletteTile)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = AppShapes.paletteTile,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = elevation.dp,
        shadowElevation = elevation.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.frostedGlass(
                blurRadius = if (selected) 12f else 6f,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.6f)
                )
            )
        ) {
            Canvas(modifier = Modifier.size(48.dp)) {
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
            
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(tween(MotionTokens.Quick)) + 
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth / 2 },
                        animationSpec = tween(MotionTokens.Responsive),
                    ),
                exit = fadeOut(tween(MotionTokens.Quick))
            ) {
                Surface(
                    shape = CircleShape,
                    color = previewOnColor(option.seed),
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.padding(6.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
