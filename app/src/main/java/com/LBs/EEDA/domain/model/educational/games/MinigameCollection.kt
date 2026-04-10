package com.LBs.EEDA.domain.model.educational.games

import com.LBs.EEDA.domain.model.educational.*

/**
 * Colección Masiva de Minijuegos Gamificados E.E.D.A. 2026.
 * Incluye mecánicas de RTS, plataformas, lógica y simulación avanzada.
 */
object MinigameCollection {

    /**
     * THE KERNEL WAR: RTS de Gestión de RAM y Algoritmos.
     * El mapa es la memoria RAM organizada en celdas hexadecimales.
     */
    fun createKernelWar(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_kernel_war_001",
            type = MinigameType.KERNEL_WAR_RTS,
            title = "The Kernel War: Memory Conquest",
            description = "Despliega algoritmos tácticos para conquistar sectores de RAM. Mantén el uso de ciclos de CPU bajo control mientras eliminas procesos parásitos.",
            conceptId = "os_fundamentals",
            difficulty = difficulty,
            ageBracket = AgeBracket.ADOLESCENT_EARLY,
            estimatedDurationMinutes = 15,
            xpReward = 500,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Despliega una unidad 'QuickSort' en el sector 0x4F para organizar los datos caóticos.",
                    elements = listOf(
                        GameElement("u1", ElementVisualType.ALGORITHM_UNIT, "QuickSort", "O(n log n)", category = "sorting"),
                        GameElement("u2", ElementVisualType.ALGORITHM_UNIT, "BubbleSort", "O(n²)", category = "sorting"),
                        GameElement("s1", ElementVisualType.RAM_CELL, "Hex 0x00", "Free"),
                        GameElement("s2", ElementVisualType.RAM_CELL, "Hex 0x4F", "Fragmented")
                    ),
                    correctSolution = Solution(
                        correctPairs = mapOf("u1" to "s2")
                    ),
                    hints = listOf(
                        AdaptiveHint(
                            hintId = "hint_kw_1",
                            triggerPattern = ErrorPattern(type = CodeErrorType.LOGIC_ERROR),
                            hintLevel = HintLevel.SUBTLE,
                            message = "Mira el sector con datos fragmentados. QuickSort es más eficiente que BubbleSort para este volumen."
                        )
                    )
                )
            ),
            config = MinigameConfig(sandboxMode = true)
        )
    }

    /**
     * DEBUGGER QUEST: Plataformas de Lógica y Sintaxis.
     */
    fun createDebuggerQuest(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_debugger_quest_001",
            type = MinigameType.DEBUGGER_QUEST,
            title = "Debugger Quest: The Null Void",
            description = "Salta sobre excepciones de puntero nulo y repara el código roto para avanzar por los niveles de la BIOS.",
            conceptId = "debugging_syntax",
            difficulty = difficulty,
            ageBracket = AgeBracket.PRE_ADOLESCENT,
            estimatedDurationMinutes = 10,
            xpReward = 300,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "El puente de datos está roto por un punto y coma faltante. ¡Repáralo!",
                    elements = listOf(
                        GameElement("p1", ElementVisualType.CODE_BLOCK, "print('Hello')", "println"),
                        GameElement("p2", ElementVisualType.SYNTAX_ERROR, ";", "missing_token")
                    ),
                    correctSolution = Solution(orderedElements = listOf("p1", "p2")),
                    hints = listOf()
                )
            ),
            config = MinigameConfig()
        )
    }

    /**
     * BINARY BRIDGE: Rompecabezas Booleano.
     */
    fun createBinaryBridge(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_binary_bridge_001",
            type = MinigameType.BINARY_BRIDGE,
            title = "Binary Bridge Builder",
            description = "Construye conexiones lógicas usando compuertas AND, OR y XOR para que los paquetes de datos lleguen a su destino.",
            conceptId = "boolean_logic",
            difficulty = difficulty,
            ageBracket = AgeBracket.CHILDHOOD,
            estimatedDurationMinutes = 5,
            xpReward = 200,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Envía señal '1' activando ambas entradas de la compuerta AND.",
                    elements = listOf(
                        GameElement("i1", ElementVisualType.BINARY_DIGIT, "Input A", "1"),
                        GameElement("i2", ElementVisualType.BINARY_DIGIT, "Input B", "1"),
                        GameElement("g1", ElementVisualType.LOGIC_GATE, "AND Gate", "logic_and")
                    ),
                    correctSolution = Solution(correctPairs = mapOf("i1" to "g1", "i2" to "g1")),
                    hints = listOf()
                )
            ),
            config = MinigameConfig()
        )
    }

    /**
     * AI LAB: Entrenamiento de Redes Neuronales.
     */
    fun createAILab(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_ai_lab_001",
            type = MinigameType.AI_TRAINING_SIM,
            title = "Neural Lab Explorer",
            description = "Ajusta los pesos de las neuronas para que el modelo aprenda a distinguir entre gatos y datos corruptos.",
            conceptId = "machine_learning",
            difficulty = difficulty,
            ageBracket = AgeBracket.ADOLESCENT_EARLY,
            estimatedDurationMinutes = 12,
            xpReward = 450,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Incrementa el peso del sesgo para reducir el error de aproximación.",
                    elements = emptyList(),
                    correctSolution = Solution(correctValue = "higher_bias"),
                    hints = listOf()
                )
            ),
            config = MinigameConfig(adaptiveDifficulty = true)
        )
    }

    /**
     * MARKET CRASH: Simulación de mercados financieros.
     */
    fun createMarketCrash(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_market_crash_001",
            type = MinigameType.MARKET_SIMULATOR,
            title = "Eco-Market Survival",
            description = "Gestiona inversiones en energías renovables mientras navegas por una crisis de mercado simulada en tiempo real.",
            conceptId = "finance_markets",
            difficulty = difficulty,
            ageBracket = AgeBracket.ADOLESCENT_LATE,
            estimatedDurationMinutes = 20,
            xpReward = 600,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Diversifica tu cartera: Invierte 40% en Eólica y 60% en Solar antes de que suban las tasas.",
                    elements = emptyList(),
                    correctSolution = Solution(correctValue = "diversified"),
                    hints = listOf()
                )
            ),
            config = MinigameConfig(sandboxMode = true)
        )
    }

    /**
     * SPACE COLONY ALPHA: Simulación de Supervivencia en Marte.
     */
    fun createSpaceColony(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_space_colony_001",
            type = MinigameType.SPACE_COLONY_SIM,
            title = "Martian Colony: Life Support",
            description = "Gestiona los niveles de oxigeno, agua y energía mientras enfrentas tormentas solares.",
            conceptId = "space_survival",
            difficulty = difficulty,
            ageBracket = AgeBracket.ADOLESCENT_LATE,
            estimatedDurationMinutes = 25,
            xpReward = 800,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Regula el flujo de CO2 para que las plantas produzcan oxigeno sin saturar los filtros.",
                    elements = listOf(
                        GameElement("e1", ElementVisualType.RESOURCE_CRATE, "Hidrógeno", "H2"),
                        GameElement("e2", ElementVisualType.RESOURCE_CRATE, "Oxígeno", "O2")
                    ),
                    correctSolution = Solution(correctValue = "balance_o2"),
                    hints = listOf()
                )
            ),
            config = MinigameConfig(sandboxMode = true)
        )
    }

    /**
     * QUANTUM REFACTOR: Optimización de Código en tiempo real.
     */
    fun createQuantumRefactor(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "game_refactor_001",
            type = MinigameType.CODE_REFACTOR_CHALLENGE,
            title = "Quantum Refactor: Big-O Optimization",
            description = "Toma un algoritmo O(n²) y conviértelo en O(n log n) usando técnicas de programación dinámica.",
            conceptId = "algo_optimization",
            difficulty = difficulty,
            ageBracket = AgeBracket.YOUNG_ADULT,
            estimatedDurationMinutes = 15,
            xpReward = 1000,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Elimina los bucles anidados innecesarios usando un Hash Map.",
                    elements = listOf(
                        GameElement("c1", ElementVisualType.CODE_BLOCK, "for(i in list)", "loop"),
                        GameElement("c2", ElementVisualType.CODE_BLOCK, "hashMap.put()", "optim")
                    ),
                    correctSolution = Solution(orderedElements = listOf("c2")),
                    hints = listOf()
                )
            ),
            config = MinigameConfig()
        )
    }

    /**
     * Obtiene una lista de todos los juegos disponibles para la edad del usuario.
     */
    fun getGamesForAge(age: Int): List<Minigame> {
        val difficulty = when {
            age < 7 -> ContentDifficulty.ELEMENTARY
            age < 12 -> ContentDifficulty.BEGINNER
            age < 16 -> ContentDifficulty.INTERMEDIATE
            else -> ContentDifficulty.ADVANCED
        }
        
        return listOf(
            createBinaryBridge(difficulty),
            createDebuggerQuest(difficulty),
            createKernelWar(difficulty),
            createAILab(difficulty),
            createMarketCrash(difficulty),
            createSpaceColony(difficulty),
            createQuantumRefactor(difficulty)
        ).filter { it.ageBracket.range.first <= age }
    }
}
