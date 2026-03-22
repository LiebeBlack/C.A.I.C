package com.liebeblack.isla_digital.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IslaColors.OceanBlue,
    secondary = IslaColors.Sunflower,
    tertiary = IslaColors.SunsetPink,
    background = IslaColors.SoftWhite,
    surface = IslaColors.White,
    error = IslaColors.CoralReef,
    onPrimary = Color.White,
    onSecondary = IslaColors.Charcoal,
    onBackground = IslaColors.Charcoal,
    onSurface = IslaColors.Charcoal
)

@Composable
fun IslaDigitalTheme(
    content: @Composable () -> Unit
) {
    // Para una app de niños, solemos preferir el tema claro o uno muy vibrante
    // Por ahora seguiremos el diseño de Flutter
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
