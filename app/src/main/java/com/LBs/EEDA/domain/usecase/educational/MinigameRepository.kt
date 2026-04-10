package com.LBs.EEDA.domain.usecase.educational

import com.LBs.EEDA.domain.model.educational.Minigame
import com.LBs.EEDA.domain.model.educational.MinigameAttempt
import com.LBs.EEDA.domain.model.educational.UserMinigameProgress

/**
 * Repositorio de Minijuegos Educativos.
 * Gestiona el catálogo de minijuegos y el progreso del usuario.
 */
interface MinigameRepository {
    /**
     * Obtiene un minijuego por su ID.
     */
    suspend fun getMinigame(id: String): Minigame?

    /**
     * Obtiene el progreso del usuario en minijuegos.
     */
    suspend fun getUserProgress(userId: String): UserMinigameProgress

    /**
     * Guarda un intento de minijuego completado.
     */
    suspend fun saveAttempt(attempt: MinigameAttempt)
}
