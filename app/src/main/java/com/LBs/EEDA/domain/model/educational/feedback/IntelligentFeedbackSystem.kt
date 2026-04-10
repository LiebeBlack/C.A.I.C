package com.LBs.EEDA.domain.model.educational.feedback

import com.LBs.EEDA.domain.model.educational.*

/**
 * Sistema de Feedback Inteligente EEDA 2026.
 * No dice "Error"; explica el error y da sugerencias para que el niño lo haga bien.
 */
object IntelligentFeedbackSystem {
    
    /**
     * Genera feedback personalizado basado en el error específico del usuario.
     */
    fun generateFeedback(
        error: ErrorType,
        userInput: String,
        expectedSolution: Solution,
        conceptId: String,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        return when (error) {
            ErrorType.WRONG_ORDER -> generateWrongOrderFeedback(userInput, expectedSolution, age, attemptCount)
            ErrorType.MISSING_ELEMENT -> generateMissingElementFeedback(userInput, expectedSolution, age, attemptCount)
            ErrorType.INCORRECT_VALUE -> generateIncorrectValueFeedback(userInput, expectedSolution, age, attemptCount)
            ErrorType.LOGIC_ERROR -> generateLogicErrorFeedback(userInput, conceptId, age, attemptCount)
            ErrorType.SYNTAX_ERROR -> generateSyntaxErrorFeedback(userInput, conceptId, age, attemptCount)
            ErrorType.CONCEPTUAL_MISUNDERSTANDING -> generateConceptualFeedback(conceptId, age, attemptCount)
            else -> generateGenericFeedback(error, age, attemptCount)
        }
    }
    
    private fun generateWrongOrderFeedback(
        userInput: String,
        expectedSolution: Solution,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val baseMessage = when {
            age <= 7 -> "¡Casi! Los pasos son correctos pero necesitan ir en otro orden. " +
                    "Es como ponerse los zapatos antes de los calcetines."
            age <= 13 -> "Has identificado los elementos correctos, pero el orden es importante. " +
                    "Piensa en la secuencia lógica: ¿qué debe venir primero?"
            else -> "Secuencia identificada correctamente pero ordenada erróneamente. " +
                    "Analiza las dependencias causales entre los elementos."
        }
        
        val suggestion = when (attemptCount) {
            1 -> "Intenta identificar cuál es el primer paso que inicia todo."
            2 -> "Busca el elemento que no depende de ningún otro - ese va primero."
            else -> "Tip: En una receta, primero preparas ingredientes, luego cocinas, luego sirves."
        }
        
        val visualAid = when {
            age <= 7 -> "🔄 Imagina una línea de tiempo con flechas conectando cada paso"
            age <= 13 -> "📊 Diagrama de flujo: entrada → proceso → salida"
            else -> "🧮 Grafo DAG: nodos = pasos, aristas = dependencias"
        }
        
        return IntelligentFeedback(
            message = baseMessage,
            suggestion = suggestion,
            visualAid = visualAid,
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = attemptCount <= 2,
            retryPrompt = "¿Quieres intentarlo de nuevo con esta pista?"
        )
    }
    
    private fun generateMissingElementFeedback(
        userInput: String,
        expectedSolution: Solution,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val missingElements = findMissingElements(userInput, expectedSolution)
        
        val baseMessage = when {
            age <= 7 -> "¡Buen intento! Te falta algo importante. " +
                    "Es como armar un rompecabezas con piezas faltantes."
            age <= 13 -> "Tu solución está parcialmente correcta pero faltan elementos clave. " +
                    "¿Qué más necesitas para completar el objetivo?"
            else -> "Solución incompleta: elementos críticos ausentes. " +
                    "Revisa los requisitos del problema."
        }
        
        val suggestion = when {
            attemptCount == 1 && missingElements.isNotEmpty() -> 
                "Pista: Falta ${if (missingElements.size == 1) "un elemento" else "${missingElements.size} elementos"} por incluir."
            attemptCount == 2 -> "Revisa la lista de requisitos. ¿Cada uno está cubierto?"
            else -> "En programación, como en cocina: cada ingrediente tiene un propósito."
        }
        
        return IntelligentFeedback(
            message = baseMessage,
            suggestion = suggestion,
            visualAid = if (age <= 7) "🔍 Usa la lupa para buscar lo que falta" else "📝 Checklist de requisitos",
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = true,
            retryPrompt = "Inténtalo nuevamente con esta ayuda"
        )
    }
    
    private fun generateIncorrectValueFeedback(
        userInput: String,
        expectedSolution: Solution,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val baseMessage = when {
            age <= 7 -> "¡Estás cerca! El valor que pusiste no es el correcto. " +
                    "Es como cuando buscas el juguete equivocado."
            age <= 13 -> "Tu razonamiento va por buen camino, pero el valor específico es incorrecto. " +
                    "Revisa tus cálculos o tu lógica."
            else -> "Valor incorrecto. Verifica: 1) Fórmula aplicada, 2) Datos de entrada, 3) Unidades."
        }
        
        val suggestion = when (attemptCount) {
            1 -> "Revisa si usaste los números correctos del problema."
            2 -> "¿Recuerdas la fórmula? Aplica paso a paso."
            else -> "Tip: Descompón el problema. Calcula cada parte por separado."
        }
        
        return IntelligentFeedback(
            message = baseMessage,
            suggestion = suggestion,
            visualAid = if (age <= 13) "🧮 Revisa tu cálculo paso a paso" else "📐 Verificación dimensional",
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = true,
            retryPrompt = "¿Intentar de nuevo?"
        )
    }
    
    private fun generateLogicErrorFeedback(
        userInput: String,
        conceptId: String,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val baseMessage = when {
            age <= 7 -> "Tu respuesta muestra que estás pensando, pero hay algo en la lógica que no cuadra. " +
                    "Es como intentar encajar una pieza cuadrada en un hueco redondo."
            age <= 13 -> "Hay un error de razonamiento en tu solución. " +
                    "Analiza cada paso: ¿sigue la lógica del problema?"
            else -> "Fallo lógico detectado. Tu razonamiento contiene una falacia o premisa incorrecta."
        }
        
        val suggestion = when (conceptId) {
            "password_security" -> when (attemptCount) {
                1 -> "Una contraseña fuerte necesita variedad: mayúsculas, minúsculas, números, símbolos."
                2 -> "Evita palabras comunes o información personal obvia."
                else -> "La entropía es clave: cuanto más impredecible, mejor."
            }
            "encryption" -> when (attemptCount) {
                1 -> "Recuerda el desplazamiento. Si el cifrado es +3, el descifrado es -3."
                2 -> "Módulo 26: después de Z viene A."
                else -> "Aplica la función inversa del algoritmo de cifrado."
            }
            "algorithm" -> when (attemptCount) {
                1 -> "Los algoritmos son pasos ordenados. ¿Cada paso lleva al siguiente?"
                2 -> "Verifica condiciones de borde y casos especiales."
                else -> "Invariantes de bucle: ¿qué se mantiene verdadero en cada iteración?"
            }
            else -> when (attemptCount) {
                1 -> "Relee el problema cuidadosamente. ¿Entendiste todo lo que se pide?"
                2 -> "Divide el problema en partes más pequeñas y verifica cada una."
                else -> "A veces explicar el problema a alguien más ayuda a ver el error."
            }
        }
        
        return IntelligentFeedback(
            message = baseMessage,
            suggestion = suggestion,
            visualAid = if (age <= 13) "🤔 Diagrama de lógica paso a paso" else "🧩 Árbol de decisión",
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = attemptCount <= 3,
            retryPrompt = "¡Tú puedes! Intenta de nuevo"
        )
    }
    
    private fun generateSyntaxErrorFeedback(
        userInput: String,
        conceptId: String,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val baseMessage = when {
            age <= 7 -> "Tu idea es buena, pero hay un error en cómo lo escribiste. " +
                    "Es como escribir una palabra con letras mezcladas."
            age <= 13 -> "Error de sintaxis detectado. Las computadoras son muy literales: " +
                    "cada paréntesis, coma y punto importa."
            else -> "Error sintáctico. Verifica: balance de delimitadores, keywords reservadas, " +
                    "y estructura gramatical del lenguaje."
        }
        
        val suggestion = when (attemptCount) {
            1 -> "Revisa que todos los paréntesis tengan su pareja de cierre."
            2 -> "Verifica palabras reservadas: ¿escribiste correctamente cada comando?"
            else -> "Tip: La indentación ayuda a ver la estructura. ¿Todo está alineado correctamente?"
        }
        
        return IntelligentFeedback(
            message = baseMessage,
            suggestion = suggestion,
            visualAid = "🎯 Resaltado de sintaxis con error marcado",
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = true,
            retryPrompt = "Corrige y vuelve a intentar"
        )
    }
    
    private fun generateConceptualFeedback(
        conceptId: String,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        val conceptExplanation = when (conceptId) {
            "password_security" -> when {
                age <= 7 -> "Una contraseña es como una llave mágica. Debe ser única y solo tú debes conocerla."
                age <= 13 -> "La seguridad de contraseñas se basa en entropía: impredecibilidad. " +
                        "Mezcla tipos de caracteres para aumentarla."
                else -> "La robustez de contraseñas se mide en bits de entropía. " +
                        "Longitud > complejidad. Usa gestores de contraseñas."
            }
            "encryption" -> when {
                age <= 7 -> "El cifrado es como un idioma secreto que solo tú y tu amigo entienden."
                age <= 13 -> "El cifrado transforma mensajes usando algoritmos y claves. " +
                        "Sin la clave, el mensaje parece basura."
                else -> "Criptografía simétrica vs asimétrica. Cifrado perfecto forward secrecy."
            }
            "algorithm" -> when {
                age <= 7 -> "Un algoritmo es una receta: pasos en orden para hacer algo."
                age <= 13 -> "Los algoritmos resuelven problemas sistemáticamente. " +
                        "Tienen entrada, procesamiento y salida."
                else -> "Algoritmos: complejidad temporal y espacial. Optimización vs legibilidad."
            }
            else -> "Vamos a revisar juntos el concepto fundamental de este tema."
        }
        
        return IntelligentFeedback(
            message = "Parece que hay un malentendido del concepto. $conceptExplanation",
            suggestion = "Prueba con un ejemplo más simple primero, luego aplica aquí.",
            visualAid = "📚 Mini-lección del concepto con animación",
            hintLevel = HintLevel.EXPLANATORY,
            encouraging = true,
            retryPrompt = "¿Quieres intentarlo ahora con esta explicación?"
        )
    }
    
    private fun generateGenericFeedback(
        error: ErrorType,
        age: Int,
        attemptCount: Int
    ): IntelligentFeedback {
        return IntelligentFeedback(
            message = when {
                age <= 7 -> "Eso no es lo que buscábamos, pero ¡sigue intentando!"
                age <= 13 -> "Respuesta incorrecta. Analiza el problema y prueba otra estrategia."
                else -> "Incorrecto. Reevalúa tu aproximación al problema."
            },
            suggestion = when (attemptCount) {
                1 -> "Lee el problema nuevamente con calma."
                2 -> "Intenta explicar el problema en voz alta."
                else -> "A veces un descanso de 5 minutos ayuda a ver claro."
            },
            visualAid = "💡 Pista general",
            hintLevel = calculateHintLevel(attemptCount),
            encouraging = true,
            retryPrompt = "¿Intentar de nuevo?"
        )
    }
    
    // ==================== UTILIDADES ====================
    
    private fun findMissingElements(userInput: String, expectedSolution: Solution): List<String> {
        val userElements = parseElements(userInput)
        val expectedElements = expectedSolution.orderedElements ?: emptyList()
        return expectedElements.filter { it !in userElements }
    }
    
    private fun parseElements(input: String): List<String> {
        // Simplificación: asume formato JSON o lista separada por comas
        return input.split(",", "[", "]", "\"").filter { it.isNotBlank() }
    }
    
    private fun calculateHintLevel(attemptCount: Int): HintLevel {
        return when (attemptCount) {
            1 -> HintLevel.SUBTLE
            2 -> HintLevel.DIRECT
            3 -> HintLevel.EXPLANATORY
            else -> HintLevel.SOLUTION
        }
    }
    
    /**
     * Genera retroalimentación positiva para respuestas correctas.
     */
    fun generatePositiveFeedback(
        perfectRun: Boolean,
        timeBonus: Boolean,
        age: Int
    ): PositiveFeedback {
        val baseMessage = when {
            perfectRun && timeBonus -> when {
                age <= 7 -> "¡¡¡SUPERESTRELLA!!! 🌟🌟🌟 ¡Perfecto y rápido!"
                age <= 13 -> "¡Ejecución impecable! Precisión + velocidad = maestría."
                else -> "Rendimiento óptimo. Eficiencia algorítmica demostrada."
            }
            perfectRun -> when {
                age <= 7 -> "¡PERFECTO! 🌟¡Lo hiciste sin errores!"
                age <= 13 -> "¡Solución perfecta! Cada paso correcto."
                else -> "Solución óptima verificada."
            }
            timeBonus -> when {
                age <= 7 -> "¡Rápido como un rayo! ⚡"
                age <= 13 -> "¡Excelente velocidad de resolución!"
                else -> "Eficiente temporalmente."
            }
            else -> when {
                age <= 7 -> "¡Muy bien! 🎉 ¡Lo lograste!"
                age <= 13 -> "¡Correcto! Buen trabajo."
                else -> "Solución válida."
            }
        }
        
        val funFact = when {
            age <= 7 -> "¿Sabías? ¡Los delfines también aprenden jugando!"
            age <= 13 -> "Curiosidad: Este concepto se usa en ${listOf("videojuegos", "robots", "aplicaciones").random()}."
            else -> "Aplicación real: Este principio se utiliza en sistemas de producción."
        }
        
        return PositiveFeedback(
            message = baseMessage,
            funFact = funFact,
            xpGained = if (perfectRun) 100 else if (timeBonus) 75 else 50,
            unlockMessage = if (perfectRun) "¡Desbloqueaste un nuevo desafío!" else null
        )
    }
}

/**
 * Modelo de datos para feedback inteligente.
 */
data class IntelligentFeedback(
    val message: String,
    val suggestion: String,
    val visualAid: String?,
    val hintLevel: HintLevel,
    val encouraging: Boolean,
    val retryPrompt: String
)

data class PositiveFeedback(
    val message: String,
    val funFact: String,
    val xpGained: Int,
    val unlockMessage: String?
)

/**
 * Extensiones para mostrar feedback en UI.
 */
fun IntelligentFeedback.getEncouragingEmoji(): String {
    return when {
        encouraging && hintLevel == HintLevel.SUBTLE -> "💪"
        encouraging && hintLevel == HintLevel.DIRECT -> "🤔"
        encouraging && hintLevel == HintLevel.EXPLANATORY -> "💡"
        encouraging -> "⭐"
        else -> "📝"
    }
}

fun PositiveFeedback.getCelebrationEmoji(age: Int): String {
    return when {
        age <= 7 -> listOf("🎉", "🌟", "🏆", "🎊", "🥳").random()
        age <= 13 -> listOf("🎯", "🚀", "⚡", "🔥", "✨").random()
        else -> listOf("✓", "◆", "▲", "●").random()
    }
}
