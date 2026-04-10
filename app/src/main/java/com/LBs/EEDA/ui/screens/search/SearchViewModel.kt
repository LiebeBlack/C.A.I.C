package com.LBs.EEDA.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.LessonData
import com.LBs.EEDA.domain.model.SkillTreeData
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val results: List<SearchResult> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val popularSearches: List<String> = listOf(
        "Python", "Seguridad", "IA", "Blockchain", "Programación"
    ),
    val isLoading: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val repository: ChildProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: kotlinx.coroutines.Job? = null

    init {
        loadRecentSearches()
    }

    private fun loadRecentSearches() {
        // Load from preferences or local storage
        _uiState.update {
            it.copy(recentSearches = listOf("Contraseñas", "Loops", "Redes"))
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        // Debounce search
        searchJob?.cancel()
        if (query.isNotEmpty()) {
            searchJob = viewModelScope.launch {
                kotlinx.coroutines.delay(300)
                search()
            }
        } else {
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    fun clearQuery() {
        _uiState.update {
            it.copy(query = "", results = emptyList())
        }
    }

    fun onFilterChange(filter: SearchFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        if (_uiState.value.query.isNotEmpty()) {
            search()
        }
    }

    fun search() {
        val query = _uiState.value.query.lowercase()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val allLessons = LessonData.getLessons()
                val allSkills = SkillTreeData.getDefaultSkillTree()

                val lessonResults = if (_uiState.value.selectedFilter != SearchFilter.SKILLS) {
                    allLessons.filter {
                        it.title.lowercase().contains(query) ||
                        it.description.lowercase().contains(query) ||
                        it.category.name.lowercase().contains(query)
                    }.map { lesson ->
                        SearchResult.LessonResult(
                            id = lesson.id,
                            title = lesson.title,
                            description = lesson.description,
                            duration = lesson.estimatedMinutes,
                            xpReward = lesson.xpReward,
                            category = lesson.category.name
                        )
                    }
                } else emptyList()

                val skillResults = if (_uiState.value.selectedFilter != SearchFilter.LESSONS) {
                    allSkills.filter {
                        it.name.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
                    }.map { skill ->
                        SearchResult.SkillResult(
                            id = skill.id,
                            name = skill.name,
                            description = skill.description,
                            isUnlocked = false, // Would check from profile
                            requiredPhase = skill.requiredPhase.name
                        )
                    }
                } else emptyList()

                val combined = (lessonResults + skillResults)
                    .sortedBy { 
                        when (it) {
                            is SearchResult.LessonResult -> it.title.lowercase().indexOf(query)
                            is SearchResult.SkillResult -> it.name.lowercase().indexOf(query)
                        }
                    }

                _uiState.update {
                    it.copy(
                        results = combined,
                        isLoading = false
                    )
                }

                // Save to recent searches
                saveRecentSearch(query)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al buscar: ${e.message}"
                    )
                }
            }
        }
    }

    private fun saveRecentSearch(query: String) {
        // Would save to preferences
    }
}
