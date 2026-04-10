package com.LBs.EEDA.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.LBs.EEDA.domain.model.DigitalPhase

/**
 * Tema principal de E.E.D.A con soporte adaptativo por fase.
 * El tema Material3 se configura dinámicamente según la fase del usuario,
 * y los CompositionLocal proveen acceso a paletas extendidas.
 */
@Composable
fun EedaTheme(
    phase: DigitalPhase = DigitalPhase.SENSORIAL,
    content: @Composable () -> Unit
) {
    val phaseColors = PhaseColors.forPhase(phase)
    val phaseTypo = PhaseTypography.forPhase(phase)

    val colorScheme = if (phaseColors.isDark) {
        darkColorScheme(
            primary = phaseColors.primary,
            onPrimary = phaseColors.onPrimary,
            primaryContainer = phaseColors.primaryVariant,
            secondary = phaseColors.secondary,
            onSecondary = phaseColors.onSecondary,
            secondaryContainer = phaseColors.secondaryVariant,
            tertiary = phaseColors.accent,
            background = phaseColors.background,
            onBackground = phaseColors.onBackground,
            surface = phaseColors.surface,
            onSurface = phaseColors.onSurface,
            surfaceVariant = phaseColors.surfaceVariant,
            error = phaseColors.error
        )
    } else {
        lightColorScheme(
            primary = phaseColors.primary,
            onPrimary = phaseColors.onPrimary,
            primaryContainer = phaseColors.primaryVariant,
            secondary = phaseColors.secondary,
            onSecondary = phaseColors.onSecondary,
            secondaryContainer = phaseColors.secondaryVariant,
            tertiary = phaseColors.accent,
            background = phaseColors.background,
            onBackground = phaseColors.onBackground,
            surface = phaseColors.surface,
            onSurface = phaseColors.onSurface,
            surfaceVariant = phaseColors.surfaceVariant,
            error = phaseColors.error
        )
    }

    ProvideEedaTheme(phase = phase) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = phaseTypo.typography,
            content = content
        )
    }
}
