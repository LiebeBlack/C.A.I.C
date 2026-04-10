package com.LBs.EEDA.domain.model.educational.modules

import com.LBs.EEDA.domain.model.educational.*

/**
 * Banco Maestro de Preguntas de Alta Complejidad E.E.D.A. 2026.
 * Incluye niveles: Principiante, Intermedio y EXPERTO (Preguntas Duras).
 */
object EedaQuestionBank {
    
    data class QiuzQuestion(
        val id: String,
        val text: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        val explanation: String,
        val difficulty: ContentDifficulty = ContentDifficulty.INTERMEDIATE
    )

    private val questionStorage = mutableMapOf<String, List<QiuzQuestion>>()

    init {
        generateExpertScience()
        generateExpertTech()
        generateExpertFinance()
        generateStrategyLogic()
        // Generación masiva (simulada por espacio, pero estructurada funcionalmente)
        generateMassiveCatalog()
    }

    fun getQuestionsForTopic(topicId: String): List<QiuzQuestion> {
        return questionStorage[topicId] ?: listOf(
            QiuzQuestion("q_gen", "¿Cuál es la base de este sistema?", listOf("Lógica", "Suerte", "Caos", "Paciencia"), 0, "Este sistema se basa en lógica pura.")
        )
    }

    private fun generateExpertScience() {
        questionStorage["sc_quantum"] = listOf(
            QiuzQuestion("ex_sc_1", "¿Qué fenómeno describe que una partícula puede estar en dos estados simultáneamente hasta ser observada?", listOf("Entrelazamiento", "Superposición", "Efecto Túnel", "Decoherencia"), 1, "La superposición cuántica es fundamental en la computación Qubit.", ContentDifficulty.ADVANCED),
            QiuzQuestion("ex_sc_2", "En la relatividad general, ¿qué causa la curvatura del espaciotiempo?", listOf("Luz", "Tiempo", "Energía y Masa", "Velocidad"), 2, "La masa le dice al espaciotiempo cómo curvarse.", ContentDifficulty.ADVANCED)
        )
    }

    private fun generateExpertTech() {
        questionStorage["te_security"] = listOf(
            QiuzQuestion("ex_te_1", "¿Cuál es la principal diferencia entre cifrado simétrico y asimétrico?", listOf("Velocidad", "Número de llaves", "Longitud de bits", "Algoritmo"), 1, "El simétrico usa una llave; el asimétrico usa un par (pública/privada).", ContentDifficulty.ADVANCED),
            QiuzQuestion("ex_te_2", "¿Qué ataque de ciberseguridad busca saturar un servidor con tráfico masivo?", listOf("Phishing", "Man-in-the-Middle", "DDoS", "SQL Injection"), 2, "Distributed Denial of Service inunda el ancho de banda.", ContentDifficulty.EXPERT)
        )
    }

    private fun generateExpertFinance() {
        questionStorage["fi_stock"] = listOf(
            QiuzQuestion("ex_fi_1", "¿Qué mide el indicador 'Beta' en una cartera de inversión?", listOf("Rentabilidad", "Riesgo sistemático", "Gasto", "Pasivos"), 1, "Beta compara la volatilidad de un activo frente al mercado general.", ContentDifficulty.EXPERT)
        )
    }

    private fun generateStrategyLogic() {
        // Preguntas de Estrategia Pura
        questionStorage["strategy_logic"] = listOf(
            QiuzQuestion("st_1", "En teoría de juegos, ¿qué es el Equilibrio de Nash?", listOf("Ganar siempre", "Ningún jugador puede mejorar su estado unilateralmente", "Empatar", "Colapsar el sistema"), 1, "Nash demostró que existe un estado donde nadie tiene incentivo a cambiar su estrategia solo.", ContentDifficulty.EXPERT)
        )
    }

    private fun generateMassiveCatalog() {
        // Expandir hasta llegar a las 400+ preguntas simuladas pero funcionalmente asignables
        val disciplines = listOf("sc", "te", "hi", "fi", "sp", "we", "ar")
        disciplines.forEach { d ->
            for (i in 1..60) {
                val id = "${d}_gen_$i"
                questionStorage[id] = listOf(
                    QiuzQuestion("q_${d}_$i", "Desafío de $d nivel $i", listOf("Opción 1", "Opción 2", "Opción 3", "Opción 4"), (0..3).random(), "Retroalimentación para el desafío maestro $i.")
                )
            }
        }
    }
}
