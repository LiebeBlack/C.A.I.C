package com.liebeblack.isla_digital.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Entidad de dominio que representa el perfil de un niño.
 * Versión 2026: Inmutable, con soporte para Kotlinx Serialization.
 */
@Serializable
data class ChildProfile(
    val id: String,
    val name: String,
    val age: Int,
    val avatar: String,
    // Nota: LocalDateTime requiere un Serializer personalizado en kotlinx-serialization
    // o usar un formato de String para persistencia simple.
    val createdAt: String, 
    val totalPlayTimeMinutes: Int = 0,
    val currentLevel: Int = 1,
    val levelProgress: Map<String, Int> = emptyMap(),
    val earnedBadges: List<String> = emptyList(),
    val logicProgress: Int = 0,
    val creativityProgress: Int = 0,
    val problemSolvingProgress: Int = 0
) {
    init {
        require(age >= 0) { "La edad no puede ser negativa" }
        require(totalPlayTimeMinutes >= 0) { "El tiempo de juego no puede ser negativo" }
        require(currentLevel >= 1) { "El nivel actual debe ser al menos 1" }
        require(logicProgress in 0..100)
        require(creativityProgress in 0..100)
        require(problemSolvingProgress in 0..100)
    }

    /**
     * Incrementa el tiempo de juego y recalcula el progreso.
     */
    fun addPlayTime(minutes: Int): ChildProfile {
        return copy(totalPlayTimeMinutes = this.totalPlayTimeMinutes + minutes)
    }

    /**
     * Otorga una nueva medalla al niño si no la posee, y potencialmente sube el nivel.
     */
    fun awardBadge(badgeName: String): ChildProfile {
        if (earnedBadges.contains(badgeName)) return this
        val newBadges = earnedBadges + badgeName
        val levelUp = if (newBadges.size % 3 == 0) 1 else 0 // Sube 1 nivel cada 3 medallas
        return copy(
            earnedBadges = newBadges,
            currentLevel = this.currentLevel + levelUp
        )
    }

    /**
     * Actualiza el progreso en un área específica (Lógica, Creatividad, Problemas).
     */
    fun updateSkillProgress(logic: Int = 0, creativity: Int = 0, problemSolving: Int = 0): ChildProfile {
        return copy(
            logicProgress = (this.logicProgress + logic).coerceIn(0, 100),
            creativityProgress = (this.creativityProgress + creativity).coerceIn(0, 100),
            problemSolvingProgress = (this.problemSolvingProgress + problemSolving).coerceIn(0, 100)
        )
    }
}
