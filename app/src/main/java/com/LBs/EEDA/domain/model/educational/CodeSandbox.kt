package com.LBs.EEDA.domain.model.educational

import kotlinx.serialization.Serializable

/**
 * Sandbox de Código Interactivo para EEDA.
 * Permite a los usuarios experimentar con código en un entorno seguro
 * y progresar desbloqueando niveles mediante la solución correcta.
 */
@Serializable
data class CodeSandbox(
    val id: String,
    val title: String,
    val conceptId: String,
    val ageBracket: AgeBracket,
    val difficulty: ContentDifficulty,
    val language: SandboxLanguage,
    val description: String,
    val initialCode: String,
    val instructions: String,
    val objectives: List<CodeObjective>,
    val testCases: List<TestCase>,
    val hints: List<CodeHint>,
    val xpReward: Long,
    val unlocksNextLevel: Boolean = true,
    val sandboxConfig: SandboxConfig
)

enum class SandboxLanguage {
    PYTHON,          // Sintaxis amigable para principiantes
    KOTLIN_SIMPLE,   // Kotlin simplificado
    BLOCK_CODE,      // Bloques visuales (Scratch-like)
    PSEUDOCODE       // Pseudocódigo sin sintaxis estricta
}

@Serializable
data class CodeObjective(
    val id: String,
    val description: String,
    val validationType: ValidationType,
    val expectedOutput: String? = null,
    val expectedPattern: String? = null, // Regex para validar código
    val requiredElements: List<String>? = null // Elementos que debe contener el código
)

enum class ValidationType {
    OUTPUT_MATCH,       // La salida debe coincidir exactamente
    OUTPUT_CONTAINS,    // La salida debe contener cierto texto
    CODE_STRUCTURE,     // Validar estructura del código (if, for, etc.)
    CODE_PATTERN,       // Validar con regex
    EXECUTION_SUCCESS,  // Solo debe ejecutarse sin errores
    CUSTOM_LOGIC        // Lógica de validación personalizada
}

@Serializable
data class TestCase(
    val id: String,
    val input: String,
    val expectedOutput: String,
    val isHidden: Boolean = false,
    val description: String? = null
)

@Serializable
data class CodeHint(
    val id: String,
    val triggerCondition: HintTrigger,
    val level: HintLevel,
    val message: String,
    val codeSnippet: String? = null,
    val lineHighlight: Int? = null
)

@Serializable
data class HintTrigger(
    val errorType: CodeErrorType? = null,
    val attemptNumber: Int? = null,
    val timeSpent: Int? = null, // segundos
    val outputContains: String? = null,
    val customCondition: String? = null
)

enum class CodeErrorType {
    SYNTAX_ERROR,
    RUNTIME_ERROR,
    LOGIC_ERROR,
    TIMEOUT,
    INFINITE_LOOP,
    WRONG_OUTPUT,
    MISSING_CODE,
    WRONG_ORDER,
    MISSING_ELEMENT,
    INCORRECT_VALUE,
    TOO_MANY_ATTEMPTS,
    CONCEPTUAL_MISUNDERSTANDING
}

@Serializable
data class SandboxConfig(
    val maxExecutionTimeMs: Int = 5000,
    val maxMemoryMb: Int = 64,
    val allowImports: List<String> = emptyList(),
    val forbiddenOperations: List<String> = listOf("file", "network", "exec"),
    val autoCompleteEnabled: Boolean = true,
    val syntaxHighlighting: Boolean = true,
    val lineNumbers: Boolean = true
)

/**
 * Motor de ejecución de código sandbox.
 * Maneja la ejecución segura y la validación de resultados.
 */
class SandboxEngine(private val sandbox: CodeSandbox) {
    
    private val executionHistory = mutableListOf<ExecutionRecord>()
    private var currentAttempt = 0
    
    fun execute(code: String): ExecutionResult {
        currentAttempt++
        val startTime = System.currentTimeMillis()
        
        // Simulación de ejecución segura
        // En implementación real, esto usaría un contenedor aislado
        return try {
            // Validar sintaxis básica
            val syntaxValidation = validateSyntax(code, sandbox.language)
            if (!syntaxValidation.isValid) {
                return ExecutionResult.Error(
                    errorType = CodeErrorType.SYNTAX_ERROR,
                    message = syntaxValidation.errorMessage ?: "Error de sintaxis",
                    lineNumber = syntaxValidation.lineNumber,
                    hint = findHint(CodeErrorType.SYNTAX_ERROR, code)
                )
            }
            
            // Validar código malicioso
            if (containsForbiddenOperations(code)) {
                return ExecutionResult.Error(
                    errorType = CodeErrorType.RUNTIME_ERROR,
                    message = "Operaciones no permitidas detectadas",
                    lineNumber = null,
                    hint = null
                )
            }
            
            // Ejecutar y capturar resultado
            val output = simulateExecution(code)
            val executionTime = System.currentTimeMillis() - startTime
            
            // Validar contra test cases
            val validation = validateOutput(output, code)
            
            val record = ExecutionRecord(
                timestamp = startTime,
                code = code,
                output = output,
                executionTimeMs = executionTime,
                success = validation.isValid,
                errors = validation.errors
            )
            executionHistory.add(record)
            
            if (validation.isValid) {
                ExecutionResult.Success(
                    output = output,
                    executionTimeMs = executionTime,
                    allObjectivesMet = validation.objectivesMet,
                    xpEarned = calculateXp(validation),
                    nextLevelUnlocked = sandbox.unlocksNextLevel
                )
            } else {
                ExecutionResult.Error(
                    errorType = validation.errors.firstOrNull()?.type ?: CodeErrorType.LOGIC_ERROR,
                    message = validation.errors.firstOrNull()?.message ?: "Error desconocido",
                    lineNumber = findErrorLine(code, validation.errors),
                    hint = findHintForError(validation.errors.firstOrNull(), code)
                )
            }
            
        } catch (e: Exception) {
            ExecutionResult.Error(
                errorType = CodeErrorType.RUNTIME_ERROR,
                message = e.message ?: "Error de ejecución",
                lineNumber = null,
                hint = findHint(CodeErrorType.RUNTIME_ERROR, code)
            )
        }
    }
    
    private fun validateSyntax(code: String, language: SandboxLanguage): SyntaxValidation {
        return when (language) {
            SandboxLanguage.PYTHON -> validatePythonSyntax(code)
            SandboxLanguage.KOTLIN_SIMPLE -> validateKotlinSyntax(code)
            SandboxLanguage.BLOCK_CODE -> SyntaxValidation(true, null, null) // Siempre válido
            SandboxLanguage.PSEUDOCODE -> SyntaxValidation(true, null, null) // Siempre válido
        }
    }
    
    private fun validatePythonSyntax(code: String): SyntaxValidation {
        // Validaciones básicas de Python
        val errors = mutableListOf<String>()
        
        // Verificar indentación consistente
        val lines = code.lines()
        var previousIndent = 0
        lines.forEachIndexed { index, line ->
            if (line.isNotBlank()) {
                val currentIndent = line.takeWhile { it == ' ' }.length
                if (currentIndent % 4 != 0 && currentIndent > 0) {
                    errors.add("Línea ${index + 1}: Indentación debe ser múltiplo de 4 espacios")
                }
                if (currentIndent > previousIndent && currentIndent != previousIndent + 4) {
                    errors.add("Línea ${index + 1}: Indentación inconsistente")
                }
                previousIndent = currentIndent
            }
        }
        
        // Verificar paréntesis balanceados
        val openParens = code.count { it == '(' }
        val closeParens = code.count { it == ')' }
        if (openParens != closeParens) {
            errors.add("Paréntesis desbalanceados: $openParens abiertos, $closeParens cerrados")
        }
        
        return if (errors.isEmpty()) {
            SyntaxValidation(true, null, null)
        } else {
            SyntaxValidation(false, errors.first(), 1)
        }
    }
    
    private fun validateKotlinSyntax(code: String): SyntaxValidation {
        // Validaciones básicas de Kotlin simplificado
        val errors = mutableListOf<String>()
        
        // Verificar llaves balanceadas
        val openBraces = code.count { it == '{' }
        val closeBraces = code.count { it == '}' }
        if (openBraces != closeBraces) {
            errors.add("Llaves desbalanceadas: $openBraces abiertas, $closeBraces cerradas")
        }
        
        // Verificar paréntesis
        val openParens = code.count { it == '(' }
        val closeParens = code.count { it == ')' }
        if (openParens != closeParens) {
            errors.add("Paréntesis desbalanceados")
        }
        
        return if (errors.isEmpty()) {
            SyntaxValidation(true, null, null)
        } else {
            SyntaxValidation(false, errors.first(), 1)
        }
    }
    
    private fun containsForbiddenOperations(code: String): Boolean {
        val forbiddenPatterns = listOf(
            "import os", "import sys", "subprocess", "exec(", "eval(",
            "open(", "read(", "write(", "socket", "http", "urllib",
            "file://", "ftp://", "https://", "kotlin.io.File",
            "Runtime.getRuntime", "ProcessBuilder"
        )
        return forbiddenPatterns.any { code.contains(it, ignoreCase = true) }
    }
    
    private fun simulateExecution(code: String): String {
        // Simulación de ejecución - en producción usaría un intérprete real
        return when (sandbox.language) {
            SandboxLanguage.PYTHON -> simulatePython(code)
            SandboxLanguage.KOTLIN_SIMPLE -> simulateKotlin(code)
            SandboxLanguage.BLOCK_CODE -> "Bloques ejecutados correctamente"
            SandboxLanguage.PSEUDOCODE -> simulatePseudocode(code)
        }
    }
    
    private fun simulatePython(code: String): String {
        // Simulación simple de ejecución Python
        val output = StringBuilder()
        
        // Procesar print statements
        val printRegex = """print\s*\(\s*["']?(.*?)["']?\s*\)""".toRegex()
        printRegex.findAll(code).forEach { match ->
            output.appendLine(match.groupValues[1])
        }
        
        // Simular operaciones matemáticas
        val mathRegex = """(\d+)\s*([+\-*/])\s*(\d+)""".toRegex()
        mathRegex.findAll(code).forEach { match ->
            val num1 = match.groupValues[1].toInt()
            val op = match.groupValues[2]
            val num2 = match.groupValues[3].toInt()
            val result = when (op) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> if (num2 != 0) num1 / num2 else "Error: División por cero"
                else -> "Error"
            }
            if (!code.contains("print")) {
                output.appendLine(result)
            }
        }
        
        return output.toString().trim()
    }
    
    private fun simulateKotlin(code: String): String {
        // Simulación simple de ejecución Kotlin
        val output = StringBuilder()
        
        // Procesar println
        val printRegex = """println\s*\(\s*["']?(.*?)["']?\s*\)""".toRegex()
        printRegex.findAll(code).forEach { match ->
            output.appendLine(match.groupValues[1])
        }
        
        return output.toString().trim()
    }
    
    private fun simulatePseudocode(code: String): String {
        // Simulación de pseudocódigo
        return "Algoritmo ejecutado correctamente"
    }
    
    private fun validateOutput(output: String, code: String): ValidationDetails {
        val errors = mutableListOf<ValidationError>()
        val objectivesMet = mutableListOf<String>()
        
        sandbox.testCases.forEach { testCase ->
            if (!testCase.isHidden) {
                if (!output.contains(testCase.expectedOutput)) {
                    errors.add(ValidationError(
                        type = CodeErrorType.WRONG_OUTPUT,
                        message = "Salida no coincide con caso de prueba: ${testCase.description ?: testCase.id}",
                        lineNumber = null,
                        details = emptyList()
                    ))
                } else {
                    objectivesMet.add(testCase.id)
                }
            }
        }
        
        // Validar objetivos adicionales
        sandbox.objectives.forEach { objective ->
            val isMet = when (objective.validationType) {
                ValidationType.OUTPUT_MATCH -> output == objective.expectedOutput
                ValidationType.OUTPUT_CONTAINS -> objective.expectedOutput?.let { output.contains(it) } == true
                ValidationType.CODE_STRUCTURE -> validateCodeStructure(code, objective.requiredElements)
                ValidationType.CODE_PATTERN -> objective.expectedPattern?.let { 
                    code.matches(it.toRegex()) 
                } == true
                ValidationType.EXECUTION_SUCCESS -> true // Ya pasó la ejecución
                ValidationType.CUSTOM_LOGIC -> true // Simplificado
            }
            
            if (isMet) {
                objectivesMet.add(objective.id)
            } else {
                errors.add(ValidationError(
                    type = CodeErrorType.LOGIC_ERROR,
                    message = "Objetivo no cumplido: ${objective.description}",
                    lineNumber = null,
                    details = emptyList()
                ))
            }
        }
        
        return ValidationDetails(
            isValid = errors.isEmpty() && objectivesMet.isNotEmpty(),
            errors = errors,
            objectivesMet = objectivesMet
        )
    }
    
    private fun validateCodeStructure(code: String, requiredElements: List<String>?): Boolean {
        if (requiredElements == null) return true
        return requiredElements.all { element ->
            when (element.lowercase()) {
                "if", "if-statement", "if statement" -> code.contains("if", ignoreCase = true)
                "else" -> code.contains("else", ignoreCase = true)
                "for", "for-loop", "for loop" -> code.contains("for", ignoreCase = true)
                "while", "while-loop", "while loop" -> code.contains("while", ignoreCase = true)
                "function", "def", "fun" -> code.contains(Regex("""\b(def|fun|function)\b"""))
                "variable", "assignment" -> code.contains("=")
                "return" -> code.contains("return", ignoreCase = true)
                else -> code.contains(element, ignoreCase = true)
            }
        }
    }
    
    private fun findHint(errorType: CodeErrorType, code: String): CodeHint? {
        return sandbox.hints.firstOrNull { hint ->
            hint.triggerCondition.errorType == errorType
        }
    }
    
    private fun findHintForError(error: ValidationError?, code: String): CodeHint? {
        if (error == null) return null
        
        // Buscar hint por intento actual
        val attemptHint = sandbox.hints.firstOrNull { 
            it.triggerCondition.attemptNumber == currentAttempt 
        }
        if (attemptHint != null) return attemptHint
        
        // Buscar hint por tipo de error
        return sandbox.hints.firstOrNull { 
            it.triggerCondition.errorType == error.type 
        }
    }
    
    private fun findErrorLine(code: String, errors: List<ValidationError>): Int? {
        // Heurística simple para encontrar línea de error
        return errors.firstOrNull()?.lineNumber
    }
    
    private fun calculateXp(validation: ValidationDetails): Long {
        val baseXp = sandbox.xpReward
        val attemptPenalty = (currentAttempt - 1) * (baseXp * 0.1).toLong()
        val timeBonus = 0L // Calcular según tiempo
        
        return (baseXp - attemptPenalty + timeBonus).coerceAtLeast(baseXp / 2)
    }
    
    fun getExecutionHistory(): List<ExecutionRecord> = executionHistory.toList()
    
    fun getHintForCurrentAttempt(): CodeHint? {
        return sandbox.hints.firstOrNull { 
            it.triggerCondition.attemptNumber == currentAttempt 
        }
    }
}

// Data classes de soporte
@Serializable
data class SyntaxValidation(
    val isValid: Boolean,
    val errorMessage: String?,
    val lineNumber: Int?
)

@Serializable
data class ExecutionRecord(
    val timestamp: Long,
    val code: String,
    val output: String,
    val executionTimeMs: Long,
    val success: Boolean,
    val errors: List<ValidationError> = emptyList()
)

@Serializable
data class ValidationDetails(
    val isValid: Boolean,
    val errors: List<ValidationError>,
    val objectivesMet: List<String>
)


sealed class ExecutionResult {
    @Serializable
    data class Success(
        val output: String,
        val executionTimeMs: Long,
        val allObjectivesMet: List<String>,
        val xpEarned: Long,
        val nextLevelUnlocked: Boolean
    ) : ExecutionResult()
    
    @Serializable
    data class Error(
        val errorType: CodeErrorType,
        val message: String,
        val lineNumber: Int?,
        val hint: CodeHint?
    ) : ExecutionResult()
}

@Serializable
data class ValidationError(
    val type: CodeErrorType,
    val message: String,
    val lineNumber: Int? = null,
    val details: List<String> = emptyList()
)
