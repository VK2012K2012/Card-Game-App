package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.NavigationAppearance
import com.example.ui.theme.ExpressiveCorners

private const val SOURCE_URL = "https://github.com/VK2012K2012/Card-Game-App"

@Composable
fun SettingsCustomizerScreen(
    onOpenDesign: () -> Unit,
    onOpenAbout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                modifier = Modifier.statusBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
                Text(
                    text = "Make it yours.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Tune,
                title = "Design customization",
                subtitle = "Choose how the bar at the bottom of the app looks.",
                onClick = onOpenDesign
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Info,
                title = "About app",
                subtitle = "Version, source code, and credits.",
                onClick = onOpenAbout
            )
        }
    }
}

@Composable
fun DesignCustomizationScreen(
    currentAppearance: NavigationAppearance,
    onAppearanceChange: (NavigationAppearance) -> Unit,
    onBack: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsHeader(
                eyebrow = "DESIGN CUSTOMIZATION",
                title = "Choose your bottom bar.",
                onBack = onBack
            )
        }
        item {
            Text(
                text = "Tap one option below. The change is saved on this phone and appears immediately.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            NavigationAppearanceChoice(
                title = "Material 3 Expressive",
                subtitle = "The normal navigation bar. Every page shows an icon and its name. The selected page has a colored rounded pill.",
                appearance = NavigationAppearance.STANDARD,
                selected = currentAppearance == NavigationAppearance.STANDARD,
                onSelected = { onAppearanceChange(NavigationAppearance.STANDARD) }
            )
        }
        item {
            NavigationAppearanceChoice(
                title = "Compact icon dock",
                subtitle = "A smaller bar with icons only. It gives more room to the game, but page names are hidden.",
                appearance = NavigationAppearance.COMPACT,
                selected = currentAppearance == NavigationAppearance.COMPACT,
                onSelected = { onAppearanceChange(NavigationAppearance.COMPACT) }
            )
        }
    }
}

@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SettingsHeader(
                eyebrow = "ABOUT APP",
                title = "Card Game Hub.",
                onBack = onBack
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Code,
                title = "GitHub project",
                subtitle = "Source code and the latest published changes.",
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL))) },
                trailing = Icons.AutoMirrored.Filled.OpenInNew
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ExpressiveCorners.ExtraExtraLarge,
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "Build ${BuildConfig.VERSION_CODE}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = ExpressiveCorners.ExtraExtraLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "Made by developers with love.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsHeader(eyebrow: String, title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )
        Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun SettingsEntry(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ExpressiveCorners.ExtraExtraLarge)
            .clickable(onClick = onClick),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = ExpressiveCorners.Full,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = trailing,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NavigationAppearanceChoice(
    title: String,
    subtitle: String,
    appearance: NavigationAppearance,
    selected: Boolean,
    onSelected: () -> Unit
) {
    val container = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
    val content = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ExpressiveCorners.ExtraExtraLarge)
            .clickable(onClick = onSelected),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = container
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = content)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = content.copy(alpha = 0.78f))
                }
                if (selected) {
                    Surface(shape = ExpressiveCorners.Full, color = content.copy(alpha = 0.14f)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = content)
                            Text("Active", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = content)
                        }
                    }
                }
            }
            Text(
                text = "Preview",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = content.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                NavigationPreview(appearance = appearance)
            }
        }
    }
}

@Composable
private fun NavigationPreview(appearance: NavigationAppearance) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 420.dp),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Card Game Hub",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.58f)
                )
            }
            when (appearance) {
                NavigationAppearance.STANDARD -> LabeledNavigationPreview()
                NavigationAppearance.COMPACT -> CompactNavigationPreview()
            }
        }
    }
}

/** This uses the same official Material 3 components as the persistent app navigation. */
@Composable
private fun LabeledNavigationPreview() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        // The preview is embedded in a card, so it must not reserve the device system-bar area.
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
            label = { Text("Play") },
            alwaysShowLabel = true,
            colors = previewNavigationItemColors()
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Stats") },
            alwaysShowLabel = true,
            colors = previewNavigationItemColors()
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
            alwaysShowLabel = true,
            colors = previewNavigationItemColors()
        )
    }
}

@Composable
private fun previewNavigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
    selectedTextColor = MaterialTheme.colorScheme.secondary,
    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
)


@Composable
private fun CompactNavigationPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreviewDockItem(Icons.Default.PlayArrow, selected = true)
        PreviewDockItem(Icons.Default.BarChart, selected = false)
        PreviewDockItem(Icons.Default.Settings, selected = false)
    }
}

@Composable
private fun PreviewDockItem(icon: ImageVector, selected: Boolean) {
    Surface(
        modifier = Modifier.size(width = 56.dp, height = 36.dp),
        shape = ExpressiveCorners.Full,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(21.dp),
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
