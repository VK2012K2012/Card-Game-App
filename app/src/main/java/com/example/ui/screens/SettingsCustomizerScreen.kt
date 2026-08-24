package com.example.ui.screens

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.NavigationAppearance
import com.example.ui.theme.CustomPalette
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.theme.PaletteCatalog
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.hsvColorForThemeEditor
import com.example.ui.theme.palettePreviewTones
import com.example.ui.theme.previewOnColor
import com.example.update.GitHubReleaseInfo
import com.example.update.GitHubUpdateChecker
import kotlinx.coroutines.launch

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
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SettingsEntry(
                    icon = Icons.Default.Tune,
                    title = "Design customization",
                    subtitle = "Colors, mode, navigation and type.",
                    index = 0,
                    count = 2,
                    onClick = onOpenDesign,
                )
                SettingsEntry(
                    icon = Icons.Default.Info,
                    title = "About app",
                    subtitle = "Version and updates.",
                    index = 1,
                    count = 2,
                    onClick = onOpenAbout,
                )
            }
        }
    }
}

@Composable
fun DesignCustomizationScreen(
    currentMode: ThemeMode,
    systemTheme: Boolean,
    amoledBlack: Boolean,
    currentPalette: PaletteOption,
    currentCustom: CustomPalette?,
    currentAppearance: NavigationAppearance,
    useGoogleSansFlex: Boolean,
    onModeChange: (ThemeMode) -> Unit,
    onSystemThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onPaletteChange: (PaletteOption) -> Unit,
    onCustomApply: (CustomPalette) -> Unit,
    onAppearanceChange: (NavigationAppearance) -> Unit,
    onGoogleSansFlexChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    // Keyed on currentCustom/currentPalette so switching presets (or reopening this
    // screen after a different palette was applied elsewhere) re-seeds the editor's
    // starting hues instead of reusing whatever was first composed.
    var showCustom by remember { mutableStateOf(currentCustom != null) }
    var primaryHue by remember(currentCustom, currentPalette.id) {
        mutableFloatStateOf(currentCustom?.primary?.hueForSettings() ?: currentPalette.seed.hueForSettings())
    }
    var secondaryHue by remember(currentCustom, currentPalette.id) {
        mutableFloatStateOf(currentCustom?.secondary?.hueForSettings() ?: ((primaryHue + 32f) % 360f))
    }
    var tertiaryHue by remember(currentCustom, currentPalette.id) {
        mutableFloatStateOf(currentCustom?.tertiary?.hueForSettings() ?: ((primaryHue + 68f) % 360f))
    }
    val customPalette = CustomPalette(
        primary = hsvColorForThemeEditor(primaryHue, 0.72f, 0.88f),
        secondary = hsvColorForThemeEditor(secondaryHue, 0.62f, 0.82f),
        tertiary = hsvColorForThemeEditor(tertiaryHue, 0.56f, 0.86f),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SettingsHeader(
                eyebrow = "DESIGN CUSTOMIZATION",
                title = "Make it yours.",
                onBack = onBack,
            )
        }
        item {
            SettingsSectionLabel(icon = Icons.Default.DarkMode, title = "Appearance")
        }
        item {
            SegmentedListItem(
                onClick = { onSystemThemeChange(!systemTheme) },
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                supportingContent = { Text("Use the phone's light or dark mode and Dynamic Color") },
                trailingContent = {
                    Switch(
                        checked = systemTheme,
                        onCheckedChange = onSystemThemeChange,
                        colors = SwitchDefaults.colors(),
                    )
                },
                content = { Text("Use system theme") },
            )
        }
        item {
            Text(
                text = "App theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (systemTheme) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
        }
        item {
            Text(
                text = if (systemTheme) "Turn off system theme to choose light or dark manually." else "Choose the app appearance.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            // Disabled (not just visually inert) while system theme drives light/dark,
            // so the control never looks selectable without doing anything.
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val manualModes = listOf(ThemeMode.LIGHT, ThemeMode.DARK)
                manualModes.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = !systemTheme && currentMode == mode,
                        enabled = !systemTheme,
                        onClick = { onModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, manualModes.size),
                    ) { Text(mode.title, style = MaterialTheme.typography.labelLarge) }
                }
            }
        }
        item {
            SegmentedListItem(
                onClick = if (systemTheme) ({}) else ({ onAmoledBlackChange(!amoledBlack) }),
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    headlineColor = if (systemTheme) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                supportingContent = {
                    Text(if (systemTheme) "Available when system theme is off" else "Use a true black background")
                },
                trailingContent = {
                    Switch(
                        checked = amoledBlack,
                        onCheckedChange = if (systemTheme) null else onAmoledBlackChange,
                        enabled = !systemTheme,
                    )
                },
                content = { Text("AMOLED black") },
            )
        }
        if (!systemTheme) {
            item {
                SettingsSectionLabel(icon = Icons.Default.Palette, title = "Colors")
            }
            item {
                Text("Choose a preset or create your own palette.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(PaletteCatalog.all, key = { it.id }) { option ->
                        SettingsPaletteTile(
                            option = option,
                            selected = !showCustom && currentCustom == null && currentPalette.id == option.id,
                            onClick = {
                                showCustom = false
                                onPaletteChange(option)
                            },
                        )
                    }
                }
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showCustom = !showCustom },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                ) {
                    Text(if (showCustom) "Use preset palette" else "Create custom")
                }
            }
            if (showCustom) {
                item {
                    CustomSettingsPaletteEditor(
                        primaryHue = primaryHue,
                        secondaryHue = secondaryHue,
                        tertiaryHue = tertiaryHue,
                        onPrimaryHue = { primaryHue = it },
                        onSecondaryHue = { secondaryHue = it },
                        onTertiaryHue = { tertiaryHue = it },
                        palette = customPalette,
                        onApply = { onCustomApply(customPalette) },
                    )
                }
            }
        }
        item {
            SettingsSectionLabel(icon = Icons.Default.Navigation, title = "Navigation")
        }
        item {
            Text(
                "Choose how the bottom navigation bar looks.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)) {
                NavigationAppearanceListRow(
                    title = "Material 3 Expressive",
                    subtitle = "Labeled bottom navigation",
                    selected = currentAppearance == NavigationAppearance.STANDARD,
                    index = 0,
                    count = 2,
                    onSelected = { onAppearanceChange(NavigationAppearance.STANDARD) },
                )
                NavigationAppearanceListRow(
                    title = "Compact",
                    subtitle = "Icons-only bottom navigation",
                    selected = currentAppearance == NavigationAppearance.COMPACT,
                    index = 1,
                    count = 2,
                    onSelected = { onAppearanceChange(NavigationAppearance.COMPACT) },
                )
            }
        }
        item {
            SettingsSectionLabel(icon = Icons.Default.TextFields, title = "Typography")
        }
        item {
            SegmentedListItem(
                onClick = { onGoogleSansFlexChange(!useGoogleSansFlex) },
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                supportingContent = { Text("Google variable font") },
                trailingContent = { Switch(checked = useGoogleSansFlex, onCheckedChange = onGoogleSansFlexChange) },
                content = { Text("Google Sans Flex") },
            )
        }
    }
}

/**
 * Consistent section header used throughout Design customization: a small
 * tonal icon chip plus a titleLarge label. Replaces the previous bare
 * [Text] headers so every group (Appearance, Colors, Navigation, Typography)
 * reads as a distinct section instead of Colors being the only one with
 * visual weight.
 */
@Composable
private fun SettingsSectionLabel(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(6.dp).size(18.dp),
            )
        }
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsPaletteTile(
    option: PaletteOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) option.seed.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(180),
        label = "paletteTileContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) option.seed else Color.Transparent,
        animationSpec = tween(180),
        label = "paletteTileBorder",
    )
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(58.dp)) {
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
                Surface(
                    shape = CircleShape,
                    color = previewOnColor(option.seed),
                    contentColor = option.seed,
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(9.dp))
                }
            }
        }
    }
}

@Composable
private fun CustomSettingsPaletteEditor(
    primaryHue: Float,
    secondaryHue: Float,
    tertiaryHue: Float,
    onPrimaryHue: (Float) -> Unit,
    onSecondaryHue: (Float) -> Unit,
    onTertiaryHue: (Float) -> Unit,
    palette: CustomPalette,
    onApply: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Custom palette", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SettingsColorSlider("Primary", primaryHue, palette.primary, onPrimaryHue)
            SettingsColorSlider("Secondary", secondaryHue, palette.secondary, onSecondaryHue)
            SettingsColorSlider("Tertiary", tertiaryHue, palette.tertiary, onTertiaryHue)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = palette.primary,
                    contentColor = previewOnColor(palette.primary),
                ) {
                    Text(
                        "Light",
                        Modifier.padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = palette.tertiary,
                    contentColor = previewOnColor(palette.tertiary),
                ) {
                    Text(
                        "Dark",
                        Modifier.padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onApply,
                colors = ButtonDefaults.filledTonalButtonColors(),
            ) { Text("Use custom palette") }
        }
    }
}

@Composable
private fun SettingsColorSlider(
    label: String,
    hue: Float,
    color: Color,
    onHue: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, Modifier.width(76.dp), style = MaterialTheme.typography.labelLarge)
        Slider(
            value = hue,
            onValueChange = onHue,
            valueRange = 0f..360f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
            ),
        )
    }
}

private fun Color.hueForSettings(): Float {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(toArgb(), hsv)
    return hsv[0]
}

private sealed interface UpdateStatus {
    data object Idle : UpdateStatus
    data object Checking : UpdateStatus
    data object Downloading : UpdateStatus
    data class Available(val release: GitHubReleaseInfo) : UpdateStatus
    data class UpToDate(val release: GitHubReleaseInfo) : UpdateStatus
    data class Failed(val message: String) : UpdateStatus
}

@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var updateStatus by remember { mutableStateOf<UpdateStatus>(UpdateStatus.Idle) }
    val busy = updateStatus is UpdateStatus.Checking || updateStatus is UpdateStatus.Downloading

    fun checkForUpdates() {
        if (busy) return
        scope.launch {
            updateStatus = UpdateStatus.Checking
            runCatching { GitHubUpdateChecker.fetchLatestRelease() }
                .onSuccess { release ->
                    updateStatus = if (GitHubUpdateChecker.isNewerVersion(release.tagName, BuildConfig.VERSION_NAME)) {
                        UpdateStatus.Available(release)
                    } else {
                        UpdateStatus.UpToDate(release)
                    }
                }
                .onFailure { error ->
                    updateStatus = UpdateStatus.Failed(error.message ?: "Could not check for updates.")
                }
        }
    }

    fun startInstall(release: GitHubReleaseInfo) {
        if (!GitHubUpdateChecker.canRequestInstallPackages(context)) {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}"),
                )
            )
            return
        }
        scope.launch {
            updateStatus = UpdateStatus.Downloading
            runCatching {
                GitHubUpdateChecker.downloadApk(context, release.apk).also {
                    GitHubUpdateChecker.launchInstaller(context, it)
                }
            }.onFailure { error ->
                updateStatus = UpdateStatus.Failed(error.message ?: "Could not download the update.")
            }
        }
    }

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
                icon = Icons.Default.Refresh,
                title = "Check for updates",
                subtitle = when (val status = updateStatus) {
                    UpdateStatus.Idle -> "Look for a newer GitHub release."
                    UpdateStatus.Checking -> "Checking GitHub releases…"
                    UpdateStatus.Downloading -> "Downloading the update…"
                    is UpdateStatus.Available -> "Version ${status.release.tagName} is available."
                    is UpdateStatus.UpToDate -> "You have the latest published version."
                    is UpdateStatus.Failed -> status.message
                },
                onClick = ::checkForUpdates,
                trailing = if (busy) Icons.Default.Refresh else Icons.AutoMirrored.Filled.ArrowForward,
                index = 0,
                count = 1
            )
        }
        if (updateStatus is UpdateStatus.Checking || updateStatus is UpdateStatus.Downloading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text(
                        text = if (updateStatus is UpdateStatus.Checking) "Checking GitHub…" else "Downloading APK…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        val available = (updateStatus as? UpdateStatus.Available)?.release
        if (available != null) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ExpressiveCorners.ExtraExtraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(available.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = available.notes.ifBlank { "A new version is ready to install." }.take(240),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Button(
                            onClick = { startInstall(available) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Download and install")
                        }
                    }
                }
            }
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
            SettingsEntry(
                icon = Icons.Default.Code,
                title = "GitHub project",
                subtitle = "Source code and published releases.",
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL))) },
                trailing = Icons.AutoMirrored.Filled.OpenInNew,
                index = 0,
                count = 1
            )
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
    index: Int = 0,
    count: Int = 1,
    onClick: () -> Unit,
    trailing: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.segmentedShapes(index = index, count = count),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        supportingContent = { Text(subtitle) },
        trailingContent = { Icon(trailing, contentDescription = null) },
        content = { Text(title) }
    )
}

@Composable
private fun NavigationAppearanceListRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    index: Int,
    count: Int,
    onSelected: () -> Unit
) {
    SegmentedListItem(
        selected = selected,
        onClick = onSelected,
        shapes = ListItemDefaults.segmentedShapes(index = index, count = count),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        supportingContent = { Text(subtitle) },
        trailingContent = { RadioButton(selected = selected, onClick = null) },
        content = { Text(title) }
    )
}
