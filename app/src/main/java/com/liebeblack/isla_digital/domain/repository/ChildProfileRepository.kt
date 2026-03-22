package com.liebeblack.isla_digital.domain.repository

import com.liebeblack.isla_digital.domain.model.ChildProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio moderno 2026: Usamos Result y Flows para reactividad.
 */
interface ChildProfileRepository {
    /**
     * Obtiene el perfil actual de forma reactiva.
     */
    fun getProfile(): Flow<Result<ChildProfile?>>
    
    /**
     * Guarda un perfil.
     */
    suspend fun saveProfile(profile: ChildProfile): Result<Unit>
    
    /**
     * Elimina el perfil.
     */
    suspend fun deleteProfile(): Result<Unit>
}
