package com.LBs.EEDA.domain.model.educational.analytics

import com.LBs.EEDA.domain.model.educational.ContentDifficulty
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Sistema de Auto-Evaluación Adaptativa EEDA 2026
 * Proporciona evaluación continua, retroalimentación inteligente y ajuste dinámico
 */

@Serializable
data class SelfAssessmentSession(
    val sessionId: String,
    val userId: String,
    val startTime: Long,
    val targetConcepts: List<String>,
    val preAnalysis: PreAssessmentAnalysis,
    var questions: List<AdaptiveAssessmentQuestion> = emptyList(),
    var currentQuestionIndex: Int = 0,
    var responses: MutableList<QuestionResponse> = mutableListOf(),
    var sessionState: AssessmentSessionState = AssessmentSessionState.PREPARING,
    var accumulatedScore: Double = 0.0,
    var confidenceLevel: Double = 0.5,
    var difficultyAdjustment: Double = 0.0,
    var estimatedRemainingTime: Int = 0,
    var emotionalState: EmotionalState = EmotionalState.NEUTRAL,
    var fatigueIndicators: FatigueIndicators = FatigueIndicators()
)

enum class AssessmentSessionState {
    PREPARING,
    IN_PROGRESS,
    ADAPTING,
    COMPLETED,
    PAUSED,
    ABANDONED
}

@Serializable
data class AdaptiveAssessmentQuestion(
    val id: String,
    val conceptId: String,
    val questionType: QuestionType,
    val difficulty: ContentDifficulty,
    val content: QuestionContent,
    val hints: List<AdaptiveHint>,
    val timeLimitSeconds: Int,
    val cognitiveLoadEstimate: CognitiveLoad,
    val prerequisites: List<String>,
    val learningObjective: String,
    val bloomsLevel: BloomsTaxonomyLevel,
    val estimatedDiscrimination: Double,
    val estimatedDifficulty: Double
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    MULTIPLE_SELECT,
    FILL_BLANK,
    MATCHING,
    SEQUENCING,
    CODE_COMPLETION,
    VISUAL_RECOGNITION,
    DRAG_DROP,
    SHORT_ANSWER,
    HOTSPOT,
    SIMULATION
}

enum class StimulusType {
    TEXT,
    DIAGRAM,
    IMAGE,
    ANIMATION,
    CODE_SNIPPET,
    INTERACTIVE_DEMO
}

@Serializable
data class QuestionContent(
    val stem: String,
    val stimulus: StimulusType? = null,
    val options: List<QuestionOption>? = null,
    val correctAnswers: List<String>,
    val explanation: String,
    val misconceptionExplanations: Map<String, String> = emptyMap()
)

@Serializable
data class QuestionOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean,
    val distractorType: DistractorType? = null,
    val misconceptionTag: String? = null
)

enum class DistractorType {
    PLAUSIBLE_ALTERNATIVE,
    COMMON_MISCONCEPTION,
    PARTIALLY_CORRECT,
    OPOSITE_CORRECT,
    IRRELEVANT,
    TOO_EXTREME
}

enum class BloomsTaxonomyLevel {
    REMEMBER,
    UNDERSTAND,
    APPLY,
    ANALYZE,
    EVALUATE,
    CREATE
}

enum class CognitiveLoad {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH
}

@Serializable
data class QuestionResponse(
    val questionId: String,
    val timestamp: Long,
    val answer: String,
    val isCorrect: Boolean,
    val timeSpentSeconds: Int,
    val hintsUsed: Int,
    val confidenceRating: Int,
    val attempts: Int,
    val emotionalState: EmotionalState,
    val responsePattern: ResponsePattern
)

@Serializable
data class ResponsePattern(
    val hesitationTimeMs: Long,
    val changesOfMind: Int,
    val hintUsagePattern: HintUsagePattern,
    val interactionSequence: List<String>
)

enum class HintUsagePattern {
    NONE,
    IMMEDIATE,
    AFTER_ERROR,
    STRATEGIC,
    EXCESSIVE
}

enum class EmotionalState {
    ENGAGED,
    FRUSTRATED,
    CONFIDENT,
    ANXIOUS,
    BORED,
    NEUTRAL,
    EXCITED,
    CONFUSED
}

@Serializable
data class FatigueIndicators(
    val responseTimeTrend: TrendDirection = TrendDirection.STABLE,
    val accuracyTrend: TrendDirection = TrendDirection.STABLE,
    val attentionDriftIndicators: Int = 0,
    val recommendation: FatigueRecommendation = FatigueRecommendation.CONTINUE
)

enum class FatigueRecommendation {
    CONTINUE,
    SHORT_BREAK,
    LONG_BREAK,
    END_SESSION
}

@Serializable
data class AdaptiveHint(
    val id: String,
    val level: HintLevel,
    val content: String,
    val visualAid: VisualAid? = null,
    val triggers: List<HintTrigger>
)

enum class HintLevel {
    SUBTLE,
    GUIDING,
    DIRECT,
    EXPLICIT
}

@Serializable
data class HintTrigger(
    val condition: TriggerCondition,
    val threshold: Double
)

enum class TriggerCondition {
    TIME_ELAPSED,
    ERROR_COUNT,
    HESITATION,
    EMOTIONAL_STATE,
    FATIGUE_LEVEL
}

@Serializable
data class VisualAid(
    val type: VisualAidType,
    val content: String,
    val highlights: List<String> = emptyList()
)

enum class VisualAidType {
    DIAGRAM,
    ANIMATION,
    HIGHLIGHT,
    ARROW,
    COLOR_CODE,
    ZOOM
}

/**
 * Motor de Auto-Evaluación Adaptativa
 * Controla la sesión de evaluación con ajuste dinámico en tiempo real
 */
class SelfAssessmentEngine(
    private val questionBank: QuestionBank,
    private val analyticsEngine: PreAssessmentAnalyzer
) {
    /**
     * Inicia una sesión de auto-evaluación con análisis previo
     */
    suspend fun startAssessment(
        userId: String,
        targetConcepts: List<String>,
        historicalData: UserHistoricalData
    ): Flow<AssessmentEvent> = flow {
        // Análisis previo
        emit(AssessmentEvent.Analyzing("Analizando perfil cognitivo..."))
        val preAnalysis = analyticsEngine.analyzeBeforeAssessment(
            userId, targetConcepts, historicalData
        )
        
        emit(AssessmentEvent.AnalysisComplete(preAnalysis))
        
        // Generar preguntas adaptativas
        emit(AssessmentEvent.GeneratingQuestions("Generando preguntas personalizadas..."))
        val questions = generateAdaptiveQuestions(preAnalysis)
        
        val session = SelfAssessmentSession(
            sessionId = "session_${System.currentTimeMillis()}",
            userId = userId,
            startTime = System.currentTimeMillis(),
            targetConcepts = targetConcepts,
            preAnalysis = preAnalysis,
            questions = questions,
            estimatedRemainingTime = questions.sumOf { it.timeLimitSeconds } / 60,
            sessionState = AssessmentSessionState.IN_PROGRESS
        )
        
        emit(AssessmentEvent.SessionReady(session))
        
        // Emitir primera pregunta
        if (questions.isNotEmpty()) {
            emit(AssessmentEvent.QuestionPresented(questions[0], 0, questions.size))
        }
    }
    
    /**
     * Procesa respuesta y determina siguiente acción
     */
    fun processResponse(
        session: SelfAssessmentSession,
        response: QuestionResponse
    ): AssessmentAction {
        session.responses.add(response)
        
        // Actualizar métricas
        updateSessionMetrics(session, response)
        
        // Detectar fatiga
        val fatigueCheck = detectFatigue(session)
        
        // Determinar siguiente paso
        return when {
            fatigueCheck.recommendation == FatigueRecommendation.END_SESSION ->
                AssessmentAction.Complete(generatePartialResults(session))
                
            fatigueCheck.recommendation == FatigueRecommendation.SHORT_BREAK ->
                AssessmentAction.SuggestBreak(5, "Parece que necesitas un breve descanso")
                
            session.currentQuestionIndex >= session.questions.size - 1 ->
                AssessmentAction.Complete(generateFinalResults(session))
                
            needsDifficultyAdjustment(session) -> {
                val adjustedQuestion = adjustNextQuestion(session)
                AssessmentAction.PresentQuestion(adjustedQuestion)
            }
            
            else -> {
                session.currentQuestionIndex++
                val nextQuestion = session.questions.getOrNull(session.currentQuestionIndex)
                if (nextQuestion != null) {
                    AssessmentAction.PresentQuestion(nextQuestion)
                } else {
                    AssessmentAction.Complete(generateFinalResults(session))
                }
            }
        }
    }
    
    private fun generateAdaptiveQuestions(analysis: PreAssessmentAnalysis): List<AdaptiveAssessmentQuestion> {
        val questions = mutableListOf<AdaptiveAssessmentQuestion>()
        
        // Generar preguntas basadas en el path adaptativo
        analysis.adaptivePath.forEach { node ->
            val conceptQuestions = questionBank.getQuestionsForConcept(
                conceptId = node.conceptId,
                difficulty = node.difficulty,
                count = 2
            )
            questions.addAll(conceptQuestions)
        }
        
        // Añadir preguntas de áreas fuertes para mantener motivación
        analysis.strengthAreas.take(2).forEach { conceptId ->
            val confidenceQuestions = questionBank.getQuestionsForConcept(
                conceptId = conceptId,
                difficulty = ContentDifficulty.ADVANCED,
                count = 1
            )
            questions.addAll(confidenceQuestions)
        }
        
        return questions.shuffled()
    }
    
    private fun updateSessionMetrics(session: SelfAssessmentSession, response: QuestionResponse) {
        // Actualizar score
        val questionWeight = when (session.questions.getOrNull(session.currentQuestionIndex)?.bloomsLevel) {
            BloomsTaxonomyLevel.REMEMBER -> 1.0
            BloomsTaxonomyLevel.UNDERSTAND -> 1.2
            BloomsTaxonomyLevel.APPLY -> 1.5
            BloomsTaxonomyLevel.ANALYZE -> 1.8
            BloomsTaxonomyLevel.EVALUATE -> 2.0
            BloomsTaxonomyLevel.CREATE -> 2.5
            null -> 1.0
        }
        
        if (response.isCorrect) {
            session.accumulatedScore += 100.0 * questionWeight
        } else {
            session.accumulatedScore += (if (response.hintsUsed > 0) 25.0 else 0.0) * questionWeight
        }
        
        // Actualizar nivel de confianza
        val recentResponses = session.responses.takeLast(3)
        val recentAccuracy = recentResponses.count { it.isCorrect }.toDouble() / recentResponses.size
        session.confidenceLevel = session.confidenceLevel * 0.7 + recentAccuracy * 0.3
        
        // Actualizar tiempo restante estimado
        session.estimatedRemainingTime = (session.questions.size - session.currentQuestionIndex - 1) * 2
        
        // Detectar estado emocional basado en patrones
        session.emotionalState = detectEmotionalState(session, response)
    }
    
    private fun detectEmotionalState(session: SelfAssessmentSession, lastResponse: QuestionResponse): EmotionalState {
        val recentResponses = session.responses.takeLast(5)
        
        return when {
            // Múltiples errores seguidos = frustración
            recentResponses.size >= 3 && recentResponses.all { !it.isCorrect } ->
                EmotionalState.FRUSTRATED
                
            // Múltiples aciertos + confianza alta = confianza
            recentResponses.size >= 3 && recentResponses.all { it.isCorrect } &&
            lastResponse.confidenceRating > 4 ->
                EmotionalState.CONFIDENT
                
            // Muchos hints usados + tiempo largo = ansiedad
            recentResponses.averageOf { it.hintsUsed.toDouble() } > 2 &&
            lastResponse.timeSpentSeconds > lastResponse.timeSpentSeconds * 2 ->
                EmotionalState.ANXIOUS
                
            // Respuestas muy rápidas + aciertos = aburrimiento (demasiado fácil)
            recentResponses.averageOf { it.timeSpentSeconds.toDouble() } < 5 &&
            recentResponses.all { it.isCorrect } ->
                EmotionalState.BORED
                
            // Participación activa = enganche
            lastResponse.responsePattern.interactionSequence.size > 5 ->
                EmotionalState.ENGAGED
                
            else -> EmotionalState.NEUTRAL
        }
    }
    
    private fun detectFatigue(session: SelfAssessmentSession): FatigueIndicators {
        val responses = session.responses
        if (responses.size < 5) return FatigueIndicators()
        
        val recent = responses.takeLast(5)
        val previous = responses.dropLast(5).takeLast(5)
        
        // Análisis de tendencias
        val timeTrend = if (recent.averageOf { it.timeSpentSeconds.toDouble() } >
            previous.averageOf { it.timeSpentSeconds.toDouble() } * 1.3) {
            TrendDirection.DECLINING
        } else TrendDirection.STABLE
        
        val accuracyTrend = if (recent.count { it.isCorrect }.toDouble() / recent.size <
            previous.count { it.isCorrect }.toDouble() / previous.size * 0.7) {
            TrendDirection.DECLINING
        } else TrendDirection.STABLE
        
        val attentionDrift = recent.count { it.responsePattern.hesitationTimeMs > 5000 }
        
        val recommendation = when {
            timeTrend == TrendDirection.DECLINING &&
            accuracyTrend == TrendDirection.DECLINING &&
            attentionDrift >= 3 -> FatigueRecommendation.END_SESSION
            
            timeTrend == TrendDirection.DECLINING || attentionDrift >= 2 ->
                FatigueRecommendation.SHORT_BREAK
                
            accuracyTrend == TrendDirection.DECLINING && recent.size > 8 ->
                FatigueRecommendation.LONG_BREAK
                
            else -> FatigueRecommendation.CONTINUE
        }
        
        return FatigueIndicators(
            responseTimeTrend = timeTrend,
            accuracyTrend = accuracyTrend,
            attentionDriftIndicators = attentionDrift,
            recommendation = recommendation
        )
    }
    
    private fun needsDifficultyAdjustment(session: SelfAssessmentSession): Boolean {
        val recent = session.responses.takeLast(3)
        if (recent.size < 3) return false
        
        val allCorrect = recent.all { it.isCorrect }
        val allIncorrect = recent.all { !it.isCorrect }
        
        // Ajustar si todos aciertos (subir dificultad) o todos errores (bajar)
        return allCorrect || allIncorrect
    }
    
    private fun adjustNextQuestion(session: SelfAssessmentSession): AdaptiveAssessmentQuestion {
        val recent = session.responses.takeLast(3)
        val allCorrect = recent.all { it.isCorrect }
        
        val currentDifficulty = session.questions.getOrNull(session.currentQuestionIndex)?.difficulty
        
        val newDifficulty = when {
            allCorrect && currentDifficulty != ContentDifficulty.EXPERT ->
                increaseDifficulty(currentDifficulty)
            !allCorrect && currentDifficulty != ContentDifficulty.BEGINNER ->
                decreaseDifficulty(currentDifficulty)
            else -> currentDifficulty ?: ContentDifficulty.INTERMEDIATE
        }
        
        session.difficultyAdjustment += if (allCorrect) 0.1 else -0.1
        session.currentQuestionIndex++
        
        // Buscar o generar pregunta con nueva dificultad
        val nextConcept = session.questions.getOrNull(session.currentQuestionIndex)?.conceptId
            ?: session.targetConcepts.random()
            
        return questionBank.getQuestionsForConcept(nextConcept, newDifficulty, 1).first()
    }
    
    private fun increaseDifficulty(current: ContentDifficulty?): ContentDifficulty {
        return when (current) {
            ContentDifficulty.BEGINNER -> ContentDifficulty.ELEMENTARY
            ContentDifficulty.ELEMENTARY -> ContentDifficulty.INTERMEDIATE
            ContentDifficulty.INTERMEDIATE -> ContentDifficulty.ADVANCED
            ContentDifficulty.ADVANCED -> ContentDifficulty.EXPERT
            ContentDifficulty.EXPERT -> ContentDifficulty.EXPERT
            null -> ContentDifficulty.INTERMEDIATE
        }
    }
    
    private fun decreaseDifficulty(current: ContentDifficulty?): ContentDifficulty {
        return when (current) {
            ContentDifficulty.EXPERT -> ContentDifficulty.ADVANCED
            ContentDifficulty.ADVANCED -> ContentDifficulty.INTERMEDIATE
            ContentDifficulty.INTERMEDIATE -> ContentDifficulty.ELEMENTARY
            ContentDifficulty.ELEMENTARY -> ContentDifficulty.BEGINNER
            ContentDifficulty.BEGINNER -> ContentDifficulty.BEGINNER
            null -> ContentDifficulty.INTERMEDIATE
        }
    }
    
    private fun generatePartialResults(session: SelfAssessmentSession): AssessmentResults {
        return generateResults(session, isComplete = false)
    }
    
    private fun generateFinalResults(session: SelfAssessmentSession): AssessmentResults {
        session.sessionState = AssessmentSessionState.COMPLETED
        return generateResults(session, isComplete = true)
    }
    
    private fun generateResults(session: SelfAssessmentSession, isComplete: Boolean): AssessmentResults {
        val responses = session.responses
        val totalQuestions = if (isComplete) session.questions.size else session.currentQuestionIndex + 1
        
        val correctCount = responses.count { it.isCorrect }
        val percentage = if (totalQuestions > 0) (correctCount * 100.0 / totalQuestions) else 0.0
        
        val conceptPerformance = responses
            .groupBy { session.questions.find { q -> q.id == it.questionId }?.conceptId ?: "unknown" }
            .map { (conceptId, conceptResponses) ->
                ConceptPerformance(
                    conceptId = conceptId,
                    correct = conceptResponses.count { it.isCorrect },
                    total = conceptResponses.size,
                    averageTime = conceptResponses.map { it.timeSpentSeconds }.average().toInt(),
                    masteryLevel = calculateMasteryLevel(conceptResponses)
                )
            }
        
        val recommendations = generateRecommendations(session, conceptPerformance)
        
        return AssessmentResults(
            sessionId = session.sessionId,
            totalQuestions = totalQuestions,
            correctAnswers = correctCount,
            percentage = percentage,
            timeSpentMinutes = ((System.currentTimeMillis() - session.startTime) / 60000).toInt(),
            conceptPerformance = conceptPerformance,
            cognitiveProfileDelta = calculateCognitiveDelta(session),
            recommendations = recommendations,
            nextSteps = generateNextSteps(session, conceptPerformance),
            isComplete = isComplete
        )
    }
    
    private fun calculateMasteryLevel(responses: List<QuestionResponse>): MasteryLevel {
        val accuracy = responses.count { it.isCorrect }.toDouble() / responses.size
        val avgTime = responses.map { it.timeSpentSeconds }.average()
        val avgHints = responses.map { it.hintsUsed }.average()
        
        return when {
            accuracy > 0.9 && avgTime < 10 && avgHints < 0.5 -> MasteryLevel.EXPERT
            accuracy > 0.8 && avgHints < 1 -> MasteryLevel.PROFICIENT
            accuracy > 0.6 -> MasteryLevel.DEVELOPING
            accuracy > 0.4 -> MasteryLevel.EMERGING
            else -> MasteryLevel.BEGINNER
        }
    }
    
    private fun generateRecommendations(
        session: SelfAssessmentSession,
        performance: List<ConceptPerformance>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        // Basadas en conceptos débiles
        performance.filter { it.masteryLevel.ordinal < MasteryLevel.DEVELOPING.ordinal }
            .forEach {
                recommendations.add("Refuerza: ${it.conceptId} - Practica conceptos básicos")
            }
        
        // Basadas en patrones de error
        val errorPattern = session.responses
            .filter { !it.isCorrect }
            .groupBy { it.responsePattern.hintUsagePattern }
        
        if (errorPattern[HintUsagePattern.EXCESSIVE]?.size ?: 0 > 3) {
            recommendations.add("Trabaja en autonomía - intenta resolver sin hints primero")
        }
        
        // Basadas en tiempo
        val avgTime = session.responses.map { it.timeSpentSeconds }.average()
        if (avgTime > 60) {
            recommendations.add("Practica fluidez - trabaja en velocidad de respuesta")
        }
        
        return recommendations
    }
    
    private fun generateNextSteps(session: SelfAssessmentSession, performance: List<ConceptPerformance>): List<String> {
        val nextSteps = mutableListOf<String>()
        
        val weakConcepts = performance.filter { it.masteryLevel.ordinal < MasteryLevel.PROFICIENT.ordinal }
        
        if (weakConcepts.isNotEmpty()) {
            nextSteps.add("Revisar módulos: ${weakConcepts.take(3).joinToString { it.conceptId }}")
        }
        
        if (performance.any { it.masteryLevel == MasteryLevel.EXPERT }) {
            nextSteps.add("Explorar contenido avanzado y desafíos de enriquecimiento")
        }
        
        return nextSteps
    }
    
    private fun calculateCognitiveDelta(session: SelfAssessmentSession): CognitiveDelta {
        val currentResponses = session.responses
        val historical = session.preAnalysis.cognitiveProfile.previousPerformanceHistory
        
        val currentAvgTime = currentResponses.map { it.timeSpentSeconds }.average()
        val historicalAvgTime = historical.map { it.averageTimeMs / 1000.0 }.average()
        
        return CognitiveDelta(
            processingSpeedChange = ((historicalAvgTime - currentAvgTime) / historicalAvgTime).takeIf { !it.isNaN() } ?: 0.0,
            accuracyChange = session.confidenceLevel - session.preAnalysis.confidenceScore,
            confidenceChange = session.confidenceLevel - 0.5
        )
    }
    
    private inline fun <T> List<T>.averageOf(selector: (T) -> Double): Double {
        return if (isEmpty()) 0.0 else map(selector).average()
    }
}

// Eventos de la sesión de evaluación
sealed class AssessmentEvent {
    data class Analyzing(val message: String) : AssessmentEvent()
    data class AnalysisComplete(val analysis: PreAssessmentAnalysis) : AssessmentEvent()
    data class GeneratingQuestions(val message: String) : AssessmentEvent()
    data class SessionReady(val session: SelfAssessmentSession) : AssessmentEvent()
    data class QuestionPresented(val question: AdaptiveAssessmentQuestion, val current: Int, val total: Int) : AssessmentEvent()
    data class FeedbackProvided(val isCorrect: Boolean, val explanation: String) : AssessmentEvent()
    data class AdaptationOccurred(val reason: String, val newDifficulty: ContentDifficulty) : AssessmentEvent()
}

// Acciones del motor
sealed class AssessmentAction {
    data class PresentQuestion(val question: AdaptiveAssessmentQuestion) : AssessmentAction()
    data class SuggestBreak(val minutes: Int, val reason: String) : AssessmentAction()
    data class Complete(val results: AssessmentResults) : AssessmentAction()
}

// Resultados finales
@Serializable
data class AssessmentResults(
    val sessionId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val percentage: Double,
    val timeSpentMinutes: Int,
    val conceptPerformance: List<ConceptPerformance>,
    val cognitiveProfileDelta: CognitiveDelta,
    val recommendations: List<String>,
    val nextSteps: List<String>,
    val isComplete: Boolean
)

@Serializable
data class ConceptPerformance(
    val conceptId: String,
    val correct: Int,
    val total: Int,
    val averageTime: Int,
    val masteryLevel: MasteryLevel
)

enum class MasteryLevel { BEGINNER, EMERGING, DEVELOPING, PROFICIENT, EXPERT }

@Serializable
data class CognitiveDelta(
    val processingSpeedChange: Double,
    val accuracyChange: Double,
    val confidenceChange: Double
)

// Interfaz del banco de preguntas
interface QuestionBank {
    fun getQuestionsForConcept(conceptId: String, difficulty: ContentDifficulty, count: Int): List<AdaptiveAssessmentQuestion>
}
