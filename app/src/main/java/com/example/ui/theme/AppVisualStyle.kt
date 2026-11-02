package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

enum class AppVisualStyle(
    val title: String,
    val description: String,
) {
    MATERIAL_3_EXPRESSIVE(
        title = "Material 3 Expressive",
        description = "Expressive shapes, color and motion",
    ),
    ONE_UI_INSPIRED(
        title = "One UI-inspired",
        description = "Calmer, more restrained surfaces",
    );

    companion object {
        fun fromKey(key: String?): AppVisualStyle =
            entries.firstOrNull { it.name == key } ?: MATERIAL_3_EXPRESSIVE
    }
}

val LocalAppVisualStyle = staticCompositionLocalOf { AppVisualStyle.MATERIAL_3_EXPRESSIVE }
