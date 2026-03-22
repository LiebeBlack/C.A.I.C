package com.liebeblack.isla_digital.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liebeblack.isla_digital.domain.model.ChildProfile
import com.liebeblack.isla_digital.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * State holder para la HomeScreen siguiendo patrones 2026.
 */
class HomeViewModel(
    private val repository: ChildProfileRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getProfile()
        .map { result ->
            result.fold(
                onSuccess = { profile -> 
                    if (profile != null) {
                        HomeUiState.Success(profile)
                    } else {
                        // Creación automática de un perfil inicial por defecto si está vacío
                        createDefaultProfileIfNeeded()
                        HomeUiState.Loading // Mientras lo crea, mostramos estado Loading
                    }
                },
                onFailure = { error -> HomeUiState.Error(error.message ?: "Error desconocido al procesar el perfil del aventurero.") }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    /**
     * Trigger explícito si un perfil no se encuentra.
     */
    private fun createDefaultProfileIfNeeded() {
        val defaultProfile = ChildProfile(
            id = "default_explorer_01",
            name = "Explorador",
            age = 5,
            avatar = "avatar_base",
            createdAt = "2026-01-01T12:00:00Z"
        )
        // Guardamos asíncronamente en el repositorio sin romper el Flow
        viewModelScope.launch {
            repository.saveProfile(defaultProfile)
        }
    }

    /**
     * Función que incrementará medallas cuando el jugador supere desafíos.
     */
    fun awardBadgeToCurrentProfile(badgeName: String) {
        val currentState = uiState.value
        if (currentState is HomeUiState.Success) {
            val updatedProfile = currentState.profile?.awardBadge(badgeName)
            if (updatedProfile != null) {
                viewModelScope.launch { repository.saveProfile(updatedProfile) }
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val profile: ChildProfile?) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
