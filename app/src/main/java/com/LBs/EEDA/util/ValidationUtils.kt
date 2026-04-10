package com.LBs.EEDA.util

import java.util.regex.Pattern

/**
 * Utilidades de validación para E.E.D.A
 */
object ValidationUtils {

    // Patrones de validación
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
    )

    private val PIN_PATTERN = Pattern.compile("^\\d{4,6}$")

    /**
     * Valida un nombre de usuario
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "El nombre no puede estar vacío")
            name.length < 2 -> ValidationResult(false, "El nombre debe tener al menos 2 caracteres")
            name.length > 50 -> ValidationResult(false, "El nombre no puede exceder 50 caracteres")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) ->
                ValidationResult(false, "El nombre solo puede contener letras y espacios")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Valida una edad
     */
    fun validateAge(age: Int): ValidationResult {
        return when {
            age < 3 -> ValidationResult(false, "La edad mínima es 3 años")
            age > 100 -> ValidationResult(false, "La edad máxima es 100 años")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Valida un email
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "El email no puede estar vacío")
            !EMAIL_PATTERN.matcher(email).matches() ->
                ValidationResult(false, "Formato de email inválido")
            email.length > 254 -> ValidationResult(false, "Email demasiado largo")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Valida un PIN parental
     */
    fun validatePin(pin: String): ValidationResult {
        return when {
            pin.isBlank() -> ValidationResult(false, "El PIN no puede estar vacío")
            !PIN_PATTERN.matcher(pin).matches() ->
                ValidationResult(false, "El PIN debe tener entre 4 y 6 dígitos")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Valida una contraseña segura
     */
    fun validatePassword(password: String): ValidationResult {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        return when {
            password.length < 8 ->
                ValidationResult(false, "La contraseña debe tener al menos 8 caracteres")
            !hasUppercase ->
                ValidationResult(false, "Debe contener al menos una mayúscula")
            !hasLowercase ->
                ValidationResult(false, "Debe contener al menos una minúscula")
            !hasDigit ->
                ValidationResult(false, "Debe contener al menos un número")
            !hasSpecial ->
                ValidationResult(false, "Debe contener al menos un carácter especial")
            else -> ValidationResult(true, null)
        }
    }

    /**
     * Valida código Python básico
     */
    fun validatePythonCode(code: String): CodeValidationResult {
        val errors = mutableListOf<String>()

        // Verificar indentación
        val lines = code.lines()
        lines.forEachIndexed { index, line ->
            if (line.isNotBlank()) {
                val spaces = line.takeWhile { it == ' ' }.length
                if (spaces % 4 != 0 && spaces > 0) {
                    errors.add("Línea ${index + 1}: La indentación debe ser múltiplo de 4 espacios")
                }
            }
        }

        // Verificar paréntesis balanceados
        val openParens = code.count { it == '(' }
        val closeParens = code.count { it == ')' }
        if (openParens != closeParens) {
            errors.add("Paréntesis desbalanceados")
        }

        // Verificar llaves (para diccionarios)
        val openBraces = code.count { it == '{' }
        val closeBraces = code.count { it == '}' }
        if (openBraces != closeBraces) {
            errors.add("Llaves desbalanceadas")
        }

        // Verificar corchetes
        val openBrackets = code.count { it == '[' }
        val closeBrackets = code.count { it == ']' }
        if (openBrackets != closeBrackets) {
            errors.add("Corchetes desbalanceados")
        }

        // Palabras prohibidas
        val forbidden = listOf("exec", "eval", "__import__", "open", "file")
        forbidden.forEach { word ->
            if (code.contains(word)) {
                errors.add("Uso no permitido de: $word")
            }
        }

        return CodeValidationResult(errors.isEmpty(), errors)
    }

    /**
     * Calcula la fortaleza de una contraseña (0-100)
     */
    fun calculatePasswordStrength(password: String): Int {
        var score = 0

        // Longitud
        score += (password.length * 4).coerceAtMost(40)

        // Variedad de caracteres
        if (password.any { it.isUpperCase() }) score += 10
        if (password.any { it.isLowerCase() }) score += 10
        if (password.any { it.isDigit() }) score += 10
        if (password.any { !it.isLetterOrDigit() }) score += 15

        // Patrones comunes débiles
        val commonPatterns = listOf("123", "abc", "password", "qwerty", "111")
        if (commonPatterns.any { password.lowercase().contains(it) }) {
            score -= 20
        }

        return score.coerceIn(0, 100)
    }

    /**
     * Valida respuesta de evaluación
     */
    fun validateAssessmentAnswer(answer: String, expectedAnswer: String, type: AnswerType): Boolean {
        return when (type) {
            AnswerType.EXACT -> answer.trim().equals(expectedAnswer.trim(), ignoreCase = true)
            AnswerType.CONTAINS -> answer.trim().contains(expectedAnswer.trim(), ignoreCase = true)
            AnswerType.NUMERIC -> {
                val answerNum = answer.trim().toFloatOrNull()
                val expectedNum = expectedAnswer.trim().toFloatOrNull()
                answerNum != null && expectedNum != null &&
                        Math.abs(answerNum - expectedNum) < 0.01
            }
            AnswerType.CODE -> validatePythonCode(answer).isValid
        }
    }

    /**
     * Sanitiza entrada de usuario
     */
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace(Regex("<script[^>]*>.*?</script>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<[^>]+>"), "")
            .replace(Regex("javascript:", RegexOption.IGNORE_CASE), "")
            .replace(Regex("on\\w+\\s*=", RegexOption.IGNORE_CASE), "")
    }

    /**
     * Trunca texto a longitud máxima
     */
    fun truncateText(text: String, maxLength: Int, ellipsis: String = "..."): String {
        return if (text.length > maxLength) {
            text.take(maxLength - ellipsis.length) + ellipsis
        } else {
            text
        }
    }

    /**
     * Formatea número con separadores de miles
     */
    fun formatNumber(number: Long): String {
        return String.format("%,d", number)
    }

    /**
     * Formatea número abreviado (K, M, B)
     */
    fun formatCompactNumber(number: Long): String {
        return when {
            number >= 1_000_000_000 -> String.format("%.1fB", number / 1_000_000_000.0)
            number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
            number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
            else -> number.toString()
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)

data class CodeValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

enum class AnswerType {
    EXACT,      // Coincidencia exacta
    CONTAINS,   // Contiene texto esperado
    NUMERIC,    // Valor numérico con tolerancia
    CODE        // Código válido
}
