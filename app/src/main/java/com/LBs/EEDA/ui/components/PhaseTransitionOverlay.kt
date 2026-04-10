package com.LBs.EEDA.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.ui.theme.PhaseColors
import kotlinx.coroutines.delay

/**
 * Overlay de transición animado cuando el usuario cambia de fase digital en E.E.D.A.
 * Proporciona un efecto "Wow" y feedback visual de crecimiento.
 */
@Composable
fun PhaseTransitionOverlay(
    newPhase: DigitalPhase,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colors = PhaseColors.forPhase(newPhase)

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(4000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(800)) + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = fadeOut(tween(600)) + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Círculos de luz de fondo
                    Box(
                        Modifier
                            .size(200.dp)
                            .scale(1.5f)
                            .background(colors.primary.copy(alpha = 0.1f), CircleShape)
                    )
                    Text(
                        text = newPhase.emoji,
                        fontSize = 100.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "¡Has evolucionado!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Ahora estás en la fase",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colors.onBackground.copy(alpha = 0.7f)
                    )
                )
                
                Text(
                    text = newPhase.displayName.uppercase(),
                    style = MaterialTheme.typography.displaySmall.copy(
                        color = colors.primary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = newPhase.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = colors.onBackground.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
