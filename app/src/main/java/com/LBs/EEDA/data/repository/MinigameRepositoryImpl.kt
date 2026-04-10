package com.LBs.EEDA.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.LBs.EEDA.domain.model.educational.*
import com.LBs.EEDA.domain.usecase.educational.MinigameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Implementación del repositorio de minijuegos usando SharedPreferences.
 */
class MinigameRepositoryImpl(
    private val context: Context,
    private val json: Json
) : MinigameRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("educational_progress", Context.MODE_PRIVATE)

    override suspend fun getMinigame(id: String): Minigame? {
        return withContext(Dispatchers.IO) {
            getSampleMinigames().find { it.id == id }
        }
    }

    override suspend fun getUserProgress(userId: String): UserMinigameProgress {
        return withContext(Dispatchers.IO) {
            try {
                val progressJson = prefs.getString("minigame_progress_$userId", "{}") ?: "{}"
                val progress = json.decodeFromString<Map<String, Int>>(progressJson)
                
                UserMinigameProgress(
                    userId = userId,
                    completedMinigames = emptyMap(),
                    skillLevels = progress.mapValues { it.value / 20 } // Convertir progreso a nivel
                )
            } catch (e: Exception) {
                UserMinigameProgress(userId, emptyMap(), emptyMap())
            }
        }
    }

    override suspend fun saveAttempt(attempt: MinigameAttempt) {
        withContext(Dispatchers.IO) {
            val key = "attempts_${attempt.userId}_${attempt.minigameId}"
            val currentJson = prefs.getString(key, "[]") ?: "[]"
            val attempts = json.decodeFromString<MutableList<MinigameAttempt>>(currentJson)
            attempts.add(attempt)
            prefs.edit { putString(key, json.encodeToString(attempts)) }
            
            // Actualizar progreso
            val progressKey = "minigame_progress_${attempt.userId}"
            val progressJson = prefs.getString(progressKey, "{}") ?: "{}"
            val progress = json.decodeFromString<MutableMap<String, Int>>(progressJson)
            val currentScore = progress.getOrDefault(attempt.minigameId, 0)
            if (attempt.score > currentScore) {
                progress[attempt.minigameId] = attempt.score
                prefs.edit { putString(progressKey, json.encodeToString(progress)) }
            }
        }
    }

    private fun getSampleMinigames(): List<Minigame> {
        return listOf(
            createPasswordMinigame(),
            createEncryptionMinigame(),
            createAlgorithmMinigame(),
            createNetworkMinigame()
        )
    }

    private fun createPasswordMinigame(): Minigame {
        return Minigame(
            id = "minigame_password_001",
            type = MinigameType.SEQUENCE_PUZZLE,
            title = "Construye una Contraseña Fuerte",
            description = "Ordena los elementos para crear una contraseña segura",
            conceptId = "password_security",
            difficulty = ContentDifficulty.ELEMENTARY,
            ageBracket = AgeBracket.CHILDHOOD,
            estimatedDurationMinutes = 5,
            xpReward = 100,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Ordena estos elementos para crear una contraseña segura:",
                    elements = listOf(
                        GameElement("1", ElementVisualType.TEXT_CARD, "Letra mayúscula", "A", category = "char"),
                        GameElement("2", ElementVisualType.TEXT_CARD, "Número", "5", category = "char"),
                        GameElement("3", ElementVisualType.TEXT_CARD, "Símbolo", "@", category = "char"),
                        GameElement("4", ElementVisualType.TEXT_CARD, "Letra minúscula", "g", category = "char")
                    ),
                    correctSolution = Solution(
                        orderedElements = listOf("1", "4", "2", "3")
                    ),
                    maxAttempts = 3,
                    hints = listOf(
                        AdaptiveHint(
                            hintId = "hint_password_1",
                            triggerPattern = ErrorPattern(ErrorType.WRONG_ORDER),
                            hintLevel = HintLevel.DIRECT,
                            message = "Empieza con mayúscula, luego minúsculas, número y símbolo",
                            visualAid = VisualAid(
                                type = VisualAidType.ARROW_POINTING,
                                content = "Mayúscula → Minúsculas → Número → Símbolo",
                                highlightIds = listOf("1", "4", "2", "3")
                            )
                        )
                    )
                )
            ),
            config = MinigameConfig()
        )
    }

    private fun createEncryptionMinigame(): Minigame {
        return Minigame(
            id = "minigame_encrypt_001",
            type = MinigameType.DRAG_DROP_SORTING,
            title = "Cifrado César",
            description = "Descifra el mensaje desplazando las letras",
            conceptId = "encryption",
            difficulty = ContentDifficulty.INTERMEDIATE,
            ageBracket = AgeBracket.PRE_ADOLESCENT,
            estimatedDurationMinutes = 8,
            xpReward = 150,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "El mensaje 'KROD' está cifrado con desplazamiento 3. ¿Cuál es el mensaje original?",
                    elements = listOf(
                        GameElement("a", ElementVisualType.TEXT_CARD, "H", "H"),
                        GameElement("b", ElementVisualType.TEXT_CARD, "O", "O"),
                        GameElement("c", ElementVisualType.TEXT_CARD, "L", "L"),
                        GameElement("d", ElementVisualType.TEXT_CARD, "A", "A")
                    ),
                    correctSolution = Solution(
                        orderedElements = listOf("a", "c", "b", "d")
                    ),
                    maxAttempts = 5,
                    hints = listOf(
                        AdaptiveHint(
                            hintId = "hint_encrypt_1",
                            triggerPattern = ErrorPattern(
                                type = ErrorType.WRONG_ORDER,
                                attemptCountMin = 2,
                                attemptCountMax = 3
                            ),
                            hintLevel = HintLevel.EXPLANATORY,
                            message = "César - 3 = letra original. K(10) - 3 = H(7)",
                            visualAid = VisualAid(
                                type = VisualAidType.DIAGRAM_OVERLAY,
                                content = "A B C D E F G H I J K L M...",
                                highlightIds = listOf("K", "H")
                            )
                        )
                    )
                )
            ),
            config = MinigameConfig()
        )
    }

    private fun createAlgorithmMinigame(): Minigame {
        return Minigame(
            id = "minigame_algo_001",
            type = MinigameType.SEQUENCE_PUZZLE,
            title = "Ordena los Pasos",
            description = "Secuencia correcta para cepillarse los dientes",
            conceptId = "programming_logic",
            difficulty = ContentDifficulty.BEGINNER,
            ageBracket = AgeBracket.EARLY_CHILDHOOD,
            estimatedDurationMinutes = 3,
            xpReward = 50,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Ordena los pasos para cepillarte los dientes:",
                    elements = listOf(
                        GameElement("1", ElementVisualType.ICON, "Tomar cepillo", "brush", category = "step"),
                        GameElement("2", ElementVisualType.ICON, "Enjuagar", "water", category = "step"),
                        GameElement("3", ElementVisualType.ICON, "Poner pasta", "paste", category = "step"),
                        GameElement("4", ElementVisualType.ICON, "Cepillar", "brush_action", category = "step")
                    ),
                    correctSolution = Solution(
                        orderedElements = listOf("1", "3", "4", "2")
                    ),
                    maxAttempts = 3,
                    hints = listOf(
                        AdaptiveHint(
                            hintId = "hint_algo_1",
                            triggerPattern = ErrorPattern(ErrorType.WRONG_ORDER),
                            hintLevel = HintLevel.SUBTLE,
                            message = "¿Puedes enjuagar antes de cepillar?",
                            visualAid = null
                        )
                    )
                )
            ),
            config = MinigameConfig()
        )
    }

    private fun createNetworkMinigame(): Minigame {
        return Minigame(
            id = "minigame_network_001",
            type = MinigameType.DRAG_DROP_SORTING,
            title = "Conecta los Dispositivos",
            description = "Une cada dispositivo con su función de red",
            conceptId = "network",
            difficulty = ContentDifficulty.INTERMEDIATE,
            ageBracket = AgeBracket.PRE_ADOLESCENT,
            estimatedDurationMinutes = 6,
            xpReward = 120,
            stages = listOf(
                MinigameStage(
                    stageNumber = 1,
                    instruction = "Conecta cada dispositivo con su función:",
                    elements = listOf(
                        GameElement("router", ElementVisualType.NETWORK_NODE, "Router", "router", category = "device"),
                        GameElement("firewall", ElementVisualType.NETWORK_NODE, "Firewall", "firewall", category = "device"),
                        GameElement("switch", ElementVisualType.NETWORK_NODE, "Switch", "switch", category = "device"),
                        GameElement("router_func", ElementVisualType.TEXT_CARD, "Enruta paquetes", "route", category = "function"),
                        GameElement("firewall_func", ElementVisualType.TEXT_CARD, "Filtra tráfico", "filter", category = "function"),
                        GameElement("switch_func", ElementVisualType.TEXT_CARD, "Conecta dispositivos", "connect", category = "function")
                    ),
                    correctSolution = Solution(
                        correctPairs = mapOf(
                            "router" to "router_func",
                            "firewall" to "firewall_func",
                            "switch" to "switch_func"
                        )
                    ),
                    maxAttempts = 4,
                    hints = listOf(
                        AdaptiveHint(
                            hintId = "hint_net_1",
                            triggerPattern = ErrorPattern(ErrorType.LOGIC_ERROR),
                            hintLevel = HintLevel.DIRECT,
                            message = "El Router decide el camino, el Firewall revisa seguridad",
                            visualAid = null
                        )
                    )
                )
            ),
            config = MinigameConfig()
        )
    }
}
