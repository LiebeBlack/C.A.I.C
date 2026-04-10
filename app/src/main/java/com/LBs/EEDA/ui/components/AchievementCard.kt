package com.LBs.EEDA.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.gamification.Achievement
import com.LBs.EEDA.domain.model.gamification.Rarity
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

/**
 * Componentes de visualización de logros y badges
 */

@Composable
fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = EedaAdaptiveTheme.colors
    val rarityColor = getRarityColor(achievement.rarity)

    val isUnlocked = achievement.isUnlocked

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) colors.surface else colors.surface.copy(alpha = 0.5f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del logro
            AchievementIcon(
                emoji = achievement.icon,
                rarity = achievement.rarity,
                isUnlocked = isUnlocked,
                modifier = Modifier.size(56.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) colors.onSurface else colors.onSurface.copy(alpha = 0.5f)
                )

                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = colors.onSurface.copy(alpha = 0.6f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Progreso o estado
                if (isUnlocked) {
                    UnlockedBadge(
                        date = DateUtils.formatDisplayDate(achievement.unlockedAt ?: 0),
                        xpReward = achievement.xpReward
                    )
                } else {
                    ProgressBar(
                        current = achievement.progress,
                        max = achievement.maxProgress
                    )
                }
            }

            // Rarity badge
            RarityBadge(rarity = achievement.rarity)
        }
    }
}

@Composable
private fun AchievementIcon(
    emoji: String,
    rarity: Rarity,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val rarityColor = getRarityColor(rarity)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                if (isUnlocked) {
                    Brush.radialGradient(
                        colors = listOf(
                            rarityColor.copy(alpha = 0.3f),
                            rarityColor.copy(alpha = 0.1f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.2f),
                            Color.Gray.copy(alpha = 0.1f)
                        )
                    )
                }
            )
            .border(
                width = 2.dp,
                color = if (isUnlocked) rarityColor else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 28.sp,
            modifier = Modifier.alpha(if (isUnlocked) 1f else 0.4f)
        )

        if (!isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Bloqueado",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun UnlockedBadge(date: String, xpReward: Long) {
    val colors = EedaAdaptiveTheme.colors

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Desbloqueado: $date",
            fontSize = 11.sp,
            color = colors.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            color = colors.primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "+$xpReward XP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun ProgressBar(current: Int, max: Int) {
    val colors = EedaAdaptiveTheme.colors
    val progress = current.toFloat() / max

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Progreso",
                fontSize = 11.sp,
                color = colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "$current / $max",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = colors.primary,
            trackColor = colors.primary.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun RarityBadge(rarity: Rarity) {
    val (color, label) = when (rarity) {
        Rarity.COMMON -> Pair(Color(0xFF9E9E9E), "Común")
        Rarity.UNCOMMON -> Pair(Color(0xFF4CAF50), "Especial")
        Rarity.RARE -> Pair(Color(0xFF2196F3), "Raro")
        Rarity.EPIC -> Pair(Color(0xFF9C27B0), "Épico")
        Rarity.LEGENDARY -> Pair(Color(0xFFFF9800), "Legendario")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BadgeDisplay(
    icon: String,
    tier: Int,
    modifier: Modifier = Modifier
) {
    val tierColor = when (tier) {
        1 -> Color(0xFFCD7F32) // Bronze
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFFFD700) // Gold
        4 -> Color(0xFFE5E4E2) // Platinum
        5 -> Color(0xFF00CED1) // Diamond
        else -> Color.Gray
    }

    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        // Star background
        repeat(tier) { index ->
            val rotation = (index * 72f) - 90f
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .offset(
                        x = (24 * kotlin.math.cos(Math.toRadians(rotation.toDouble()))).dp,
                        y = (24 * kotlin.math.sin(Math.toRadians(rotation.toDouble()))).dp
                    )
                    .clip(CircleShape)
                    .background(tierColor)
            )
        }

        // Center icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            tierColor.copy(alpha = 0.3f),
                            tierColor.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(2.dp, tierColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }

        // Tier indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tierColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            repeat(tier) {
                Text(
                    text = "★",
                    fontSize = 8.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AchievementUnlockedPopup(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration icon
                Text(
                    text = "🎉",
                    fontSize = 48.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¡Logro Desbloqueado!",
                    fontSize = 14.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Achievement icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    getRarityColor(achievement.rarity).copy(alpha = 0.3f),
                                    getRarityColor(achievement.rarity).copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(
                            3.dp,
                            getRarityColor(achievement.rarity),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.icon,
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = achievement.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Text(
                    text = achievement.description,
                    fontSize = 14.sp,
                    color = colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // XP reward
                Surface(
                    color = colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "+${achievement.xpReward} XP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("¡Genial!")
                }
            }
        }
    }
}

private fun getRarityColor(rarity: Rarity): Color {
    return when (rarity) {
        Rarity.COMMON -> Color(0xFF9E9E9E)
        Rarity.UNCOMMON -> Color(0xFF4CAF50)
        Rarity.RARE -> Color(0xFF2196F3)
        Rarity.EPIC -> Color(0xFF9C27B0)
        Rarity.LEGENDARY -> Color(0xFFFF9800)
    }
}
