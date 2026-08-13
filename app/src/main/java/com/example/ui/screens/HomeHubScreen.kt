package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStatsEntity
import com.example.durak.model.BotDifficulty
import com.example.durak.model.GameMode
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.OpponentEngine
import com.example.ui.theme.ExpressiveCorners

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeHubScreen(
    stats: GameStatsEntity,
    onStartDurak: (LocalMatchSetup) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit
) {
    var showMatchSetup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = ExpressiveCorners.Full,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("♠", fontSize = 22.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Spacer(Modifier.size(12.dp))
                        Column {
                            Text("Card Game Hub", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                            Text("Local game night, ready when you are", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Open activity")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Open settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HeroDurakCard(
                    onQuickPlay = { onStartDurak(LocalMatchSetup()) },
                    onCustomize = { showMatchSetup = true }
                )
            }
            item {
                Text("YOUR TABLE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            item {
                StatsStrip(stats = stats, onOpenStats = onOpenStats)
            }
            item {
                Text("MORE WAYS TO PLAY", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FutureModeRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Smart bot",
                        subtitle = "On-device model opponent — foundation ready",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    FutureModeRow(
                        icon = Icons.Default.Groups,
                        title = "Play with friends",
                        subtitle = "Secure multiplayer rooms are coming next",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    FutureModeRow(
                        icon = Icons.Default.MoreHoriz,
                        title = "More card games",
                        subtitle = "The hub is ready to grow beyond Durak",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    if (showMatchSetup) {
        DurakMatchSetupSheet(
            onDismiss = { showMatchSetup = false },
            onStart = { setup ->
                showMatchSetup = false
                onStartDurak(setup)
            }
        )
    }
}

@Composable
private fun HeroDurakCard(onQuickPlay: () -> Unit, onCustomize: () -> Unit) {
    Card(
        shape = ExpressiveCorners.ExtraExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AssistChip(
                onClick = onCustomize,
                label = { Text("DURAK · OFFLINE", fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(18.dp)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Text("A good hand.\nA sharper move.", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.ExtraBold)
            Text("Play a complete local Durak match against bots. No account, connection, or wait required.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onQuickPlay,
                    shape = ExpressiveCorners.Full,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text("Play Durak", fontWeight = FontWeight.Bold)
                }
                AssistChip(
                    onClick = onCustomize,
                    label = { Text("Set up match") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun StatsStrip(stats: GameStatsEntity, onOpenStats: () -> Unit) {
    val winRate = if (stats.totalGamesPlayed == 0) 0 else (stats.totalWins * 100 / stats.totalGamesPlayed)
    Surface(
        onClick = onOpenStats,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatPill("Win rate", "$winRate%", MaterialTheme.colorScheme.primary)
            StatPill("Wins", stats.totalWins.toString(), MaterialTheme.colorScheme.secondary)
            StatPill("Played", stats.totalGamesPlayed.toString(), MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
private fun FutureModeRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(modifier = Modifier.size(44.dp), shape = ExpressiveCorners.Full, color = tint.copy(alpha = 0.16f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = tint) }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("SOON", style = MaterialTheme.typography.labelSmall, color = tint, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurakMatchSetupSheet(onDismiss: () -> Unit, onStart: (LocalMatchSetup) -> Unit) {
    var playerCount by remember { mutableIntStateOf(2) }
    var deckSize by remember { mutableIntStateOf(36) }
    var difficulty by remember { mutableStateOf(BotDifficulty.MEDIUM) }
    var gameMode by remember { mutableStateOf(GameMode.PODKIDNOY) }
    var opponentEngine by remember { mutableStateOf(OpponentEngine.CLASSIC) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ExpressiveCorners.ExtraExtraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 24.dp, end = 24.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Set up your table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text("Everything below plays entirely offline.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                SetupChoiceGroup("Players") {
                    listOf(2, 3, 4).forEach { count ->
                        SetupChip("$count players", playerCount == count) { playerCount = count }
                    }
                }
            }
            item {
                SetupChoiceGroup("Deck") {
                    listOf(24, 36, 52).forEach { count ->
                        SetupChip("$count cards", deckSize == count) { deckSize = count }
                    }
                }
            }
            item {
                SetupChoiceGroup("Classic bot difficulty") {
                    BotDifficulty.entries.forEach { value ->
                        SetupChip(value.displayName, difficulty == value) { difficulty = value }
                    }
                }
            }
            item {
                SetupChoiceGroup("Rules") {
                    SetupChip(GameMode.PODKIDNOY.title, gameMode == GameMode.PODKIDNOY) { gameMode = GameMode.PODKIDNOY }
                    SetupChip(GameMode.CLASSIC.title, gameMode == GameMode.CLASSIC) { gameMode = GameMode.CLASSIC }
                }
            }
            item {
                SetupChoiceGroup("Opponent engine") {
                    SetupChip(OpponentEngine.CLASSIC.displayName, opponentEngine == OpponentEngine.CLASSIC) { opponentEngine = OpponentEngine.CLASSIC }
                    SetupChip("Smart bot · Preview", opponentEngine == OpponentEngine.SMART_ON_DEVICE) { opponentEngine = OpponentEngine.SMART_ON_DEVICE }
                }
            }
            item {
                if (opponentEngine == OpponentEngine.SMART_ON_DEVICE) {
                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.tertiaryContainer) {
                        Text("Smart bot is reserved for a future bundled on-device model. This match stays offline and uses the Classic bot safely for now.", modifier = Modifier.padding(14.dp), color = MaterialTheme.colorScheme.onTertiaryContainer, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Button(
                    onClick = {
                        onStart(
                            LocalMatchSetup(playerCount, gameMode, deckSize, difficulty, opponentEngine).normalized()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = ExpressiveCorners.Full
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Start local match", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SetupChoiceGroup(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    }
}

@Composable
private fun SetupChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = ExpressiveCorners.Full,
        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}

@Composable
fun StatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
