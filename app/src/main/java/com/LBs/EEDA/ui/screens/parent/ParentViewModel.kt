package com.LBs.EEDA.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.ActivityEntry
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.model.ContentFilterLevel
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ParentUiState(
    val profile: ChildProfile? = null,
    val isLoading: Boolean = false,
    val isAuthSuccess: Boolean = false,
    val error: String? = null,
    val activityLog: List<ActivityEntry> = emptyList(),
    val currentPinAttempt: String = ""
)

class ParentViewModel(private val repository: ChildProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ParentUiState())
    val uiState: StateFlow<ParentUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result.onSuccess { profile ->
                    _uiState.update { it.copy(
                        profile = profile,
                        activityLog = profile?.parentConfig?.activityLog ?: emptyList(),
                        isLoading = false
                    ) }
                }.onFailure { e ->
                    _uiState.update { it.copy(error = "Error al cargar el perfil: ${e.localizedMessage}", isLoading = false) }
                }
            }
        }
    }

    fun authenticate(pin: String) {
        val state = _uiState.value
        val correctPin = state.profile?.parentConfig?.parentPin ?: "1234"
        if (pin == correctPin) { 
            _uiState.update { it.copy(isAuthSuccess = true, error = null) }
        } else {
            _uiState.update { it.copy(error = "PIN incorrecto", isAuthSuccess = false) }
        }
    }

    fun logoutParent() {
        _uiState.update { it.copy(isAuthSuccess = false) }
    }

    fun resetProfile(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            repository.deleteProfile().onSuccess {
                onComplete()
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Error al eliminar: ${e.localizedMessage}", isLoading = false) }
            }
        }
    }

    fun updateScreenTimeLimit(limitInMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.profile?.let { profile ->
                val updatedConfig = profile.parentConfig.copy(dailyTimeLimitMinutes = limitInMinutes)
                val updatedProfile = profile.copy(parentConfig = updatedConfig)
                repository.saveProfile(updatedProfile)
            }
        }
    }

    fun setContentFilterLevel(level: ContentFilterLevel) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.profile?.let { profile ->
                val updatedConfig = profile.parentConfig.copy(contentFilterLevel = level)
                val updatedProfile = profile.copy(parentConfig = updatedConfig)
                repository.saveProfile(updatedProfile)
            }
        }
    }

    fun toggleWeeklyReports(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value.profile?.let { profile ->
                val updatedConfig = profile.parentConfig.copy(weeklyReportEnabled = enabled)
                val updatedProfile = profile.copy(parentConfig = updatedConfig)
                repository.saveProfile(updatedProfile)
            }
        }
    }
}
