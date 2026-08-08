package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStatsEntity
import com.example.durak.model.BotDifficulty
import com.example.durak.model.GameMode
import com.example.ui.theme.ExpressiveCorners

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeHubScreen(
    stats: GameStatsEntity,
    onStartDurak: (playerCount: Int, mode: GameMode, deckSize: Int, botDiff: BotDifficulty) -> Unit,
    onOpenMultiplayer: () -> Unit,
    onOpenCustomizer: () -> Unit,
    onOpenStats: () -> Unit
) {
    var selectedPlayerCount by remember { mutableStateOf(2) }
    var selectedMode by remember { mutableStateOf(GameMode.PODKIDNOY) }
    var selectedBotDiff by remember { mutableStateOf(BotDifficulty.LOCAL_NEURAL_AI) }
    var selectedDeckSize by remember { mutableStateOf(36) }
    var showQuickSetupSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = ExpressiveCorners.Full,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("♠", fontSize = 20.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Card Game Hub",
                                fontWeight = FontWeight.ExtraBold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                "Durak · Material You Live Theme",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCustomizer) {
                        Icon(Icons.Default.Palette, contentDescription = "Themes", tint = MaterialTheme.colorScheme.onSurface)
                    }
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Default.BarChart, contentDescription = "Stats", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 3.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenMultiplayer,
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Multiplayer") },
                    label = { Text("Lobby") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenCustomizer,
                    icon = { Icon(Icons.Default.Palette, contentDescription = "Themes") },
                    label = { Text("Display") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOpenStats,
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                    label = { Text("Stats") }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showQuickSetupSheet = true },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                text = { Text("New Match", fontWeight = FontWeight.Bold) },
                shape = ExpressiveCorners.Full,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner: Daily Challenge / Master the Fool
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onStartDurak(2, GameMode.PODKIDNOY, 36, BotDifficulty.LOCAL_NEURAL_AI)
                        },
                    shape = ExpressiveCorners.ExtraExtraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = ExpressiveCorners.Full,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                                ) {
                                    Text(
                                        text = "DAILY CHALLENGE",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                    )
                                }
                                Surface(
                                    shape = ExpressiveCorners.Full,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Memory,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Master the Fool",
                                style = MaterialTheme.typography.displaySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Face the local neural bot in a 36-card tactical duel — 100% offline",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    onStartDurak(2, GameMode.PODKIDNOY, 36, BotDifficulty.LOCAL_NEURAL_AI)
                                },
                                shape = ExpressiveCorners.Full,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Play Challenge Now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Section Label: Game Modes Bento Grid
            item {
                Text(
                    text = "GAME MODES & ENGINE",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // Bento Grid Row 1: Local Neural AI vs Base Rule Bot
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BentoTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Memory,
                        iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        title = "Neural AI",
                        subtitle = "Offline On-Device",
                        onClick = { onStartDurak(2, GameMode.PODKIDNOY, 36, BotDifficulty.LOCAL_NEURAL_AI) }
                    )
                    BentoTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.SmartToy,
                        iconContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                        title = "Base Rule Bot",
                        subtitle = "Classic Heuristics",
                        onClick = { onStartDurak(2, GameMode.PODKIDNOY, 36, BotDifficulty.EASY) }
                    )
                }
            }

            // Bento Grid Row 2: Multiplayer & Custom Setup
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BentoTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Groups,
                        iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconTint = MaterialTheme.colorScheme.onTertiaryContainer,
                        title = "Multiplayer",
                        subtitle = "Crossplay Room Code",
                        onClick = onOpenMultiplayer
                    )
                    BentoTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Tune,
                        iconContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                        title = "Custom Setup",
                        subtitle = "Players & Variants",
                        onClick = { showQuickSetupSheet = true }
                    )
                }
            }

            // Recent Achievements / Player Stats
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ExpressiveCorners.ExtraExtraLarge,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Match History Stats",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = onOpenStats) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Stats", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val winRate = if (stats.totalGamesPlayed > 0) (stats.totalWins * 100 / stats.totalGamesPlayed) else 0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatPill(
                                label = "Win Rate",
                                value = "$winRate%",
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatPill(
                                label = "Wins",
                                value = "${stats.totalWins}",
                                color = MaterialTheme.colorScheme.secondary
                            )
                            StatPill(
                                label = "Games Played",
                                value = "${stats.totalGamesPlayed}",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // Quick Setup Bottom Sheet
        if (showQuickSetupSheet) {
            ModalBottomSheet(
                onDismissRequest = { showQuickSetupSheet = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Durak Match Setup",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Player Count
                    Text("Players Count", style = MaterialTheme.typography.titleSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        listOf(2, 3, 4).forEach { count ->
                            FilterChip(
                                selected = (selectedPlayerCount == count),
                                onClick = { selectedPlayerCount = count },
                                label = { Text("$count Players") },
                                shape = ExpressiveCorners.Full
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bot Engine Selection (Base Bot vs Local Neural AI)
                    Text("Opponent Engine (100% Offline)", style = MaterialTheme.typography.titleSmall)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        listOf(
                            BotDifficulty.EASY to "Base Rule-based Bot",
                            BotDifficulty.MEDIUM to "Standard Heuristic Bot",
                            BotDifficulty.HARD to "Master Card Counter",
                            BotDifficulty.LOCAL_NEURAL_AI to "Local Neural AI Bot"
                        ).forEach { (diff, labelText) ->
                            FilterChip(
                                selected = (selectedBotDiff == diff),
                                onClick = { selectedBotDiff = diff },
                                label = { Text(labelText) },
                                shape = ExpressiveCorners.Full,
                                leadingIcon = {
                                    if (diff == BotDifficulty.LOCAL_NEURAL_AI) {
                                        Icon(Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(16.dp))
                                    } else {
                                        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Game Mode
                    Text("Rules Variant", style = MaterialTheme.typography.titleSmall)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        GameMode.values().forEach { mode ->
                            FilterChip(
                                selected = (selectedMode == mode),
                                onClick = { selectedMode = mode },
                                label = { Text(mode.title.split(" ").first()) },
                                shape = ExpressiveCorners.Full
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            showQuickSetupSheet = false
                            onStartDurak(selectedPlayerCount, selectedMode, selectedDeckSize, selectedBotDiff)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = ExpressiveCorners.Full,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Start Match", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun BentoTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconContainerColor: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(136.dp)
            .clickable { onClick() },
        shape = ExpressiveCorners.ExtraExtraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = ExpressiveCorners.Full,
                color = iconContainerColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
