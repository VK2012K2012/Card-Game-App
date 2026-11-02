package com.example.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class AppVisualStyleTest {
    @Test
    fun knownKeysRestoreTheSelectedVisualStyle() {
        assertEquals(
            AppVisualStyle.MATERIAL_3_EXPRESSIVE,
            AppVisualStyle.fromKey(AppVisualStyle.MATERIAL_3_EXPRESSIVE.name),
        )
        assertEquals(
            AppVisualStyle.ONE_UI_INSPIRED,
            AppVisualStyle.fromKey(AppVisualStyle.ONE_UI_INSPIRED.name),
        )
    }

    @Test
    fun missingOrUnknownKeyFallsBackToMaterial3Expressive() {
        assertEquals(AppVisualStyle.MATERIAL_3_EXPRESSIVE, AppVisualStyle.fromKey(null))
        assertEquals(AppVisualStyle.MATERIAL_3_EXPRESSIVE, AppVisualStyle.fromKey("not-a-style"))
    }
}
