package com.liebeblack.isla_digital.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.liebeblack.isla_digital.domain.model.SkillNode
import com.liebeblack.isla_digital.ui.theme.IslaAdaptiveTheme

/**
 * Vista visual de un nodo del Árbol de Habilidades Digitales.
 * Muestra nombre, categoría, estado (bloqueado/desbloqueado/completado)
 * y barra de progreso XP.
 */
@Composable
fun SkillTreeNodeView(
    skillNode: SkillNode,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    xpEarned: Int = 0,
    onClick: () -> Unit = {}
) {
    val colors = IslaAdaptiveTheme.colors
    val config = IslaAdaptiveTheme.typoConfig

    val nodeAlpha = when {
        isCompleted -> 1f
        isUnlocked -> 0.9f
        else -> 0.45f
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            isCompleted -> colors.success
            isUnlocked -> colors.primary
            else -> colors.divider
        },
        animationSpec = tween(400),
        label = "nodeBorder"
    )

    val bgColor = when {
        isCompleted -> colors.success.copy(alpha = 0.1f)
        isUnlocked -> colors.surface
        else -> colors.surfaceVariant.copy(alpha = 0.5f)
    }

    val progress = if (skillNode.xpRequired > 0) {
        xpEarned.toFloat() / skillNode.xpRequired
    } else 0f

    Column(
        modifier = modifier
            .width(140.dp)
            .clip(RoundedCornerShape(config.borderRadius.dp))
            .background(bgColor)
            .border(
                width = if (isCompleted) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(config.borderRadius.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono de estado
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = when {
                    isCompleted -> colors.success.copy(alpha = 0.2f)
                    isUnlocked -> colors.primary.copy(alpha = 0.15f)
                    else -> colors.divider.copy(alpha = 0.3f)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Rounded.Check
                            !isUnlocked -> Icons.Rounded.Lock
                            else -> Icons.Rounded.Check // Placeholder, idealmente el ícono de la categoría
                        },
                        contentDescription = null,
                        tint = when {
                            isCompleted -> colors.success
                            isUnlocked -> colors.primary
                            else -> colors.onBackground.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nombre del skill
        Text(
            text = skillNode.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colors.onBackground.copy(alpha = nodeAlpha)
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Categoría
        Text(
            text = skillNode.category.displayName,
            style = MaterialTheme.typography.labelSmall.copy(
                color = colors.onBackground.copy(alpha = 0.5f)
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Barra de progreso XP
        if (isUnlocked && !isCompleted) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.progressTrack)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(colors.progressFill, colors.primary)
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$xpEarned / ${skillNode.xpRequired} XP",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colors.onBackground.copy(alpha = 0.5f)
                )
            )
        }

        // Badge completado
        if (isCompleted) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "✅ Completado",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = colors.success,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
