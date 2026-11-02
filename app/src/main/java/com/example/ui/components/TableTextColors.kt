package com.example.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Text color roles used on top of TableFeltBackground, shared by every game
 * screen (Durak and the new games alike) so they all adapt to the active
 * Material theme — including custom/dynamic color — the same way.
 */
@Composable
fun tablePrimaryTextColor(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun tableSecondaryTextColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun tableAccentTextColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun tableDisabledTextColor(): Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
