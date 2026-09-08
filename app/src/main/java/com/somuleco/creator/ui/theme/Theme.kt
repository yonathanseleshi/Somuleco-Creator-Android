package com.somuleco.creator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SomulecoBlue,
    onPrimary = Color.White,
    primaryContainer = SomulecoBlueLight,
    onPrimaryContainer = SomulecoBlueDark,
    secondary = SomulecoPurple,
    onSecondary = Color.White,
    secondaryContainer = SomulecoPurpleLight,
    onSecondaryContainer = SomulecoPurpleDark,
    tertiary = SomulecoPink,
    onTertiary = Color.White,
    tertiaryContainer = SomulecoPinkLight,
    onTertiaryContainer = SomulecoPinkDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderStrong
)

private val DarkColorScheme = darkColorScheme(
    primary = SomulecoBlue,
    onPrimary = Color.White,
    secondary = SomulecoPurple,
    onSecondary = Color.White,
    tertiary = SomulecoPink,
    onTertiary = Color.White,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

@Composable
fun SomulecoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally maintain the bright, warm human Somuleco palette as specified in the Design Guide
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias for template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SomulecoTheme(darkTheme = darkTheme, content = content)
}
