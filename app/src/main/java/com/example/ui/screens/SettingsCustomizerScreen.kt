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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
                subtitle = "Choose your bottom navigation style.",
                onClick = onOpenDesign
            )
        }
        item {
            SettingsEntry(
                icon = Icons.Default.Info,
                title = "About app",
                subtitle = "Version, source, and credits.",
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
                title = "Navigation that fits you.",
                onBack = onBack
            )
        }
        item {
            Text(
                text = "The selected style is saved on this device and applies immediately.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            NavigationAppearanceChoice(
                title = "Standard",
                subtitle = "Labeled Material navigation with a soft active indicator.",
                appearance = NavigationAppearance.STANDARD,
                selected = currentAppearance == NavigationAppearance.STANDARD,
                onSelected = { onAppearanceChange(NavigationAppearance.STANDARD) }
            )
        }
        item {
            NavigationAppearanceChoice(
                title = "Compact dock",
                subtitle = "A low icon dock with more room for the game.",
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
                trailing = Icons.Default.OpenInNew
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
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
    trailing: ImageVector = Icons.Default.ArrowForward
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
                if (selected) Icon(Icons.Default.Check, contentDescription = "Selected", tint = content)
            }
            NavigationPreview(appearance = appearance, activeColor = content, selected = selected)
        }
    }
}

@Composable
private fun NavigationPreview(appearance: NavigationAppearance, activeColor: androidx.compose.ui.graphics.Color, selected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(activeColor.copy(alpha = 0.08f), ExpressiveCorners.ExtraExtraLarge)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (appearance == NavigationAppearance.STANDARD) {
            listOf("Play", "Stats", "Settings").forEachIndexed { index, label ->
                val isActive = index == 0
                Text(
                    text = label,
                    modifier = if (isActive) Modifier.background(activeColor.copy(alpha = 0.16f), ExpressiveCorners.Full).padding(horizontal = 9.dp, vertical = 4.dp) else Modifier,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                    color = activeColor
                )
            }
        } else {
            repeat(3) { index ->
                Surface(
                    modifier = Modifier.size(width = if (index == 0) 44.dp else 24.dp, height = 20.dp),
                    shape = ExpressiveCorners.Full,
                    color = if (index == 0) activeColor.copy(alpha = 0.20f) else activeColor.copy(alpha = 0.08f)
                ) {}
            }
        }
    }
}
