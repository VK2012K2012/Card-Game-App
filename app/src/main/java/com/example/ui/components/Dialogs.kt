package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * M3E Compliant Dialog Components
 * 
 * Following Material Design 3 guidelines:
 * https://m3.material.io/components/dialogs/overview
 */

/**
 * Standard M3E Dialog with blur background
 */
@Composable
fun M3EDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    text: String? = null,
    confirmText: String = "OK",
    dismissText: String? = null,
    onConfirm: () -> Unit = onDismissRequest,
    onDismiss: () -> Unit = onDismissRequest,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false
    )
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        EnhancedTableFeltBackground {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Header with optional icon
                    if (title != null || icon != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (icon != null) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    icon()
                                }
                            }
                            Text(
                                text = title ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    // Content
                    if (text != null) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (content != null) {
                        content()
                    }
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dismissText != null) {
                            Button(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors()
                            ) {
                                Text(dismissText)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Button(
                            onClick = onConfirm,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(confirmText)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Confirmation Dialog with M3E styling
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmDestructive: Boolean = false
) {
    M3EDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = message,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    )
}

/**
 * Warning Dialog with M3E styling
 */
@Composable
fun WarningDialog(
    title: String,
    message: String,
    confirmText: String = "Continue",
    dismissText: String = "Go back",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    M3EDialog(
        onDismissRequest = onDismiss,
        title = title,
        text = message,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    )
}

/**
 * Success Dialog with M3E styling
 */
@Composable
fun SuccessDialog(
    title: String,
    message: String,
    confirmText: String = "Great!",
    onConfirm: () -> Unit
) {
    M3EDialog(
        onDismissRequest = onConfirm,
        title = title,
        text = message,
        confirmText = confirmText,
        onConfirm = onConfirm,
        icon = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        dismissText = null
    )
}

/**
 * Full-screen modal with blur background
 */
@Composable
fun FullScreenModal(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        EnhancedTableFeltBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                content()
            }
        }
    }
}

/**
 * Bottom Sheet-like Dialog
 */
@Composable
fun BottomSheetDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (title != null) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        content()
                    }
                }
            }
        }
    }
}

/**
 * Loading Overlay with M3E styling
 */
@Composable
fun LoadingOverlay(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * Toast-like Snackbar
 */
@Composable
fun ToastMessage(
    message: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .widthIn(max = 480.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            if (actionText != null) {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(actionText)
                }
            }
        }
    }
}
