package com.LBs.EEDA.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val age: Int = 5,
    val avatar: String = "avatar_base",
    val step: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(private val repository: ChildProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age.coerceIn(3, 20))
    }

    fun selectAvatar(avatar: String) {
        _uiState.value = _uiState.value.copy(avatar = avatar)
    }

    fun nextStep() {
        if (_uiState.value.step == 1 && _uiState.value.name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Por favor, introduce tu nombre para continuar")
            return
        }
        _uiState.value = _uiState.value.copy(step = _uiState.value.step + 1, error = null)
    }

    fun previousStep() {
        if (_uiState.value.step > 1) {
            _uiState.value = _uiState.value.copy(step = _uiState.value.step - 1, error = null)
        }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.value = state.copy(error = "El nombre es obligatorio")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val newProfile = ChildProfile.createDefault(state.name, state.age).copy(
                    avatar = state.avatar
                )
                val result = repository.saveProfile(newProfile)
                result.fold(
                    onSuccess = {
                        onComplete()
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Error al crear perfil: ${e.localizedMessage}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.localizedMessage}"
                )
            }
        }
    }
}
