package com.track.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
    dynamicColor: Boolean = false, // Set to false to maintain brand identity
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
