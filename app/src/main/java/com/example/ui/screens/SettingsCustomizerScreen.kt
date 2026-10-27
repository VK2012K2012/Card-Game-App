package com.example.ui.screens

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.NavigationAppearance
import com.example.ui.components.BlurIntensity
import com.example.ui.components.EnhancedTableFeltBackground
import com.example.ui.components.blurEffect
import com.example.ui.components.frostedGlass
import com.example.ui.theme.AppShapes
import com.example.ui.theme.CustomPalette
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.ExpressiveCorners
import com.example.ui.theme.MotionTokens
import com.example.ui.theme.PaletteCatalog
import com.example.ui.theme.PaletteOption
import com.example.ui.theme.ShapeTokens
import com.example.ui.theme.ThemeMode
import com.example.ui.components.ExpressiveBackButton
import com.example.ui.theme.hsvColorForThemeEditor
import com.example.ui.theme.palettePreviewTones
import com.example.ui.theme.previewOnColor
import com.example.update.GitHubReleaseInfo
import com.example.update.GitHubUpdateChecker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SOURCE_URL = "https://github.com/VK2012K2012/Card-Game-App"

/**
 * Main Settings Screen
 * M3E Compliant with animated entries, blur effects, and proper hierarchy
 */
@Composable
fun SettingsCustomizerScreen(
    onOpenDesign: () -> Unit,
    onOpenAbout: () -> Unit
) {
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()

    EnhancedTableFeltBackground {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .blurEffect(BlurIntensity.SUBTLE),
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
                SettingsCard {
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
}

/**
 * Settings Card with M3E styling and blur
 */
@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.settingsCard,
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
        }
    }
}

/**
 * Design Customization Screen
 * M3E Compliant with animated transitions, blur effects, and proper color editing
 */
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
    var showCustom by remember(currentCustom, currentPalette.id) { 
        mutableStateOf(currentCustom != null) 
    }
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
    val customPalette = CustomPalette(
        primaryColor.toColor(), 
        secondaryColor.toColor(), 
        tertiaryColor.toColor()
    )

    EnhancedTableFeltBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .blurEffect(BlurIntensity.SUBTLE),
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                SettingsHeader(
                    eyebrow = "DESIGN CUSTOMIZATION",
                    title = "Make it yours.",
                    onBack = onBack
                )
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        SettingsSectionLabel(
                            icon = Icons.Default.DarkMode, 
                            title = "Appearance"
                        )
                        Text(
                            "Choose the theme mode and one accent color.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        
                        // System theme toggle
                        SettingsListItem(
                            title = "Use system theme",
                            subtitle = "Follow your phone's light or dark mode",
                            index = 0,
                            count = 1,
                            trailingContent = {
                                AppearanceSwitch(
                                    checked = systemTheme,
                                    onCheckedChange = onSystemThemeChange,
                                )
                            },
                            onClick = { onSystemThemeChange(!systemTheme) }
                        )
                        
                        // Custom vs Preset palette
                        AnimatedContent(
                            targetState = showCustom,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(MotionTokens.Responsive)) + 
                                    slideInHorizontally(animationSpec = tween(MotionTokens.Responsive)) { width -> 
                                        width / 2 
                                    }).togetherWith(
                                    fadeOut(animationSpec = tween(MotionTokens.Quick)) + 
                                        slideOutHorizontally(animationSpec = tween(MotionTokens.Quick)) { width -> 
                                            -width / 2 
                                        }
                                )
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
                                        itemsIndexed(
                                            PaletteCatalog.all, 
                                            key = { _, option -> option.id 
                                        }) { index, option ->
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
                                        shape = AppShapes.button
                                    ) { 
                                        Text("Create custom palette") 
                                    }
                                }
                            }
                        }
                        
                        // App theme selection
                        Text(
                            "App theme",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (systemTheme) MaterialTheme.colorScheme.onSurfaceVariant 
                                   else MaterialTheme.colorScheme.onSurface,
                        )
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val manualModes = listOf(ThemeMode.LIGHT, ThemeMode.DARK)
                            manualModes.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = !systemTheme && currentMode == mode,
                                    enabled = !systemTheme,
                                    onClick = { onModeChange(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(index, manualModes.size),
                                ) { 
                                    Text(
                                        mode.title, 
                                        style = MaterialTheme.typography.labelLarge
                                    ) 
                                }
                            }
                        }
                        
                        // AMOLED black toggle
                        SettingsListItem(
                            title = "AMOLED black",
                            subtitle = if (systemTheme) "Turn off system theme first" else "Black background with dark-gray surfaces",
                            index = 0,
                            count = 1,
                            trailingContent = {
                                AppearanceSwitch(
                                    checked = amoledBlack,
                                    onCheckedChange = if (systemTheme) null else onAmoledBlackChange,
                                    enabled = !systemTheme,
                                )
                            },
                            onClick = if (systemTheme) ({}) else ({ onAmoledBlackChange(!amoledBlack) })
                        )
                    }
                }
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsSectionLabel(
                            icon = Icons.Default.Navigation, 
                            title = "Navigation"
                        )
                        Text(
                            "Choose how the bottom navigation bar looks.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                }
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SettingsSectionLabel(
                            icon = Icons.Default.TextFields, 
                            title = "Typography"
                        )
                        SettingsListItem(
                            title = "Google Sans Flex",
                            subtitle = "Google variable font",
                            index = 0,
                            count = 1,
                            trailingContent = { 
                                Switch(
                                    checked = useGoogleSansFlex, 
                                    onCheckedChange = onGoogleSansFlexChange
                                ) 
                            },
                            onClick = { onGoogleSansFlexChange(!useGoogleSansFlex) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Settings Section Label with icon badge
 */
@Composable
private fun SettingsSectionLabel(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = AppShapes.badge,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(6.dp).size(18.dp),
            )
        }
        Text(
            title, 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Settings Palette Tile with M3E styling and animations
 */
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
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(MotionTokens.Responsive, easing = MotionTokens.StandardEasing),
        label = "paletteTileScale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (selected) 4.dp else 2.dp,
        animationSpec = tween(MotionTokens.Responsive),
        label = "paletteTileElevation"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary 
                      else MaterialTheme.colorScheme.outlineVariant,
        animationSpec = tween(MotionTokens.Responsive),
        label = "paletteTileBorder"
    )

    Box(
        modifier = Modifier
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
            )
            .then(
                if (hasSelection) {
                    Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        shape = AppShapes.paletteTile
                    )
                } else {
                    Modifier
                }
            )
            .frostedGlass(
                blurRadius = if (selected) 16f else 8f,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
                )
            ),
        contentAlignment = Alignment.Center
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
        
        if (selected && hasSelection) {
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(tween(MotionTokens.Quick)) + 
                    keyframes(MotionTokens.Responsive) { initialOffsetX -> initialOffsetX / 2 },
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

/**
 * Custom Settings Palette Editor
 */
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
    var selectedChannel by remember { mutableStateOf(CustomColorChannel.PRIMARY) }
    val updateActive: (CustomColorChannel, HsvControlState) -> Unit = { channel, state ->
        when (channel) {
            CustomColorChannel.PRIMARY -> onPrimaryChange(state)
            CustomColorChannel.SECONDARY -> onSecondaryChange(state)
            CustomColorChannel.TERTIARY -> onTertiaryChange(state)
        }
    }
    
    val selectedState = when (selectedChannel) {
        CustomColorChannel.PRIMARY -> primary
        CustomColorChannel.SECONDARY -> secondary
        CustomColorChannel.TERTIARY -> tertiary
    }
    val selectedColor = selectedState.toColor()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onChoosePreset,
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = AppShapes.button
        ) { Text("Choose from presets") }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomColorRoleRow(
                label = "Primary",
                color = primary.toColor(),
                selected = selectedChannel == CustomColorChannel.PRIMARY,
                onClick = { selectedChannel = CustomColorChannel.PRIMARY }
            )
            CustomColorRoleRow(
                label = "Secondary",
                color = secondary.toColor(),
                selected = selectedChannel == CustomColorChannel.SECONDARY,
                onClick = { selectedChannel = CustomColorChannel.SECONDARY }
            )
            CustomColorRoleRow(
                label = "Tertiary",
                color = tertiary.toColor(),
                selected = selectedChannel == CustomColorChannel.TERTIARY,
                onClick = { selectedChannel = CustomColorChannel.TERTIARY }
            )
        }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "${selectedChannel.title} controls",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            SettingsColorSlider(
                label = "Hue",
                value = selectedState.hue,
                color = selectedColor,
                valueRange = 0f..360f,
                onValueChange = { hue -> 
                    updateActive(selectedChannel, selectedState.copy(hue = hue)) 
                },
            )
            SettingsColorSlider(
                label = "Saturation",
                value = selectedState.saturation,
                color = selectedColor,
                valueRange = 0f..1f,
                onValueChange = { saturation -> 
                    updateActive(selectedChannel, selectedState.copy(saturation = saturation)) 
                },
            )
            SettingsColorSlider(
                label = "Brightness",
                value = selectedState.value,
                color = selectedColor,
                valueRange = 0f..1f,
                onValueChange = { brightness -> 
                    updateActive(selectedChannel, selectedState.copy(value = brightness)) 
                },
            )
        }
        
        val lightPreview = com.example.ui.theme.lightSchemeForCustom(palette)
        val darkPreview = com.example.ui.theme.darkSchemeForCustom(palette)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                Modifier.weight(1f),
                shape = AppShapes.previewCard,
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
                shape = AppShapes.previewCard,
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
            shape = AppShapes.button
        ) { Text("Save custom palette") }
    }
}

/**
 * Custom Color Role Row
 */
@Composable
private fun CustomColorRoleRow(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.18f) 
                      else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(MotionTokens.Responsive),
        label = "customRoleContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) color else Color.Transparent,
        animationSpec = tween(MotionTokens.Responsive),
        label = "customRoleBorder",
    )
    
    Surface(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        shape = AppShapes.colorRole,
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    label, 
                    style = MaterialTheme.typography.labelLarge, 
                    fontWeight = FontWeight.Bold
                )
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

/**
 * Settings Color Slider with M3E styling
 */
@Composable
private fun SettingsColorSlider(
    label: String,
    value: Float,
    color: Color,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            label, 
            Modifier.width(86.dp), 
            style = MaterialTheme.typography.labelLarge
        )
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
            text = if (valueRange.endInclusive > 1f) "${value.toInt()}\u00b0" 
                  else "${(value * 100f).toInt()}%",
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Update Status for About Screen
 */
private enum class UpdateStatus {
    Idle,
    Checking,
    Downloading,
    Available,
    UpToDate,
    Failed
}

/**
 * About App Screen with M3E styling and blur
 */
@Composable
fun AboutAppScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var updateStatus by remember { mutableStateOf<UpdateStatus>(UpdateStatus.Idle) }
    var updateMessage by remember { mutableStateOf("") }
    val busy = updateStatus == UpdateStatus.Checking || updateStatus == UpdateStatus.Downloading

    fun checkForUpdates() {
        if (busy) return
        scope.launch {
            updateStatus = UpdateStatus.Checking
            runCatching { GitHubUpdateChecker.fetchLatestRelease() }
                .onSuccess { release ->
                    updateStatus = if (GitHubUpdateChecker.isNewerVersion(
                        release.tagName, 
                        BuildConfig.VERSION_NAME
                    )) {
                        UpdateStatus.Available
                    } else {
                        UpdateStatus.UpToDate
                    }
                    updateMessage = release.tagName
                }
                .onFailure { error ->
                    updateStatus = UpdateStatus.Failed
                    updateMessage = error.message ?: "Could not check for updates."
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
                updateStatus = UpdateStatus.Failed
                updateMessage = error.message ?: "Could not download the update."
            }
        }
    }

    EnhancedTableFeltBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .blurEffect(BlurIntensity.SUBTLE),
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
                SettingsCard {
                    AnimatedSettingsEntry(
                        icon = Icons.Default.Refresh,
                        title = "Check for updates",
                        subtitle = when (updateStatus) {
                            UpdateStatus.Idle -> "Look for a newer GitHub release."
                            UpdateStatus.Checking -> "Checking GitHub releases\u2026"
                            UpdateStatus.Downloading -> "Downloading the update\u2026"
                            UpdateStatus.Available -> "Version $updateMessage is available."
                            UpdateStatus.UpToDate -> "You have the latest published version."
                            UpdateStatus.Failed -> updateMessage
                        },
                        onClick = ::checkForUpdates,
                        trailing = if (busy) Icons.Default.Refresh 
                                  else Icons.AutoMirrored.Filled.ArrowForward,
                        busy = busy,
                    )
                }
            }
            
            if (busy) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = if (updateStatus == UpdateStatus.Checking) 
                                "Checking GitHub\u2026" else "Downloading APK\u2026",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            "Version ${BuildConfig.VERSION_NAME}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Build ${BuildConfig.VERSION_CODE}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            item {
                SettingsCard {
                    SettingsEntry(
                        icon = Icons.Default.Code,
                        title = "GitHub project",
                        subtitle = "Source code and published releases.",
                        onClick = { 
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(SOURCE_URL))
                            ) 
                        },
                        trailing = Icons.AutoMirrored.Filled.OpenInNew,
                        index = 0,
                        count = 1
                    )
                }
            }
        }
    }
}

/**
 * Settings Header with back button and proper M3E styling
 */
@Composable
private fun SettingsHeader(
    eyebrow: String,
    title: String,
    onBack: () -> Unit
) {
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

/**
 * Appearance Switch with M3E styling
 */
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

/**
 * Animated Settings Entry with spring animations
 */
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
        targetValue = if (pressed) ShapeTokens.Medium else ShapeTokens.Large,
        animationSpec = tween(MotionTokens.Responsive, easing = MotionTokens.StandardEasing),
        label = "updateEntryCorner",
    )
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.975f else 1f,
        animationSpec = tween(MotionTokens.Responsive, easing = MotionTokens.StandardEasing),
        label = "updateEntryScale",
    )
    
    val trailingRotation by animateFloatAsState(
        targetValue = if (busy) 180f else 0f,
        animationSpec = tween(MotionTokens.Natural, easing = MotionTokens.StandardEasing),
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
            .clickable(
                interactionSource = interactionSource, 
                indication = null, 
                onClick = onClick
            )
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

/**
 * Settings List Item - Replaces SegmentedListItem for M3 1.4.0 compatibility
 */
@Composable
private fun SettingsListItem(
    title: String,
    subtitle: String,
    index: Int,
    count: Int,
    trailingContent: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(MotionTokens.Responsive),
        label = "settingsListItemScale"
    )
    
    val shape = when {
        count == 1 -> RoundedCornerShape(12.dp)
        index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
        index == count - 1 -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailingContent()
    }
}

/**
 * Settings Entry with M3E styling
 */
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
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(MotionTokens.Responsive),
        label = "settingsEntryScale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(
                when {
                    count == 1 -> RoundedCornerShape(12.dp)
                    index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                    index == count - 1 -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
                    else -> RoundedCornerShape(0.dp)
                }
            )
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(trailing, contentDescription = null)
    }
}

/**
 * Navigation Appearance List Row
 */
@Composable
private fun NavigationAppearanceListRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    index: Int,
    count: Int,
    onSelected: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = tween(MotionTokens.Responsive),
        label = "navAppearanceScale"
    )
    
    val shape = when {
        count == 1 -> RoundedCornerShape(12.dp)
        index == 0 -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
        index == count - 1 -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelected
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButton(selected = selected, onClick = null)
    }
}

/**
 * Helper data classes and enums
 */
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
