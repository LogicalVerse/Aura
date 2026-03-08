package com.example.aura.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Aura dark color scheme — primary theme for the app.
 * Fashion-forward, premium feel.
 */
private val AuraDarkColorScheme = darkColorScheme(
    primary = AuraPrimary,
    onPrimary = AuraOnPrimary,
    secondary = AuraSecondary,
    tertiary = AuraTertiary,
    tertiaryContainer = AuraTertiaryContainer,
    onTertiaryContainer = AuraOnTertiaryContainer,
    secondaryContainer = AuraSecondaryContainer,
    onSecondaryContainer = AuraOnSecondaryContainer,
    background = AuraBlack,
    surface = AuraSurface,
    surfaceVariant = AuraSurfaceVariant,
    onBackground = AuraTextPrimary,
    onSurface = AuraTextPrimary,
    onSurfaceVariant = AuraTextSecondary,
    outline = AuraOutline,
    error = AuraError,
    onError = AuraTextPrimary
)

/**
 * Aura light color scheme — fallback, not primary.
 */
private val AuraLightColorScheme = lightColorScheme(
    primary = AuraLightPrimary,
    onPrimary = AuraLightOnPrimary,
    background = AuraLightBackground,
    surface = AuraLightSurface,
    onBackground = AuraBlack,
    onSurface = AuraBlack,
)

/**
 * Aura theme wrapper.
 * Forces dark theme for the hackathon demo to look premium.
 *
 * Module D owner: Customize further — add dynamic color support,
 * gradient backgrounds, custom shapes, etc.
 */
@Composable
fun AuraTheme(
    darkTheme: Boolean = true, // Force dark for demo
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AuraDarkColorScheme else AuraLightColorScheme

    // Set status bar color to match theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        content = content
    )
}