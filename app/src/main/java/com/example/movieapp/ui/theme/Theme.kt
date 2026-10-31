package com.example.movieapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinemaColorScheme = darkColorScheme(
    background = DarkBackground,
    surface = DarkSurface,
    primary = WarmGold,
    secondary = VelvetRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onPrimary = DarkBackground
)

@Composable
fun MovieAppTheme(content: @Composable () -> Unit ){
    MaterialTheme(
        colorScheme = CinemaColorScheme,
        content = content
    )
}