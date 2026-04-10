package com.LBs.EEDA.domain.model.gamification

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Sistema completo de gamificación E.E.D.A 2026
 * Maneja XP, niveles, rachas, logros y recompensas
 */
@Serializable
data class GamificationState(
    val totalXp: Long = 0,
    val currentLevel: Int = 1,
    val currentTier: Tier = Tier.EXPLORER,
    val streakDays: Int = 0,
    val lastActiveTimestamp: Long = 0,
    val achievements: List<Achievement> = emptyList(),
    val badges: List<Badge> = emptyList(),
    val dailyQuests: List<DailyQuest> = emptyList(),
    val weeklyQuests: List<WeeklyQuest> = emptyList(),
    val specialEvents: List<SpecialEvent> = emptyList(),
    val unlockedTitles: List<String> = listOf("Explorador Digital"),
    val currentTitle: String = "Explorador Digital",
    val statistics: GameStatistics = GameStatistics()
)

@Serializable
enum class Tier(val displayName: String, val minLevel: Int, val color: String) {
    EXPLORER("Explorador", 1, "#4CAF50"),
    APPRENTICE("Aprendiz", 10, "#2196F3"),
    SPECIALIST("Especialista", 25, "#9C27B0"),
    EXPERT("Experto", 50, "#FF9800"),
    MASTER("Maestro", 75, "#F44336"),
    LEGEND("Leyenda", 100, "#FFD700"),
    INNOVATOR("Innovador", 150, "#00BCD4")
}

@Serializable
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val rarity: Rarity,
    val xpReward: Long,
    val unlockedAt: Long? = null,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val icon: String
) {
    val isUnlocked: Boolean get() = unlockedAt != null
    val progressPercent: Float get() = progress.toFloat() / maxProgress
}

@Serializable
enum class AchievementCategory {
    LEARNING,      // Completar lecciones
    SKILL,         // Desbloquear habilidades
    SOCIAL,        // Interacciones sociales
    EXPLORATION,   // Explorar contenido
    MASTERY,       // Dominar temas
    STREAK,        // Rachas de actividad
    SPECIAL        // Eventos especiales
}

@Serializable
enum class Rarity(val displayName: String, val weight: Int) {
    COMMON("Común", 60),
    UNCOMMON("Poco Común", 25),
    RARE("Raro", 10),
    EPIC("Épico", 4),
    LEGENDARY("Legendario", 1)
}

@Serializable
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val category: BadgeCategory,
    val tier: BadgeTier,
    val earnedAt: Long,
    val displayOrder: Int
)

@Serializable
enum class BadgeCategory {
    SECURITY, CREATIVITY, LOGIC, LEADERSHIP, EXPLORATION, COLLABORATION
}

@Serializable
enum class BadgeTier(val stars: Int) {
    BRONZE(1), SILVER(2), GOLD(3), PLATINUM(4), DIAMOND(5)
}

@Serializable
data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val type: QuestType,
    val targetAmount: Int,
    val currentAmount: Int = 0,
    val xpReward: Long,
    val expiresAt: Long,
    val completed: Boolean = false
)

@Serializable
data class WeeklyQuest(
    val id: String,
    val title: String,
    val description: String,
    val objectives: List<QuestObjective>,
    val xpReward: Long,
    val badgeReward: String? = null,
    val expiresAt: Long,
    val completed: Boolean = false
)

@Serializable
data class QuestObjective(
    val type: QuestType,
    val target: Int,
    val current: Int = 0
)

@Serializable
enum class QuestType {
    COMPLETE_LESSONS,
    EARN_XP,
    UNLOCK_SKILLS,
    PLAY_MINIGAMES,
    MAINTAIN_STREAK,
    ASSESSMENT_SCORE,
    EXPLORE_MODULES,
    HELP_OTHERS
}

@Serializable
data class SpecialEvent(
    val id: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val bonusMultiplier: Float,
    val exclusiveRewards: List<String>,
    val isActive: Boolean
)

@Serializable
data class GameStatistics(
    val totalLessonsCompleted: Int = 0,
    val totalSkillsUnlocked: Int = 0,
    val totalMinigamesPlayed: Int = 0,
    val totalAssessmentsPassed: Int = 0,
    val totalTimeSpentMinutes: Int = 0,
    val highestStreak: Int = 0,
    val perfectScores: Int = 0,
    val hintsUsed: Int = 0,
    val questsCompleted: Int = 0
)

/**
 * Motor de Gamificación - Lógica de Negocio Completa
 */
class GamificationEngine(private val state: GamificationState) {

    companion object {
        const val XP_BASE = 100
        const val LEVEL_SCALING = 1.15
        const val STREAK_BONUS_MULTIPLIER = 0.1f
        const val MAX_DAILY_QUESTS = 3
        const val MAX_WEEKLY_QUESTS = 2
    }

    /**
     * Calcula XP necesario para subir al siguiente nivel
     */
    fun xpForNextLevel(): Long {
        return (XP_BASE * Math.pow(LEVEL_SCALING, state.currentLevel - 1.0)).toLong()
    }

    /**
     * Calcula XP total acumulado para el nivel actual
     */
    fun xpForCurrentLevel(): Long {
        var total = 0L
        for (i in 1 until state.currentLevel) {
            total += (XP_BASE * Math.pow(LEVEL_SCALING, i - 1.0)).toLong()
        }
        return total
    }

    /**
     * Progreso hacia el siguiente nivel (0.0 - 1.0)
     */
    fun levelProgress(): Float {
        val currentLevelBase = xpForCurrentLevel()
        val nextLevelXp = xpForNextLevel()
        val currentLevelXp = state.totalXp - currentLevelBase
        return (currentLevelXp.toFloat() / nextLevelXp).coerceIn(0f, 1f)
    }

    /**
     * Agrega XP al usuario con todos los multiplicadores
     */
    fun addXp(baseAmount: Long, source: XpSource): GamificationState {
        val multiplier = calculateMultiplier(source)
        val finalXp = (baseAmount * multiplier).toLong()
        val newTotal = state.totalXp + finalXp
        val newLevel = calculateLevel(newTotal)
        val newTier = calculateTier(newLevel)

        return state.copy(
            totalXp = newTotal,
            currentLevel = newLevel,
            currentTier = newTier,
            statistics = state.statistics.copy(
                totalTimeSpentMinutes = state.statistics.totalTimeSpentMinutes + when(source) {
                    is XpSource.LessonCompletion -> source.durationMinutes
                    else -> 0
                }
            )
        )
    }

    /**
     * Calcula multiplicadores de XP basados en rachas y eventos
     */
    private fun calculateMultiplier(source: XpSource): Float {
        var multiplier = 1.0f

        // Bonus por racha
        multiplier += (state.streakDays * STREAK_BONUS_MULTIPLIER).coerceAtMost(0.5f)

        // Bonus por evento especial
        state.specialEvents.find { it.isActive }?.let {
            multiplier *= it.bonusMultiplier
        }

        // Bonus por source específico
        when (source) {
            is XpSource.PerfectScore -> multiplier *= 1.5f
            is XpSource.FirstTime -> multiplier *= 1.2f
            is XpSource.SpeedBonus -> multiplier *= 1.3f
            else -> {}
        }

        return multiplier
    }

    /**
     * Calcula el nivel basado en XP total
     */
    private fun calculateLevel(totalXp: Long): Int {
        var level = 1
        var accumulated = 0L
        while (accumulated < totalXp) {
            accumulated += (XP_BASE * Math.pow(LEVEL_SCALING, level - 1.0)).toLong()
            level++
        }
        return level.coerceAtMost(200)
    }

    /**
     * Determina el tier basado en nivel
     */
    private fun calculateTier(level: Int): Tier {
        return Tier.entries.findLast { level >= it.minLevel } ?: Tier.EXPLORER
    }

    /**
     * Actualiza la racha de actividad diaria
     */
    fun updateStreak(): GamificationState {
        val now = Instant.now().epochSecond
        val lastActive = state.lastActiveTimestamp
        val daysSinceLastActive = (now - lastActive) / 86400

        val newStreak = when {
            daysSinceLastActive == 0L -> state.streakDays // Mismo día
            daysSinceLastActive == 1L -> state.streakDays + 1 // Continúa racha
            else -> 1 // Se perdió la racha
        }

        return state.copy(
            streakDays = newStreak,
            lastActiveTimestamp = now,
            statistics = state.statistics.copy(
                highestStreak = maxOf(state.statistics.highestStreak, newStreak)
            )
        )
    }

    /**
     * Verifica y desbloquea logros nuevos
     */
    fun checkAchievements(): Pair<GamificationState, List<Achievement>> {
        val newUnlocks = mutableListOf<Achievement>()
        var updatedState = state

        AchievementCatalog.getAllAchievements().forEach { achievement ->
            if (!state.achievements.any { it.id == achievement.id && it.isUnlocked }) {
                val progress = calculateAchievementProgress(achievement)
                if (progress >= achievement.maxProgress) {
                    val unlocked = achievement.copy(
                        unlockedAt = Instant.now().epochSecond,
                        progress = achievement.maxProgress
                    )
                    newUnlocks.add(unlocked)
                    updatedState = updatedState.copy(
                        achievements = updatedState.achievements + unlocked
                    )
                    // Dar XP por logro
                    updatedState = GamificationEngine(updatedState)
                        .addXp(achievement.xpReward, XpSource.AchievementUnlock)
                }
            }
        }

        return updatedState to newUnlocks
    }

    private fun calculateAchievementProgress(achievement: Achievement): Int {
        return when (achievement.category) {
            AchievementCategory.LEARNING -> state.statistics.totalLessonsCompleted
            AchievementCategory.SKILL -> state.statistics.totalSkillsUnlocked
            AchievementCategory.EXPLORATION -> state.statistics.totalMinigamesPlayed
            AchievementCategory.MASTERY -> state.statistics.perfectScores
            AchievementCategory.STREAK -> state.streakDays
            else -> 0
        }
    }

    /**
     * Genera quests diarias aleatorias
     */
    fun generateDailyQuests(): List<DailyQuest> {
        if (state.dailyQuests.any { it.expiresAt > Instant.now().epochSecond }) {
            return state.dailyQuests
        }

        val tomorrow = Instant.now().plusSeconds(86400).epochSecond
        return listOf(
            DailyQuest(
                id = "daily_1_${System.currentTimeMillis()}",
                title = "Estudiante Dedicado",
                description = "Completa 2 lecciones hoy",
                type = QuestType.COMPLETE_LESSONS,
                targetAmount = 2,
                xpReward = 150,
                expiresAt = tomorrow
            ),
            DailyQuest(
                id = "daily_2_${System.currentTimeMillis()}",
                title = "Cazador de XP",
                description = "Gana 500 XP",
                type = QuestType.EARN_XP,
                targetAmount = 500,
                xpReward = 200,
                expiresAt = tomorrow
            ),
            DailyQuest(
                id = "daily_3_${System.currentTimeMillis()}",
                title = "Desbloqueador",
                description = "Desbloquea 1 nueva habilidad",
                type = QuestType.UNLOCK_SKILLS,
                targetAmount = 1,
                xpReward = 300,
                expiresAt = tomorrow
            )
        )
    }

    /**
     * Completa una quest diaria
     */
    fun completeDailyQuest(questId: String): GamificationState {
        val updatedQuests = state.dailyQuests.map { quest ->
            if (quest.id == questId && !quest.completed) {
                quest.copy(completed = true)
            } else quest
        }

        val quest = state.dailyQuests.find { it.id == questId }
        var updatedState = state.copy(dailyQuests = updatedQuests)

        quest?.let {
            updatedState = GamificationEngine(updatedState)
                .addXp(it.xpReward, XpSource.QuestCompletion)
        }

        return updatedState
    }

    /**
     * Obtiene título actual basado en tier y logros
     */
    fun getCurrentTitle(): String {
        val specialTitle = state.unlockedTitles.lastOrNull()
        return specialTitle ?: state.currentTier.displayName
    }

    /**
     * Calcula ranking global aproximado
     */
    fun estimateGlobalRanking(): String {
        return when (state.currentLevel) {
            in 1..9 -> "Top ${100 - state.currentLevel}%"
            in 10..24 -> "Top ${95 - (state.currentLevel - 10)}%"
            in 25..49 -> "Top ${85 - (state.currentLevel - 25) * 2}%"
            in 50..74 -> "Top ${35 - (state.currentLevel - 50)}%"
            in 75..99 -> "Top 10%"
            else -> "Top 1% - Élite"
        }
    }
}

/**
 * Catálogo completo de logros
 */
object AchievementCatalog {
    fun getAllAchievements(): List<Achievement> = listOf(
        // LEARNING
        Achievement("ach_lesson_1", "Primeros Pasos", "Completa tu primera lección",
            AchievementCategory.LEARNING, Rarity.COMMON, 50, icon = "🎓"),
        Achievement("ach_lesson_10", "Estudiante Dedicado", "Completa 10 lecciones",
            AchievementCategory.LEARNING, Rarity.UNCOMMON, 200, maxProgress = 10, icon = "📚"),
        Achievement("ach_lesson_50", "Erudito", "Completa 50 lecciones",
            AchievementCategory.LEARNING, Rarity.RARE, 1000, maxProgress = 50, icon = "🎓"),
        Achievement("ach_lesson_100", "Maestro del Conocimiento", "Completa 100 lecciones",
            AchievementCategory.LEARNING, Rarity.EPIC, 5000, maxProgress = 100, icon = "👑"),

        // SKILL
        Achievement("ach_skill_1", "Desbloqueador", "Desbloquea tu primera habilidad",
            AchievementCategory.SKILL, Rarity.COMMON, 100, icon = "🔓"),
        Achievement("ach_skill_10", "Árbol en Crecimiento", "Desbloquea 10 habilidades",
            AchievementCategory.SKILL, Rarity.UNCOMMON, 500, maxProgress = 10, icon = "🌳"),
        Achievement("ach_skill_all", "Sabiduría Completa", "Desbloquea todas las habilidades",
            AchievementCategory.SKILL, Rarity.LEGENDARY, 10000, maxProgress = 50, icon = "🧠"),

        // STREAK
        Achievement("ach_streak_3", "Constancia", "3 días consecutivos",
            AchievementCategory.STREAK, Rarity.COMMON, 150, maxProgress = 3, icon = "🔥"),
        Achievement("ach_streak_7", "Semana Perfecta", "7 días consecutivos",
            AchievementCategory.STREAK, Rarity.UNCOMMON, 500, maxProgress = 7, icon = "🔥"),
        Achievement("ach_streak_30", "Adicto al Aprendizaje", "30 días consecutivos",
            AchievementCategory.STREAK, Rarity.EPIC, 5000, maxProgress = 30, icon = "🔥"),

        // EXPLORATION
        Achievement("ach_minigame_10", "Jugador", "Juega 10 minijuegos",
            AchievementCategory.EXPLORATION, Rarity.COMMON, 200, maxProgress = 10, icon = "🎮"),
        Achievement("ach_minigame_100", "Gamer Educativo", "Juega 100 minijuegos",
            AchievementCategory.EXPLORATION, Rarity.RARE, 1500, maxProgress = 100, icon = "🏆"),

        // MASTERY
        Achievement("ach_perfect_5", "Sin Errores", "5 evaluaciones perfectas",
            AchievementCategory.MASTERY, Rarity.UNCOMMON, 500, maxProgress = 5, icon = "⭐"),
        Achievement("ach_perfect_20", "Perfeccionista", "20 evaluaciones perfectas",
            AchievementCategory.MASTERY, Rarity.RARE, 2000, maxProgress = 20, icon = "💎")
    )
}

/**
 * Fuentes de XP para tracking
 */
sealed class XpSource {
    data class LessonCompletion(val durationMinutes: Int) : XpSource()
    data object SkillUnlock : XpSource()
    data class MinigameScore(val score: Int) : XpSource()
    data class AssessmentPassed(val grade: Int) : XpSource()
    data object QuestCompletion : XpSource()
    data object AchievementUnlock : XpSource()
    data object DailyLogin : XpSource()
    data object PerfectScore : XpSource()
    data object FirstTime : XpSource()
    data object SpeedBonus : XpSource()
    data object Referral : XpSource()
}
