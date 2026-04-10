package com.LBs.EEDA.domain.usecase.educational

import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.model.educational.modules.*
import com.LBs.EEDA.domain.model.educational.content.*
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Sistema de Evaluaciones Adaptativas E.E.D.A 2026
 * Evaluaciones inteligentes que se adaptan al nivel del usuario
 */
@Serializable
data class Assessment(
    val id: String,
    val title: String,
    val description: String,
    val discipline: Discipline,
    val phase: DigitalPhase,
    val difficultyRange: DifficultyRange,
    val questions: List<AdaptiveQuestion>,
    val config: AssessmentConfig,
    val timeLimitMinutes: Int? = null,
    val passingScore: Int = 70
)

@Serializable
data class DifficultyRange(
    val min: ContentDifficulty,
    val max: ContentDifficulty
)

@Serializable
data class AssessmentConfig(
    val adaptiveMode: Boolean = true,
    val showHints: Boolean = true,
    val allowRetry: Boolean = true,
    val showCorrectAnswers: Boolean = true,
    val shuffleQuestions: Boolean = true,
    val questionsCount: Int = 10,
    val timeBonusEnabled: Boolean = true,
    val partialCreditEnabled: Boolean = true
)

@Serializable
data class AdaptiveQuestion(
    val id: String,
    val type: QuestionType,
    val difficulty: ContentDifficulty,
    val conceptId: String,
    val stem: String, // Enunciado principal
    val scenario: String? = null, // Contexto opcional
    val options: List<QuestionOption> = emptyList(),
    val correctAnswer: CorrectAnswer,
    val hint: AdaptiveHint,
    val explanation: Explanation,
    val media: QuestionMedia? = null,
    val timeEstimateSeconds: Int = 60,
    val xpValue: Int = 100
)

@Serializable
enum class QuestionType {
    SINGLE_CHOICE,      // Selección única
    MULTIPLE_CHOICE,    // Selección múltiple
    TRUE_FALSE,         // Verdadero/Falso
    FILL_IN_BLANK,      // Completar espacio
    MATCHING,           // Emparejamiento
    ORDERING,           // Ordenar secuencia
    CODE_COMPLETION,    // Completar código
    SHORT_ANSWER,       // Respuesta corta
    INTERACTIVE_SIM     // Simulación interactiva
}

@Serializable
data class QuestionOption(
    val id: String,
    val text: String,
    val isCorrect: Boolean = false,
    val feedback: String? = null,
    val media: QuestionMedia? = null
)

@Serializable
data class CorrectAnswer(
    val optionIds: List<String>, // Para multiple/single choice
    val textAnswer: String? = null, // Para fill-in-blank
    val codeAnswer: String? = null, // Para code completion
    val orderedIds: List<String>? = null // Para ordering
)

@Serializable
data class Explanation(
    val correct: String,
    val incorrect: String? = null,
    val detailed: String? = null,
    val relatedConcept: String? = null,
    val furtherReading: List<String> = emptyList()
)

@Serializable
data class QuestionMedia(
    val type: MediaType,
    val url: String,
    val altText: String? = null
)

@Serializable
enum class MediaType {
    IMAGE, VIDEO, AUDIO, DIAGRAM, INTERACTIVE
}

/**
 * Sesión de evaluación activa
 */
@Serializable
data class AssessmentSession(
    val sessionId: String,
    val assessmentId: String,
    val userId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val answers: Map<String, UserAnswer> = emptyMap(),
    val currentQuestionIndex: Int = 0,
    val revealedHints: List<String> = emptyList(),
    val state: SessionState = SessionState.IN_PROGRESS,
    val timeSpentSeconds: Int = 0
)

@Serializable
enum class SessionState {
    IN_PROGRESS, PAUSED, COMPLETED, ABANDONED, TIME_UP
}

@Serializable
data class UserAnswer(
    val questionId: String,
    val selectedOptions: List<String> = emptyList(),
    val textAnswer: String? = null,
    val codeAnswer: String? = null,
    val timeSpentSeconds: Int = 0,
    val hintsUsed: Int = 0,
    val confidenceLevel: ConfidenceLevel = ConfidenceLevel.MEDIUM,
    val timestamp: Long
)

@Serializable
enum class ConfidenceLevel {
    LOW, MEDIUM, HIGH, VERY_HIGH
}

/**
 * Resultado de evaluación
 */
@Serializable
data class AssessmentResult(
    val sessionId: String,
    val assessmentId: String,
    val score: Int, // 0-100
    val grade: Grade,
    val correctCount: Int,
    val totalQuestions: Int,
    val timeSpentMinutes: Int,
    val xpEarned: Long,
    val passed: Boolean,
    val questionResults: List<QuestionResult>,
    val conceptMastery: Map<String, Float>,
    val recommendations: List<String>,
    val certificateEarned: String? = null
)

@Serializable
enum class Grade {
    A_PLUS, A, B_PLUS, B, C_PLUS, C, D, F
}

@Serializable
data class QuestionResult(
    val questionId: String,
    val isCorrect: Boolean,
    val partialCredit: Float,
    val timeSpentSeconds: Int,
    val hintsUsed: Int,
    val userAnswer: UserAnswer,
    val feedback: String
)

/**
 * Motor de Evaluación - Lógica Completa
 */
class AssessmentEngine(
    val assessment: Assessment,
    private val userProgress: Map<String, Float>
) {

    private var currentDifficulty = assessment.difficultyRange.min
    private val questionHistory = mutableListOf<QuestionPerformance>()

    /**
     * Inicia una nueva sesión de evaluación
     */
    fun startSession(userId: String): AssessmentSession {
        return AssessmentSession(
            sessionId = UUID.randomUUID().toString(),
            assessmentId = assessment.id,
            userId = userId,
            startTime = System.currentTimeMillis(),
            answers = emptyMap()
        )
    }

    /**
     * Obtiene la siguiente pregunta adaptativa
     */
    fun getNextQuestion(session: AssessmentSession): AdaptiveQuestion? {
        if (session.currentQuestionIndex >= assessment.config.questionsCount) {
            return null
        }

        // Filtrar preguntas por dificultad actual y no respondidas
        val availableQuestions = assessment.questions.filter { question ->
            question.difficulty == currentDifficulty &&
            question.id !in session.answers.keys
        }

        return if (availableQuestions.isNotEmpty()) {
            // Seleccionar pregunta que pruebe conceptos débiles
            selectOptimalQuestion(availableQuestions)
        } else {
            // Si no hay preguntas de esta dificultad, ajustar
            adjustDifficulty()
            getNextQuestion(session)
        }
    }

    private fun selectOptimalQuestion(questions: List<AdaptiveQuestion>): AdaptiveQuestion {
        // Priorizar conceptos con menor mastery
        return questions.sortedBy { question ->
            userProgress[question.conceptId] ?: 0f
        }.first()
    }

    private fun adjustDifficulty() {
        currentDifficulty = when (currentDifficulty) {
            ContentDifficulty.ELEMENTARY -> ContentDifficulty.BASIC
            ContentDifficulty.BASIC -> ContentDifficulty.INTERMEDIATE
            ContentDifficulty.INTERMEDIATE -> ContentDifficulty.ADVANCED
            ContentDifficulty.ADVANCED -> ContentDifficulty.EXPERT
            else -> currentDifficulty
        }
    }

    /**
     * Procesa la respuesta del usuario
     */
    fun submitAnswer(
        session: AssessmentSession,
        question: AdaptiveQuestion,
        answer: UserAnswer
    ): AnswerFeedback {
        val isCorrect = evaluateAnswer(question, answer)
        val partialCredit = if (assessment.config.partialCreditEnabled) {
            calculatePartialCredit(question, answer)
        } else {
            if (isCorrect) 1.0f else 0.0f
        }

        // Actualizar historial para adaptación
        questionHistory.add(QuestionPerformance(
            questionId = question.id,
            difficulty = question.difficulty,
            correct = isCorrect,
            timeSpent = answer.timeSpentSeconds
        ))

        // Adaptar dificultad basado en rendimiento
        adaptDifficulty()

        return AnswerFeedback(
            isCorrect = isCorrect,
            partialCredit = partialCredit,
            correctAnswer = question.correctAnswer,
            explanation = question.explanation,
            xpEarned = (question.xpValue * partialCredit).toInt(),
            conceptMasteryDelta = if (isCorrect) 0.1f else -0.05f
        )
    }

    private fun evaluateAnswer(question: AdaptiveQuestion, answer: UserAnswer): Boolean {
        return when (question.type) {
            QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE -> {
                answer.selectedOptions.size == 1 &&
                answer.selectedOptions.first() in question.correctAnswer.optionIds
            }
            QuestionType.MULTIPLE_CHOICE -> {
                answer.selectedOptions.toSet() == question.correctAnswer.optionIds.toSet()
            }
            QuestionType.FILL_IN_BLANK -> {
                answer.textAnswer?.trim()?.lowercase() == question.correctAnswer.textAnswer?.lowercase()
            }
            QuestionType.CODE_COMPLETION -> {
                // Validación básica de código
                answer.codeAnswer?.trim()?.isNotBlank() == true
            }
            QuestionType.ORDERING -> {
                answer.selectedOptions == question.correctAnswer.orderedIds
            }
            else -> false
        }
    }

    private fun calculatePartialCredit(question: AdaptiveQuestion, answer: UserAnswer): Float {
        return when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                val correct = question.correctAnswer.optionIds
                val selected = answer.selectedOptions
                val correctSelected = selected.count { it in correct }
                val incorrectSelected = selected.count { it !in correct }
                val missedCorrect = correct.count { it !in selected }

                val points = correctSelected.toFloat() / correct.size
                val penalty = (incorrectSelected + missedCorrect) * 0.1f
                (points - penalty).coerceIn(0f, 1f)
            }
            else -> if (evaluateAnswer(question, answer)) 1.0f else 0.0f
        }
    }

    private fun adaptDifficulty() {
        if (!assessment.config.adaptiveMode) return

        val recentPerformance = questionHistory.takeLast(3)
        if (recentPerformance.isEmpty()) return

        val successRate = recentPerformance.count { it.correct }.toFloat() / recentPerformance.size

        currentDifficulty = when {
            successRate >= 0.8f && currentDifficulty.ordinal < ContentDifficulty.EXPERT.ordinal -> {
                ContentDifficulty.values()[currentDifficulty.ordinal + 1]
            }
            successRate <= 0.4f && currentDifficulty.ordinal > ContentDifficulty.ELEMENTARY.ordinal -> {
                ContentDifficulty.values()[currentDifficulty.ordinal - 1]
            }
            else -> currentDifficulty
        }
    }

    /**
     * Finaliza la evaluación y calcula resultados
     */
    fun completeSession(session: AssessmentSession): AssessmentResult {
        val answers = session.answers.values.toList()
        val totalQuestions = assessment.config.questionsCount
        val correct = answers.count { answer ->
            val question = assessment.questions.find { it.id == answer.questionId }
            question?.let { evaluateAnswer(it, answer) } ?: false
        }

        val score = (correct * 100 / totalQuestions).coerceAtMost(100)
        val passed = score >= assessment.passingScore

        val questionResults = session.answers.map { (questionId, answer) ->
            val question = assessment.questions.find { it.id == questionId }!!
            val isCorrect = evaluateAnswer(question, answer)
            QuestionResult(
                questionId = questionId,
                isCorrect = isCorrect,
                partialCredit = calculatePartialCredit(question, answer),
                timeSpentSeconds = answer.timeSpentSeconds,
                hintsUsed = answer.hintsUsed,
                userAnswer = answer,
                feedback = if (isCorrect) "¡Correcto!" else question.explanation.incorrect ?: "Revisa la explicación"
            )
        }

        // Calcular XP con bonus
        val baseXp = questionResults.sumOf { (it.partialCredit * 100).toLong() }
        val timeBonus = if (assessment.config.timeBonusEnabled) {
            val avgTimePerQuestion = session.timeSpentSeconds / answers.size
            if (avgTimePerQuestion < 30) baseXp * 0.2 else 0
        } else 0
        val perfectBonus = if (score == 100) 500L else 0

        return AssessmentResult(
            sessionId = session.sessionId,
            assessmentId = assessment.id,
            score = score,
            grade = calculateGrade(score),
            correctCount = correct,
            totalQuestions = totalQuestions,
            timeSpentMinutes = session.timeSpentSeconds / 60,
            xpEarned = baseXp + timeBonus + perfectBonus,
            passed = passed,
            questionResults = questionResults,
            conceptMastery = calculateConceptMastery(questionResults),
            recommendations = generateRecommendations(questionResults),
            certificateEarned = if (passed && score >= 90) "CERT_${assessment.discipline.name}_EXPERT" else null
        )
    }

    private fun calculateGrade(score: Int): Grade {
        return when (score) {
            in 98..100 -> Grade.A_PLUS
            in 93..97 -> Grade.A
            in 90..92 -> Grade.B_PLUS
            in 85..89 -> Grade.B
            in 80..84 -> Grade.C_PLUS
            in 70..79 -> Grade.C
            in 60..69 -> Grade.D
            else -> Grade.F
        }
    }

    private fun calculateConceptMastery(results: List<QuestionResult>): Map<String, Float> {
        val conceptPerformance = mutableMapOf<String, MutableList<Float>>()

        results.forEach { result ->
            val question = assessment.questions.find { it.id == result.questionId }
            question?.let {
                conceptPerformance.getOrPut(it.conceptId) { mutableListOf() }
                    .add(result.partialCredit)
            }
        }

        return conceptPerformance.mapValues { (_, scores) ->
            scores.average().toFloat()
        }
    }

    private fun generateRecommendations(results: List<QuestionResult>): List<String> {
        val weakConcepts = results.filter { it.partialCredit < 0.6f }
            .mapNotNull { result ->
                assessment.questions.find { it.id == result.questionId }?.conceptId
            }.distinct()

        return weakConcepts.map { "Repasa el concepto: $it" }
    }

    /**
     * Obtiene estadísticas de la sesión en tiempo real
     */
    fun getSessionStats(session: AssessmentSession): SessionStats {
        val answered = session.answers.size
        val correct = session.answers.count { (questionId, answer) ->
            val question = assessment.questions.find { it.id == questionId }
            question?.let { evaluateAnswer(it, answer) } ?: false
        }

        return SessionStats(
            questionsAnswered = answered,
            questionsRemaining = assessment.config.questionsCount - answered,
            currentScore = if (answered > 0) (correct * 100 / answered) else 0,
            estimatedTimeRemaining = (assessment.config.questionsCount - answered) * 60,
            isOnTrackToPass = (correct.toFloat() / assessment.config.questionsCount) >= assessment.passingScore / 100f
        )
    }
}

@Serializable
data class QuestionPerformance(
    val questionId: String,
    val difficulty: ContentDifficulty,
    val correct: Boolean,
    val timeSpent: Int
)

@Serializable
data class AnswerFeedback(
    val isCorrect: Boolean,
    val partialCredit: Float,
    val correctAnswer: CorrectAnswer,
    val explanation: Explanation,
    val xpEarned: Int,
    val conceptMasteryDelta: Float
)

@Serializable
data class SessionStats(
    val questionsAnswered: Int,
    val questionsRemaining: Int,
    val currentScore: Int,
    val estimatedTimeRemaining: Int,
    val isOnTrackToPass: Boolean
)

/**
 * Factory para crear evaluaciones predefinidas
 */
object AssessmentFactory {

    fun createScienceAssessment(age: Int): Assessment {
        return Assessment(
            id = "assessment_science_${age}",
            title = "Evaluación de Ciencias",
            description = "Demuestra tu conocimiento en física, química y biología",
            discipline = Discipline.SCIENCE,
            phase = when (age) {
                in 3..7 -> DigitalPhase.SENSORIAL
                in 8..14 -> DigitalPhase.CREATIVE
                in 15..20 -> DigitalPhase.PROFESSIONAL
                else -> DigitalPhase.INNOVATOR
            },
            difficultyRange = DifficultyRange(ContentDifficulty.ELEMENTARY, ContentDifficulty.ADVANCED),
            questions = generateScienceQuestions(age),
            config = AssessmentConfig(
                adaptiveMode = true,
                questionsCount = 10,
                passingScore = 70
            ),
            timeLimitMinutes = if (age > 14) 30 else null
        )
    }

    private fun generateScienceQuestions(age: Int): List<AdaptiveQuestion> {
        return when (age) {
            in 3..7 -> listOf(
                createQuestion(
                    type = QuestionType.SINGLE_CHOICE,
                    stem = "¿Qué color tiene el cielo en un día soleado?",
                    options = listOf(
                        QuestionOption("1", "Azul", true),
                        QuestionOption("2", "Verde"),
                        QuestionOption("3", "Rojo")
                    ),
                    correct = CorrectAnswer(optionIds = listOf("1")),
                    concept = "colors_sky"
                ),
                createQuestion(
                    type = QuestionType.TRUE_FALSE,
                    stem = "Los peces respiran bajo el agua usando branquias",
                    options = listOf(
                        QuestionOption("1", "Verdadero", true),
                        QuestionOption("2", "Falso")
                    ),
                    correct = CorrectAnswer(optionIds = listOf("1")),
                    concept = "fish_breathing"
                )
            )
            in 8..14 -> listOf(
                createQuestion(
                    type = QuestionType.SINGLE_CHOICE,
                    stem = "¿Cuál es el planeta más cercano al Sol?",
                    options = listOf(
                        QuestionOption("1", "Venus"),
                        QuestionOption("2", "Mercurio", true),
                        QuestionOption("3", "Tierra")
                    ),
                    correct = CorrectAnswer(optionIds = listOf("2")),
                    concept = "solar_system"
                ),
                createQuestion(
                    type = QuestionType.FILL_IN_BLANK,
                    stem = "El agua hierve a ___ grados Celsius (al nivel del mar)",
                    correct = CorrectAnswer(textAnswer = "100"),
                    concept = "water_boiling"
                )
            )
            else -> listOf(
                createQuestion(
                    type = QuestionType.SINGLE_CHOICE,
                    stem = "¿Cuál es la fórmula química del ácido sulfúrico?",
                    options = listOf(
                        QuestionOption("1", "HCl"),
                        QuestionOption("2", "H₂SO₄", true),
                        QuestionOption("3", "HNO₃")
                    ),
                    correct = CorrectAnswer(optionIds = listOf("2")),
                    concept = "chemistry_acids"
                ),
                createQuestion(
                    type = QuestionType.CODE_COMPLETION,
                    stem = "Completa la función para calcular la fuerza usando la segunda ley de Newton: F = ___",
                    correct = CorrectAnswer(codeAnswer = "m * a"),
                    concept = "physics_newton"
                )
            )
        }
    }

    private fun createQuestion(
        type: QuestionType,
        stem: String,
        options: List<QuestionOption>,
        correct: CorrectAnswer,
        concept: String
    ): AdaptiveQuestion {
        return AdaptiveQuestion(
            id = UUID.randomUUID().toString(),
            type = type,
            difficulty = ContentDifficulty.BASIC,
            conceptId = concept,
            stem = stem,
            options = options,
            correctAnswer = correct,
            hint = AdaptiveHint(
                hintId = "hint_${concept}",
                triggerPattern = ErrorPattern(type = ErrorType.CONCEPTUAL_MISUNDERSTANDING),
                hintLevel = HintLevel.DIRECT,
                message = "Piensa en los conceptos fundamentales"
            ),
            explanation = Explanation(
                correct = "¡Excelente! Has aplicado el concepto correctamente.",
                incorrect = "Revisa los fundamentos de este tema",
                detailed = "Este concepto es fundamental para temas avanzados"
            )
        )
    }

    fun createProgrammingAssessment(): Assessment {
        return Assessment(
            id = "assessment_programming",
            title = "Fundamentos de Programación",
            description = "Evaluación de conceptos básicos de algoritmos y lógica",
            discipline = Discipline.TECHNOLOGY,
            phase = DigitalPhase.CREATIVE,
            difficultyRange = DifficultyRange(ContentDifficulty.BASIC, ContentDifficulty.ADVANCED),
            questions = listOf(
                createCodeQuestion(
                    stem = "¿Qué imprime este código?\n\nfor i in range(3):\n    print(i)",
                    options = listOf(
                        QuestionOption("1", "0 1 2", true),
                        QuestionOption("2", "1 2 3"),
                        QuestionOption("3", "0 1 2 3")
                    ),
                    correct = CorrectAnswer(optionIds = listOf("1")),
                    concept = "loops_basic"
                ),
                createCodeQuestion(
                    stem = "Completa: Una función debe ___ para poder reutilizarse",
                    correct = CorrectAnswer(textAnswer = "retornar"),
                    concept = "functions_return"
                )
            ),
            config = AssessmentConfig(
                adaptiveMode = true,
                questionsCount = 15,
                showHints = true,
                timeLimitMinutes = 45
            )
        )
    }

    private fun createCodeQuestion(
        stem: String,
        options: List<QuestionOption>,
        correct: CorrectAnswer,
        concept: String
    ): AdaptiveQuestion {
        return AdaptiveQuestion(
            id = UUID.randomUUID().toString(),
            type = if (options.size > 1) QuestionType.SINGLE_CHOICE else QuestionType.FILL_IN_BLANK,
            difficulty = ContentDifficulty.INTERMEDIATE,
            conceptId = concept,
            stem = stem,
            options = options,
            correctAnswer = correct,
            hint = AdaptiveHint(
                hintId = "code_hint_${concept}",
                triggerPattern = ErrorPattern(type = ErrorType.SYNTAX_ERROR),
                hintLevel = HintLevel.EXPLANATORY,
                message = "Revisa la sintaxis y la lógica del código"
            ),
            explanation = Explanation(
                correct = "¡Código correcto! Buena comprensión de la lógica.",
                incorrect = "Revisa la ejecución paso a paso",
                detailed = "En programación, cada línea se ejecuta secuencialmente"
            ),
            xpValue = 150
        )
    }
}

// Type aliases para compatibilidad
private typealias AdaptiveHint = com.LBs.EEDA.domain.model.educational.AdaptiveHint
private typealias ErrorPattern = com.LBs.EEDA.domain.model.educational.ErrorPattern
private typealias ErrorType = com.LBs.EEDA.domain.model.educational.CodeErrorType
private typealias HintLevel = com.LBs.EEDA.domain.model.educational.HintLevel
