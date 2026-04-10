package com.LBs.EEDA.domain.model.educational.gamification

import com.LBs.EEDA.domain.model.educational.analytics.TrendDirection
import kotlinx.serialization.Serializable

/**
 * Sistema de Gamificación Completo EEDA 2026
 * Logros, medallas, niveles, leaderboards, recompensas y progresión
 */

object GamificationSystem {
    
    // ==================== SISTEMA DE NIVELES Y XP ====================
    
    @Serializable
    data class UserLevel(
        val currentLevel: Int,
        val currentXP: Long,
        val xpForNextLevel: Long,
        val totalXPEarned: Long,
        val title: String,
        val rank: UserRank
    ) {
        companion object {
            fun calculateLevel(totalXP: Long): UserLevel {
                val level = calculateLevelFromXP(totalXP)
                val xpForCurrentLevel = calculateXPForLevel(level)
                val xpForNext = calculateXPForLevel(level + 1)
                val progress = totalXP - xpForCurrentLevel
                
                return UserLevel(
                    currentLevel = level,
                    currentXP = progress,
                    xpForNextLevel = xpForNext - xpForCurrentLevel,
                    totalXPEarned = totalXP,
                    title = getTitleForLevel(level),
                    rank = getRankForLevel(level)
                )
            }
            
            private fun calculateLevelFromXP(xp: Long): Int {
                var level = 1
                var xpNeeded = 100L
                var accumulated = 0L
                
                while (accumulated + xpNeeded <= xp && level < 100) {
                    accumulated += xpNeeded
                    xpNeeded = (xpNeeded * 1.2).toLong()
                    level++
                }
                return level
            }
            
            private fun calculateXPForLevel(level: Int): Long {
                var xp = 0L
                var xpNeeded = 100L
                for (i in 1 until level) {
                    xp += xpNeeded
                    xpNeeded = (xpNeeded * 1.2).toLong()
                }
                return xp
            }
            
            private fun getTitleForLevel(level: Int): String {
                return when (level) {
                    in 1..5 -> "Novato Curioso"
                    in 6..10 -> "Aprendiz Dedicado"
                    in 11..15 -> "Estudiante Aplicado"
                    in 16..20 -> "Conocedor Digital"
                    in 21..25 -> "Experto en Seguridad"
                    in 26..30 -> "Maestro del Cifrado"
                    in 31..40 -> "Arquitecto de Redes"
                    in 41..50 -> "Ingeniero de IA"
                    in 51..60 -> "Guardián del Ciberespacio"
                    in 61..70 -> "Visionario Tecnológico"
                    in 71..80 -> "Leyenda Educativa"
                    in 81..90 -> "Sabio de la Era Digital"
                    in 91..99 -> "Titán del Conocimiento"
                    100 -> "Dios de la Educación Digital"
                    else -> "Leyenda"
                }
            }
            
            private fun getRankForLevel(level: Int): UserRank {
                return when (level) {
                    in 1..10 -> UserRank.BRONZE
                    in 11..25 -> UserRank.SILVER
                    in 26..50 -> UserRank.GOLD
                    in 51..75 -> UserRank.PLATINUM
                    in 76..90 -> UserRank.DIAMOND
                    in 91..99 -> UserRank.MASTER
                    100 -> UserRank.LEGEND
                    else -> UserRank.BRONZE
                }
            }
        }
    }
    
    enum class UserRank {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, LEGEND
    }
    
    // ==================== SISTEMA DE LOGROS (ACHIEVEMENTS) ====================
    
    @Serializable
    data class Achievement(
        val id: String,
        val name: String,
        val description: String,
        val icon: String,
        val rarity: AchievementRarity,
        val category: AchievementCategory,
        val requirement: AchievementRequirement,
        val reward: AchievementReward,
        var unlocked: Boolean = false,
        var unlockDate: Long? = null,
        var progress: Int = 0,
        val maxProgress: Int = 1
    )
    
    enum class AchievementRarity {
        COMMON, RARE, EPIC, LEGENDARY, MYTHIC
    }
    
    enum class AchievementCategory {
        LEARNING, QUIZ, MINIGAME, SOCIAL, STREAK, EXPLORATION, MASTERY
    }
    
    @Serializable
    data class AchievementRequirement(
        val type: RequirementType,
        val targetValue: Int,
        val specificCondition: String? = null
    )
    
    enum class RequirementType {
        QUESTIONS_ANSWERED, CORRECT_ANSWERS, STREAK_COUNT, MINIGAMES_COMPLETED,
        PERFECT_SCORES, TOPICS_MASTERED, DAYS_ACTIVE, FRIENDS_HELPED,
        TIME_SPENT, CHALLENGES_WON, SPEED_RECORD, ZERO_HINTS_RUN
    }
    
    @Serializable
    data class AchievementReward(
        val xpBonus: Int,
        val cosmeticReward: String? = null,
        val titleReward: String? = null,
        val currencyReward: Int = 0
    )
    
    // Colección de logros
    fun getAllAchievements(): List<Achievement> = listOf(
        // LOGROS DE APRENDIZAJE
        Achievement(
            id = "first_steps",
            name = "Primeros Pasos",
            description = "Completa tu primera lección",
            icon = "🎯",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.LEARNING,
            requirement = AchievementRequirement(RequirementType.QUESTIONS_ANSWERED, 1),
            reward = AchievementReward(50)
        ),
        Achievement(
            id = "knowledge_seeker",
            name = "Buscador de Conocimiento",
            description = "Responde 100 preguntas correctamente",
            icon = "📚",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.LEARNING,
            requirement = AchievementRequirement(RequirementType.CORRECT_ANSWERS, 100),
            reward = AchievementReward(200)
        ),
        Achievement(
            id = "wisdom_accumulator",
            name = "Acumulador de Sabiduría",
            description = "Responde 1000 preguntas correctamente",
            icon = "🏛️",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.LEARNING,
            requirement = AchievementRequirement(RequirementType.CORRECT_ANSWERS, 1000),
            reward = AchievementReward(1000, titleReward = "Erudito")
        ),
        Achievement(
            id = "grand_scholar",
            name = "Gran Erudito",
            description = "Responde 10,000 preguntas correctamente",
            icon = "👑",
            rarity = AchievementRarity.LEGENDARY,
            category = AchievementCategory.LEARNING,
            requirement = AchievementRequirement(RequirementType.CORRECT_ANSWERS, 10000),
            reward = AchievementReward(5000, cosmeticReward = "golden_avatar_frame", titleReward = "Gran Erudito")
        ),
        
        // LOGROS DE RACHAS
        Achievement(
            id = "on_fire",
            name = "¡En Llamas!",
            description = "Alcanza una racha de 5 respuestas correctas",
            icon = "🔥",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.STREAK,
            requirement = AchievementRequirement(RequirementType.STREAK_COUNT, 5),
            reward = AchievementReward(100)
        ),
        Achievement(
            id = "unstoppable",
            name = "Imparable",
            description = "Alcanza una racha de 10 respuestas correctas",
            icon = "⚡",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.STREAK,
            requirement = AchievementRequirement(RequirementType.STREAK_COUNT, 10),
            reward = AchievementReward(300)
        ),
        Achievement(
            id = "godlike",
            name = "Asemejándose a un Dios",
            description = "Alcanza una racha de 20 respuestas correctas",
            icon = "🌟",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.STREAK,
            requirement = AchievementRequirement(RequirementType.STREAK_COUNT, 20),
            reward = AchievementReward(800, cosmeticReward = "flame_trail")
        ),
        Achievement(
            id = "legendary_streak",
            name = "Racha Legendaria",
            description = "Alcanza una racha de 50 respuestas correctas",
            icon = "🌠",
            rarity = AchievementRarity.LEGENDARY,
            category = AchievementCategory.STREAK,
            requirement = AchievementRequirement(RequirementType.STREAK_COUNT, 50),
            reward = AchievementReward(2000, cosmeticReward = "legendary_aura", titleReward = "Inmortal")
        ),
        
        // LOGROS DE EVALUACIONES
        Achievement(
            id = "quiz_champion",
            name = "Campeón de Cuestionarios",
            description = "Completa 10 cuestionarios con calificación A",
            icon = "🏆",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.QUIZ,
            requirement = AchievementRequirement(RequirementType.PERFECT_SCORES, 10),
            reward = AchievementReward(500)
        ),
        Achievement(
            id = "speed_demon",
            name = "Demonio de la Velocidad",
            description = "Completa un cuestionario con 100% de precisión en menos de 2 minutos",
            icon = "🏎️",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.QUIZ,
            requirement = AchievementRequirement(RequirementType.SPEED_RECORD, 1, "perfect_under_2min"),
            reward = AchievementReward(600, cosmeticReward = "speed_boost_effect")
        ),
        Achievement(
            id = "no_hints_needed",
            name = "Sin Ayuda Necesaria",
            description = "Completa 5 cuestionarios perfectos sin usar ningún hint",
            icon = "🧠",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.QUIZ,
            requirement = AchievementRequirement(RequirementType.ZERO_HINTS_RUN, 5),
            reward = AchievementReward(750, titleReward = "Autónomo")
        ),
        
        // LOGROS DE MINIJUEGOS
        Achievement(
            id = "game_novice",
            name = "Jugador Novato",
            description = "Completa tu primer minijuego",
            icon = "🎮",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.MINIGAME,
            requirement = AchievementRequirement(RequirementType.MINIGAMES_COMPLETED, 1),
            reward = AchievementReward(50)
        ),
        Achievement(
            id = "game_enthusiast",
            name = "Entusiasta de Juegos",
            description = "Completa 25 minijuegos",
            icon = "🕹️",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.MINIGAME,
            requirement = AchievementRequirement(RequirementType.MINIGAMES_COMPLETED, 25),
            reward = AchievementReward(250)
        ),
        Achievement(
            id = "master_gamer",
            name = "Maestro Gamer",
            description = "Completa 100 minijuegos con puntuación perfecta",
            icon = "🎲",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.MINIGAME,
            requirement = AchievementRequirement(RequirementType.MINIGAMES_COMPLETED, 100),
            reward = AchievementReward(1000, cosmeticReward = "gamer_badge")
        ),
        
        // LOGROS DE MAESTRÍA
        Achievement(
            id = "security_expert",
            name = "Experto en Seguridad",
            description = "Alcanza maestría EXPERT en todos los temas de ciberseguridad",
            icon = "🔒",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.MASTERY,
            requirement = AchievementRequirement(RequirementType.TOPICS_MASTERED, 5, "cybersecurity"),
            reward = AchievementReward(1500, titleReward = "Guardián Digital")
        ),
        Achievement(
            id = "crypto_master",
            name = "Maestro del Cifrado",
            description = "Alcanza maestría EXPERT en encriptación",
            icon = "🔐",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.MASTERY,
            requirement = AchievementRequirement(RequirementType.TOPICS_MASTERED, 1, "encryption"),
            reward = AchievementReward(1200, cosmeticReward = "crypto_shield")
        ),
        Achievement(
            id = "ai_wizard",
            name = "Mago de la IA",
            description = "Alcanza maestría EXPERT en inteligencia artificial",
            icon = "🤖",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.MASTERY,
            requirement = AchievementRequirement(RequirementType.TOPICS_MASTERED, 1, "ai_ml"),
            reward = AchievementReward(1200, cosmeticReward = "neural_crown")
        ),
        Achievement(
            id = "universal_scholar",
            name = "Erudito Universal",
            description = "Alcanza maestría en TODOS los temas disponibles",
            icon = "🌍",
            rarity = AchievementRarity.MYTHIC,
            category = AchievementCategory.MASTERY,
            requirement = AchievementRequirement(RequirementType.TOPICS_MASTERED, 20),
            reward = AchievementReward(5000, cosmeticReward = "universal_halo", titleReward = "Polímata Digital")
        ),
        
        // LOGROS SOCIALES
        Achievement(
            id = "helper",
            name = "Mano Amiga",
            description = "Ayuda a 5 amigos con sus dudas",
            icon = "🤝",
            rarity = AchievementRarity.COMMON,
            category = AchievementCategory.SOCIAL,
            requirement = AchievementRequirement(RequirementType.FRIENDS_HELPED, 5),
            reward = AchievementReward(150)
        ),
        Achievement(
            id = "mentor",
            name = "Mentor",
            description = "Ayuda a 20 amigos a completar sus primeros cuestionarios",
            icon = "👨‍🏫",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.SOCIAL,
            requirement = AchievementRequirement(RequirementType.FRIENDS_HELPED, 20),
            reward = AchievementReward(500, titleReward = "Mentor")
        ),
        
        // LOGROS DE EXPLORACIÓN
        Achievement(
            id = "explorer",
            name = "Explorador",
            description = "Completa al menos una actividad en cada categoría de temas",
            icon = "🧭",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.TOPICS_MASTERED, 6, "one_per_category"),
            reward = AchievementReward(400)
        ),
        Achievement(
            id = "dedicated",
            name = "Dedicado",
            description = "Mantén una racha de actividad diaria por 7 días",
            icon = "📅",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.DAYS_ACTIVE, 7),
            reward = AchievementReward(300)
        ),
        Achievement(
            id = "committed",
            name = "Comprometido",
            description = "Mantén una racha de actividad diaria por 30 días",
            icon = "📆",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.DAYS_ACTIVE, 30),
            reward = AchievementReward(1000, cosmeticReward = "dedication_flame")
        ),
        Achievement(
            id = "unstoppable_dedication",
            name = "Dedicación Inquebrantable",
            description = "Mantén una racha de actividad diaria por 365 días",
            icon = "🗓️",
            rarity = AchievementRarity.LEGENDARY,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.DAYS_ACTIVE, 365),
            reward = AchievementReward(5000, cosmeticReward = "eternal_flame", titleReward = "Inquebrantable")
        ),
        
        // LOGROS ESPECIALES Y OCULTOS
        Achievement(
            id = "night_owl",
            name = "Búho Nocturno",
            description = "Completa un cuestionario perfecto entre las 2 AM y 5 AM",
            icon = "🦉",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.QUESTIONS_ANSWERED, 1, "night_time_perfect"),
            reward = AchievementReward(200)
        ),
        Achievement(
            id = "early_bird",
            name = "Madrugador",
            description = "Completa un cuestionario perfecto antes de las 6 AM",
            icon = "🐦",
            rarity = AchievementRarity.RARE,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.QUESTIONS_ANSWERED, 1, "early_morning_perfect"),
            reward = AchievementReward(200)
        ),
        Achievement(
            id = "easter_egg_hunter",
            name = "Cazador de Easter Eggs",
            description = "Encuentra un Easter Egg oculto en la aplicación",
            icon = "🥚",
            rarity = AchievementRarity.EPIC,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.QUESTIONS_ANSWERED, 1, "found_easter_egg"),
            reward = AchievementReward(500, cosmeticReward = "easter_egg_hat")
        ),
        Achievement(
            id = "beta_tester",
            name = "Pionero Beta",
            description = "Usa la aplicación durante la fase beta",
            icon = "🧪",
            rarity = AchievementRarity.LEGENDARY,
            category = AchievementCategory.EXPLORATION,
            requirement = AchievementRequirement(RequirementType.DAYS_ACTIVE, 1, "beta_user"),
            reward = AchievementReward(1000, cosmeticReward = "beta_badge", titleReward = "Pionero")
        )
    )
    
    // ==================== SISTEMA DE LEADERBOARDS ====================
    
    @Serializable
    data class Leaderboard(
        val id: String,
        val name: String,
        val type: LeaderboardType,
        val timeFrame: TimeFrame,
        val entries: List<LeaderboardEntry>,
        val userRank: Int? = null,
        val userEntry: LeaderboardEntry? = null
    )
    
    @Serializable
    data class LeaderboardEntry(
        val rank: Int,
        val userId: String,
        val userName: String,
        val avatar: String,
        val score: Long,
        val level: Int,
        val trend: TrendDirection,
        val isFriend: Boolean = false
    )
    
    enum class LeaderboardType {
        GLOBAL_XP, WEEKLY_XP, QUIZ_ACCURACY, MINIGAME_SCORE, STREAK_COUNT,
        TOPIC_MASTERY, SPEED_RUN, CHALLENGE_WINS
    }
    
    enum class TimeFrame {
        ALL_TIME, MONTHLY, WEEKLY, DAILY
    }
    
    // ==================== SISTEMA DE RECOMPENSAS DIARIAS ====================
    
    @Serializable
    data class DailyReward(
        val day: Int,
        val rewardType: RewardType,
        val amount: Int,
        val claimed: Boolean = false,
        val claimDate: Long? = null
    )
    
    enum class RewardType {
        XP, COINS, HINT_TOKEN, COSMETIC, BOOSTER
    }
    
    fun getDailyRewardsCalendar(): List<DailyReward> = listOf(
        DailyReward(1, RewardType.XP, 50),
        DailyReward(2, RewardType.XP, 75),
        DailyReward(3, RewardType.COINS, 25),
        DailyReward(4, RewardType.XP, 100),
        DailyReward(5, RewardType.HINT_TOKEN, 1),
        DailyReward(6, RewardType.XP, 125),
        DailyReward(7, RewardType.COSMETIC, 1) // Recompensa semanal especial
    )
    
    // ==================== SISTEMA DE MISIONES ====================
    
    @Serializable
    data class Mission(
        val id: String,
        val title: String,
        val description: String,
        val type: MissionType,
        val requirements: List<MissionRequirement>,
        val rewards: MissionRewards,
        val duration: MissionDuration,
        var progress: Int = 0,
        var completed: Boolean = false,
        val expiryDate: Long? = null
    )
    
    enum class MissionType {
        DAILY, WEEKLY, MONTHLY, SPECIAL, EVENT
    }
    
    @Serializable
    data class MissionRequirement(
        val description: String,
        val targetValue: Int,
        var currentValue: Int = 0
    )
    
    @Serializable
    data class MissionRewards(
        val xp: Int,
        val coins: Int,
        val achievementId: String? = null
    )
    
    enum class MissionDuration {
        ONE_DAY, THREE_DAYS, ONE_WEEK, TWO_WEEKS, ONE_MONTH
    }
    
    fun generateDailyMissions(): List<Mission> = listOf(
        Mission(
            id = "daily_practice",
            title = "Práctica Diaria",
            description = "Responde 10 preguntas correctamente",
            type = MissionType.DAILY,
            requirements = listOf(MissionRequirement("Respuestas correctas", 10)),
            rewards = MissionRewards(100, 20),
            duration = MissionDuration.ONE_DAY
        ),
        Mission(
            id = "daily_quiz",
            title = "Cuestionario del Día",
            description = "Completa 1 cuestionario completo",
            type = MissionType.DAILY,
            requirements = listOf(MissionRequirement("Cuestionarios completados", 1)),
            rewards = MissionRewards(150, 30),
            duration = MissionDuration.ONE_DAY
        ),
        Mission(
            id = "daily_minigame",
            title = "Jugador del Día",
            description = "Juega 2 minijuegos",
            type = MissionType.DAILY,
            requirements = listOf(MissionRequirement("Minijuegos completados", 2)),
            rewards = MissionRewards(100, 20),
            duration = MissionDuration.ONE_DAY
        )
    )
    
    fun generateWeeklyMissions(): List<Mission> = listOf(
        Mission(
            id = "weekly_master",
            title = "Maestro Semanal",
            description = "Alcanza maestría en 2 temas diferentes",
            type = MissionType.WEEKLY,
            requirements = listOf(MissionRequirement("Temas dominados", 2)),
            rewards = MissionRewards(500, 100),
            duration = MissionDuration.ONE_WEEK
        ),
        Mission(
            id = "weekly_streak",
            title = "Racha Semanal",
            description = "Mantén una racha de 3 días consecutivos",
            type = MissionType.WEEKLY,
            requirements = listOf(MissionRequirement("Días consecutivos activo", 3)),
            rewards = MissionRewards(400, 80),
            duration = MissionDuration.ONE_WEEK
        ),
        Mission(
            id = "weekly_scholar",
            title = "Erudito de la Semana",
            description = "Responde 100 preguntas correctamente",
            type = MissionType.WEEKLY,
            requirements = listOf(MissionRequirement("Respuestas correctas", 100)),
            rewards = MissionRewards(600, 120),
            duration = MissionDuration.ONE_WEEK
        )
    )
    
    // ==================== SISTEMA DE COSMÉTICOS ====================
    
    @Serializable
    data class CosmeticItem(
        val id: String,
        val name: String,
        val type: CosmeticType,
        val rarity: AchievementRarity,
        val previewUrl: String,
        var unlocked: Boolean = false,
        val unlockRequirement: String? = null
    )
    
    enum class CosmeticType {
        AVATAR_FRAME, PROFILE_BACKGROUND, BADGE, STICKER, ANIMATION, SOUND_EFFECT, TITLE
    }
    
    fun getAllCosmetics(): List<CosmeticItem> = listOf(
        CosmeticItem("default_frame", "Marco Estándar", CosmeticType.AVATAR_FRAME, AchievementRarity.COMMON, "default.png"),
        CosmeticItem("bronze_frame", "Marco Bronce", CosmeticType.AVATAR_FRAME, AchievementRarity.COMMON, "bronze_frame.png", unlockRequirement = "Alcanzar nivel 10"),
        CosmeticItem("silver_frame", "Marco Plata", CosmeticType.AVATAR_FRAME, AchievementRarity.RARE, "silver_frame.png", unlockRequirement = "Alcanzar nivel 25"),
        CosmeticItem("gold_frame", "Marco Oro", CosmeticType.AVATAR_FRAME, AchievementRarity.RARE, "gold_frame.png", unlockRequirement = "Alcanzar nivel 50"),
        CosmeticItem("platinum_frame", "Marco Platino", CosmeticType.AVATAR_FRAME, AchievementRarity.EPIC, "platinum_frame.png", unlockRequirement = "Alcanzar nivel 75"),
        CosmeticItem("diamond_frame", "Marco Diamante", CosmeticType.AVATAR_FRAME, AchievementRarity.EPIC, "diamond_frame.png", unlockRequirement = "Alcanzar nivel 90"),
        CosmeticItem("legendary_frame", "Marco Legendario", CosmeticType.AVATAR_FRAME, AchievementRarity.LEGENDARY, "legendary_frame.png", unlockRequirement = "Alcanzar nivel 100"),
        CosmeticItem("flame_trail", "Estela de Fuego", CosmeticType.ANIMATION, AchievementRarity.EPIC, "flame_trail.gif", unlockRequirement = "Logro 'Asemejándose a un Dios'"),
        CosmeticItem("legendary_aura", "Aura Legendaria", CosmeticType.ANIMATION, AchievementRarity.LEGENDARY, "legendary_aura.gif", unlockRequirement = "Logro 'Racha Legendaria'"),
        CosmeticItem("crypto_shield", "Escudo Criptográfico", CosmeticType.BADGE, AchievementRarity.EPIC, "crypto_shield.png", unlockRequirement = "Logro 'Maestro del Cifrado'"),
        CosmeticItem("neural_crown", "Corona Neural", CosmeticType.BADGE, AchievementRarity.EPIC, "neural_crown.png", unlockRequirement = "Logro 'Mago de la IA'")
    )
    
    // ==================== SISTEMA DE NOTIFICACIONES ====================
    
    @Serializable
    data class GamificationNotification(
        val id: String,
        val type: NotificationType,
        val title: String,
        val message: String,
        val timestamp: Long,
        val read: Boolean = false,
        val actionData: Map<String, String>? = null
    )
    
    enum class NotificationType {
        ACHIEVEMENT_UNLOCKED, LEVEL_UP, LEADERBOARD_CHANGE, FRIEND_ACTIVITY,
        MISSION_COMPLETE, DAILY_REWARD, STREAK_WARNING, CHALLENGE_INVITE
    }
    
    // ==================== SISTEMA DE EVENTOS ESPECIALES ====================
    
    @Serializable
    data class SpecialEvent(
        val id: String,
        val name: String,
        val description: String,
        val startDate: Long,
        val endDate: Long,
        val type: EventType,
        val specialRules: List<String>,
        val exclusiveRewards: List<String>,
        val progress: Int = 0,
        val maxProgress: Int = 100
    )
    
    enum class EventType {
        DOUBLE_XP, HALF_TIME_CHALLENGE, TOPIC_FOCUS, COMMUNITY_CHALLENGE,
        SEASONAL_EVENT, ANNIVERSARY, COLLABORATION
    }
    
    fun getActiveEvents(): List<SpecialEvent> = listOf(
        SpecialEvent(
            id = "cybersecurity_month",
            name = "Mes de la Ciberseguridad",
            description = "¡Doble XP en todos los temas de seguridad durante octubre!",
            startDate = System.currentTimeMillis(),
            endDate = System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000,
            type = EventType.TOPIC_FOCUS,
            specialRules = listOf("2x XP en ciberseguridad", "Misiones especiales de seguridad"),
            exclusiveRewards = listOf("security_expert_badge", "hacker_defender_skin")
        )
    )
    
    // ==================== UTILIDADES DE CÁLCULO ====================
    
    fun calculateXPMultiplier(
        baseXP: Int,
        streak: Int,
        difficulty: com.LBs.EEDA.domain.model.educational.ContentDifficulty,
        eventActive: SpecialEvent? = null
    ): Int {
        var multiplier = 1.0
        
        // Bonus por racha
        multiplier += when {
            streak >= 20 -> 0.5
            streak >= 10 -> 0.3
            streak >= 5 -> 0.2
            streak >= 3 -> 0.1
            else -> 0.0
        }
        
        // Bonus por dificultad
        multiplier += when (difficulty) {
            com.LBs.EEDA.domain.model.educational.ContentDifficulty.EXPERT -> 0.5
            com.LBs.EEDA.domain.model.educational.ContentDifficulty.ADVANCED -> 0.3
            com.LBs.EEDA.domain.model.educational.ContentDifficulty.INTERMEDIATE -> 0.15
            else -> 0.0
        }
        
        // Bonus de evento
        if (eventActive?.type == EventType.DOUBLE_XP) {
            multiplier *= 2.0
        }
        
        return (baseXP * multiplier).toInt()
    }
}
