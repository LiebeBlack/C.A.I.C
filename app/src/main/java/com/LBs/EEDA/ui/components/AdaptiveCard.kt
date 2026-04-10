package com.LBs.EEDA.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.domain.model.PerformanceTier
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import com.LBs.EEDA.ui.theme.LocalHardwareProfile

/**
 * Card adaptativa que cambia su estética según la fase del usuario
 * y se optimiza según el hardware detectado.
 */
@Composable
fun AdaptiveCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val config = EedaAdaptiveTheme.typoConfig
    val hardware = LocalHardwareProfile.current
    val shape = RoundedCornerShape(config.borderRadius.dp)

    // Optimización: Eliminar bordes complejos en gama baja para reducir Overdraw
    val borderModifier = if (hardware.tier != PerformanceTier.LOW) {
        Modifier.border(
            width = 1.dp,
            color = colors.cardBorder,
            shape = shape
        )
    } else Modifier

    Card(
        modifier = modifier.then(borderModifier),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        // Optimización: Desactivar sombras dinámicas en gama baja
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (hardware.tier == PerformanceTier.LOW) 0.dp else config.elevation.dp
        )
    ) {
        Box(modifier = Modifier.padding(config.cardPadding.dp)) {
            content()
        }
    }
}

/**
 * Card Glass adaptativa — se degrada elegantemente a una Card sólida en hardware antiguo.
 */
@Composable
fun AdaptiveGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val config = EedaAdaptiveTheme.typoConfig
    val hardware = LocalHardwareProfile.current
    val shape = RoundedCornerShape(config.borderRadius.dp)

    if (hardware.tier == PerformanceTier.LOW) {
        // Fallback a Card sólida para evitar blending pesado y transparencias
        AdaptiveCard(modifier, content)
    } else {
        Box(
            modifier = modifier.clip(shape)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colors.glassOverlay,
                                colors.glassOverlay.copy(alpha = colors.glassOverlay.alpha * 0.6f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colors.glassBorder,
                                colors.glassBorder.copy(alpha = colors.glassBorder.alpha * 0.3f)
                            )
                        ),
                        shape = shape
                    )
            )
            Box(modifier = Modifier.padding(config.cardPadding.dp)) {
                content()
            }
        }
    }
}
