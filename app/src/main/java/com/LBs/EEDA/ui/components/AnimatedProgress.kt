package com.LBs.EEDA.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import kotlin.math.min

/**
 * Componentes de progreso animados para E.E.D.A
 */

@Composable
fun AnimatedCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Int = 120,
    strokeWidth: Float = 12f,
    color: Color = EedaAdaptiveTheme.colors.primary,
    trackColor: Color = color.copy(alpha = 0.2f),
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        } else {
            snap()
        },
        label = "progress"
    )

    val percentage = (animatedProgress * 100).toInt()

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.dp.toPx()
            val stroke = strokeWidth.dp.toPx()
            val radius = (canvasSize - stroke) / 2
            val center = Offset(canvasSize / 2, canvasSize / 2)

            // Track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            val sweepAngle = 360 * animatedProgress
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(canvasSize - stroke, canvasSize - stroke),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
fun AnimatedLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 12,
    color: Color = EedaAdaptiveTheme.colors.primary,
    trackColor: Color = color.copy(alpha = 0.2f),
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) {
            tween(durationMillis = 800, easing = FastOutSlowInEasing)
        } else {
            snap()
        },
        label = "linear_progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(height.dp / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(color, color.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(height.dp / 2)
                )
        )
    }
}

@Composable
fun SegmentedProgress(
    segments: Int,
    completedSegments: Int,
    modifier: Modifier = Modifier,
    color: Color = EedaAdaptiveTheme.colors.primary
) {
    val animatedCompleted by animateIntAsState(
        targetValue = completedSegments,
        animationSpec = tween(500),
        label = "segments"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(segments) { index ->
            val isCompleted = index < animatedCompleted
            val segmentColor by animateColorAsState(
                targetValue = if (isCompleted) color else color.copy(alpha = 0.2f),
                animationSpec = tween(300),
                label = "segment_color"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(segmentColor)
            )
        }
    }
}

@Composable
fun XpProgressBar(
    currentXp: Long,
    nextLevelXp: Long,
    level: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (nextLevelXp > 0) {
        (currentXp.toFloat() / nextLevelXp).coerceIn(0f, 1f)
    } else 0f

    val colors = EedaAdaptiveTheme.colors

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nivel $level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = "$currentXp / $nextLevelXp XP",
                    fontSize = 12.sp,
                    color = colors.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedLinearProgress(
                progress = progress,
                height = 10,
                color = colors.primary,
                trackColor = colors.primary.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${((1 - progress) * 100).toInt()}% para nivel ${level + 1}",
                fontSize = 11.sp,
                color = colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SkillProgressIndicator(
    skillName: String,
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
    color: Color = EedaAdaptiveTheme.colors.primary
) {
    val progress = current.toFloat() / max

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = skillName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$current / $max",
                fontSize = 12.sp,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        AnimatedLinearProgress(
            progress = progress,
            height = 8,
            color = color
        )
    }
}

@Composable
fun StreakFlame(
    streakDays: Int,
    modifier: Modifier = Modifier,
    size: Int = 48
) {
    val infiniteTransition = rememberInfiniteTransition(label = "flame")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_alpha"
    )

    val flameColor = when {
        streakDays >= 30 -> Color(0xFF9C27B0) // Purple legendary
        streakDays >= 14 -> Color(0xFFFF9800) // Orange epic
        streakDays >= 7 -> Color(0xFFFF5722) // Red rare
        else -> Color(0xFFFF5722) // Normal flame
    }

    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size((size * scale).dp)
                .clip(CircleShape)
                .background(flameColor.copy(alpha = 0.3f * alpha))
        )

        // Inner flame
        Box(
            modifier = Modifier
                .size((size * 0.7f).dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFEB3B), // Yellow center
                            flameColor,
                            flameColor.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔥",
                fontSize = (size * 0.5f).sp
            )
        }

        // Days badge
        if (streakDays > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size((size * 0.4f).dp)
                    .clip(CircleShape)
                    .background(EedaAdaptiveTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = streakDays.toString(),
                    fontSize = (size * 0.2f).sp,
                    fontWeight = FontWeight.Black,
                    color = flameColor
                )
            }
        }
    }
}

@Composable
fun AchievementProgressBadge(
    icon: String,
    title: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colors = EedaAdaptiveTheme.colors

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colors.primary.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(colors.primary)
                )
            }
        }
    }
}

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    size: Int = 48,
    color: Color = EedaAdaptiveTheme.colors.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(size.dp)) {
        val canvasSize = size.dp.toPx()
        val stroke = 4.dp.toPx()
        val radius = (canvasSize - stroke) / 2
        val center = Offset(canvasSize / 2, canvasSize / 2)

        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = stroke)
        )

        drawArc(
            color = color,
            startAngle = rotation,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(stroke / 2, stroke / 2),
            size = Size(canvasSize - stroke, canvasSize - stroke),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PulseEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        content()
    }
}
