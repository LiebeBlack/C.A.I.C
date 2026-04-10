package com.LBs.EEDA.domain.model.educational.quiz

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
import com.LBs.EEDA.domain.model.educational.ContentDifficulty
import com.LBs.EEDA.domain.model.educational.analytics.*
import com.LBs.EEDA.domain.model.educational.content.QuestionBank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Sistema de Cuestionarios Adaptativos Extensos EEDA 2026
 * Cuestionarios dinámicos que se adaptan al rendimiento del usuario en tiempo real
 */

class AdaptiveQuizEngine(
    private val questionBank: QuestionBank,
    private val analyticsEngine: PreAssessmentAnalyzer
) {
    
    /**
     * Genera un cuestionario adaptativo personalizado
     */
    fun generateAdaptiveQuiz(
        userId: String,
        topic: String,
        targetQuestionCount: Int = 20,
        difficultyPreference: ContentDifficulty? = null,
        historicalData: UserHistoricalData
    ): Flow<QuizGenerationEvent> = flow {
        emit(QuizGenerationEvent.Analyzing("Analizando tu perfil..."))
        
        // Análisis previo
        val preAnalysis = analyticsEngine.analyzeBeforeAssessment(
            userId, listOf(topic), historicalData
        )
        
        emit(QuizGenerationEvent.ProfileReady(preAnalysis))
        
        // Determinar dificultad inicial
        val initialDifficulty = difficultyPreference ?: preAnalysis.recommendedDifficulty
        
        emit(QuizGenerationEvent.GeneratingQuestions("Generando preguntas personalizadas..."))
        
        // Generar cuestionario adaptativo
        val quiz = AdaptiveQuiz(
            quizId = "quiz_${System.currentTimeMillis()}",
            userId = userId,
            topic = topic,
            questions = mutableListOf(),
            currentDifficulty = initialDifficulty,
            targetQuestionCount = targetQuestionCount,
            preAnalysis = preAnalysis,
            adaptiveSettings = AdaptiveQuizSettings(
                enableDifficultyAdjustment = true,
                enableTimeAdjustment = true,
                enableHintSystem = true,
                enableStreakBonus = true,
                enableFatigueDetection = true
            ),
            state = QuizState.PREPARING
        )
        
        // Cargar preguntas iniciales
        loadNextQuestionBatch(quiz, 5)
        
        emit(QuizGenerationEvent.QuizReady(quiz))
    }
    
    /**
     * Procesa respuesta y adapta el cuestionario
     */
    fun processAnswer(
        quiz: AdaptiveQuiz,
        answer: QuizAnswer
    ): QuizUpdateResult {
        // Guardar respuesta
        quiz.answers.add(answer)
        
        // Actualizar estadísticas
        updateQuizStats(quiz, answer)
        
        // Detectar patrones
        val patterns = detectPatterns(quiz)
        
        // Adaptar dificultad si es necesario
        if (shouldAdjustDifficulty(quiz)) {
            adjustDifficulty(quiz)
        }
        
        // Detectar fatiga
        val fatigueLevel = detectFatigue(quiz)
        
        // Verificar si debemos cargar más preguntas
        if (needsMoreQuestions(quiz)) {
            loadNextQuestionBatch(quiz, 3)
        }
        
        // Determinar siguiente acción
        return when {
            isQuizComplete(quiz) -> {
                quiz.state = QuizState.COMPLETED
                QuizUpdateResult.Completed(generateFinalReport(quiz))
            }
            fatigueLevel == FatigueLevel.HIGH -> {
                QuizUpdateResult.SuggestBreak(
                    durationMinutes = 5,
                    reason = "Detectamos que podrías estar fatigado. Un breve descanso mejorará tu rendimiento."
                )
            }
            fatigueLevel == FatigueLevel.CRITICAL -> {
                quiz.state = QuizState.PAUSED
                QuizUpdateResult.ForceBreak(
                    durationMinutes = 15,
                    reason = "Necesitas descansar para mantener la calidad de aprendizaje."
                )
            }
            else -> {
                val nextQuestion = getNextQuestion(quiz)
                QuizUpdateResult.NextQuestion(
                    question = nextQuestion,
                    progress = calculateProgress(quiz),
                    streakInfo = getStreakInfo(quiz),
                    adaptiveMessage = generateAdaptiveMessage(quiz, patterns)
                )
            }
        }
    }
    
    private fun loadNextQuestionBatch(quiz: AdaptiveQuiz, count: Int) {
        val newQuestions = questionBank.getQuestionsForConcept(
            quiz.topic,
            quiz.currentDifficulty,
            count
        )
        quiz.questions.addAll(newQuestions)
    }
    
    private fun updateQuizStats(quiz: AdaptiveQuiz, answer: QuizAnswer) {
        // Actualizar racha
        if (answer.isCorrect) {
            quiz.currentStreak++
            quiz.maxStreak = maxOf(quiz.maxStreak, quiz.currentStreak)
        } else {
            quiz.currentStreak = 0
        }
        
        // Actualizar precisión por dificultad
        val currentAccuracy = quiz.accuracyByDifficulty[quiz.currentDifficulty] ?: 0.0
        val totalForDifficulty = quiz.answers.count { it.questionDifficulty == quiz.currentDifficulty }
        val correctForDifficulty = quiz.answers.count { 
            it.questionDifficulty == quiz.currentDifficulty && it.isCorrect 
        }
        
        quiz.accuracyByDifficulty[quiz.currentDifficulty] = 
            if (totalForDifficulty > 0) correctForDifficulty.toDouble() / totalForDifficulty else 0.0
        
        // Actualizar tiempo promedio
        quiz.averageTimeSeconds = quiz.answers.map { it.timeSpentSeconds }.average()
        
        // Actualizar uso de hints
        quiz.hintsUsed += answer.hintsUsed
    }
    
    private fun detectPatterns(quiz: AdaptiveQuiz): AnswerPatterns {
        val recentAnswers = quiz.answers.takeLast(5)
        
        return AnswerPatterns(
            consecutiveCorrect = recentAnswers.takeWhile { it.isCorrect }.size,
            consecutiveIncorrect = recentAnswers.takeWhile { !it.isCorrect }.size,
            slowAnswers = recentAnswers.count { it.timeSpentSeconds > quiz.averageTimeSeconds * 1.5 },
            hintDependency = recentAnswers.count { it.hintsUsed > 0 },
            confidenceTrend = calculateConfidenceTrend(quiz),
            errorTypes = recentAnswers
                .filter { !it.isCorrect }
                .groupBy { it.errorType }
                .mapNotNull { (type, errors) -> 
                    type?.let { it to errors.size }
                }
                .toMap()
        )
    }
    
    private fun calculateConfidenceTrend(quiz: AdaptiveQuiz): TrendDirection {
        val recent = quiz.answers.takeLast(3)
        val previous = quiz.answers.dropLast(3).takeLast(3)
        
        val recentCorrect = recent.count { it.isCorrect }
        val previousCorrect = previous.count { it.isCorrect }
        
        return when {
            recentCorrect > previousCorrect -> TrendDirection.IMPROVING
            recentCorrect < previousCorrect -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }
    
    private fun shouldAdjustDifficulty(quiz: AdaptiveQuiz): Boolean {
        if (!quiz.adaptiveSettings.enableDifficultyAdjustment) return false
        
        val recentAnswers = quiz.answers.takeLast(3)
        if (recentAnswers.size < 3) return false
        
        // Ajustar si todos correctos o todos incorrectos
        val allCorrect = recentAnswers.all { it.isCorrect }
        val allIncorrect = recentAnswers.all { !it.isCorrect }
        
        return allCorrect || allIncorrect
    }
    
    private fun adjustDifficulty(quiz: AdaptiveQuiz) {
        val recentAnswers = quiz.answers.takeLast(3)
        val allCorrect = recentAnswers.all { it.isCorrect }
        
        val newDifficulty = when {
            allCorrect && canIncreaseDifficulty(quiz.currentDifficulty) -> 
                increaseDifficulty(quiz.currentDifficulty)
            !allCorrect && canDecreaseDifficulty(quiz.currentDifficulty) -> 
                decreaseDifficulty(quiz.currentDifficulty)
            else -> quiz.currentDifficulty
        }
        
        quiz.currentDifficulty = newDifficulty
        quiz.difficultyAdjustmentHistory.add(
            DifficultyAdjustment(
                timestamp = System.currentTimeMillis(),
                fromDifficulty = quiz.currentDifficulty,
                toDifficulty = newDifficulty,
                reason = if (allCorrect) "Racha de aciertos" else "Racha de errores"
            )
        )
    }
    
    private fun canIncreaseDifficulty(current: ContentDifficulty): Boolean {
        return current != ContentDifficulty.EXPERT
    }
    
    private fun canDecreaseDifficulty(current: ContentDifficulty): Boolean {
        return current != ContentDifficulty.BEGINNER
    }
    
    private fun increaseDifficulty(current: ContentDifficulty): ContentDifficulty {
        return when (current) {
            ContentDifficulty.BEGINNER -> ContentDifficulty.ELEMENTARY
            ContentDifficulty.ELEMENTARY -> ContentDifficulty.INTERMEDIATE
            ContentDifficulty.INTERMEDIATE -> ContentDifficulty.ADVANCED
            ContentDifficulty.ADVANCED -> ContentDifficulty.EXPERT
            ContentDifficulty.EXPERT -> ContentDifficulty.EXPERT
        }
    }
    
    private fun decreaseDifficulty(current: ContentDifficulty): ContentDifficulty {
        return when (current) {
            ContentDifficulty.EXPERT -> ContentDifficulty.ADVANCED
            ContentDifficulty.ADVANCED -> ContentDifficulty.INTERMEDIATE
            ContentDifficulty.INTERMEDIATE -> ContentDifficulty.ELEMENTARY
            ContentDifficulty.ELEMENTARY -> ContentDifficulty.BEGINNER
            ContentDifficulty.BEGINNER -> ContentDifficulty.BEGINNER
        }
    }
    
    private fun detectFatigue(quiz: AdaptiveQuiz): FatigueLevel {
        if (!quiz.adaptiveSettings.enableFatigueDetection) return FatigueLevel.NONE
        
        val recentAnswers = quiz.answers.takeLast(5)
        if (recentAnswers.size < 5) return FatigueLevel.NONE
        
        // Señales de fatiga
        val slowResponses = recentAnswers.count { it.timeSpentSeconds > quiz.averageTimeSeconds * 1.5 }
        val decliningAccuracy = calculateConfidenceTrend(quiz) == TrendDirection.DECLINING
        val manyHints = recentAnswers.count { it.hintsUsed > 0 } >= 3
        
        return when {
            slowResponses >= 4 && decliningAccuracy -> FatigueLevel.CRITICAL
            slowResponses >= 3 || (decliningAccuracy && manyHints) -> FatigueLevel.HIGH
            slowResponses >= 2 -> FatigueLevel.MODERATE
            else -> FatigueLevel.LOW
        }
    }
    
    private fun needsMoreQuestions(quiz: AdaptiveQuiz): Boolean {
        val answeredCount = quiz.answers.size
        val availableQuestions = quiz.questions.size
        val bufferNeeded = 3
        
        return answeredCount + bufferNeeded >= availableQuestions && 
               answeredCount < quiz.targetQuestionCount
    }
    
    private fun isQuizComplete(quiz: AdaptiveQuiz): Boolean {
        return quiz.answers.size >= quiz.targetQuestionCount ||
               (quiz.answers.size >= 10 && hasReachedMastery(quiz))
    }
    
    private fun hasReachedMastery(quiz: AdaptiveQuiz): Boolean {
        val recentAccuracy = quiz.answers.takeLast(10).count { it.isCorrect } / 10.0
        return recentAccuracy >= 0.9 && quiz.currentDifficulty == ContentDifficulty.EXPERT
    }
    
    private fun getNextQuestion(quiz: AdaptiveQuiz): AdaptiveAssessmentQuestion {
        val answeredIds = quiz.answers.map { it.questionId }.toSet()
        return quiz.questions.first { it.id !in answeredIds }
    }
    
    private fun calculateProgress(quiz: AdaptiveQuiz): QuizProgress {
        return QuizProgress(
            answeredQuestions = quiz.answers.size,
            totalQuestions = quiz.targetQuestionCount,
            currentStreak = quiz.currentStreak,
            maxStreak = quiz.maxStreak,
            currentAccuracy = quiz.answers.count { it.isCorrect }.toDouble() / quiz.answers.size.coerceAtLeast(1),
            estimatedGrade = calculateEstimatedGrade(quiz),
            timeElapsed = System.currentTimeMillis() - quiz.startTime
        )
    }
    
    private fun calculateEstimatedGrade(quiz: AdaptiveQuiz): String {
        val accuracy = quiz.answers.count { it.isCorrect }.toDouble() / quiz.answers.size.coerceAtLeast(1)
        val difficultyBonus = when (quiz.currentDifficulty) {
            ContentDifficulty.EXPERT -> 0.15
            ContentDifficulty.ADVANCED -> 0.10
            ContentDifficulty.INTERMEDIATE -> 0.05
            else -> 0.0
        }
        
        val adjustedScore = accuracy + difficultyBonus
        
        return when {
            adjustedScore >= 0.90 -> "A (Excelente)"
            adjustedScore >= 0.80 -> "B (Muy bien)"
            adjustedScore >= 0.70 -> "C (Bien)"
            adjustedScore >= 0.60 -> "D (Necesita práctica)"
            else -> "F (Requiere repaso)"
        }
    }
    
    private fun getStreakInfo(quiz: AdaptiveQuiz): StreakInfo {
        return StreakInfo(
            currentStreak = quiz.currentStreak,
            maxStreak = quiz.maxStreak,
            nextMilestone = ((quiz.currentStreak / 5) + 1) * 5,
            streakBonus = calculateStreakBonus(quiz.currentStreak)
        )
    }
    
    private fun calculateStreakBonus(streak: Int): Int {
        return when {
            streak >= 10 -> 50
            streak >= 5 -> 25
            streak >= 3 -> 10
            else -> 0
        }
    }
    
    private fun generateAdaptiveMessage(quiz: AdaptiveQuiz, patterns: AnswerPatterns): String {
        return when {
            patterns.consecutiveCorrect >= 5 -> 
                "¡Increíble racha de ${patterns.consecutiveCorrect}! 🔥 Estás dominando este nivel."
            patterns.consecutiveIncorrect >= 3 -> 
                "No te preocupes, todos cometemos errores. ¡Intentemos una pregunta más sencilla!"
            patterns.hintDependency >= 2 -> 
                "Intenta la siguiente sin hints primero. ¡Confío en ti! 💪"
            quiz.currentStreak > 0 && quiz.currentStreak % 5 == 0 -> 
                "¡Racha de ${quiz.currentStreak}! ¡Sigue así! ⭐"
            else -> "¡Buen trabajo! Continuemos aprendiendo."
        }
    }
    
    private fun generateFinalReport(quiz: AdaptiveQuiz): QuizReport {
        val totalQuestions = quiz.answers.size
        val correctAnswers = quiz.answers.count { it.isCorrect }
        val accuracy = correctAnswers.toDouble() / totalQuestions
        
        // Análisis por tema
        val topicAnalysis = quiz.answers
            .groupBy { it.conceptId }
            .map { (concept, answers) ->
                TopicAnalysis(
                    conceptId = concept,
                    totalQuestions = answers.size,
                    correctAnswers = answers.count { it.isCorrect },
                    averageTime = answers.map { it.timeSpentSeconds }.average(),
                    masteryLevel = calculateMasteryForTopic(answers)
                )
            }
        
        // Recomendaciones
        val recommendations = generateRecommendations(quiz, topicAnalysis)
        
        return QuizReport(
            quizId = quiz.quizId,
            userId = quiz.userId,
            topic = quiz.topic,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            accuracy = accuracy,
            finalGrade = calculateFinalGrade(accuracy, quiz.currentDifficulty),
            timeSpentMinutes = (System.currentTimeMillis() - quiz.startTime) / 60000,
            maxStreak = quiz.maxStreak,
            hintsUsed = quiz.hintsUsed,
            finalDifficulty = quiz.currentDifficulty,
            topicAnalysis = topicAnalysis,
            recommendations = recommendations,
            comparativeStats = generateComparativeStats(quiz),
            xpEarned = calculateXPEarned(quiz)
        )
    }
    
    private fun calculateMasteryForTopic(answers: List<QuizAnswer>): MasteryLevel {
        val accuracy = answers.count { it.isCorrect }.toDouble() / answers.size
        val avgTime = answers.map { it.timeSpentSeconds }.average()
        
        return when {
            accuracy >= 0.9 && avgTime < 30 -> MasteryLevel.EXPERT
            accuracy >= 0.8 -> MasteryLevel.PROFICIENT
            accuracy >= 0.6 -> MasteryLevel.DEVELOPING
            accuracy >= 0.4 -> MasteryLevel.EMERGING
            else -> MasteryLevel.BEGINNER
        }
    }
    
    private fun calculateFinalGrade(accuracy: Double, finalDifficulty: ContentDifficulty): String {
        val difficultyBonus = when (finalDifficulty) {
            ContentDifficulty.EXPERT -> 0.10
            ContentDifficulty.ADVANCED -> 0.07
            ContentDifficulty.INTERMEDIATE -> 0.05
            else -> 0.0
        }
        
        val adjusted = accuracy + difficultyBonus
        
        return when {
            adjusted >= 0.93 -> "A+"
            adjusted >= 0.90 -> "A"
            adjusted >= 0.87 -> "A-"
            adjusted >= 0.83 -> "B+"
            adjusted >= 0.80 -> "B"
            adjusted >= 0.77 -> "B-"
            adjusted >= 0.73 -> "C+"
            adjusted >= 0.70 -> "C"
            adjusted >= 0.67 -> "C-"
            adjusted >= 0.63 -> "D+"
            adjusted >= 0.60 -> "D"
            else -> "F"
        }
    }
    
    private fun generateRecommendations(
        quiz: AdaptiveQuiz,
        topicAnalysis: List<TopicAnalysis>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Recomendaciones por temas débiles
        topicAnalysis
            .filter { it.masteryLevel.ordinal < MasteryLevel.PROFICIENT.ordinal }
            .forEach { topic ->
                recommendations.add("Repasa '${topic.conceptId}' - Dominio actual: ${topic.masteryLevel.name}")
            }
        
        // Recomendaciones por tiempo
        val slowTopics = topicAnalysis.filter { it.averageTime > 60 }
        if (slowTopics.isNotEmpty()) {
            recommendations.add("Practica fluidez en: ${slowTopics.take(2).joinToString { it.conceptId }}")
        }
        
        // Recomendaciones por uso de hints
        if (quiz.hintsUsed > quiz.answers.size * 0.5) {
            recommendations.add("Intenta completar cuestionarios usando menos pistas para mejorar confianza")
        }
        
        return recommendations
    }
    
    private fun generateComparativeStats(quiz: AdaptiveQuiz): ComparativeStats {
        // En producción, esto vendría de base de datos comparando con otros usuarios
        return ComparativeStats(
            percentile = 75, // Top 25%
            averagePeerScore = 72.0,
            yourScore = calculateFinalScore(quiz),
            globalRank = 1250,
            totalParticipants = 5000
        )
    }
    
    private fun calculateFinalScore(quiz: AdaptiveQuiz): Double {
        val accuracy = quiz.answers.count { it.isCorrect }.toDouble() / quiz.answers.size
        val difficultyMultiplier = when (quiz.currentDifficulty) {
            ContentDifficulty.EXPERT -> 1.3
            ContentDifficulty.ADVANCED -> 1.2
            ContentDifficulty.INTERMEDIATE -> 1.1
            else -> 1.0
        }
        return accuracy * 100 * difficultyMultiplier
    }
    
    private fun calculateXPEarned(quiz: AdaptiveQuiz): Int {
        val baseXP = quiz.answers.count { it.isCorrect } * 10
        val streakBonus = calculateStreakBonus(quiz.maxStreak)
        val difficultyBonus = when (quiz.currentDifficulty) {
            ContentDifficulty.EXPERT -> 100
            ContentDifficulty.ADVANCED -> 50
            ContentDifficulty.INTERMEDIATE -> 25
            else -> 0
        }
        val speedBonus = if (quiz.averageTimeSeconds < 30) 50 else 0
        
        return baseXP + streakBonus + difficultyBonus + speedBonus
    }
}

// Modelos de datos

data class AdaptiveQuiz(
    val quizId: String,
    val userId: String,
    val topic: String,
    val questions: MutableList<AdaptiveAssessmentQuestion>,
    val answers: MutableList<QuizAnswer> = mutableListOf(),
    var currentDifficulty: ContentDifficulty,
    val targetQuestionCount: Int,
    val preAnalysis: PreAssessmentAnalysis,
    val adaptiveSettings: AdaptiveQuizSettings,
    var state: QuizState,
    val startTime: Long = System.currentTimeMillis(),
    var currentStreak: Int = 0,
    var maxStreak: Int = 0,
    var hintsUsed: Int = 0,
    var averageTimeSeconds: Double = 0.0,
    val accuracyByDifficulty: MutableMap<ContentDifficulty, Double> = mutableMapOf(),
    val difficultyAdjustmentHistory: MutableList<DifficultyAdjustment> = mutableListOf()
)

data class AdaptiveQuizSettings(
    val enableDifficultyAdjustment: Boolean = true,
    val enableTimeAdjustment: Boolean = true,
    val enableHintSystem: Boolean = true,
    val enableStreakBonus: Boolean = true,
    val enableFatigueDetection: Boolean = true,
    val minimumQuestionsBeforeAdjustment: Int = 3,
    val difficultyChangeThreshold: Double = 0.8
)

data class QuizAnswer(
    val questionId: String,
    val questionDifficulty: ContentDifficulty,
    val conceptId: String,
    val answer: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val hintsUsed: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val errorType: String? = null,
    val confidenceRating: Int = 3 // 1-5
)

data class DifficultyAdjustment(
    val timestamp: Long,
    val fromDifficulty: ContentDifficulty,
    val toDifficulty: ContentDifficulty,
    val reason: String
)

data class AnswerPatterns(
    val consecutiveCorrect: Int,
    val consecutiveIncorrect: Int,
    val slowAnswers: Int,
    val hintDependency: Int,
    val confidenceTrend: TrendDirection,
    val errorTypes: Map<String, Int>
)

data class StreakInfo(
    val currentStreak: Int,
    val maxStreak: Int,
    val nextMilestone: Int,
    val streakBonus: Int
)

data class QuizProgress(
    val answeredQuestions: Int,
    val totalQuestions: Int,
    val currentStreak: Int,
    val maxStreak: Int,
    val currentAccuracy: Double,
    val estimatedGrade: String,
    val timeElapsed: Long
)

data class QuizReport(
    val quizId: String,
    val userId: String,
    val topic: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val accuracy: Double,
    val finalGrade: String,
    val timeSpentMinutes: Long,
    val maxStreak: Int,
    val hintsUsed: Int,
    val finalDifficulty: ContentDifficulty,
    val topicAnalysis: List<TopicAnalysis>,
    val recommendations: List<String>,
    val comparativeStats: ComparativeStats,
    val xpEarned: Int
)

data class TopicAnalysis(
    val conceptId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val averageTime: Double,
    val masteryLevel: MasteryLevel
)

data class ComparativeStats(
    val percentile: Int,
    val averagePeerScore: Double,
    val yourScore: Double,
    val globalRank: Int,
    val totalParticipants: Int
)

enum class QuizState {
    PREPARING, IN_PROGRESS, PAUSED, COMPLETED, ABANDONED
}

enum class FatigueLevel {
    NONE, LOW, MODERATE, HIGH, CRITICAL
}

// Eventos de generación de cuestionario
sealed class QuizGenerationEvent {
    data class Analyzing(val message: String) : QuizGenerationEvent()
    data class ProfileReady(val analysis: PreAssessmentAnalysis) : QuizGenerationEvent()
    data class GeneratingQuestions(val message: String) : QuizGenerationEvent()
    data class QuizReady(val quiz: AdaptiveQuiz) : QuizGenerationEvent()
}

// Resultados de actualización de cuestionario
sealed class QuizUpdateResult {
    data class NextQuestion(
        val question: AdaptiveAssessmentQuestion,
        val progress: QuizProgress,
        val streakInfo: StreakInfo,
        val adaptiveMessage: String
    ) : QuizUpdateResult()
    
    data class SuggestBreak(
        val durationMinutes: Int,
        val reason: String
    ) : QuizUpdateResult()
    
    data class ForceBreak(
        val durationMinutes: Int,
        val reason: String
    ) : QuizUpdateResult()
    
    data class Completed(val report: QuizReport) : QuizUpdateResult()
}
