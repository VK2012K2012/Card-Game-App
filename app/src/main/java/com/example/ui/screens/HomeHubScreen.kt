package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStatsEntity
import com.example.durak.model.BotDifficulty
import com.example.durak.model.GameMode
import com.example.durak.model.LocalMatchSetup
import com.example.durak.model.OpponentEngine
import com.example.ui.theme.ExpressiveCorners

@Composable
fun HomeHubScreen(
    stats: GameStatsEntity,
    onStartDurak: (LocalMatchSetup) -> Unit
) {
    var showMatchSetup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.statusBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "CARD GAME HUB",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp
                    )
                    Text(
                        text = "Pick up a hand.",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            item {
                DurakLaunchCard(
                    stats = stats,
                    onQuickPlay = { onStartDurak(LocalMatchSetup()) },
                    onCustomize = { showMatchSetup = true }
                )
            }
            item {
                SectionLabel("LOCAL PLAY")
            }
            item {
                LocalPlayFacts()
            }
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
private fun DurakLaunchCard(
    stats: GameStatsEntity,
    onQuickPlay: () -> Unit,
    onCustomize: () -> Unit
) {
    val winRate = if (stats.totalGamesPlayed == 0) 0 else stats.totalWins * 100 / stats.totalGamesPlayed
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
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onCustomize,
                    label = { Text("DURAK · OFFLINE", fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.68f),
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Surface(
                    shape = ExpressiveCorners.Full,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f)
                ) {
                    Text(
                        text = if (stats.totalGamesPlayed == 0) "NEW TABLE" else "$winRate% WIN RATE",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Durak,\nready when you are.",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "A complete offline match against classic bots.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.86f)
                )
            }
            Button(
                onClick = onQuickPlay,
                modifier = Modifier.fillMaxWidth(),
                shape = ExpressiveCorners.Full,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Quick play", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onCustomize,
                modifier = Modifier.fillMaxWidth(),
                shape = ExpressiveCorners.Full
            ) {
                Icon(Icons.Default.Tune, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Set up a match")
            }
        }
    }
}

@Composable
private fun LocalPlayFacts() {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = ExpressiveCorners.Full,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Built for local play", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "No account, connection, or waiting. Choose your table and play.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurakMatchSetupSheet(
    onDismiss: () -> Unit,
    onStart: (LocalMatchSetup) -> Unit
) {
    var playerCount by remember { mutableIntStateOf(2) }
    var deckSize by remember { mutableIntStateOf(36) }
    var difficulty by remember { mutableStateOf(BotDifficulty.MEDIUM) }
    var gameMode by remember { mutableStateOf(GameMode.PODKIDNOY) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = ExpressiveCorners.ExtraExtraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        LazyColumn(
            contentPadding = PaddingValues(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Build your table", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Choose the match. Everything here stays on this device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                SetupChoiceGroup("Players") {
                    SetupChoice("2", playerCount == 2) { playerCount = 2 }
                    SetupChoice("3", playerCount == 3) { playerCount = 3 }
                    SetupChoice("4", playerCount == 4) { playerCount = 4 }
                }
            }
            item {
                SetupChoiceGroup("Deck") {
                    SetupChoice("24 cards", deckSize == 24) { deckSize = 24 }
                    SetupChoice("36 cards", deckSize == 36) { deckSize = 36 }
                    SetupChoice("52 cards", deckSize == 52) { deckSize = 52 }
                }
            }
            item {
                SetupChoiceGroup("Bot difficulty") {
                    BotDifficulty.entries.forEach { value ->
                        SetupChoice(value.displayName, difficulty == value) { difficulty = value }
                    }
                }
            }
            item {
                SetupChoiceGroup("Rules") {
                    SetupChoice(GameMode.PODKIDNOY.title, gameMode == GameMode.PODKIDNOY) { gameMode = GameMode.PODKIDNOY }
                    SetupChoice(GameMode.CLASSIC.title, gameMode == GameMode.CLASSIC) { gameMode = GameMode.CLASSIC }
                }
            }
            item {
                Button(
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = ExpressiveCorners.Full
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Start match", fontWeight = FontWeight.Bold)
                }
            }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SetupChoiceGroup(
    title: String,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.SetupChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        modifier = Modifier.weight(1f),
        shape = ExpressiveCorners.Full,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}
