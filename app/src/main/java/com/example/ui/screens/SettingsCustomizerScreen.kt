package com.example.ui.screens

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.ExpressiveBackButton
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
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
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
    var showCustom by remember(currentCustom, currentPalette.id) { mutableStateOf(currentCustom != null) }
    val primarySeed = remember(currentCustom, currentPalette.id) {
        currentCustom?.primary?.hsvForSettings() ?: currentPalette.seed.hsvForSettings()
    }
    val secondarySeed = remember(currentCustom, currentPalette.id) {
        currentCustom?.secondary?.hsvForSettings()
            ?: hsvColorForThemeEditor((primarySeed.hue + 32f) % 360f, 0.62f, 0.82f).hsvForSettings()
    }
    val tertiarySeed = remember(currentCustom, currentPalette.id) {
        currentCustom?.tertiary?.hsvForSettings()
            ?: hsvColorForThemeEditor((primarySeed.hue + 68f) % 360f, 0.56f, 0.86f).hsvForSettings()
    }
    var primaryColor by remember(currentCustom, currentPalette.id) { mutableStateOf(primarySeed) }
    var secondaryColor by remember(currentCustom, currentPalette.id) { mutableStateOf(secondarySeed) }
    var tertiaryColor by remember(currentCustom, currentPalette.id) { mutableStateOf(tertiarySeed) }
    val customPalette = CustomPalette(primaryColor.toColor(), secondaryColor.toColor(), tertiaryColor.toColor())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SettingsHeader(eyebrow = "DESIGN CUSTOMIZATION", title = "Make it yours.", onBack = onBack)
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                    SettingsSectionLabel(icon = Icons.Default.DarkMode, title = "Appearance")
                    Text(
                        "Choose the theme mode and one accent color.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SegmentedListItem(
                        onClick = { onSystemThemeChange(!systemTheme) },
                        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        supportingContent = { Text("Follow your phone's light or dark mode") },
                        trailingContent = {
                            AppearanceSwitch(
                                checked = systemTheme,
                                onCheckedChange = onSystemThemeChange,
                            )
                        },
                        content = { Text("Use system theme") },
                    )
                    if (!systemTheme) {
                        Text("Accent color", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    AnimatedContent(
                        targetState = showCustom,
                        transitionSpec = {
                            fadeIn(tween(180)) togetherWith fadeOut(tween(120)) using
                                androidx.compose.animation.SizeTransform(clip = false)
                        },
                        label = "accentColorModeTransition",
                    ) { customOpen ->
                        if (customOpen) {
                            CustomSettingsPaletteEditor(
                                primary = primaryColor,
                                secondary = secondaryColor,
                                tertiary = tertiaryColor,
                                onPrimaryChange = { primaryColor = it },
                                onSecondaryChange = { secondaryColor = it },
                                onTertiaryChange = { tertiaryColor = it },
                                palette = customPalette,
                                onChoosePreset = { showCustom = false },
                                onApply = { onCustomApply(customPalette) },
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Choose one color. You can create your own below.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    modifier = Modifier.height(180.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    itemsIndexed(PaletteCatalog.all, key = { _, option -> option.id }) { index, option ->
                                        SettingsPaletteTile(
                                            option = option,
                                            selected = currentCustom == null && currentPalette.id == option.id,
                                            hasSelection = currentCustom == null,
                                            entranceIndex = index,
                                            onClick = { onPaletteChange(option) },
                                        )
                                    }
                                }
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { showCustom = true },
                                    colors = ButtonDefaults.filledTonalButtonColors(),
                                ) { Text("Create custom palette") }
                            }
                        }
                    }
                    }
                    Text(
                        "App theme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (systemTheme) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
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
                    SegmentedListItem(
                        onClick = if (systemTheme) ({}) else ({ onAmoledBlackChange(!amoledBlack) }),
                        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            headlineColor = if (systemTheme) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        ),
                        supportingContent = { Text(if (systemTheme) "Turn off system theme first" else "Black background with dark-gray surfaces") },
                        trailingContent = {
                            AppearanceSwitch(
                                checked = amoledBlack,
                                onCheckedChange = if (systemTheme) null else onAmoledBlackChange,
                                enabled = !systemTheme,
                            )
                        },
                        content = { Text("AMOLED black") },
                    )
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
    hasSelection: Boolean,
    entranceIndex: Int,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    var entered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(entranceIndex * 35L)
        entered = true
    }

    val emphasis by animateFloatAsState(
        targetValue = if (!hasSelection || selected) 1f else 0.86f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "paletteTileEmphasis",
    )
    val entryScale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.82f,
        animationSpec = tween(260, easing = FastOutSlowInEasing),
        label = "paletteTileEntryScale",
    )
    val pressScale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(150, easing = FastOutSlowInEasing),
        label = "paletteTilePressScale",
    )
    val corner by animateDpAsState(
        targetValue = if (pressed) 22.dp else if (selected) 26.dp else 20.dp,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "paletteTileCorner",
    )
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "paletteTileContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "paletteTileBorder",
    )
    val previewScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "palettePreviewScale",
    )

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = entryScale * pressScale
                scaleY = entryScale * pressScale
                alpha = if (entered) emphasis else 0f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(corner),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .size(58.dp)
                    .graphicsLayer {
                        scaleX = previewScale
                        scaleY = previewScale
                    }
                    .clip(CircleShape),
            ) {
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
            androidx.compose.animation.AnimatedVisibility(
                visible = selected,
                enter = fadeIn(tween(160)) + androidx.compose.animation.scaleIn(initialScale = 0.6f, animationSpec = tween(180)),
                exit = fadeOut(tween(100)) + androidx.compose.animation.scaleOut(targetScale = 0.6f, animationSpec = tween(120)),
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(5.dp).size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun CustomSettingsPaletteEditor(
    primary: HsvControlState,
    secondary: HsvControlState,
    tertiary: HsvControlState,
    onPrimaryChange: (HsvControlState) -> Unit,
    onSecondaryChange: (HsvControlState) -> Unit,
    onTertiaryChange: (HsvControlState) -> Unit,
    palette: CustomPalette,
    onChoosePreset: () -> Unit,
    onApply: () -> Unit,
) {
    var activeChannel by remember { mutableStateOf(CustomColorChannel.PRIMARY) }
    val activeState = when (activeChannel) {
        CustomColorChannel.PRIMARY -> primary
        CustomColorChannel.SECONDARY -> secondary
        CustomColorChannel.TERTIARY -> tertiary
    }
    val activeColor = activeState.toColor()

    fun updateActive(update: (HsvControlState) -> HsvControlState) {
        val next = update(activeState)
        when (activeChannel) {
            CustomColorChannel.PRIMARY -> onPrimaryChange(next)
            CustomColorChannel.SECONDARY -> onSecondaryChange(next)
            CustomColorChannel.TERTIARY -> onTertiaryChange(next)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ExpressiveCorners.ExtraExtraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = onChoosePreset) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Choose preset colors")
            }
            Text("Custom colors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                "Tap a color below, then adjust it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            CustomColorRoleRow(
                label = "Primary",
                color = primary.toColor(),
                selected = activeChannel == CustomColorChannel.PRIMARY,
                onClick = { activeChannel = CustomColorChannel.PRIMARY },
            )
            CustomColorRoleRow(
                label = "Secondary",
                color = secondary.toColor(),
                selected = activeChannel == CustomColorChannel.SECONDARY,
                onClick = { activeChannel = CustomColorChannel.SECONDARY },
            )
            CustomColorRoleRow(
                label = "Tertiary",
                color = tertiary.toColor(),
                selected = activeChannel == CustomColorChannel.TERTIARY,
                onClick = { activeChannel = CustomColorChannel.TERTIARY },
            )
            AnimatedContent(
                targetState = activeChannel,
                transitionSpec = {
                    (fadeIn(tween(180)) + androidx.compose.animation.slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(220),
                    )) togetherWith
                        (fadeOut(tween(120)) + androidx.compose.animation.slideOutVertically(
                            targetOffsetY = { -it / 4 },
                            animationSpec = tween(160),
                        ))
                },
                label = "customColorRoleTransition",
            ) { channel ->
                val selectedState = when (channel) {
                    CustomColorChannel.PRIMARY -> primary
                    CustomColorChannel.SECONDARY -> secondary
                    CustomColorChannel.TERTIARY -> tertiary
                }
                val selectedColor = selectedState.toColor()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "${channel.title} controls",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    SettingsColorSlider(
                        label = "Hue",
                        value = selectedState.hue,
                        color = selectedColor,
                        valueRange = 0f..360f,
                        onValueChange = { hue -> updateActive { state -> state.copy(hue = hue) } },
                    )
                    SettingsColorSlider(
                        label = "Saturation",
                        value = selectedState.saturation,
                        color = selectedColor,
                        valueRange = 0f..1f,
                        onValueChange = { saturation -> updateActive { state -> state.copy(saturation = saturation) } },
                    )
                    SettingsColorSlider(
                        label = "Brightness",
                        value = selectedState.value,
                        color = selectedColor,
                        valueRange = 0f..1f,
                        onValueChange = { brightness -> updateActive { state -> state.copy(value = brightness) } },
                    )
                }
            }
            val lightPreview = com.example.ui.theme.lightSchemeForCustom(palette)
            val darkPreview = com.example.ui.theme.darkSchemeForCustom(palette)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = lightPreview.primaryContainer,
                    contentColor = lightPreview.onPrimaryContainer,
                ) {
                    Text(
                        "Light preview",
                        Modifier.padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Surface(
                    Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    color = darkPreview.primaryContainer,
                    contentColor = darkPreview.onPrimaryContainer,
                ) {
                    Text(
                        "Dark preview",
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
            ) { Text("Save custom palette") }
        }
    }
}

@Composable
private fun CustomColorRoleRow(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(180),
        label = "customRoleContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) color else Color.Transparent,
        animationSpec = tween(180),
        label = "customRoleBorder",
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(Modifier.size(30.dp).clip(CircleShape).background(color))
            Column(Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(
                    color.toHexString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            RadioButton(selected = selected, onClick = null)
        }
    }
}

@Composable
private fun SettingsColorSlider(
    label: String,
    value: Float,
    color: Color,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.width(86.dp), style = MaterialTheme.typography.labelLarge)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
            ),
        )
        Text(
            text = if (valueRange.endInclusive > 1f) "${value.toInt()}°" else "${(value * 100f).toInt()}%",
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private enum class CustomColorChannel(val title: String) {
    PRIMARY("Primary"),
    SECONDARY("Secondary"),
    TERTIARY("Tertiary"),
}

private data class HsvControlState(
    val hue: Float,
    val saturation: Float,
    val value: Float,
) {
    fun toColor(): Color = hsvColorForThemeEditor(hue, saturation, value)
}

private fun Color.hsvForSettings(): HsvControlState {
    val hsv = FloatArray(3)
    AndroidColor.colorToHSV(toArgb(), hsv)
    return HsvControlState(hsv[0], hsv[1], hsv[2])
}

private fun Color.toHexString(): String = "#%08X".format(toArgb())

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
            AnimatedSettingsEntry(
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
                busy = busy,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ExpressiveBackButton(
            onClick = onBack,
            contentDescription = "Back",
            size = 48.dp,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun AppearanceSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
    )
}

@Composable
private fun AnimatedSettingsEntry(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: ImageVector,
    busy: Boolean,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val corner by animateDpAsState(
        targetValue = if (pressed) 18.dp else 28.dp,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "updateEntryCorner",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(170, easing = FastOutSlowInEasing),
        label = "updateEntryScale",
    )
    val trailingRotation by animateFloatAsState(
        targetValue = if (busy) 180f else 0f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "updateEntryIconRotation",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(corner))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            trailing,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer { rotationZ = trailingRotation },
        )
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
