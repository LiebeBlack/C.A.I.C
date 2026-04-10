package com.LBs.EEDA.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Entidad de dominio central que representa el perfil de un usuario en E.E.D.A.
 * Versión 2.0 (2026): Soporta usuarios de 3 a 20 años con progresión por fases,
 * árbol de habilidades, certificaciones y modo acompañante.
 */
@Serializable
data class ChildProfile(
    val id: String,
    val name: String,
    val age: Int,
    val avatar: String,
    val createdAt: String,

    // === Fase y Progresión ===
    val totalPlayTimeMinutes: Int = 0,
    val currentLevel: Int = 1,
    val totalXp: Long = 0L,
    val streakDays: Int = 0,
    val lastActiveDate: String = "",

    // === Árbol de Habilidades Digitales ===
    val skillProgress: Map<String, Int> = emptyMap(), // skillId -> xpEarned
    val completedSkillIds: List<String> = emptyList(),
    val unlockedSkillIds: List<String> = emptyList(),

    // === Logros y Misiones ===
    val achievements: List<String> = emptyList(),
    val activeQuests: List<String> = emptyList(),

    // === Certificaciones ===
    val earnedCertifications: List<EarnedCertification> = emptyList(),
    val earnedBadges: List<String> = emptyList(),

    // === Mapa de Aventuras (Niveles) ===
    val completedMapLevelIds: List<Int> = emptyList(),

    // === Lecciones ===
    val completedLessonIds: List<String> = emptyList(),
    val currentLessonId: String? = null,
    val lessonsProgress: Map<String, Int> = emptyMap(), // lessonId -> percent

    // === Modo Acompañante ===
    val parentConfig: ParentConfig = ParentConfig(),

    // === Personalización ===
    val themeOverride: String? = null,
    val preferredLanguage: String = "es",

    // === Progreso Educativo (Sistema 2026) ===
    val completedMinigameIds: List<String> = emptyList(),
    val minigameProgress: Map<String, Int> = emptyMap(), // minigameId -> percentComplete
    val completedSandboxIds: List<String> = emptyList(),
    val sandboxProgress: Map<String, Int> = emptyMap(), // sandboxId -> completionPercent
    val conceptMastery: Map<String, Int> = emptyMap(), // conceptId -> masteryLevel (0-100)
    val assessmentResults: List<AssessmentResult> = emptyList(),
    val educationalStreak: Int = 0, // Días consecutivos de actividad educativa
    val favoriteConcepts: List<String> = emptyList(),
    val struggleConcepts: List<String> = emptyList() // Para personalización de pistas
) {
    /**
     * Progreso calculado por categorías para la UI.
     */
    val logicProgress: Int get() = calculateCategoryProgress(SkillCategory.PROGRAMMING)
    val creativityProgress: Int get() = calculateCategoryProgress(SkillCategory.CONTENT_CREATION)
    val problemSolvingProgress: Int get() = calculateCategoryProgress(SkillCategory.SECURITY)

    private fun calculateCategoryProgress(category: SkillCategory): Int {
        val allSkills = SkillTreeData.getDefaultSkillTree().filter { it.category == category }
        if (allSkills.isEmpty()) return 0
        val completedInCategory = completedSkillIds.count { id -> allSkills.any { it.id == id } }
        return (completedInCategory.toFloat() / allSkills.size * 100).toInt()
    }

    init {
        require(age in 3..20) { "La edad debe estar entre 3 y 20 años" }
        require(totalPlayTimeMinutes >= 0) { "El tiempo de juego no puede ser negativo" }
        require(currentLevel >= 1) { "El nivel actual debe ser al menos 1" }
    }

    /**
     * Calcula la fase actual basándose en la edad.
     */
    val currentPhase: DigitalPhase get() = DigitalPhase.fromAge(age)

    /**
     * Título honorífico basado en certificaciones.
     */
    val highestTitle: String get() {
        if (earnedCertifications.isEmpty()) return "Explorador Digital"
        return earnedCertifications
            .maxByOrNull { it.type.tier * 10 + it.type.ordinal }
            ?.type?.displayName ?: "Explorador"
    }

    /**
     * Sugiere la siguiente acción recomendada basada en el progreso actual.
     */
    val nextRecommendedAction: String get() {
        val phase = currentPhase
        // Priorizar desbloquear habilidades si no hay suficientes
        if (unlockedSkillIds.size - completedSkillIds.size < 2) {
            return "Desbloquea una nueva habilidad en el Árbol del Conocimiento"
        }
        // Priorizar niveles del mapa no completados
        return "Continúa tu aventura en el Mapa de Niveles"
    }

    // === Funciones de Progresión ===

    fun addPlayTime(minutes: Int): ChildProfile =
        copy(totalPlayTimeMinutes = totalPlayTimeMinutes + minutes)

    fun addXp(amount: Long): ChildProfile {
        val newXp = totalXp + amount
        var level = 1
        var threshold = 100L
        var accumulated = 0L
        while (accumulated + threshold <= newXp) {
            accumulated += threshold
            level++
            threshold = (100L * level * 1.2).toLong()
        }
        return copy(totalXp = newXp, currentLevel = level)
    }

    fun finishMapLevel(levelId: Int, xpEarned: Long): ChildProfile {
        if (levelId in completedMapLevelIds) return addXp(xpEarned / 2) // XP reducida por repetición
        return copy(completedMapLevelIds = completedMapLevelIds + levelId)
            .addXp(xpEarned)
            .logActivity("Completó nivel del mapa: $levelId", "Aventuras")
    }

    fun completeSkill(skillId: String): ChildProfile {
        if (skillId in completedSkillIds) return this
        val newCompleted = completedSkillIds + skillId
        val allSkills = SkillTreeData.getDefaultSkillTree()
        val newlyUnlocked = allSkills
            .filter { skill ->
                skill.prerequisiteIds.contains(skillId) &&
                skill.prerequisiteIds.all { it in newCompleted } &&
                skill.id !in unlockedSkillIds
            }
            .map { it.id }
        
        val profileWithSkills = copy(
            completedSkillIds = newCompleted,
            unlockedSkillIds = unlockedSkillIds + newlyUnlocked
        )
        
        // Completar una habilidad otorga 250 XP
        return profileWithSkills.addXp(250)
            .checkNewCertifications()
            .logActivity("Completó habilidad: $skillId", "Habilidades")
    }

    private fun checkNewCertifications(): ChildProfile {
        val earnedIds = earnedCertifications.map { it.type.name }.toSet()
        val newEarned = CertificationType.entries.filter { cert ->
            cert.name !in earnedIds && 
            cert.requiredSkillIds.isNotEmpty() &&
            cert.requiredSkillIds.all { it in completedSkillIds }
        }.map { type -> 
            EarnedCertification(type, java.time.Instant.now().toString())
        }
        
        if (newEarned.isEmpty()) return this
        
        // Cada certificación otorga 500 XP
        return copy(earnedCertifications = earnedCertifications + newEarned)
            .addXp((newEarned.size * 500).toLong())
    }

    fun updateSkillXp(skillId: String, xp: Int): ChildProfile {
        val currentXp = skillProgress.getOrDefault(skillId, 0)
        return copy(skillProgress = skillProgress + (skillId to (currentXp + xp)))
    }

    fun awardBadge(badgeName: String): ChildProfile {
        if (earnedBadges.contains(badgeName)) return this
        val newBadges = earnedBadges + badgeName
        return copy(earnedBadges = newBadges).addXp(150)
    }

    fun completeLesson(lessonId: String): ChildProfile {
        if (lessonId in completedLessonIds) return this
        // Cada lección otorga 100 XP
        val newProfile = copy(completedLessonIds = completedLessonIds + lessonId)
            .addXp(100)
        return newProfile.logActivity("Completó lección: $lessonId", "Lecciones")
    }

    private fun logActivity(action: String, category: String, duration: Int = 0): ChildProfile {
        val entry = ActivityEntry(
            timestamp = java.time.Instant.now().toString(),
            action = action,
            category = category,
            durationMinutes = duration
        )
        val newLog = (parentConfig.activityLog + entry).takeLast(50)
        return copy(parentConfig = parentConfig.copy(activityLog = newLog))
    }

    companion object {
        fun createDefault(name: String = "Explorador", age: Int = 5): ChildProfile {
            val phase = DigitalPhase.fromAge(age)
            val initialSkills = SkillTreeData.getDefaultSkillTree()
                .filter { it.requiredPhase == phase && it.prerequisiteIds.isEmpty() }
                .map { it.id }

            return ChildProfile(
                id = "user_${System.currentTimeMillis()}",
                name = name,
                age = age,
                avatar = "avatar_base",
                createdAt = java.time.Instant.now().toString(),
                unlockedSkillIds = initialSkills
            )
        }
    }
}

@Serializable
data class AssessmentResult(
    val assessmentId: String,
    val timestamp: String,
    val conceptId: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val hintsUsed: Int,
    val timeSpentMinutes: Int
)
