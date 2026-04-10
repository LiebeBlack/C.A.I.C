package com.LBs.EEDA.domain.usecase.educational

import com.LBs.EEDA.domain.model.educational.*
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Use Case: Obtener contenido educativo adaptado por edad.
 * Implementa la lógica de negocio para personalizar contenido según el perfil del usuario.
 */
class GetAdaptiveContentUseCase(
    private val profileRepository: ChildProfileRepository
) {
    suspend operator fun invoke(
        conceptId: String,
        userId: String
    ): Result<AgeAdaptiveContent> = withContext(Dispatchers.IO) {
        try {
            val profileResult = profileRepository.getProfile().first()
            val age = profileResult.getOrNull()?.age ?: 10
            
            val adaptedContent = ContentAdaptationEngine.adaptContent(
                baseConcept = conceptId,
                age = age
            )
            Result.success(adaptedContent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Use Case: Procesar intento de minijuego.
 * Maneja la lógica de validación, puntuación y progreso.
 */
class ProcessMinigameAttemptUseCase(
    private val minigameRepository: MinigameRepository,
    private val profileRepository: ChildProfileRepository
) {
    suspend operator fun invoke(
        minigameId: String,
        userId: String,
        userSolution: UserSolution
    ): Result<MinigameResult> = withContext(Dispatchers.IO) {
        try {
            // Obtener minijuego
            val minigame = minigameRepository.getMinigame(minigameId)
                ?: return@withContext Result.failure(IllegalArgumentException("Minijuego no encontrado"))
            
            // Obtener progreso actual del usuario
            val userProgress = minigameRepository.getUserProgress(userId)
            
            // Crear motor de juego y procesar intento
            val engine = MinigameEngine(minigame, userProgress)
            val result = engine.submitAttempt(userSolution)
            
            // Persistir resultado
            when (result) {
                is AttemptResult.Success -> {
                    // Actualizar perfil con XP
                    updateUserXp(userId, result.xpEarned)
                    
                    // Guardar completitud del minijuego
                    minigameRepository.saveAttempt(
                        MinigameAttempt(
                            minigameId = minigameId,
                            userId = userId,
                            timestamp = System.currentTimeMillis(),
                            stageNumber = engine.getProgress().currentStage,
                            attemptsCount = engine.getProgress().attemptsInCurrentStage,
                            successful = true,
                            timeSpentSeconds = 0, // Calcular desde UI
                            errorsMade = emptyList(),
                            hintsUsed = emptyList(),
                            finalSolution = userSolution.toString(),
                            score = result.score,
                            xpEarned = result.xpEarned
                        )
                    )
                    
                    Result.success(MinigameResult.Success(
                        xpEarned = result.xpEarned,
                        score = result.score,
                        perfectRun = result.perfectRun,
                        nextStageAvailable = result.nextStageAvailable,
                        unlockedContent = if (result.nextStageAvailable) {
                            getUnlockedContent(minigame)
                        } else null
                    ))
                }
                is AttemptResult.Failure -> {
                    // Guardar intento fallido para análisis
                    minigameRepository.saveAttempt(
                        MinigameAttempt(
                            minigameId = minigameId,
                            userId = userId,
                            timestamp = System.currentTimeMillis(),
                            stageNumber = engine.getProgress().currentStage,
                            attemptsCount = engine.getProgress().attemptsInCurrentStage,
                            successful = false,
                            timeSpentSeconds = 0,
                            errorsMade = listOf(
                                ErrorRecord(
                                    timestamp = System.currentTimeMillis(),
                                    errorType = result.errorType,
                                    userInput = userSolution.toString(),
                                    expectedPattern = "",
                                    hintProvided = result.hint
                                )
                            ),
                            hintsUsed = result.hint?.let { listOf(it.hintId) } ?: emptyList(),
                            finalSolution = userSolution.toString(),
                            score = result.partialCredit,
                            xpEarned = 0
                        )
                    )
                    
                    Result.success(MinigameResult.Failure(
                        errorType = result.errorType,
                        message = result.message,
                        details = result.details,
                        hint = result.hint,
                        remainingAttempts = result.remainingAttempts,
                        partialCredit = result.partialCredit
                    ))
                }
                is AttemptResult.Skipped -> {
                    Result.success(MinigameResult.Skipped(
                        message = result.message,
                        xpPenalty = result.xpPenalty
                    ))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun updateUserXp(userId: String, xp: Long) {
        profileRepository.getProfile().first().onSuccess { profile ->
            profile?.let {
                val updatedProfile = it.addXp(xp)
                profileRepository.saveProfile(updatedProfile)
            }
        }
    }
    
    private fun getUnlockedContent(minigame: Minigame): String? {
        // Simplificado - verificar si el minijuego tiene más etapas
        return if (minigame.stages.isNotEmpty()) {
            "Desbloqueado: Siguiente nivel de ${minigame.conceptId}"
        } else null
    }
}

/**
 * Use Case: Ejecutar código en sandbox.
 * Proporciona un entorno seguro para experimentación con código.
 */
class ExecuteSandboxCodeUseCase(
    private val sandboxRepository: SandboxRepository
) {
    suspend operator fun invoke(
        sandboxId: String,
        code: String,
        userId: String
    ): Result<SandboxExecutionResult> = withContext(Dispatchers.IO) {
        try {
            val sandbox = sandboxRepository.getSandbox(sandboxId)
                ?: return@withContext Result.failure(IllegalArgumentException("Sandbox no encontrado"))
            
            val engine = SandboxEngine(sandbox)
            val result = engine.execute(code)
            
            // Guardar historial de ejecución
            sandboxRepository.saveExecution(userId, sandboxId, code, result)
            
            when (result) {
                is ExecutionResult.Success -> {
                    Result.success(SandboxExecutionResult.Success(
                        output = result.output,
                        executionTimeMs = result.executionTimeMs,
                        objectivesMet = result.allObjectivesMet,
                        xpEarned = result.xpEarned,
                        nextLevelUnlocked = result.nextLevelUnlocked
                    ))
                }
                is ExecutionResult.Error -> {
                    Result.success(SandboxExecutionResult.Error(
                        errorType = result.errorType,
                        message = result.message,
                        lineNumber = result.lineNumber,
                        hint = result.hint
                    ))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Use Case: Evaluar conocimientos adaptativamente.
 * Genera evaluaciones que se ajustan según las respuestas del usuario.
 */
class GenerateAdaptiveAssessmentUseCase(
    private val contentRepository: ContentRepository,
    private val profileRepository: ChildProfileRepository
) {
    suspend operator fun invoke(
        userId: String,
        conceptIds: List<String>
    ): Result<AdaptiveAssessment> = withContext(Dispatchers.IO) {
        try {
            val profileResult = profileRepository.getProfile().first()
            val age = profileResult.getOrNull()?.age ?: 10
            
            val questions = conceptIds.flatMap { conceptId ->
                generateQuestionsForConcept(conceptId, age)
            }.shuffled().take(10) // 10 preguntas máximo
            
            Result.success(
                AdaptiveAssessment(
                    id = generateAssessmentId(),
                    userId = userId,
                    questions = questions,
                    timeLimitMinutes = questions.size * 2,
                    passingScore = 70
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateQuestionsForConcept(conceptId: String, age: Int): List<AdaptiveQuestion> {
        val content = ContentAdaptationEngine.adaptContent(conceptId, age)
        
        return listOf(
            AdaptiveQuestion.MultipleChoice(
                id = "${conceptId}_mc_1",
                question = "¿Qué describe mejor: ${content.displayTitle}?",
                options = listOf(
                    content.analogy,
                    "Un concepto completamente diferente",
                    "Un tipo de comida",
                    "Un animal del bosque"
                ),
                correctAnswer = 0,
                hintOnFailure = "Piensa en: ${content.analogy.take(50)}...",
                difficulty = content.difficulty
            ),
            AdaptiveQuestion.TrueFalse(
                id = "${conceptId}_tf_1",
                question = "${content.technicalTerm ?: content.displayTitle} es importante para la seguridad digital",
                correctAnswer = true,
                hintOnFailure = "Revisa la explicación sobre seguridad",
                difficulty = content.difficulty
            ),
            AdaptiveQuestion.Matching(
                id = "${conceptId}_match_1",
                question = "Relaciona los conceptos:",
                pairs = mapOf(
                    content.displayTitle to content.analogy.take(30),
                    "Seguridad" to "Protección",
                    "Internet" to "Red de redes"
                ),
                hintOnFailure = "Busca conexiones lógicas entre términos",
                difficulty = content.difficulty
            )
        )
    }
    
    private fun generateAssessmentId(): String {
        return "assessment_${System.currentTimeMillis()}"
    }
}

/**
 * Use Case: Procesar respuesta de evaluación adaptativa.
 */
class ProcessAssessmentResponseUseCase(
    private val assessmentRepository: AssessmentRepository
) {
    suspend operator fun invoke(
        assessmentId: String,
        questionId: String,
        answer: Any,
        attemptNumber: Int
    ): Result<AssessmentResponseResult> = withContext(Dispatchers.IO) {
        try {
            val assessment = assessmentRepository.getAssessment(assessmentId)
                ?: return@withContext Result.failure(IllegalArgumentException("Evaluación no encontrada"))
            
            val question = assessment.questions.find { it.id == questionId }
                ?: return@withContext Result.failure(IllegalArgumentException("Pregunta no encontrada"))
            
            val isCorrect = validateAnswer(question, answer)
            
            // Adaptar siguiente pregunta basado en rendimiento
            val nextQuestion = if (isCorrect) {
                selectNextQuestion(assessment, questionId, increaseDifficulty = true)
            } else {
                selectNextQuestion(assessment, questionId, increaseDifficulty = false)
            }
            
            // Generar hint si es incorrecto
            val hint = if (!isCorrect && attemptNumber >= 2) {
                when (question) {
                    is AdaptiveQuestion.MultipleChoice -> question.hintOnFailure
                    is AdaptiveQuestion.TrueFalse -> question.hintOnFailure
                    is AdaptiveQuestion.Matching -> question.hintOnFailure
                    is AdaptiveQuestion.CodeCompletion -> question.hintOnFailure
                }
            } else null
            
            Result.success(
                AssessmentResponseResult(
                    isCorrect = isCorrect,
                    score = if (isCorrect) 100 else 0,
                    nextQuestionId = nextQuestion?.id,
                    hint = hint,
                    assessmentComplete = nextQuestion == null,
                    finalScore = if (nextQuestion == null) calculateFinalScore(assessmentId) else null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateAnswer(question: AdaptiveQuestion, answer: Any): Boolean {
        return when (question) {
            is AdaptiveQuestion.MultipleChoice -> answer == question.correctAnswer
            is AdaptiveQuestion.TrueFalse -> answer == question.correctAnswer
            is AdaptiveQuestion.Matching -> {
                // Validar que el mapa de respuesta coincida
                if (answer is Map<*, *>) {
                    answer == question.pairs
                } else false
            }
            is AdaptiveQuestion.CodeCompletion -> {
                // Validación de código simplificada
                if (answer is String) {
                    question.expectedPattern?.let { pattern ->
                        answer.matches(pattern.toRegex())
                    } ?: false
                } else false
            }
        }
    }
    
    private fun selectNextQuestion(
        assessment: AdaptiveAssessment,
        currentQuestionId: String,
        increaseDifficulty: Boolean
    ): AdaptiveQuestion? {
        val currentIndex = assessment.questions.indexOfFirst { it.id == currentQuestionId }
        if (currentIndex == -1 || currentIndex >= assessment.questions.size - 1) return null
        
        // Seleccionar siguiente pregunta adaptativa
        val remainingQuestions = assessment.questions.drop(currentIndex + 1)
        
        return if (increaseDifficulty) {
            remainingQuestions.find { it.difficulty.ordinal > assessment.questions[currentIndex].difficulty.ordinal }
                ?: remainingQuestions.firstOrNull()
        } else {
            remainingQuestions.find { it.difficulty.ordinal <= assessment.questions[currentIndex].difficulty.ordinal }
                ?: remainingQuestions.firstOrNull()
        }
    }
    
    private suspend fun calculateFinalScore(assessmentId: String): Int {
        val responses = assessmentRepository.getResponsesForAssessment(assessmentId)
        if (responses.isEmpty()) return 0
        val correctCount = responses.count { it.isCorrect }
        return (correctCount * 100 / responses.size)
    }
}

// Resultados y modelos de datos para los Use Cases

sealed class MinigameResult {
    data class Success(
        val xpEarned: Long,
        val score: Int,
        val perfectRun: Boolean,
        val nextStageAvailable: Boolean,
        val unlockedContent: String?
    ) : MinigameResult()
    
    data class Failure(
        val errorType: ErrorType,
        val message: String,
        val details: List<String>,
        val hint: AdaptiveHint?,
        val remainingAttempts: Int,
        val partialCredit: Int
    ) : MinigameResult()
    
    data class Skipped(
        val message: String,
        val xpPenalty: Long
    ) : MinigameResult()
}

sealed class SandboxExecutionResult {
    data class Success(
        val output: String,
        val executionTimeMs: Long,
        val objectivesMet: List<String>,
        val xpEarned: Long,
        val nextLevelUnlocked: Boolean
    ) : SandboxExecutionResult()
    
    data class Error(
        val errorType: CodeErrorType,
        val message: String,
        val lineNumber: Int?,
        val hint: CodeHint?
    ) : SandboxExecutionResult()
}

data class AdaptiveAssessment(
    val id: String,
    val userId: String,
    val questions: List<AdaptiveQuestion>,
    val timeLimitMinutes: Int,
    val passingScore: Int
)

// Tipos para AssessmentScreen - simplificados para UI
data class Question(
    val id: String,
    val type: QuestionType,
    val questionText: String,
    val options: List<String>? = null,
    val correctOptionIndex: Int? = null,
    val correctAnswerBoolean: Boolean? = null,
    val acceptedAnswers: List<String>? = null,
    val explanation: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    TEXT_INPUT,
    DRAG_DROP
}

sealed class AdaptiveQuestion(
    open val id: String,
    open val difficulty: ContentDifficulty
) {
    data class MultipleChoice(
        override val id: String,
        val question: String,
        val options: List<String>,
        val correctAnswer: Int,
        val hintOnFailure: String,
        override val difficulty: ContentDifficulty
    ) : AdaptiveQuestion(id, difficulty)
    
    data class TrueFalse(
        override val id: String,
        val question: String,
        val correctAnswer: Boolean,
        val hintOnFailure: String,
        override val difficulty: ContentDifficulty
    ) : AdaptiveQuestion(id, difficulty)
    
    data class Matching(
        override val id: String,
        val question: String,
        val pairs: Map<String, String>,
        val hintOnFailure: String,
        override val difficulty: ContentDifficulty
    ) : AdaptiveQuestion(id, difficulty)
    
    data class CodeCompletion(
        override val id: String,
        val question: String,
        val initialCode: String,
        val expectedPattern: String?,
        val hintOnFailure: String,
        override val difficulty: ContentDifficulty
    ) : AdaptiveQuestion(id, difficulty)
}

data class AssessmentResponseResult(
    val isCorrect: Boolean,
    val score: Int,
    val nextQuestionId: String?,
    val hint: String?,
    val assessmentComplete: Boolean,
    val finalScore: Int?
)

// Interfaces de repositorios (para inyección de dependencias)

interface SandboxRepository {
    suspend fun getSandbox(id: String): CodeSandbox?
    suspend fun saveExecution(userId: String, sandboxId: String, code: String, result: ExecutionResult)
}

interface AssessmentRepository {
    suspend fun getAssessment(id: String): AdaptiveAssessment?
    suspend fun saveAssessment(assessment: AdaptiveAssessment)
    suspend fun saveResponse(assessmentId: String, questionId: String, answer: Any, isCorrect: Boolean)
    suspend fun getResponsesForAssessment(assessmentId: String): List<com.LBs.EEDA.data.repository.ResponseRecord>
}

interface ContentRepository {
    suspend fun getContent(conceptId: String, age: Int): AgeAdaptiveContent
    suspend fun getMinigamesForConcept(conceptId: String, ageBracket: AgeBracket): List<Minigame>
    suspend fun getSandboxesForConcept(conceptId: String, ageBracket: AgeBracket): List<CodeSandbox>
}
