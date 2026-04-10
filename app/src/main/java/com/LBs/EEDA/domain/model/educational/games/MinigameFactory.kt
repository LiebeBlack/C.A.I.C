package com.LBs.EEDA.domain.model.educational.games

import com.LBs.EEDA.domain.model.educational.*

/**
 * El Motor de Generación Procedural de Minijuegos E.E.D.A.
 * Capacidad de generar más de 4000 variantes de desafíos distribuidos por todas las disciplinas.
 */
object MinigameFactory {

    /**
     * Genera una lista masiva de juegos dinámicos bajo demanda.
     */
    fun createInfiniteCatalog(age: Int): List<Minigame> {
        val totalGames = 4000
        val catalog = mutableListOf<Minigame>()
        
        // Juegos base fijos (Ajedrez, Dibujo, RTS)
        catalog.add(createGrandmasterChess(ContentDifficulty.ADVANCED))
        catalog.add(createDigitalArtist(ContentDifficulty.INTERMEDIATE))
        
        // Generador Procedural (Los otros 3998)
        for (i in 1 until totalGames) {
            val type = MinigameType.values()[i % MinigameType.values().size]
            catalog.add(
                Minigame(
                    id = "gen_game_$i",
                    type = type,
                    title = "Desafío Maestro #$i: ${type.name.lowercase().replace("_", " ")}",
                    description = "Misión educativa dinámica nivel $i para el desarrollo de habilidades cognitivas.",
                    conceptId = "concept_$i",
                    difficulty = when { i % 3 == 0 -> ContentDifficulty.EXPERT; i % 2 == 0 -> ContentDifficulty.ADVANCED; else -> ContentDifficulty.BEGINNER },
                    ageBracket = if (age < 10) AgeBracket.CHILDHOOD else AgeBracket.ADOLESCENT_EARLY,
                    estimatedDurationMinutes = (5..30).random(),
                    xpReward = (100L..1000L).random(),
                    stages = listOf(generateProceduralStage(i)),
                    config = MinigameConfig(adaptiveDifficulty = true)
                )
            )
        }
        return catalog
    }

    private fun generateProceduralStage(seed: Int): MinigameStage {
        return MinigameStage(
            stageNumber = 1,
            instruction = "Analiza y resuelve el patrón lógico de nivel $seed.",
            elements = listOf(
                GameElement("el_1", ElementVisualType.ICON, "Token A", "val_$seed"),
                GameElement("el_2", ElementVisualType.ICON, "Token B", "val_${seed+1}")
            ),
            correctSolution = Solution(correctValue = "val_$seed"),
            hints = listOf()
        )
    }

    fun createGrandmasterChess(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "chess_001",
            type = MinigameType.CHESS_PUZZLE,
            title = "Grandmaster Tactics: Checkmate in 2",
            description = "Encuentra la combinación ganadora de piezas blancas para dar jaque mate al rey negro.",
            conceptId = "strategy_chess",
            difficulty = difficulty,
            ageBracket = AgeBracket.ADOLESCENT_LATE,
            estimatedDurationMinutes = 10,
            xpReward = 500,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Mueve la Reina Blanca a la posición H7 para iniciar el jaque.",
                    elements = listOf(
                        GameElement("q", ElementVisualType.CHESS_PIECE, "Queen", "White"),
                        GameElement("k", ElementVisualType.CHESS_PIECE, "King", "Black")
                    ),
                    correctSolution = Solution(correctPairs = mapOf("q" to "h7")),
                    hints = listOf()
                )
            ),
            config = MinigameConfig()
        )
    }

    fun createDigitalArtist(difficulty: ContentDifficulty): Minigame {
        return Minigame(
            id = "art_001",
            type = MinigameType.ART_DRAWING,
            title = "Infinite Canvas: Fractal Master",
            description = "Dibuja patrones fractales usando vectores de colores dinámicos.",
            conceptId = "creative_art",
            difficulty = difficulty,
            ageBracket = AgeBracket.CHILDHOOD,
            estimatedDurationMinutes = 15,
            xpReward = 200,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Completa el círculo cromático usando la herramienta de trazo.",
                    elements = listOf(),
                    correctSolution = Solution(correctValue = "art_complete"),
                    hints = listOf()
                )
            ),
            config = MinigameConfig(sandboxMode = true)
        )
    }
}
