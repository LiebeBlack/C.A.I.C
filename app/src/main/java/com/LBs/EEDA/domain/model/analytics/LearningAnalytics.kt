package com.LBs.EEDA.domain.model.analytics

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Sistema de Analytics de Aprendizaje E.E.D.A 2026
 * Tracking completo de progreso, engagement y rendimiento
 */
@Serializable
data class LearningAnalytics(
    val userId: String,
    val sessions: List<LearningSession> = emptyList(),
    val conceptMastery: Map<String, ConceptMastery> = emptyMap(),
    val skillProgression: Map<String, SkillProgressionData> = emptyMap(),
    val learningPatterns: LearningPatterns = LearningPatterns(),
    val performanceMetrics: PerformanceMetrics = PerformanceMetrics(),
    val engagementMetrics: EngagementMetrics = EngagementMetrics(),
    val recommendationEngine: RecommendationData = RecommendationData(),
    val lastUpdated: Long = Instant.now().epochSecond
)

@Serializable
data class LearningSession(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val activities: List<SessionActivity> = emptyList(),
    val xpEarned: Long = 0,
    val lessonsCompleted: List<String> = emptyList(),
    val skillsUnlocked: List<String> = emptyList(),
    val deviceType: String = "mobile",
    val phaseAtSession: String
)

@Serializable
data class SessionActivity(
    val timestamp: Long,
    val type: ActivityType,
    val contentId: String,
    val durationSeconds: Int,
    val successRate: Float,
    val hintsUsed: Int,
    val mistakes: Int
)

@Serializable
enum class ActivityType {
    LESSON_STARTED, LESSON_COMPLETED,
    MINIGAME_STARTED, MINIGAME_COMPLETED,
    ASSESSMENT_STARTED, ASSESSMENT_COMPLETED,
    SKILL_UNLOCKED, SKILL_PRACTICED,
    VIDEO_WATCHED, ARTICLE_READ,
    INTERACTION, PAUSE, RESUME
}

@Serializable
data class ConceptMastery(
    val conceptId: String,
    val conceptName: String,
    val category: String,
    val masteryLevel: MasteryLevel,
    val accuracyRate: Float,
    val attempts: Int,
    val successfulAttempts: Int,
    val lastPracticed: Long,
    val estimatedTimeToMaster: Int, // minutos
    val prerequisiteStatus: List<PrerequisiteStatus>
)

@Serializable
enum class MasteryLevel {
    NOVICE, BEGINNER, INTERMEDIATE, ADVANCED, EXPERT, MASTER
}

@Serializable
data class PrerequisiteStatus(
    val conceptId: String,
    val isMastered: Boolean,
    val masteryLevel: MasteryLevel
)

@Serializable
data class SkillProgressionData(
    val skillId: String,
    val skillName: String,
    val currentLevel: Int,
    var maxLevel: Int,
    val xpInSkill: Long,
    val unlockDate: Long,
    val lastUsed: Long,
    val proficiencyScore: Float,
    val decayRate: Float
)

@Serializable
data class LearningPatterns(
    val preferredLearningTime: PreferredTimeSlot = PreferredTimeSlot.MORNING,
    val averageSessionDuration: Int = 15,
    val peakPerformanceHour: Int = 10,
    val learningStyle: LearningStyle = LearningStyle.VISUAL,
    val attentionSpan: Int = 20, // minutos
    val reviewFrequency: Float = 0.7f, // 0-1
    val practiceConsistency: Float = 0.5f
)

@Serializable
enum class PreferredTimeSlot {
    EARLY_MORNING, MORNING, AFTERNOON, EVENING, NIGHT
}

@Serializable
enum class LearningStyle {
    VISUAL, AUDITORY, READING, KINESTHETIC, LOGICAL, SOCIAL, SOLITARY
}

@Serializable
data class PerformanceMetrics(
    val overallAccuracy: Float = 0.0f,
    val averageResponseTime: Float = 0.0f, // segundos
    val problemSolvingSpeed: Float = 0.0f,
    val retentionRate: Float = 0.0f,
    val completionRate: Float = 0.0f,
    val streakConsistency: Float = 0.0f,
    val improvementRate: Float = 0.0f, // semanal
    val difficultyPreference: DifficultyPreference = DifficultyPreference.BALANCED
)

@Serializable
enum class DifficultyPreference {
    EASY, BALANCED, CHALLENGING, ADAPTIVE
}

@Serializable
data class EngagementMetrics(
    val dailyActiveDays: Int = 0,
    val weeklyActiveDays: Int = 0,
    val monthlyActiveDays: Int = 0,
    val totalSessions: Int = 0,
    val averageDailySessions: Float = 0.0f,
    val averageSessionLength: Float = 0.0f,
    val returnRate: Float = 0.0f,
    val churnRisk: Float = 0.0f,
    val satisfactionScore: Float = 0.0f
)

@Serializable
data class RecommendationData(
    val recommendedLessons: List<String> = emptyList(),
    val recommendedSkills: List<String> = emptyList(),
    val recommendedChallenges: List<String> = emptyList(),
    val nextMilestone: Milestone? = null,
    val weakAreas: List<String> = emptyList(),
    val strongAreas: List<String> = emptyList()
)

@Serializable
data class Milestone(
    val id: String,
    val title: String,
    val description: String,
    val targetXp: Long,
    val currentXp: Long,
    val deadline: Long? = null,
    val reward: String
)

/**
 * Motor de Analytics - Análisis y Predicciones
 */
class AnalyticsEngine(private val analytics: LearningAnalytics) {

    /**
     * Analiza el rendimiento actual y genera insights
     */
    fun generateInsights(): List<LearningInsight> {
        return listOf(
            analyzeStrengths(),
            analyzeWeakAreas(),
            analyzeLearningPattern(),
            predictNextMilestone(),
            detectBurnoutRisk(),
            recommendOptimalSchedule()
        ).filterNotNull()
    }

    /**
     * Identifica áreas de fortaleza
     */
    private fun analyzeStrengths(): LearningInsight? {
        val strongConcepts = analytics.conceptMastery.filter { (_, mastery) ->
            mastery.masteryLevel in listOf(MasteryLevel.ADVANCED, MasteryLevel.EXPERT, MasteryLevel.MASTER)
        }

        return if (strongConcepts.isNotEmpty()) {
            LearningInsight(
                type = InsightType.STRENGTH,
                title = "¡Excelente dominio!",
                description = "Has alcanzado nivel avanzado en ${strongConcepts.size} conceptos",
                confidence = 0.9f,
                actionItems = listOf("Considera mentoría", "Explora desafíos avanzados")
            )
        } else null
    }

    /**
     * Identifica áreas que necesitan práctica
     */
    private fun analyzeWeakAreas(): LearningInsight? {
        val weakConcepts = analytics.conceptMastery.filter { (_, mastery) ->
            mastery.masteryLevel in listOf(MasteryLevel.NOVICE, MasteryLevel.BEGINNER) &&
            mastery.attempts > 3 && mastery.accuracyRate < 0.5f
        }

        return if (weakConcepts.isNotEmpty()) {
            LearningInsight(
                type = InsightType.IMPROVEMENT,
                title = "Áreas para reforzar",
                description = "Estos conceptos necesitan más práctica: ${weakConcepts.keys.take(3).joinToString()}",
                confidence = 0.85f,
                actionItems = weakConcepts.keys.map { "Repasa: $it" }
            )
        } else null
    }

    /**
     * Analiza patrones de aprendizaje
     */
    private fun analyzeLearningPattern(): LearningInsight {
        val pattern = analytics.learningPatterns
        val bestTime = when (pattern.preferredLearningTime) {
            PreferredTimeSlot.MORNING -> "la mañana"
            PreferredTimeSlot.AFTERNOON -> "la tarde"
            else -> "la noche"
        }

        return LearningInsight(
            type = InsightType.PATTERN,
            title = "Tu ritmo óptimo",
            description = "Rendes mejor en $bestTime con sesiones de ${pattern.averageSessionDuration} minutos",
            confidence = 0.8f,
            actionItems = listOf("Programa estudios en $bestTime", "Usa técnica Pomodoro")
        )
    }

    /**
     * Predice el próximo milestone alcanzable
     */
    private fun predictNextMilestone(): LearningInsight? {
        return analytics.recommendationEngine.nextMilestone?.let { milestone ->
            val progress = milestone.currentXp.toFloat() / milestone.targetXp
            val daysToComplete = estimateDaysToMilestone(milestone)

            LearningInsight(
                type = InsightType.MILESTONE,
                title = "Próximo objetivo: ${milestone.title}",
                description = "Progreso: ${(progress * 100).toInt()}% - Estimado: $daysToComplete días",
                confidence = minOf(0.95f, progress + 0.3f),
                actionItems = listOf("Completa 2 lecciones diarias", "Enfócate en ${milestone.reward}")
            )
        }
    }

    /**
     * Detecta riesgo de abandono
     */
    private fun detectBurnoutRisk(): LearningInsight? {
        val recentSessions = analytics.sessions.filter {
            it.startTime > Instant.now().minusSeconds(7 * 24 * 3600).epochSecond
        }

        val avgDuration = if (recentSessions.isNotEmpty()) {
            recentSessions.map { it.durationMinutes }.average()
        } else 0.0

        val isOverloading = avgDuration > 60 && recentSessions.size > 14
        val isUnderactive = recentSessions.size < 2

        return when {
            isOverloading -> LearningInsight(
                type = InsightType.WARNING,
                title = "⚠️ Riesgo de sobrecarga",
                description = "Estás estudiando mucho. Toma descansos para mejor retención.",
                confidence = 0.75f,
                actionItems = listOf("Reduce sesiones a 45 min", "Añade descansos de 10 min")
            )
            isUnderactive -> LearningInsight(
                type = InsightType.WARNING,
                title = "📉 Baja actividad detectada",
                description = "Tu racha está en riesgo. ¡Una lección rápida mantendrá tu progreso!",
                confidence = 0.7f,
                actionItems = listOf("Sesión de 5 minutos hoy", "Activa recordatorios")
            )
            else -> null
        }
    }

    /**
     * Recomenda horario óptimo
     */
    private fun recommendOptimalSchedule(): LearningInsight {
        val bestHour = calculateOptimalStudyHour()
        return LearningInsight(
            type = InsightType.SCHEDULE,
            title = "Horario recomendado",
            description = "Tus mejores resultados ocurren a las ${bestHour}:00",
            confidence = 0.82f,
            actionItems = listOf("Programa sesiones a las ${bestHour}:00", "Prepara material antes")
        )
    }

    private fun calculateOptimalStudyHour(): Int {
        return analytics.learningPatterns.peakPerformanceHour
    }

    private fun estimateDaysToMilestone(milestone: Milestone): Int {
        val remaining = milestone.targetXp - milestone.currentXp
        val dailyAverage = analytics.engagementMetrics.averageDailySessions *
                analytics.sessions.filter { it.xpEarned > 0 }
                    .map { it.xpEarned }
                    .average()
                    .coerceAtLeast(100.0)
        return if (dailyAverage > 0) (remaining / dailyAverage).toInt().coerceAtLeast(1) else 30
    }

    /**
     * Calcula el mastery level para un concepto específico
     */
    fun calculateMasteryLevel(conceptId: String): MasteryLevel {
        return analytics.conceptMastery[conceptId]?.masteryLevel ?: MasteryLevel.NOVICE
    }

    /**
     * Genera recomendaciones personalizadas
     */
    fun generateRecommendations(): List<Recommendation> {
        return listOf(
            recommendNextLesson(),
            recommendSkillToUnlock(),
            recommendDifficultyAdjustment(),
            recommendReviewTopics(),
            recommendChallenge()
        ).filterNotNull()
    }

    private fun recommendNextLesson(): Recommendation? {
        val uncompleted = analytics.recommendationEngine.recommendedLessons
            .filter { lessonId ->
                analytics.sessions.none { session ->
                    lessonId in session.lessonsCompleted
                }
            }
            .take(3)

        return if (uncompleted.isNotEmpty()) {
            Recommendation(
                type = RecommendationType.LESSON,
                priority = Priority.HIGH,
                itemIds = uncompleted,
                reason = "Basado en tu progreso actual y próximos objetivos",
                expectedXp = 150
            )
        } else null
    }

    private fun recommendSkillToUnlock(): Recommendation? {
        return analytics.recommendationEngine.recommendedSkills.firstOrNull()?.let { skillId ->
            Recommendation(
                type = RecommendationType.SKILL,
                priority = Priority.MEDIUM,
                itemIds = listOf(skillId),
                reason = "Prerrequisito para contenido avanzado",
                expectedXp = 250
            )
        }
    }

    private fun recommendDifficultyAdjustment(): Recommendation? {
        val accuracy = analytics.performanceMetrics.overallAccuracy
        return when {
            accuracy > 0.9f -> Recommendation(
                type = RecommendationType.DIFFICULTY,
                priority = Priority.LOW,
                itemIds = listOf("increase"),
                reason = "Tu precisión es excelente, prueba nivel más difícil",
                expectedXp = 0
            )
            accuracy < 0.5f -> Recommendation(
                type = RecommendationType.DIFFICULTY,
                priority = Priority.HIGH,
                itemIds = listOf("decrease"),
                reason = "Considera nivel más fácil para mejorar confianza",
                expectedXp = 0
            )
            else -> null
        }
    }

    private fun recommendReviewTopics(): Recommendation? {
        val dueForReview = analytics.conceptMastery.filter { (_, mastery) ->
            val daysSinceLastPractice = (Instant.now().epochSecond - mastery.lastPracticed) / 86400
            daysSinceLastPractice > 7 && mastery.masteryLevel.ordinal < MasteryLevel.EXPERT.ordinal
        }.keys.take(5)

        return if (dueForReview.isNotEmpty()) {
            Recommendation(
                type = RecommendationType.REVIEW,
                priority = Priority.MEDIUM,
                itemIds = dueForReview.toList(),
                reason = "Repaso espaciado para mejor retención",
                expectedXp = 50
            )
        } else null
    }

    private fun recommendChallenge(): Recommendation? {
        return analytics.recommendationEngine.recommendedChallenges.firstOrNull()?.let { challengeId ->
            Recommendation(
                type = RecommendationType.CHALLENGE,
                priority = Priority.LOW,
                itemIds = listOf(challengeId),
                reason = "Desafío bonus para XP extra",
                expectedXp = 500
            )
        }
    }

    /**
     * Calcula el tiempo estimado para dominar un concepto
     */
    fun estimateMasteryTime(conceptId: String): Int {
        val mastery = analytics.conceptMastery[conceptId] ?: return 60
        val currentLevel = mastery.masteryLevel.ordinal
        val targetLevel = MasteryLevel.EXPERT.ordinal

        return if (currentLevel >= targetLevel) {
            0
        } else {
            val levelsNeeded = targetLevel - currentLevel
            val avgTimePerLevel = 30 // minutos
            levelsNeeded * avgTimePerLevel
        }
    }

    /**
     * Predice el rendimiento en una evaluación
     */
    fun predictAssessmentPerformance(assessmentTopics: List<String>): Float {
        val topicMastery = assessmentTopics.map { topic ->
            analytics.conceptMastery[topic]?.accuracyRate ?: 0.5f
        }
        return if (topicMastery.isNotEmpty()) {
            topicMastery.average().toFloat()
        } else 0.6f
    }

    /**
     * Genera reporte de progreso semanal
     */
    fun generateWeeklyReport(): WeeklyReport {
        val oneWeekAgo = Instant.now().minusSeconds(7 * 24 * 3600).epochSecond
        val weekSessions = analytics.sessions.filter { it.startTime > oneWeekAgo }

        return WeeklyReport(
            period = "Semanal",
            totalSessions = weekSessions.size,
            totalMinutes = weekSessions.sumOf { it.durationMinutes },
            totalXp = weekSessions.sumOf { it.xpEarned },
            lessonsCompleted = weekSessions.flatMap { it.lessonsCompleted }.distinct().size,
            skillsUnlocked = weekSessions.flatMap { it.skillsUnlocked }.distinct().size,
            accuracy = calculateWeeklyAccuracy(weekSessions),
            streakDays = calculateWeeklyStreak(weekSessions),
            comparisonToLastWeek = compareToPreviousWeek(weekSessions),
            topAchievements = getTopWeeklyAchievements(weekSessions),
            recommendations = generateRecommendations().take(3)
        )
    }

    private fun calculateWeeklyAccuracy(sessions: List<LearningSession>): Float {
        val activities = sessions.flatMap { it.activities }
        return if (activities.isNotEmpty()) {
            activities.map { it.successRate }.average().toFloat()
        } else 0.0f
    }

    private fun calculateWeeklyStreak(sessions: List<LearningSession>): Int {
        return sessions.map {
            Instant.ofEpochSecond(it.startTime).toString().substring(0, 10)
        }.distinct().size
    }

    private fun compareToPreviousWeek(currentWeekSessions: List<LearningSession>): WeekComparison {
        val twoWeeksAgo = Instant.now().minusSeconds(14 * 24 * 3600).epochSecond
        val oneWeekAgo = Instant.now().minusSeconds(7 * 24 * 3600).epochSecond

        val previousWeekSessions = analytics.sessions.filter {
            it.startTime in twoWeeksAgo until oneWeekAgo
        }

        val currentXp = currentWeekSessions.sumOf { it.xpEarned }
        val previousXp = previousWeekSessions.sumOf { it.xpEarned }

        return WeekComparison(
            xpChange = currentXp - previousXp,
            xpChangePercent = if (previousXp > 0) ((currentXp - previousXp) * 100 / previousXp).toFloat() else 0f,
            sessionChange = currentWeekSessions.size - previousWeekSessions.size,
            accuracyChange = calculateWeeklyAccuracy(currentWeekSessions) - calculateWeeklyAccuracy(previousWeekSessions)
        )
    }

    private fun getTopWeeklyAchievements(sessions: List<LearningSession>): List<String> {
        return sessions.flatMap { it.skillsUnlocked }.take(5)
    }
}

@Serializable
data class LearningInsight(
    val type: InsightType,
    val title: String,
    val description: String,
    val confidence: Float,
    val actionItems: List<String>,
    val timestamp: Long = Instant.now().epochSecond
)

@Serializable
enum class InsightType {
    STRENGTH, IMPROVEMENT, PATTERN, MILESTONE, WARNING, SCHEDULE, TREND
}

@Serializable
data class Recommendation(
    val type: RecommendationType,
    val priority: Priority,
    val itemIds: List<String>,
    val reason: String,
    val expectedXp: Long
)

@Serializable
enum class RecommendationType {
    LESSON, SKILL, DIFFICULTY, REVIEW, CHALLENGE, BREAK, SCHEDULE_CHANGE
}

@Serializable
enum class Priority {
    HIGH, MEDIUM, LOW
}

@Serializable
data class WeeklyReport(
    val period: String,
    val totalSessions: Int,
    val totalMinutes: Int,
    val totalXp: Long,
    val lessonsCompleted: Int,
    val skillsUnlocked: Int,
    val accuracy: Float,
    val streakDays: Int,
    val comparisonToLastWeek: WeekComparison,
    val topAchievements: List<String>,
    val recommendations: List<Recommendation>
)

@Serializable
data class WeekComparison(
    val xpChange: Long,
    val xpChangePercent: Float,
    val sessionChange: Int,
    val accuracyChange: Float
)

/**
 * Factory para crear analytics iniciales
 */
object AnalyticsFactory {
    fun createInitialAnalytics(userId: String): LearningAnalytics {
        return LearningAnalytics(
            userId = userId,
            sessions = emptyList(),
            conceptMastery = emptyMap(),
            skillProgression = emptyMap(),
            learningPatterns = LearningPatterns(),
            performanceMetrics = PerformanceMetrics(),
            engagementMetrics = EngagementMetrics(),
            recommendationEngine = RecommendationData()
        )
    }
}
