package com.LBs.EEDA.ui.components.gamification

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.educational.analytics.TrendDirection
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.Achievement
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.AchievementRarity
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.AchievementCategory
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.UserLevel
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.UserRank
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.Leaderboard
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.LeaderboardEntry
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.DailyReward
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.Mission
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.MissionType
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.MissionRewards
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem.RewardType

/**
 * Componentes UI para el Sistema de Gamificación EEDA 2026
 * Desarrollado por Yoangel Gómez con tecnología de LB's
 */

/**
 * Tarjeta de nivel de usuario con barra de progreso animada
 */
@Composable
fun UserLevelCard(
    userLevel: UserLevel,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = userLevel.currentXP.toFloat() / userLevel.xpForNextLevel.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "xp_progress"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = getRankColor(userLevel.rank)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header con nivel y rango
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de nivel
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${userLevel.currentLevel}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                // Info de rango
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = userLevel.rank.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = userLevel.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Barra de progreso
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // XP info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${userLevel.currentXP} / ${userLevel.xpForNextLevel} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Text(
                    text = "Total: ${userLevel.totalXPEarned} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun getRankColor(rank: UserRank): Color {
    return when (rank) {
        UserRank.BRONZE -> Color(0xFFCD7F32)
        UserRank.SILVER -> Color(0xFFC0C0C0)
        UserRank.GOLD -> Color(0xFFFFD700)
        UserRank.PLATINUM -> Color(0xFFE5E4E2)
        UserRank.DIAMOND -> Color(0xFFB9F2FF)
        UserRank.MASTER -> Color(0xFF9D00FF)
        UserRank.LEGEND -> Brush.verticalGradient(
            listOf(Color(0xFFFFD700), Color(0xFFFF6B00))
        ).let { Color(0xFFFF6B00) }
    }
}

/**
 * Grid de logros desbloqueados y bloqueados
 */
@Composable
fun AchievementGrid(
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Categorías
        val categories = achievements.groupBy { it.category }
        
        categories.forEach { (category, categoryAchievements) ->
            CategorySection(
                category = category,
                achievements = categoryAchievements,
                onAchievementClick = onAchievementClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CategorySection(
    category: GamificationSystem.AchievementCategory,
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit
) {
    Text(
        text = category.name.replace("_", " ").capitalize(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    
    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        achievements.forEach { achievement ->
            AchievementCard(
                achievement = achievement,
                onClick = { onAchievementClick(achievement) }
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (achievement.unlocked) 1.0f else 0.95f,
        label = "achievement_scale"
    )
    
    Card(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clickable(enabled = achievement.unlocked, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) 
                getRarityColor(achievement.rarity)
            else 
                Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = achievement.icon,
                    fontSize = 32.sp
                )
                
                if (!achievement.unlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun getRarityColor(rarity: GamificationSystem.AchievementRarity): Color {
    return when (rarity) {
        GamificationSystem.AchievementRarity.COMMON -> Color(0xFF7CB342)
        GamificationSystem.AchievementRarity.RARE -> Color(0xFF42A5F5)
        GamificationSystem.AchievementRarity.EPIC -> Color(0xFFAB47BC)
        GamificationSystem.AchievementRarity.LEGENDARY -> Color(0xFFFFA726)
        GamificationSystem.AchievementRarity.MYTHIC -> Color(0xFFEF5350)
    }
}

/**
 * Panel de leaderboard
 */
@Composable
fun LeaderboardPanel(
    leaderboard: Leaderboard,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = leaderboard.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(leaderboard.timeFrame.name)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top 3
            val top3 = leaderboard.entries.take(3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Segundo lugar
                if (top3.size > 1) {
                    LeaderboardPosition(
                        entry = top3[1],
                        position = 2,
                        color = Color(0xFFC0C0C0)
                    )
                }
                
                // Primer lugar (más grande)
                if (top3.isNotEmpty()) {
                    LeaderboardPosition(
                        entry = top3[0],
                        position = 1,
                        color = Color(0xFFFFD700),
                        isFirst = true
                    )
                }
                
                // Tercer lugar
                if (top3.size > 2) {
                    LeaderboardPosition(
                        entry = top3[2],
                        position = 3,
                        color = Color(0xFFCD7F32)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resto de posiciones
            leaderboard.entries.drop(3).take(7).forEach { entry ->
                LeaderboardRow(entry = entry, isCurrentUser = entry.userId == leaderboard.userEntry?.userId)
            }
            
            // Usuario actual (si no está en top 10)
            leaderboard.userEntry?.let { userEntry ->
                if (leaderboard.entries.take(10).none { it.userId == userEntry.userId }) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    LeaderboardRow(entry = userEntry, isCurrentUser = true, highlight = true)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardPosition(
    entry: LeaderboardEntry,
    position: Int,
    color: Color,
    isFirst: Boolean = false
) {
    val size = if (isFirst) 100.dp else 80.dp
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Podio/Posición
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.avatar,
                fontSize = if (isFirst) 48.sp else 36.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Nombre y puntaje
        Text(
            text = entry.userName.take(8),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isFirst) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = "${entry.score}",
            style = MaterialTheme.typography.bodySmall,
            color = color
        )
        
        // Tendencia
        TrendIndicator(trend = entry.trend)
    }
}

@Composable
private fun LeaderboardRow(
    entry: LeaderboardEntry,
    isCurrentUser: Boolean,
    highlight: Boolean = false
) {
    val backgroundColor = when {
        highlight -> MaterialTheme.colorScheme.primaryContainer
        isCurrentUser -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${entry.rank}",
            modifier = Modifier.width(40.dp),
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = entry.avatar,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.userName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "Nivel ${entry.level}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "${entry.score}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        
        TrendIndicator(trend = entry.trend)
    }
}

@Composable
private fun TrendIndicator(trend: TrendDirection) {
    val (icon, color) = when (trend) {
        TrendDirection.IMPROVING -> Pair(Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF4CAF50))
        TrendDirection.DECLINING -> Pair(Icons.AutoMirrored.Filled.TrendingDown, Color(0xFFF44336))
        TrendDirection.STABLE -> Pair(Icons.AutoMirrored.Filled.TrendingFlat, Color(0xFF9E9E9E))
        else -> Pair(Icons.Default.Remove, Color.Gray)
    }
    
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(16.dp)
    )
}

/**
 * Calendario de recompensas diarias
 */
@Composable
fun DailyRewardsCalendar(
    rewards: List<DailyReward>,
    currentDay: Int,
    onClaim: (DailyReward) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recompensas Diarias",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Día $currentDay de 7",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grid de días
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rewards.forEach { reward ->
                    DailyRewardItem(
                        reward = reward,
                        isCurrentDay = reward.day == currentDay,
                        onClaim = { onClaim(reward) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyRewardItem(
    reward: DailyReward,
    isCurrentDay: Boolean,
    onClaim: () -> Unit
) {
    val isClaimed = reward.claimed
    val canClaim = isCurrentDay && !isClaimed
    
    val scale by animateFloatAsState(
        targetValue = if (isCurrentDay) 1.1f else 1.0f,
        label = "reward_scale"
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(if (isCurrentDay) 60.dp else 50.dp)
                .scale(scale)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isClaimed -> Color(0xFF4CAF50)
                        isCurrentDay -> MaterialTheme.colorScheme.primary
                        else -> Color.Gray.copy(alpha = 0.3f)
                    }
                )
                .clickable(enabled = canClaim, onClick = onClaim),
            contentAlignment = Alignment.Center
        ) {
            when {
                isClaimed -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Reclamado",
                    tint = Color.White
                )
                else -> Text(
                    text = when (reward.rewardType) {
                        GamificationSystem.RewardType.XP -> "${reward.amount}XP"
                        GamificationSystem.RewardType.COINS -> "${reward.amount}🪙"
                        GamificationSystem.RewardType.HINT_TOKEN -> "💡"
                        GamificationSystem.RewardType.COSMETIC -> "✨"
                        GamificationSystem.RewardType.BOOSTER -> "⚡"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Día ${reward.day}",
            style = MaterialTheme.typography.bodySmall,
            color = if (isCurrentDay) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

/**
 * Lista de misiones activas
 */
@Composable
fun ActiveMissionsList(
    missions: List<Mission>,
    onMissionClick: (Mission) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Misiones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = { /* Ver todas */ }) {
                    Text("Ver todas")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            missions.forEach { mission ->
                MissionItem(
                    mission = mission,
                    onClick = { onMissionClick(mission) }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MissionItem(
    mission: Mission,
    onClick: () -> Unit
) {
    val progress = mission.requirements.sumOf { it.currentValue }.toFloat() / 
                   mission.requirements.sumOf { it.targetValue }.toFloat()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mission.completed) 
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = mission.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (mission.completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completada",
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progreso
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "+${mission.rewards.xp} XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Notificación de logro desbloqueado
 */
@Composable
fun AchievementUnlockNotification(
    achievement: Achievement,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = getRarityColor(achievement.rarity)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de logro
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.icon,
                        fontSize = 48.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "¡LOGRO DESBLOQUEADO!",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Recompensa
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${achievement.reward.xpBonus} XP",
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = getRarityColor(achievement.rarity)
                    )
                ) {
                    Text("¡Genial!")
                }
            }
        }
    }
}

/**
 * Animación de subida de nivel
 */
@Composable
fun LevelUpAnimation(
    newLevel: Int,
    newTitle: String,
    onAnimationComplete: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        visible = false
        onAnimationComplete()
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = spring(dampingRatio = 0.3f)) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¡SUBIDA DE NIVEL!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFF6B00))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$newLevel",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = newTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

// Utilidades
private fun String.capitalize(): String = 
    this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
