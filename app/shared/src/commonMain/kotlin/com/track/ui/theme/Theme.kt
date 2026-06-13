package com.track.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Light Color Scheme
private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryColor,
        onPrimary = TextOnPrimary,
        secondary = SecondaryColor,
        onSecondary = TextOnPrimary,
        background = BackgroundLight,
        surface = SurfaceLight,
        onSurface = TextPrimary,
        onSurfaceVariant = TextSecondary,
        error = StatusDelayed,
        onError = TextOnPrimary,
    )

// Dark Color Scheme
private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryColor,
        onPrimary = TextOnPrimary,
        secondary = Gray300,
        onSecondary = ShopBlack,
        background = BackgroundDark,
        surface = SurfaceDark,
        onSurface = ShopWhite,
        onSurfaceVariant = Gray400,
        error = StatusDelayed,
        onError = TextOnPrimary,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
