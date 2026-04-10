package com.LBs.EEDA.ui.screens.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.GameLevel
import com.LBs.EEDA.domain.model.LevelData
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LevelsUiState(
    val levels: List<GameLevel> = emptyList(),
    val completedLevelIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LevelsViewModel(private val repository: ChildProfileRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LevelsUiState(levels = LevelData.getLevels()))
    val uiState: StateFlow<LevelsUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result.onSuccess { profile ->
                    _uiState.update { it.copy(
                        completedLevelIds = profile?.completedMapLevelIds?.toSet() ?: emptySet(),
                        isLoading = false
                    ) }
                }.onFailure { e ->
                    _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
                }
            }
        }
    }

    fun completeLevel(level: GameLevel) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getProfile().first().onSuccess { profile ->
                    profile?.let {
                        val updatedProfile = it.finishMapLevel(level.id, level.xpReward)
                        repository.saveProfile(updatedProfile).onSuccess {
                            _uiState.update { state -> state.copy(isLoading = false) }
                        }.onFailure { e ->
                            _uiState.update { state -> state.copy(error = e.localizedMessage, isLoading = false) }
                        }
                    } ?: run {
                        _uiState.update { it.copy(error = "Perfil no encontrado", isLoading = false) }
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.localizedMessage, isLoading = false) }
            }
        }
    }
}
