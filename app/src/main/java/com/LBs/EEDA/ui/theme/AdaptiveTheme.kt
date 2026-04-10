package com.LBs.EEDA.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.LBs.EEDA.domain.model.DigitalPhase

/**
 * Sistema de tema adaptativo que evoluciona visualmente según la fase del usuario.
 * Provee acceso a colores y tipografía de fase a través del árbol de composición.
 *
 * Uso: EedaTheme.colors / EedaTheme.typoConfig
 */

val LocalPhaseColors = staticCompositionLocalOf { PhaseColors.Sensorial }
val LocalPhaseTypography = staticCompositionLocalOf { PhaseTypography.Sensorial }
val LocalDigitalPhase = compositionLocalOf { DigitalPhase.SENSORIAL }

object EedaAdaptiveTheme {
    /**
     * Paleta de colores actual de la fase.
     */
    val colors: PhaseColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalPhaseColors.current

    /**
     * Configuración de tipografía y dimensiones de la fase actual.
     */
    val typoConfig: PhaseTypographyConfig
        @Composable
        @ReadOnlyComposable
        get() = LocalPhaseTypography.current

    /**
     * Fase digital actual del usuario.
     */
    val phase: DigitalPhase
        @Composable
        @ReadOnlyComposable
        get() = LocalDigitalPhase.current
}

/**
 * Proveedor de tema adaptativo E.E.D.A. Envuelve el contenido con los CompositionLocal
 * necesarios para la fase especificada.
 */
@Composable
fun ProvideEedaTheme(
    phase: DigitalPhase,
    content: @Composable () -> Unit
) {
    val phaseColors = PhaseColors.forPhase(phase)
    val phaseTypo = PhaseTypography.forPhase(phase)

    CompositionLocalProvider(
        LocalPhaseColors provides phaseColors,
        LocalPhaseTypography provides phaseTypo,
        LocalDigitalPhase provides phase,
        content = content
    )
}
