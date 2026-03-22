package com.liebeblack.isla_digital.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.liebeblack.isla_digital.domain.model.ChildProfile
import com.liebeblack.isla_digital.domain.repository.ChildProfileRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * Implementación del repositorio usando SharedPreferences y Kotlinx Serialization.
 * Estándar optimizado para 2026.
 */
class ChildProfileRepositoryImpl(
    private val prefs: SharedPreferences,
    private val jsonSerializer: Json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }
) : ChildProfileRepository {

    companion object {
        private const val PROFILE_KEY = "child_profile"
    }

    override fun getProfile(): Flow<Result<ChildProfile?>> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == PROFILE_KEY) {
                trySend(readProfile())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        // Emisión inicial
        trySend(readProfile())

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private fun readProfile(): Result<ChildProfile?> {
        return try {
            val json = prefs.getString(PROFILE_KEY, null)
            val profile = if (json != null) {
                jsonSerializer.decodeFromString<ChildProfile>(json)
            } else null
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveProfile(profile: ChildProfile): Result<Unit> {
        return try {
            val json = jsonSerializer.encodeToString(profile)
            prefs.edit { putString(PROFILE_KEY, json) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(): Result<Unit> {
        return try {
            prefs.edit { remove(PROFILE_KEY) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
