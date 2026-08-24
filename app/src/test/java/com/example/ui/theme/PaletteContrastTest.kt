package com.example.ui.theme

import org.junit.Assert.assertTrue
import org.junit.Test

class PaletteContrastTest {
    @Test
    fun curatedPalettesKeepAccessibleMaterialPairs() {
        PaletteCatalog.all.forEach { palette ->
            assertSchemeAccessible(palette.id, "light", palette.lightScheme)
            assertSchemeAccessible(palette.id, "dark", palette.darkScheme)
        }
    }

    private fun assertSchemeAccessible(id: String, mode: String, scheme: androidx.compose.material3.ColorScheme) {
        assertTrue("$id/$mode primary pair", contrastRatio(scheme.onPrimary, scheme.primary) >= 4.5)
        assertTrue("$id/$mode primary container pair", contrastRatio(scheme.onPrimaryContainer, scheme.primaryContainer) >= 4.5)
        assertTrue("$id/$mode secondary pair", contrastRatio(scheme.onSecondary, scheme.secondary) >= 4.5)
        assertTrue("$id/$mode tertiary pair", contrastRatio(scheme.onTertiary, scheme.tertiary) >= 4.5)
        assertTrue("$id/$mode surface pair", contrastRatio(scheme.onSurface, scheme.surface) >= 4.5)
        assertTrue("$id/$mode surface variant pair", contrastRatio(scheme.onSurfaceVariant, scheme.surfaceVariant) >= 3.0)
    }
}
