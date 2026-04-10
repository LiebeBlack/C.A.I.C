package com.LBs.EEDA.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

/**
 * Indicador visual de la fase actual del usuario con barra de progreso.
 */
@Composable
fun PhaseIndicator(
    phase: DigitalPhase,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    progress: Float = 0f // Progreso dentro de la fase (0.0 a 1.0)
) {
    val colors = EedaAdaptiveTheme.colors
    val config = EedaAdaptiveTheme.typoConfig

    val icon: ImageVector = when (phase) {
        DigitalPhase.SENSORIAL -> Icons.Rounded.Face
        DigitalPhase.CREATIVE -> Icons.Rounded.Palette
        DigitalPhase.PROFESSIONAL -> Icons.Rounded.Work
        DigitalPhase.INNOVATOR -> Icons.Rounded.RocketLaunch
    }

    val gradientColors = listOf(colors.primary, colors.secondary)

    if (compact) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(config.borderRadius.dp))
                .background(brush = Brush.horizontalGradient(gradientColors.map { it.copy(alpha = 0.15f) }))
                .border(width = 1.dp, brush = Brush.horizontalGradient(gradientColors.map { it.copy(alpha = 0.3f) }), shape = RoundedCornerShape(config.borderRadius.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = phase.displayName, tint = colors.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = phase.displayName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = colors.primary))
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(config.borderRadius.dp))
                .background(brush = Brush.horizontalGradient(gradientColors.map { it.copy(alpha = 0.1f) }))
                .border(width = 1.dp, brush = Brush.horizontalGradient(gradientColors.map { it.copy(alpha = 0.25f) }), shape = RoundedCornerShape(config.borderRadius.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(config.borderRadius.dp / 2), color = colors.primary.copy(alpha = 0.15f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "${phase.emoji} Fase ${phase.displayName}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onBackground))
                    Text(text = phase.description, style = MaterialTheme.typography.bodySmall.copy(color = colors.onBackground.copy(alpha = 0.7f)), maxLines = 1)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Barra de progreso de la fase
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progreso de Fase", style = MaterialTheme.typography.labelSmall.copy(color = colors.onBackground.copy(alpha = 0.6f)))
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = colors.primary))
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = colors.primary,
                    trackColor = colors.primary.copy(alpha = 0.1f),
                )
            }
        }
    }
}
