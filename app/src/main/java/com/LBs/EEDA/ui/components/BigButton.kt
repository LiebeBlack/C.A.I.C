package com.LBs.EEDA.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.PerformanceTier
import com.LBs.EEDA.ui.theme.LocalHardwareProfile

/**
 * Botón "BigButton" con efecto 3D y feedback háptico.
 * Optimizado para hardware:
 * - HIGH/MEDIUM: Animaciones completas y sombras 3D.
 * - LOW: Animación simplificada y sin degradados complejos para maximizar FPS.
 */
@Composable
fun BigButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Int = 70
) {
    val haptic = LocalHapticFeedback.current
    val hardware = LocalHardwareProfile.current
    var isPressed by remember { mutableStateOf(false) }
    val currentOnClick by rememberUpdatedState(onClick)
    
    val darkColor = lerp(color, Color.Black, 0.2f)
    
    // Optimización: Desactivar animaciones de DP en gama muy baja si es necesario, 
    // o usar valores estáticos. Aquí mantenemos una versión simplificada.
    val isLowTier = hardware.tier == PerformanceTier.LOW
    
    val shadowHeight by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (isLowTier) 3.dp else 6.dp, 
        label = "shadowHeight"
    )
    val topPadding by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp, 
        label = "topPadding"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height((height + 6).dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        // Feedback háptico solo si el hardware lo permite/es eficiente
                        if (!hardware.lowPowerMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false
                        }
                        currentOnClick()
                    }
                )
            }
    ) {
        Spacer(modifier = Modifier.height(topPadding))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .background(darkColor, RoundedCornerShape(24.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.dp)
                    .offset { IntOffset(0, -shadowHeight.roundToPx()) }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        // Optimización: Color sólido en lugar de gradiente en Gama Baja
                        brush = if (isLowTier) {
                            Brush.verticalGradient(listOf(color, color))
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(color, lerp(color, darkColor, 0.3f))
                            )
                        }
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = label.uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
