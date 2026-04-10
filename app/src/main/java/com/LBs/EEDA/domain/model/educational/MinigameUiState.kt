package com.LBs.EEDA.domain.model.educational

import kotlinx.serialization.Serializable

/**
 * Representa el estado de la interfaz de usuario para un minijuego.
 */
@Serializable
data class MinigameUiState(
    val minigame: Minigame? = null,
    val currentStageIndex: Int = 0,
    val accumulatedXp: Int = 0,
    val lives: Int = 3,
    val gameState: String = "PLAYING", // "PLAYING", "SUCCESS", "GAME_OVER"
    val isLoading: Boolean = false,
    val error: String? = null
)
