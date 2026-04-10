package com.LBs.EEDA.domain.model.educational

import kotlinx.serialization.Serializable

/**
 * Sistema de Minijuegos Educativos Interactivos.
 * Cada minijuego tiene una mecánica específica, dificultad adaptativa y sistema de pistas.
 */
@Serializable
data class Minigame(
    val id: String,
    val type: MinigameType,
    val title: String,
    val description: String,
    val conceptId: String, // Relación con ContentAdaptationEngine
    val difficulty: ContentDifficulty,
    val ageBracket: AgeBracket,
    val estimatedDurationMinutes: Int,
    val xpReward: Long,
    val stages: List<MinigameStage>,
    val config: MinigameConfig
)

enum class MinigameType {
    DRAG_DROP_SORTING,
    SEQUENCE_PUZZLE,
    CODE_COMPLETION,
    NETWORK_ROUTING,
    ENCRYPTION_DECRYPT,
    PATTERN_MATCHING,
    SECURITY_AUDIT,
    LOGIC_GATE_SIM,
    MEMORY_CHALLENGE,
    QUIZ_ADAPTIVE,
    SIMULATION,
    PUZZLE,
    MATCHING,
    STRATEGY,
    MARKET_SIMULATOR,     // Financial decision making
    SPACE_COLONY_SIM,     // Resource management in orbit
    ETHICAL_DILEMMA_SIM,  // Decision making with consequences
    CODE_REFACTOR_CHALLENGE, // Optimizing existing code
    LOGIC_CIRCUIT_DESIGN, // Complex electronics/logic
    DATA_VIZ_EXPLORER,    // Interpreting graphs
    GEOPOLITICAL_SUMMIT,  // Negotiation simulation
    HEALTH_LAB_SIM,        // Biological/Medical lab
    CHESS_PUZZLE,         // Tactical chess problems
    ART_DRAWING,          // Creative canvas tasks
    KERNEL_WAR_RTS,          // Real-time strategy in kernel space
    DEBUGGER_QUEST,          // Bug hunting and fixing
    BINARY_BRIDGE,           // Logic connection game
    AI_TRAINING_SIM,         // Machine learning simulation
    INFINITE_LOGIC_GEN    // Procedurally generated puzzles
}

@Serializable
data class MinigameStage(
    val stageNumber: Int,
    val instruction: String,
    val elements: List<GameElement>,
    val correctSolution: Solution,
    val maxAttempts: Int = 3,
    val hints: List<AdaptiveHint>,
    val timeLimitSeconds: Int? = null
)

@Serializable
data class GameElement(
    val id: String,
    val type: ElementVisualType,
    val label: String,
    val value: String,
    val category: String? = null, // Para agrupar en drag-drop
    val isDraggable: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

enum class ElementVisualType {
    CODE_BLOCK,      // Bloque de código estilo Scratch
    ICON,            // Icono representativo
    TEXT_CARD,       // Tarjeta de texto
    LOGIC_GATE,      // Compuerta lógica (AND, OR, NOT)
    NETWORK_NODE,    // Nodo de red
    DATA_PACKET,     // Paquete de datos
    LOCK_KEY,        // Candado/llave
    BINARY_DIGIT,    // 0 o 1
    // Game Specific Elements
    RAM_CELL,        // Memory addresses for Kernel War
    ALGORITHM_UNIT,  // RTS units (Sorting, Search, Hash)
    SYNTAX_ERROR,    // Obstacles in Debugger Quest
    BOOLEAN_BRIDGE,  // Connectors in Binary Bridge
    VOLTAGE_METER,   // Feedback for logic gates
    MARKET_CHART,    // Data for financial simulation
    RESOURCE_CRATE,  // Items for colony sim
    DNA_STRAND,      // Biological segments
    DILEMMA_NODE,    // Decision points
    CIRCUIT_PART,    // Resistors, capacitors, etc.
    CHESS_PIECE,     // King, Queen, etc.
    DRAWING_STROKE   // Canvas paths
}

@Serializable
data class Solution(
    val orderedElements: List<String>? = null, // Para secuencias ordenadas
    val correctPairs: Map<String, String>? = null, // Para matching
    val correctValue: String? = null, // Para input único
    val conditions: List<Condition>? = null // Para validación compleja
)

@Serializable
data class Condition(
    val type: ConditionType,
    val target: String,
    val expected: String,
    val errorMessage: String
)

enum class ConditionType {
    EQUALS,
    CONTAINS,
    STARTS_WITH,
    MATCHES_REGEX,
    LESS_THAN,
    GREATER_THAN
}

/**
 * Sistema de pistas adaptativas basadas en errores.
 * Cada pista está asociada a patrones de error específicos.
 */
@Serializable
data class AdaptiveHint(
    val hintId: String,
    val triggerPattern: ErrorPattern,
    val hintLevel: HintLevel,
    val message: String,
    val visualAid: VisualAid? = null,
    val relatedConcept: String? = null
)

@Serializable
data class ErrorPattern(
    val type: ErrorType,
    val containsText: List<String>? = null,
    val attemptCountMin: Int? = null, // Mínimo de intentos
    val attemptCountMax: Int? = null, // Máximo de intentos  
    val timeSpentSeconds: Int? = null,
    val previousHintsShown: List<String>? = null
)

typealias ErrorType = CodeErrorType

enum class HintLevel {
    SUBTLE,      // Pista vaga, empoderadora
    DIRECT,      // Pista específica
    EXPLANATORY, // Explicación del concepto
    SOLUTION     // Mostrar solución (último recurso)
}

@Serializable
data class VisualAid(
    val type: VisualAidType,
    val content: String,
    val highlightIds: List<String>? = null
)

enum class VisualAidType {
    ARROW_POINTING,
    COLOR_HIGHLIGHT,
    ANIMATION_DEMO,
    DIAGRAM_OVERLAY
}

@Serializable
data class MinigameConfig(
    val allowSkipAfterAttempts: Int = 3,
    val partialCreditEnabled: Boolean = true,
    val showHintsAutomatically: Boolean = true,
    val adaptiveDifficulty: Boolean = true,
    val sandboxMode: Boolean = false // Permitir experimentación libre
)

/**
 * Resultado de un intento de minijuego.
 * Captura métricas para análisis de aprendizaje.
 */
@Serializable
data class MinigameAttempt(
    val minigameId: String,
    val userId: String,
    val timestamp: Long,
    val stageNumber: Int,
    val attemptsCount: Int,
    val successful: Boolean,
    val timeSpentSeconds: Int,
    val errorsMade: List<ErrorRecord>,
    val hintsUsed: List<String>,
    val finalSolution: String?, // JSON de la solución final
    val score: Int, // 0-100
    val xpEarned: Long
)

@Serializable
data class ErrorRecord(
    val timestamp: Long,
    val errorType: ErrorType,
    val userInput: String,
    val expectedPattern: String,
    val hintProvided: AdaptiveHint?
)

/**
 * Motor de Minijuegos - Lógica de Negocio
 * Separa la presentación de la lógica del juego.
 */
class MinigameEngine(
    private val minigame: Minigame,
    private val userProgress: UserMinigameProgress
) {
    private var currentStageIndex = 0
    private var currentAttempt = 0
    private val errorHistory = mutableListOf<ErrorRecord>()
    private val hintsShown = mutableListOf<String>()
    
    fun getCurrentStage(): MinigameStage = minigame.stages[currentStageIndex]
    
    fun submitAttempt(userSolution: UserSolution): AttemptResult {
        currentAttempt++
        val stage = getCurrentStage()
        val validation = validateSolution(userSolution, stage.correctSolution)
        
        return if (validation.isCorrect) {
            handleSuccess(stage)
        } else {
            handleFailure(validation, stage, userSolution)
        }
    }
    
    private fun validateSolution(
        userSolution: UserSolution,
        correctSolution: Solution
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        // Validar orden si aplica
        userSolution.orderedElements?.let { userOrder ->
            correctSolution.orderedElements?.let { correctOrder ->
                if (userOrder != correctOrder) {
                    errors.add(ValidationError(
                        type = ErrorType.WRONG_ORDER,
                        message = "El orden no es correcto",
                        details = findOrderingErrors(userOrder, correctOrder)
                    ))
                }
            }
        }
        
        // Validar pares si aplica
        userSolution.pairs?.let { userPairs ->
            correctSolution.correctPairs?.let { correctPairs ->
                val wrongPairs = userPairs.filter { (k, v) -> correctPairs[k] != v }
                if (wrongPairs.isNotEmpty()) {
                    errors.add(ValidationError(
                        type = ErrorType.LOGIC_ERROR,
                        message = "Algunas conexiones son incorrectas",
                        details = wrongPairs.keys.toList()
                    ))
                }
            }
        }
        
        // Validar valor si aplica
        userSolution.value?.let { userValue ->
            correctSolution.correctValue?.let { correctValue ->
                if (userValue != correctValue) {
                    errors.add(ValidationError(
                        type = ErrorType.INCORRECT_VALUE,
                        message = "El valor ingresado no es correcto",
                        details = listOf("Esperado: $correctValue, Recibido: $userValue")
                    ))
                }
            }
        }
        
        // Validar condiciones complejas
        correctSolution.conditions?.forEach { condition ->
            val isValid = when (condition.type) {
                ConditionType.EQUALS -> userSolution.value == condition.expected
                ConditionType.CONTAINS -> userSolution.value?.contains(condition.expected) == true
                ConditionType.STARTS_WITH -> userSolution.value?.startsWith(condition.expected) == true
                ConditionType.MATCHES_REGEX -> userSolution.value?.matches(Regex(condition.expected)) == true
                ConditionType.LESS_THAN -> (userSolution.value?.toIntOrNull() ?: 0) < condition.expected.toInt()
                ConditionType.GREATER_THAN -> (userSolution.value?.toIntOrNull() ?: 0) > condition.expected.toInt()
            }
            
            if (!isValid) {
                errors.add(ValidationError(
                    type = ErrorType.LOGIC_ERROR,
                    message = condition.errorMessage,
                    details = listOf("Condición fallida: ${condition.type}")
                ))
            }
        }
        
        return ValidationResult(
            isCorrect = errors.isEmpty(),
            errors = errors,
            partialCredit = calculatePartialCredit(errors, correctSolution)
        )
    }
    
    private fun findOrderingErrors(userOrder: List<String>, correctOrder: List<String>): List<String> {
        return userOrder.mapIndexedNotNull { index, element ->
            val correctIndex = correctOrder.indexOf(element)
            if (correctIndex != -1 && correctIndex != index) {
                "'$element' debería estar en posición ${correctIndex + 1}, no ${index + 1}"
            } else null
        }
    }
    
    private fun calculatePartialCredit(
        errors: List<ValidationError>,
        solution: Solution
    ): Int {
        if (errors.isEmpty()) return 100
        
        val totalElements = (solution.orderedElements?.size ?: 0) +
                           (solution.correctPairs?.size ?: 0) +
                           (if (solution.correctValue != null) 1 else 0)
        
        val errorWeight = if (totalElements > 0) 100 / totalElements else 0
        return (100 - (errors.size * errorWeight)).coerceAtLeast(0)
    }
    
    private fun handleSuccess(stage: MinigameStage): AttemptResult {
        val score = calculateScore(stage)
        val xpEarned = (minigame.xpReward * score) / 100
        
        return AttemptResult.Success(
            score = score,
            xpEarned = xpEarned,
            timeBonus = calculateTimeBonus(stage),
            nextStageAvailable = currentStageIndex < minigame.stages.size - 1,
            perfectRun = errorHistory.isEmpty()
        )
    }
    
    private fun handleFailure(
        validation: ValidationResult,
        stage: MinigameStage,
        userSolution: UserSolution
    ): AttemptResult {
        val errorType = validation.errors.firstOrNull()?.type ?: ErrorType.LOGIC_ERROR
        
        val errorRecord = ErrorRecord(
            timestamp = System.currentTimeMillis(),
            errorType = errorType,
            userInput = userSolution.toString(),
            expectedPattern = stage.correctSolution.toString(),
            hintProvided = determineNextHint(errorType, stage)
        )
        errorHistory.add(errorRecord)
        
        // Mostrar pista si está habilitado
        if (minigame.config.showHintsAutomatically && errorRecord.hintProvided != null) {
            hintsShown.add(errorRecord.hintProvided.hintId)
        }
        
        return AttemptResult.Failure(
            errorType = errorType,
            message = validation.errors.firstOrNull()?.message ?: "Respuesta incorrecta",
            details = validation.errors.flatMap { it.details },
            hint = errorRecord.hintProvided,
            remainingAttempts = stage.maxAttempts - currentAttempt,
            partialCredit = validation.partialCredit
        )
    }
    
    private fun determineNextHint(errorType: ErrorType, stage: MinigameStage): AdaptiveHint? {
        // Buscar pista que coincida con el patrón de error
        return stage.hints.firstOrNull { hint ->
            hint.triggerPattern.type == errorType &&
            hint.triggerPattern.attemptCountMin?.let { currentAttempt >= it } != false &&
            hint.triggerPattern.attemptCountMax?.let { currentAttempt <= it } != false &&
            hint.hintLevel.ordinal <= getAppropriateHintLevel()
        }
    }
    
    private fun getAppropriateHintLevel(): Int {
        // Más intentos = pistas más directas
        return when (currentAttempt) {
            1 -> HintLevel.SUBTLE.ordinal
            2 -> HintLevel.DIRECT.ordinal
            3 -> HintLevel.EXPLANATORY.ordinal
            else -> HintLevel.SOLUTION.ordinal
        }
    }
    
    private fun calculateScore(stage: MinigameStage): Int {
        val baseScore = 100
        val attemptPenalty = (currentAttempt - 1) * 10
        val timePenalty = stage.timeLimitSeconds?.let { limit ->
            // Calcular penalización basada en tiempo
            0 // Simplificado
        } ?: 0
        
        return (baseScore - attemptPenalty - timePenalty).coerceIn(0, 100)
    }
    
    private fun calculateTimeBonus(stage: MinigameStage): Long {
        // Bonus por velocidad si hay límite de tiempo
        return 0L // Implementación según necesidad
    }
    
    fun getHint(): AdaptiveHint? {
        val stage = getCurrentStage()
        return determineNextHint(ErrorType.CONCEPTUAL_MISUNDERSTANDING, stage)
    }
    
    fun skipStage(): AttemptResult {
        return AttemptResult.Skipped(
            message = "Etapa omitida",
            xpPenalty = minigame.xpReward / 4
        )
    }
    
    fun isComplete(): Boolean = currentStageIndex >= minigame.stages.size
    
    fun getProgress(): MinigameProgress = MinigameProgress(
        currentStage = currentStageIndex + 1,
        totalStages = minigame.stages.size,
        attemptsInCurrentStage = currentAttempt,
        errorCount = errorHistory.size,
        hintsUsed = hintsShown.size
    )
}

// Data classes para el motor
@Serializable
data class UserSolution(
    val orderedElements: List<String>? = null,
    val pairs: Map<String, String>? = null,
    val value: String? = null,
    val metadata: Map<String, String>? = null
)

@Serializable
data class ValidationResult(
    val isCorrect: Boolean,
    val errors: List<ValidationError>,
    val partialCredit: Int
)

@Serializable
data class UserMinigameProgress(
    val userId: String,
    val completedMinigames: Map<String, MinigameCompletion>,
    val skillLevels: Map<String, Int> // conceptId → nivel de dominio (0-100)
)

@Serializable
data class MinigameCompletion(
    val minigameId: String,
    val completedAt: Long,
    val finalScore: Int,
    val xpEarned: Long,
    val perfectStages: Int,
    val totalStages: Int
)

@Serializable
data class MinigameProgress(
    val currentStage: Int,
    val totalStages: Int,
    val attemptsInCurrentStage: Int,
    val errorCount: Int,
    val hintsUsed: Int
)

sealed class AttemptResult {
    @Serializable
    data class Success(
        val score: Int,
        val xpEarned: Long,
        val timeBonus: Long,
        val nextStageAvailable: Boolean,
        val perfectRun: Boolean
    ) : AttemptResult()
    
    @Serializable
    data class Failure(
        val errorType: ErrorType,
        val message: String,
        val details: List<String>,
        val hint: AdaptiveHint?,
        val remainingAttempts: Int,
        val partialCredit: Int
    ) : AttemptResult()
    
    @Serializable
    data class Skipped(
        val message: String,
        val xpPenalty: Long
    ) : AttemptResult()
}
