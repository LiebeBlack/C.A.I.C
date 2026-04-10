package com.LBs.EEDA.ui.screens.showcase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.model.CertificationType
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado UI para la pantalla de Showcase (Galería de Logros)
 */
data class ShowcaseUiState(
    val isLoading: Boolean = true,
    val profile: ChildProfile? = null,
    val achievements: List<ShowcaseItem> = emptyList(),
    val certifications: List<CertificationItem> = emptyList(),
    val userLevel: GamificationSystem.UserLevel? = null
)

/**
 * Item de logro para mostrar en la galería
 */
data class ShowcaseItem(
    val id: String,
    val title: String,
    val description: String,
    val level: String,
    val iconEmoji: String,
    val rarity: GamificationSystem.AchievementRarity,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)

/**
 * Item de certificación para mostrar
 */
data class CertificationItem(
    val id: String,
    val title: String,
    val description: String,
    val tier: Int,
    val emoji: String,
    val earnedAt: String? = null
)

class ShowcaseViewModel(
    private val profileRepository: ChildProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowcaseUiState())
    val uiState: StateFlow<ShowcaseUiState> = _uiState.asStateFlow()

    init {
        loadShowcaseData()
    }

    private fun loadShowcaseData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val profile = profileRepository.getChildProfile().first()
                
                if (profile != null) {
                    val userLevel = GamificationSystem.UserLevel.Companion.calculateLevel(profile.totalXp)
                    
                    // Mapear logros del sistema gamification
                    val allAchievements = GamificationSystem.getAllAchievements()
                    val showcaseItems = allAchievements.map { achievement ->
                        val isUnlocked = profile.achievements.contains(achievement.id)
                        ShowcaseItem(
                            id = achievement.id,
                            title = achievement.name,
                            description = achievement.description,
                            level = when (achievement.rarity) {
                                GamificationSystem.AchievementRarity.COMMON -> "Común"
                                GamificationSystem.AchievementRarity.RARE -> "Rara"
                                GamificationSystem.AchievementRarity.EPIC -> "Épica"
                                GamificationSystem.AchievementRarity.LEGENDARY -> "Legendaria"
                                GamificationSystem.AchievementRarity.MYTHIC -> "Mítica"
                            },
                            iconEmoji = achievement.icon,
                            rarity = achievement.rarity,
                            isUnlocked = isUnlocked,
                            unlockedDate = if (isUnlocked) System.currentTimeMillis() else null
                        )
                    }.sortedBy { it.isUnlocked }
                    
                    // Mapear certificaciones
                    val certItems = profile.earnedCertifications.map { cert ->
                        CertificationItem(
                            id = cert.type.name,
                            title = cert.type.displayName,
                            description = cert.type.description,
                            tier = cert.type.tier,
                            emoji = cert.type.emoji,
                            earnedAt = cert.earnedAt
                        )
                    }
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            achievements = showcaseItems,
                            certifications = certItems,
                            userLevel = userLevel
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun refreshData() {
        loadShowcaseData()
    }
}
