package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class MimirThemeMode(val displayName: String, val isDark: Boolean, val subtitle: String) {
    CARVED_WOOD_DARK("Carved Oak", true, "Default nocturnal wood & amber"),
    SLATE_DARK("Fjord Slate", true, "Cool runestone granite & mist"),
    PARCHMENT_LIGHT("Parchment", false, "Warm Icelandic vellum & walnut"),
    BIRCH_BARK_LIGHT("Birch-Bark", false, "Soft pale bark & copper ink")
}

private val CarvedWoodDarkScheme = darkColorScheme(
    primary = BurnishedGold,
    onPrimary = Color(0xFF1F1607),
    primaryContainer = BurnishedGoldDark,
    onPrimaryContainer = Color(0xFFF6DEB0),
    secondary = MimirWellTeal,
    onSecondary = Color(0xFF061E20),
    secondaryContainer = MimirWellTealDark,
    onSecondaryContainer = Color(0xFFBDE6E8),
    tertiary = EmberRust,
    onTertiary = Color(0xFF280D06),
    tertiaryContainer = Color(0xFF472016),
    onTertiaryContainer = Color(0xFFFAD3C7),
    background = WoodObsidianBg,
    onBackground = BoneIvory,
    surface = WoodCarvedSurface,
    onSurface = BoneIvory,
    surfaceVariant = WoodCarvedVariant,
    onSurfaceVariant = AshMuted,
    outline = CarvedOutline,
    outlineVariant = Color(0xFF2E2822)
)

private val FjordSlateDarkScheme = darkColorScheme(
    primary = FjordGold,
    onPrimary = Color(0xFF1E1706),
    primaryContainer = Color(0xFF3A311B),
    onPrimaryContainer = Color(0xFFF2E2B8),
    secondary = FjordCyan,
    onSecondary = Color(0xFF071E24),
    secondaryContainer = Color(0xFF17353E),
    onSecondaryContainer = Color(0xFFC4EAEF),
    tertiary = Color(0xFFC47A68),
    onTertiary = Color(0xFF240D08),
    background = FjordSlateBg,
    onBackground = FjordMistText,
    surface = FjordSurface,
    onSurface = FjordMistText,
    surfaceVariant = FjordSurfaceVariant,
    onSurfaceVariant = FjordMutedText,
    outline = FjordOutline,
    outlineVariant = Color(0xFF23303C)
)

private val ParchmentLightScheme = lightColorScheme(
    primary = OxbloodBronze,
    onPrimary = Color(0xFFFFF8F5),
    primaryContainer = OxbloodContainer,
    onPrimaryContainer = Color(0xFF42150B),
    secondary = SageWell,
    onSecondary = Color(0xFFF3FCFB),
    secondaryContainer = SageContainer,
    onSecondaryContainer = Color(0xFF123331),
    tertiary = Color(0xFF8C6223),
    onTertiary = Color.White,
    background = ParchmentBg,
    onBackground = WalnutInk,
    surface = ParchmentSurface,
    onSurface = WalnutInk,
    surfaceVariant = ParchmentVariant,
    onSurfaceVariant = WalnutMuted,
    outline = ParchmentOutline,
    outlineVariant = Color(0xFFD6C7B0)
)

private val BirchBarkLightScheme = lightColorScheme(
    primary = BirchCopper,
    onPrimary = Color(0xFFFFF8F4),
    primaryContainer = BirchCopperContainer,
    onPrimaryContainer = Color(0xFF3E1C09),
    secondary = BirchPine,
    onSecondary = Color(0xFFF4FAFA),
    secondaryContainer = Color(0xFFCFE0E2),
    onSecondaryContainer = Color(0xFF152C2F),
    tertiary = Color(0xFF7D6333),
    onTertiary = Color.White,
    background = BirchBg,
    onBackground = BirchInk,
    surface = BirchSurface,
    onSurface = BirchInk,
    surfaceVariant = BirchVariant,
    onSurfaceVariant = BirchMuted,
    outline = BirchOutline,
    outlineVariant = Color(0xFFD0C6B6)
)

@Composable
fun MimirTheme(
    themeMode: MimirThemeMode = MimirThemeMode.CARVED_WOOD_DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        MimirThemeMode.CARVED_WOOD_DARK -> CarvedWoodDarkScheme
        MimirThemeMode.SLATE_DARK -> FjordSlateDarkScheme
        MimirThemeMode.PARCHMENT_LIGHT -> ParchmentLightScheme
        MimirThemeMode.BIRCH_BARK_LIGHT -> BirchBarkLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MimirTypography,
        content = content
    )
}
