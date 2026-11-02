package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanPulse,
    onPrimary = Color(0xFF041824),
    primaryContainer = Color(0xFF0C3852),
    onPrimaryContainer = Color(0xFFCBEBFF),
    secondary = MagentaHeart,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A102A),
    onSecondaryContainer = Color(0xFFFFD9E4),
    tertiary = EmeraldVitals,
    onTertiary = Color(0xFF003919),
    tertiaryContainer = Color(0xFF005327),
    onTertiaryContainer = Color(0xFF6BFF9E),
    background = MidnightBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderDark
)

private val LightColorScheme = DarkColorScheme // Wearable & Bioscience telemetry apps look best in immersive Dark OLED mode

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Keep intentional immersive cyber-bioscience branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
