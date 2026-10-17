package com.cinecompare.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700),
    onPrimary = Color(0xFF1A1A2E),
    primaryContainer = Color(0xFF3D2B00),
    secondary = Color(0xFFE91E63),
    onSecondary = Color.White,
    background = Color(0xFF0F0F1A),
    onBackground = Color(0xFFF0F0F0),
    surface = Color(0xFF1A1A2E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF252540),
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A2E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD700),
    secondary = Color(0xFFE91E63),
    onSecondary = Color.White,
    background = Color(0xFFF8F8FF),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    error = Color(0xFFB00020)
)

@Composable
fun CineCompareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = CineCompareTypography,
        content = content
    )
}
