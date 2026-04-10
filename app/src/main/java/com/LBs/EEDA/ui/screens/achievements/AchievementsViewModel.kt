package com.LBs.EEDA.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.gamification.Achievement
import com.LBs.EEDA.domain.model.gamification.AchievementCategory
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val filteredAchievements: List<Achievement> = emptyList(),
    val selectedCategory: AchievementCategory? = null,
    val totalCount: Int = 0,
    val unlockedCount: Int = 0,
    val totalXpEarned: Long = 0,
    val nextAchievement: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AchievementsViewModel(
    private val repository: ChildProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AchievementsUiState>(AchievementsUiState(isLoading = true))
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                repository.getProfile().collect { result ->
                    result.onSuccess { profile ->
                        val allAchievements = profile?.achievements?.mapNotNull { achievementId ->
                            AchievementCatalog.getById(achievementId)
                        } ?: emptyList()

                        val unlocked = allAchievements.filter { it.isUnlocked }
                        val totalXp = unlocked.sumOf { it.xpReward }

                        val next = allAchievements
                            .filter { !it.isUnlocked }
                            .minByOrNull { it.progressPercent }
                            ?.name

                        _uiState.update { state ->
                            state.copy(
                                achievements = allAchievements,
                                filteredAchievements = allAchievements,
                                totalCount = allAchievements.size,
                                unlockedCount = unlocked.size,
                                totalXpEarned = totalXp,
                                nextAchievement = next,
                                isLoading = false
                            )
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Error al cargar logros: ${error.message}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectCategory(category: AchievementCategory?) {
        _uiState.update { state ->
            val filtered = if (category == null) {
                state.achievements
            } else {
                state.achievements.filter { it.category == category }
            }

            state.copy(
                selectedCategory = category,
                filteredAchievements = filtered
            )
        }
    }

    fun onAchievementClick(achievement: Achievement) {
        // Navegar a detalle o mostrar más información
    }
}

// Catalogo de logros disponibles
object AchievementCatalog {
    private val achievements = listOf(
        Achievement("ach_first_lesson", "Primer Paso", "Completa tu primera lección", AchievementCategory.LEARNING, com.LBs.EEDA.domain.model.gamification.Rarity.COMMON, 50, icon = "🎓"),
        Achievement("ach_10_lessons", "Estudiante", "10 lecciones completadas", AchievementCategory.LEARNING, com.LBs.EEDA.domain.model.gamification.Rarity.UNCOMMON, 200, maxProgress = 10, icon = "📚"),
        Achievement("ach_first_skill", "Desbloqueador", "Primera habilidad desbloqueada", AchievementCategory.SKILL, com.LBs.EEDA.domain.model.gamification.Rarity.COMMON, 100, icon = "🔓"),
        Achievement("ach_streak_7", "Semana Perfecta", "7 días de racha", AchievementCategory.STREAK, com.LBs.EEDA.domain.model.gamification.Rarity.RARE, 500, maxProgress = 7, icon = "🔥")
    )

    fun getById(id: String): Achievement? = achievements.find { it.id == id }
    fun getAll(): List<Achievement> = achievements
}

sealed class AchievementsUiState {
    data object Loading : AchievementsUiState()
    data class Success(
        val achievements: List<Achievement>,
        val filteredAchievements: List<Achievement>,
        val selectedCategory: AchievementCategory?,
        val totalCount: Int,
        val unlockedCount: Int,
        val totalXpEarned: Long,
        val nextAchievement: String?
    ) : AchievementsUiState()
    data class Error(val message: String) : AchievementsUiState()
}
