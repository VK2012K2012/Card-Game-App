package com.example.ui.theme

import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object ThemePreferences {
    const val THEME_MODE_KEY = "theme_mode_v2"
    const val COLOR_PRESET_KEY = "color_preset_v2"
    const val PALETTE_ID_KEY = "palette_id_v1"
    const val CUSTOM_PRIMARY_KEY = "custom_primary_v1"
    const val CUSTOM_SECONDARY_KEY = "custom_secondary_v1"
    const val CUSTOM_TERTIARY_KEY = "custom_tertiary_v1"
    const val CUSTOM_ENABLED_KEY = "custom_palette_enabled_v1"
    const val SYSTEM_THEME_KEY = "system_theme_v3"
    const val AMOLED_BLACK_KEY = "amoled_black_v1"

    fun readMode(prefs: SharedPreferences): ThemeMode =
        ThemeMode.fromKey(prefs.getString(THEME_MODE_KEY, ThemeMode.SYSTEM.key))

    fun writeMode(prefs: SharedPreferences, value: ThemeMode) {
        prefs.edit().putString(THEME_MODE_KEY, value.key).apply()
    }

    fun readSystemTheme(prefs: SharedPreferences): Boolean =
        if (prefs.contains(SYSTEM_THEME_KEY)) {
            prefs.getBoolean(SYSTEM_THEME_KEY, false)
        } else {
            readMode(prefs) == ThemeMode.SYSTEM
        }

    fun writeSystemTheme(prefs: SharedPreferences, enabled: Boolean) {
        prefs.edit().putBoolean(SYSTEM_THEME_KEY, enabled).apply()
    }

    fun readAmoledBlack(prefs: SharedPreferences): Boolean =
        prefs.getBoolean(AMOLED_BLACK_KEY, false)

    fun writeAmoledBlack(prefs: SharedPreferences, enabled: Boolean) {
        prefs.edit().putBoolean(AMOLED_BLACK_KEY, enabled).apply()
    }

    fun readPreset(prefs: SharedPreferences): ThemeColorPreset {
        val legacy = prefs.getString("color_preset_v1", ThemeColorPreset.SYSTEM.name)
        return prefs.getString(COLOR_PRESET_KEY, legacy)
            ?.let { runCatching { ThemeColorPreset.valueOf(it) }.getOrNull() }
            ?: ThemeColorPreset.SYSTEM
    }

    fun writePreset(prefs: SharedPreferences, value: ThemeColorPreset) {
        prefs.edit()
            .putString(COLOR_PRESET_KEY, value.name)
            .putString(PALETTE_ID_KEY, value.name.lowercase())
            .putBoolean(CUSTOM_ENABLED_KEY, false)
            .apply()
    }

    fun readPalette(prefs: SharedPreferences): PaletteOption? =
        prefs.getString(PALETTE_ID_KEY, null)?.let { PaletteCatalog.all.firstOrNull { option -> option.id == it } }

    fun writePalette(prefs: SharedPreferences, value: PaletteOption) {
        prefs.edit()
            .putString(PALETTE_ID_KEY, value.id)
            .putBoolean(CUSTOM_ENABLED_KEY, false)
            .apply()
    }

    fun readCustom(prefs: SharedPreferences): CustomPalette? {
        if (!prefs.getBoolean(CUSTOM_ENABLED_KEY, false)) return null
        val primary = prefs.getInt(CUSTOM_PRIMARY_KEY, Int.MIN_VALUE)
        val secondary = prefs.getInt(CUSTOM_SECONDARY_KEY, Int.MIN_VALUE)
        val tertiary = prefs.getInt(CUSTOM_TERTIARY_KEY, Int.MIN_VALUE)
        if (primary == Int.MIN_VALUE || secondary == Int.MIN_VALUE || tertiary == Int.MIN_VALUE) return null
        return CustomPalette(Color(primary), Color(secondary), Color(tertiary))
    }

    fun writeCustom(prefs: SharedPreferences, value: CustomPalette) {
        prefs.edit()
            .putInt(CUSTOM_PRIMARY_KEY, value.primary.toArgb())
            .putInt(CUSTOM_SECONDARY_KEY, value.secondary.toArgb())
            .putInt(CUSTOM_TERTIARY_KEY, value.tertiary.toArgb())
            .putBoolean(CUSTOM_ENABLED_KEY, true)
            .apply()
    }
}
