package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = YtProFrostPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3F2B68),
    onPrimaryContainer = Color(0xFFE9D5FF),
    secondary = YtProFrostLavender,
    onSecondary = Color(0xFF1E1035),
    secondaryContainer = Color(0xFF38234D),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = YtProFrostMagenta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4A1536),
    onTertiaryContainer = Color(0xFFFFD7E8),
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    outline = DarkBorder,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = YtProIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),
    onPrimaryContainer = Color(0xFF3730A3),
    secondary = YtProCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFEFF),
    onSecondaryContainer = Color(0xFF155E75),
    tertiary = YtProRose,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF1F2),
    onTertiaryContainer = Color(0xFF9F1239),
    background = LightBg,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    outline = LightBorder,
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun YTProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
