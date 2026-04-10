package com.LBs.EEDA.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ChildProfileRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getProfile()
        .map { result ->
            result.fold(
                onSuccess = { profile ->
                    HomeUiState.Success(profile)
                },
                onFailure = { error -> HomeUiState.Error(error.message ?: "Error desconocido") }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )



    fun awardBadgeToCurrentProfile(badgeName: String) {
        val currentState = uiState.value
        if (currentState is HomeUiState.Success) {
            val updatedProfile = currentState.profile?.awardBadge(badgeName)
            if (updatedProfile != null) {
                viewModelScope.launch(Dispatchers.IO) { repository.saveProfile(updatedProfile) }
            }
        }
    }

    /**
     * Obtiene la fase digital del perfil actual.
     */
    fun getCurrentPhase(): DigitalPhase {
        val state = uiState.value
        return if (state is HomeUiState.Success && state.profile != null) {
            DigitalPhase.fromAge(state.profile.age)
        } else {
            DigitalPhase.SENSORIAL
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val profile: ChildProfile?) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
