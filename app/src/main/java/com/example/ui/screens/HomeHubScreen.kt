package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.data.GameStatsEntity
import com.example.durak.model.BotDifficulty
import com.example.durak.model.GameMode
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.OpponentEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class SettingsOption(val key: String, val label: String, val selected: Boolean)
private data class SettingsCategory(val section: SettingsSection, val title: String, val value: String)

private enum class SettingsSection(val title: String, val supportingText: String) {
    PLAYERS("Players", "Choose how many players join the table"),
    DECK("Deck", "Choose the deck size for this table"),
    GAME_MODE("Game mode", "Choose how the attack continues"),
    DIFFICULTY("Bot difficulty", "Choose how sharply local bots play")
}

@Composable
fun HomeHubScreen(
    stats: GameStatsEntity,
    onStartDurak: (LocalMatchSetup) -> Unit
) {
    var modeMenuExpanded by remember { mutableStateOf(false) }
    var gameMode by remember { mutableStateOf(GameMode.PODKIDNOY) }
    var playerCount by remember { mutableIntStateOf(2) }
    var deckSize by remember { mutableIntStateOf(36) }
    var botDifficulty by remember { mutableStateOf(BotDifficulty.MEDIUM) }
    var settingsSection by remember { mutableStateOf<SettingsSection?>(null) }
    val buttonScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "CARD GAME HUB",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Durak,\nready when you are.",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (stats.totalGamesPlayed == 0) {
                    "Play your first local game."
                } else {
                    "${stats.totalGamesPlayed} local games played. Your table is ready."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "READY TO PLAY",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Your next table is waiting",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "A private offline table with local bots. Start now or tune the rules before you deal.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ModePill("$playerCount players", Modifier.weight(1f))
                            ModePill("$deckSize cards", Modifier.weight(1f))
                            ModePill(gameMode.title, Modifier.weight(1f))
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 520.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(76.dp),
                                contentAlignment = Alignment.Center
                            ) {
                            val leadingInteractionSource = remember { MutableInteractionSource() }
                            val trailingInteractionSource = remember { MutableInteractionSource() }
                            val leadingPressed by leadingInteractionSource.collectIsPressedAsState()
                            val trailingPressed by trailingInteractionSource.collectIsPressedAsState()
                            var leadingClickToken by remember { mutableIntStateOf(0) }
                            var trailingClickToken by remember { mutableIntStateOf(0) }
                            var leadingClickPulse by remember { mutableStateOf(false) }
                            var trailingClickPulse by remember { mutableStateOf(false) }
                            LaunchedEffect(leadingClickToken) {
                                if (leadingClickToken == 0) return@LaunchedEffect
                                leadingClickPulse = true
                                delay(190)
                                leadingClickPulse = false
                            }
                            LaunchedEffect(trailingClickToken) {
                                if (trailingClickToken == 0) return@LaunchedEffect
                                trailingClickPulse = true
                                delay(190)
                                trailingClickPulse = false
                            }
                            val leadingActive = leadingPressed || leadingClickPulse
                            val trailingActive = trailingPressed || trailingClickPulse
                            val leadingScale by animateFloatAsState(
                                targetValue = if (leadingActive) 0.96f else 1f,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "leadingButtonPress"
                            )
                            val trailingScale by animateFloatAsState(
                                targetValue = if (trailingActive) 0.96f else 1f,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "trailingButtonPress"
                            )
                            val leadingOuterCorner by animateDpAsState(
                                targetValue = if (leadingActive) 24.dp else 38.dp,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "leadingButtonOuterCorner"
                            )
                            val leadingInnerCorner by animateDpAsState(
                                targetValue = if (leadingActive) 8.dp else 14.dp,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "leadingButtonInnerCorner"
                            )
                            val trailingOuterCorner by animateDpAsState(
                                targetValue = if (trailingActive) 24.dp else 38.dp,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "trailingButtonOuterCorner"
                            )
                            val trailingInnerCorner by animateDpAsState(
                                targetValue = if (trailingActive) 8.dp else 14.dp,
                                animationSpec = tween(170, easing = FastOutSlowInEasing),
                                label = "trailingButtonInnerCorner"
                            )
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                Button(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                        .graphicsLayer {
                                            scaleX = leadingScale
                                            scaleY = leadingScale
                                        },
                                    onClick = {
                                        leadingClickToken += 1
                                        buttonScope.launch {
                                            delay(90)
                                            onStartDurak(
                                                LocalMatchSetup(
                                                    playerCount = playerCount,
                                                    deckSize = deckSize,
                                                    gameMode = gameMode,
                                                    botDifficulty = botDifficulty,
                                                    opponentEngine = OpponentEngine.CLASSIC
                                                ).normalized()
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(
                                        topStart = leadingOuterCorner,
                                        bottomStart = leadingOuterCorner,
                                        topEnd = leadingInnerCorner,
                                        bottomEnd = leadingInnerCorner
                                    ),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp),
                                    interactionSource = leadingInteractionSource
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.size(8.dp))
                                    Text("Start a match", fontWeight = FontWeight.ExtraBold)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .graphicsLayer {
                                            scaleX = trailingScale
                                            scaleY = trailingScale
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        modifier = Modifier.fillMaxSize(),
                                        onClick = {
                                            trailingClickToken += 1
                                            if (modeMenuExpanded) settingsSection = null
                                            modeMenuExpanded = !modeMenuExpanded
                                        },
                                        shape = RoundedCornerShape(
                                            topStart = trailingInnerCorner,
                                            bottomStart = trailingInnerCorner,
                                            topEnd = trailingOuterCorner,
                                            bottomEnd = trailingOuterCorner
                                        ),
                                        contentPadding = PaddingValues(0.dp),
                                        interactionSource = trailingInteractionSource
                                    ) {
                                        val arrowRotation by animateFloatAsState(
                                            targetValue = if (modeMenuExpanded) 180f else 0f,
                                            animationSpec = tween(220, easing = FastOutSlowInEasing),
                                            label = "modeMenuArrow"
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = if (modeMenuExpanded) "Close game mode menu" else "Open game mode menu",
                                            modifier = Modifier.graphicsLayer { rotationZ = arrowRotation }
                                        )
                                    }
                                }
                                }
                            }
                            AnimatedVisibility(
                                visible = modeMenuExpanded,
                                enter = fadeIn(tween(180)) + expandVertically(
                                    animationSpec = tween(260, easing = FastOutSlowInEasing),
                                    expandFrom = Alignment.Top
                                ),
                                exit = fadeOut(tween(120)) + shrinkVertically(
                                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                                    shrinkTowards = Alignment.Top
                                )
                            ) {
                                AnimatedContent(
                                    targetState = settingsSection,
                                    transitionSpec = {
                                        if (targetState == null) {
                                            (slideInHorizontally(
                                                initialOffsetX = { -it },
                                                animationSpec = tween(240, easing = FastOutSlowInEasing)
                                            ) + fadeIn(tween(180))) togetherWith
                                                (slideOutHorizontally(
                                                    targetOffsetX = { it },
                                                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                                                ) + fadeOut(tween(120)))
                                        } else {
                                            (slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(240, easing = FastOutSlowInEasing)
                                            ) + fadeIn(tween(180))) togetherWith
                                                (slideOutHorizontally(
                                                    targetOffsetX = { -it },
                                                    animationSpec = tween(200, easing = FastOutSlowInEasing)
                                                ) + fadeOut(tween(120)))
                                        }
                                    },
                                    label = "settingsListDetailTransition"
                                ) { section ->
                                    if (section == null) {
                                        InlineSettingsMenu(
                                            playerCount = playerCount,
                                            deckSize = deckSize,
                                            gameMode = gameMode,
                                            botDifficulty = botDifficulty,
                                            onOpen = { settingsSection = it }
                                        )
                                    } else {
                                        SettingsDetailView(
                                            section = section,
                                            playerCount = playerCount,
                                            deckSize = deckSize,
                                            gameMode = gameMode,
                                            botDifficulty = botDifficulty,
                                            onBack = { settingsSection = null },
                                            onPlayerCountChange = { playerCount = it },
                                            onDeckSizeChange = { deckSize = it },
                                            onGameModeChange = { gameMode = it },
                                            onDifficultyChange = { botDifficulty = it }
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "Use the arrow to tune the table before you deal.",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

}

/**
 * A large Material 3 Expressive action: pressing tightens the corners into a softer
 * rounded form while subtly compressing the surface, then restores the relaxed shape.
 */
@Composable
private fun InlineSettingsMenu(
    playerCount: Int,
    deckSize: Int,
    gameMode: GameMode,
    botDifficulty: BotDifficulty,
    onOpen: (SettingsSection) -> Unit
) {
    val categories = listOf(
        SettingsCategory(SettingsSection.PLAYERS, "Players", "$playerCount players"),
        SettingsCategory(SettingsSection.DECK, "Deck", "$deckSize cards"),
        SettingsCategory(SettingsSection.GAME_MODE, "Game mode", gameMode.title),
        SettingsCategory(SettingsSection.DIFFICULTY, "Bot difficulty", botDifficulty.displayName)
    )
    val colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        categories.forEachIndexed { index, category ->
            SegmentedListItem(
                onClick = { onOpen(category.section) },
                shapes = ListItemDefaults.segmentedShapes(index = index, count = categories.size),
                colors = colors,
                leadingContent = {
                    when (category.section) {
                        SettingsSection.PLAYERS -> Icon(Icons.Default.Group, contentDescription = null)
                        SettingsSection.DECK -> Icon(Icons.Default.Layers, contentDescription = null)
                        SettingsSection.GAME_MODE -> Icon(Icons.Default.Style, contentDescription = null)
                        SettingsSection.DIFFICULTY -> Icon(Icons.Default.SmartToy, contentDescription = null)
                    }
                },
                supportingContent = { Text(category.value) },
                trailingContent = {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Change ${category.title}"
                    )
                },
                content = { Text(category.title) }
            )
        }
    }
}

@Composable
private fun SettingsDetailView(
    section: SettingsSection,
    playerCount: Int,
    deckSize: Int,
    gameMode: GameMode,
    botDifficulty: BotDifficulty,
    onBack: () -> Unit,
    onPlayerCountChange: (Int) -> Unit,
    onDeckSizeChange: (Int) -> Unit,
    onGameModeChange: (GameMode) -> Unit,
    onDifficultyChange: (BotDifficulty) -> Unit
) {
    val colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    val options = when (section) {
        SettingsSection.PLAYERS -> listOf(2, 3, 4).map { count ->
            SettingsOption(count.toString(), "$count players", playerCount == count)
        }
        SettingsSection.DECK -> listOf(24, 36, 52).map { size ->
            SettingsOption(size.toString(), "$size cards", deckSize == size)
        }
        SettingsSection.GAME_MODE -> listOf(GameMode.PODKIDNOY, GameMode.CLASSIC).map { mode ->
            SettingsOption(mode.name, mode.title, gameMode == mode)
        }
        SettingsSection.DIFFICULTY -> BotDifficulty.entries.map { difficulty ->
            SettingsOption(difficulty.name, difficulty.displayName, botDifficulty == difficulty)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to settings")
            }
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = section.supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
        ) {
            options.forEachIndexed { index, option ->
                SegmentedListItem(
                    selected = option.selected,
                    onClick = {
                        when (section) {
                            SettingsSection.PLAYERS -> onPlayerCountChange(option.key.toInt())
                            SettingsSection.DECK -> onDeckSizeChange(option.key.toInt())
                            SettingsSection.GAME_MODE -> onGameModeChange(GameMode.valueOf(option.key))
                            SettingsSection.DIFFICULTY -> onDifficultyChange(BotDifficulty.valueOf(option.key))
                        }
                    },
                    shapes = ListItemDefaults.segmentedShapes(index = index, count = options.size),
                    colors = colors,
                    trailingContent = {
                        RadioButton(selected = option.selected, onClick = null)
                    },
                    content = { Text(option.label) }
                )
            }
        }
    }
}

@Composable
private fun ModePill(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MorphAction(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 22.dp else 34.dp,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "cornerMorph"
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "pressScale"
    )
    val shape = RoundedCornerShape(corner)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (compact) 54.dp else 88.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(containerColor)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = if (compact) 16.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier.size(if (compact) 34.dp else 46.dp),
            contentAlignment = Alignment.Center
        ) { icon() }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = contentColor)
            if (!compact) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = contentColor.copy(alpha = 0.82f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurakMatchSetupSheet(
    gameMode: GameMode,
    onDismiss: () -> Unit,
    onStart: (LocalMatchSetup) -> Unit
) {
    var playerCount by remember { mutableIntStateOf(2) }
    var deckSize by remember { mutableIntStateOf(36) }
    var difficulty by remember { mutableStateOf(BotDifficulty.MEDIUM) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Build your table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text("Everything stays on this device.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                SetupGroup("Players") {
                    SetupOption("2 players", playerCount == 2) { playerCount = 2 }
                    SetupOption("3 players", playerCount == 3) { playerCount = 3 }
                    SetupOption("4 players", playerCount == 4) { playerCount = 4 }
                }
            }
            item {
                SetupGroup("Deck") {
                    SetupOption("24 cards", deckSize == 24) { deckSize = 24 }
                    SetupOption("36 cards", deckSize == 36) { deckSize = 36 }
                    SetupOption("52 cards", deckSize == 52) { deckSize = 52 }
                }
            }
            item {
                SetupGroup("Bot difficulty") {
                    BotDifficulty.entries.forEach { level -> SetupOption(level.displayName, difficulty == level) { difficulty = level } }
                }
            }
            item {
                MorphAction(
                    title = "Start match",
                    subtitle = "",
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    onClick = {
                        onStart(
                            LocalMatchSetup(
                                playerCount = playerCount,
                                deckSize = deckSize,
                                botDifficulty = difficulty,
                                gameMode = gameMode,
                                opponentEngine = OpponentEngine.CLASSIC
                            ).normalized()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun SetupGroup(title: String, content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SetupOption(label: String, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(if (pressed) 10.dp else 18.dp, label = "choiceMorph")
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "choicePress"
    )
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = Modifier
            .weight(1f)
            .height(46.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
