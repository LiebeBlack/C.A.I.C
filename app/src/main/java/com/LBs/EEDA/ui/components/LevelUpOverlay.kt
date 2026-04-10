package com.LBs.EEDA.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import kotlinx.coroutines.delay
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator

/**
 * Overlay minimalista y premium para celebrar niveles alcanzados en E.E.D.A.
 */
@Composable
fun LevelUpOverlay(
    level: Int,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = fadeOut(tween(400)) + scaleOut(targetScale = 1.1f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.padding(32.dp).scale(scale),
                shape = CircleShape,
                color = colors.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, Modifier.size(48.dp), tint = colors.secondary)
                    Spacer(Modifier.height(8.dp))
                    Text("¡NUEVO NIVEL!", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, color = colors.secondary, letterSpacing = 2.sp))
                    Text("$level", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, color = colors.primary))
                    Text("Sigue así, explorador", style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.6f)))
                }
            }
        }
    }
}

private fun Interpolator.asEasing() = Easing { x -> getInterpolation(x) }
