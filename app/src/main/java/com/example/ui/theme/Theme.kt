package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonRed,
    secondary = EmeraldGreenLight,
    tertiary = Gold,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = White,
    onSecondary = White,
    onTertiary = DarkBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    primaryContainer = DeepBloodRed,
    secondaryContainer = EmeraldGreen
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    secondary = EmeraldGreen,
    tertiary = GoldAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    primaryContainer = DeepBloodRed,
    secondaryContainer = EmeraldGreenLight
)


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
