package com.somuleco.creator.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Somuleco Core Brand Colors
val SomulecoBlue = Color(0xFF2563EB)
val SomulecoBlueDark = Color(0xFF1D4ED8)
val SomulecoBlueLight = Color(0xFFEFF6FF)
val SomulecoBlueBorder = Color(0xFFDBEAFE)

val SomulecoPurple = Color(0xFF7C3AED)
val SomulecoPurpleDark = Color(0xFF6D28D9)
val SomulecoPurpleLight = Color(0xFFF5F3FF)
val SomulecoPurpleBorder = Color(0xFFEDE9FE)

val SomulecoPink = Color(0xFFEC4899)
val SomulecoPinkDark = Color(0xFFDB2777)
val SomulecoPinkLight = Color(0xFFFDF2F8)
val SomulecoPinkBorder = Color(0xFFFCE7F3)

val SomulecoGreen = Color(0xFF22C55E)
val SomulecoGreenDark = Color(0xFF16A34A)
val SomulecoGreenSurface = Color(0xFFF0FDF4)
val SomulecoGreenBorder = Color(0xFFBBF7D0)
val SomulecoGreenText = Color(0xFF166534)

val SomulecoCyan = Color(0xFF06B6D4)
val SomulecoCyanLight = Color(0xFFECFEFF)

// Neutrals & Surfaces
val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceCard = Color(0xFFFFFFFF)
val SurfaceMuted = Color(0xFFF1F5F9)
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF475569)
val TextMuted = Color(0xFF94A3B8)
val BorderSubtle = Color(0xFFE2E8F0)
val BorderStrong = Color(0xFFCBD5E1)

// Semantic Gradients
val CreatorGradient = Brush.linearGradient(
    colors = listOf(SomulecoPurple, SomulecoPink, SomulecoBlue)
)

val CreatorIntelligenceGradient = Brush.linearGradient(
    colors = listOf(SomulecoBlue, SomulecoPurple, SomulecoCyan)
)

val HeroSurfaceGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF8FAFC), Color(0xFFEFF6FF), Color(0xFFFAF5FF))
)
