package com.LBs.EEDA.ui.screens.skilltree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.SkillCategory
import com.LBs.EEDA.domain.model.SkillNode
import com.LBs.EEDA.domain.model.SkillTreeData
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SkillTreeUiState(
    val skills: List<SkillNode> = emptyList(),
    val filteredSkills: List<SkillNode> = emptyList(),
    val selectedSkill: SkillNode? = null,
    val completedSkillIds: Set<String> = emptySet(),
    val unlockedSkillIds: Set<String> = emptySet(),
    val totalXp: Long = 0,
    val selectedCategory: SkillCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SkillTreeViewModel(private val repository: ChildProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SkillTreeUiState(skills = SkillTreeData.getDefaultSkillTree()))
    val uiState: StateFlow<SkillTreeUiState> = _uiState.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { result ->
                result.onSuccess { profile ->
                    _uiState.update { state ->
                        val updatedState = state.copy(
                            completedSkillIds = profile?.completedSkillIds?.toSet() ?: emptySet(),
                            unlockedSkillIds = profile?.unlockedSkillIds?.toSet() ?: emptySet(),
                            totalXp = profile?.totalXp ?: 0
                        )
                        updatedState.copy(filteredSkills = filterSkills(updatedState))
                    }
                }.onFailure { e ->
                    _uiState.update { it.copy(error = "Error al cargar el perfil: ${e.localizedMessage}") }
                }
            }
        }
    }

    private fun filterSkills(state: SkillTreeUiState): List<SkillNode> {
        return if (state.selectedCategory == null) {
            state.skills
        } else {
            state.skills.filter { it.category == state.selectedCategory }
        }
    }

    fun selectSkill(skill: SkillNode?) {
        _uiState.update { it.copy(selectedSkill = skill) }
    }

    fun unlockSkill(skillId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.getProfile().firstOrNull()?.onSuccess { profile ->
                    profile?.let {
                        val skill = _uiState.value.skills.find { it.id == skillId }
                        if (skill == null) {
                            _uiState.update { it.copy(error = "Habilidad no encontrada", isLoading = false) }
                            return@onSuccess
                        }

                        // Verificación de requisitos: Ya están desbloqueados por el dominio?
                        if (!it.unlockedSkillIds.contains(skillId)) {
                            _uiState.update { it.copy(error = "No cumples los requisitos previos", isLoading = false) }
                            return@onSuccess
                        }

                        // Verificación de XP (opcional, en este modelo el XP se gana al completar, pero si costara XP:)
                        // if (it.totalXp < skill.xpRequired) { ... }

                        val updatedProfile = it.completeSkill(skillId)
                        repository.saveProfile(updatedProfile).onSuccess {
                            _uiState.update { state -> state.copy(isLoading = false, selectedSkill = null) }
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

    fun selectCategory(category: SkillCategory?) {
        _uiState.update { state ->
            val newState = state.copy(selectedCategory = category)
            newState.copy(filteredSkills = filterSkills(newState))
        }
    }
}
