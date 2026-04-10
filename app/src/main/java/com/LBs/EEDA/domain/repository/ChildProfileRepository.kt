package com.LBs.EEDA.domain.repository

import com.LBs.EEDA.domain.model.ChildProfile
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
     * Obtiene el perfil del niño como Flow directo (sin Result wrapper).
     * Conveniencia para casos donde solo se necesita el perfil o null.
     */
    fun getChildProfile(): Flow<ChildProfile?>

    /**
     * Guarda un perfil.
     */
    suspend fun saveProfile(profile: ChildProfile): Result<Unit>

    /**
     * Elimina el perfil.
     */
    suspend fun deleteProfile(): Result<Unit>
}
