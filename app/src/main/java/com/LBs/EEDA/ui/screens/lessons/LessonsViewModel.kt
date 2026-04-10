package com.LBs.EEDA.ui.screens.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.Lesson
import com.LBs.EEDA.domain.model.LessonData
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LessonsUiState(
    val lessons: List<Lesson> = emptyList(),
    val filteredLessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val completedLessonIds: Set<String> = emptySet(),
    val currentFilterCategory: String? = null,
    val error: String? = null
)

class LessonsViewModel(private val repository: ChildProfileRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LessonsUiState(lessons = LessonData.getLessons()))
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result.onSuccess { profile ->
                    _uiState.update { state ->
                        val updated = state.copy(
                            completedLessonIds = profile?.completedLessonIds?.toSet() ?: emptySet()
                        )
                        updated.copy(filteredLessons = filterLessons(updated))
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(error = "Error al cargar el perfil: ${e.localizedMessage}") }
                }
            }
        }
    }

    private fun filterLessons(state: LessonsUiState): List<Lesson> {
        return if (state.currentFilterCategory == null) {
            state.lessons
        } else {
            state.lessons.filter { it.category.name == state.currentFilterCategory }
        }
    }

    fun completeLesson(lesson: Lesson) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getProfile().firstOrNull()?.onSuccess { profile ->
                    profile?.let {
                        val updatedProfile = it.completeLesson(lesson.id)
                        repository.saveProfile(updatedProfile).onSuccess {
                            _uiState.update { state -> state.copy(isLoading = false) }
                        }.onFailure { e ->
                            _uiState.update { state -> state.copy(error = "Error al guardar: ${e.localizedMessage}", isLoading = false) }
                        }
                    }
                } ?: run {
                    _uiState.update { it.copy(error = "Perfil no encontrado", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error inesperado: ${e.localizedMessage}", isLoading = false) }
            }
        }
    }

    fun filterByCategory(categoryName: String?) {
        _uiState.update { state ->
            val updated = state.copy(currentFilterCategory = categoryName)
            updated.copy(filteredLessons = filterLessons(updated))
        }
    }
}
