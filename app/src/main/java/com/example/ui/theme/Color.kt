package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Frosted Glass Distinctive Palette
val YtProFrostPurple = Color(0xFFA084E8)
val YtProFrostMagenta = Color(0xFF6F1E51)
val YtProFrostLavender = Color(0xFFC4B5FD)
val YtProIndigo = Color(0xFFA084E8)
val YtProIndigoLight = Color(0xFFC4B5FD)
val YtProCyan = Color(0xFF38BDF8)
val YtProCyanLight = Color(0xFF7DD3FC)
val YtProRose = Color(0xFFF43F5E)
val YtProRoseLight = Color(0xFFFB7185)
val YtProAmber = Color(0xFFF59E0B)
val YtProEmerald = Color(0xFF10B981)

// Frosted Glass Tokens
val GlassSurfaceDark = Color(0xCC18181B)
val GlassSurfaceLight = Color(0xDD272727)
val GlassCard = Color(0x40272727)
val GlassBorder = Color(0x26FFFFFF) // ~15% white border
val GlassBorderSubtle = Color(0x14FFFFFF) // ~8% white border
val GlassHighlight = Color(0x33A084E8) // Translucent lavender

// Gradient Brushes
val FrostedPurpleGradient = Brush.linearGradient(
    listOf(YtProFrostPurple, YtProFrostMagenta)
)
val GlassScrimGradient = Brush.verticalGradient(
    listOf(Color.Transparent, Color(0x99000000), Color(0xF00F0F0F))
)

// Dark Palette (Frosted Glass Dark)
val DarkBg = Color(0xFF0F0F0F)
val DarkSurface = Color(0xFF161616)
val DarkSurfaceVariant = Color(0xFF272727)
val DarkSurfaceContainer = Color(0xFF1E1E1E)
val DarkSurfaceContainerHigh = Color(0xFF2E2E2E)
val DarkTextPrimary = Color(0xFFF1F1F1)
val DarkTextSecondary = Color(0xFFAAAAAA)
val DarkBorder = Color(0x26FFFFFF)

// Light Palette (Frosted Glass Crisp Studio)
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightSurfaceContainer = Color(0xFFE2E8F0)
val LightSurfaceContainerHigh = Color(0xFFCBD5E1)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightBorder = Color(0xFFE2E8F0)

