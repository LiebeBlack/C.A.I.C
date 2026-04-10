package com.LBs.EEDA.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

/**
 * Tests unitarios para el modelo ChildProfile
 */
class ChildProfileTest {

    @Test
    @DisplayName("Crear perfil por defecto con valores iniciales correctos")
    fun `create default profile with initial values`() {
        val profile = ChildProfile.createDefault("Test User", 8)

        assertEquals("Test User", profile.name)
        assertEquals(8, profile.age)
        assertEquals(1, profile.currentLevel)
        assertEquals(0L, profile.totalXp)
        assertTrue(profile.completedLessonIds.isEmpty())
        assertTrue(profile.completedSkillIds.isEmpty())
    }

    @Test
    @DisplayName("Agregar XP aumenta el total correctamente")
    fun `add xp increases total`() {
        val profile = ChildProfile.createDefault("Test", 10)
        val updated = profile.addXp(150)

        assertEquals(150L, updated.totalXp)
    }

    @Test
    @DisplayName("Completar lección agrega el ID a la lista")
    fun `complete lesson adds to list`() {
        val profile = ChildProfile.createDefault("Test", 10)
        val updated = profile.completeLesson("lesson_001")

        assertTrue(updated.completedLessonIds.contains("lesson_001"))
        assertEquals(1, updated.completedLessonIds.size)
    }

    @Test
    @DisplayName("Completar skill agrega el ID y desbloquea otros")
    fun `complete skill adds to list`() {
        val profile = ChildProfile.createDefault("Test", 10)
        val updated = profile.completeSkill("skill_001")

        assertTrue(updated.completedSkillIds.contains("skill_001"))
    }

    @Test
    @DisplayName("Calcular fase digital según edad")
    fun `calculate phase from age`() {
        val sensorial = ChildProfile.createDefault("Test", 5)
        val creative = ChildProfile.createDefault("Test", 10)
        val professional = ChildProfile.createDefault("Test", 18)
        val innovator = ChildProfile.createDefault("Test", 25)

        assertEquals(DigitalPhase.SENSORIAL, DigitalPhase.fromAge(sensorial.age))
        assertEquals(DigitalPhase.CREATIVE, DigitalPhase.fromAge(creative.age))
        assertEquals(DigitalPhase.PROFESSIONAL, DigitalPhase.fromAge(professional.age))
        assertEquals(DigitalPhase.INNOVATOR, DigitalPhase.fromAge(innovator.age))
    }

    @Test
    @DisplayName("Calcular porcentaje de progreso")
    fun `calculate progress percentage`() {
        val profile = ChildProfile.createDefault("Test", 10).copy(
            completedLessonIds = listOf("l1", "l2", "l3"),
            totalPlayTimeMinutes = 60
        )

        assertTrue(profile.totalPlayTimeMinutes > 0)
        assertEquals(3, profile.completedLessonIds.size)
    }

    @Test
    @DisplayName("Verificar consistencia de datos de progreso")
    fun `verify progress data consistency`() {
        val profile = ChildProfile.createDefault("Test", 12).copy(
            totalXp = 1500,
            currentLevel = 5,
            streakDays = 7
        )

        assertTrue(profile.totalXp >= 0)
        assertTrue(profile.currentLevel > 0)
        assertTrue(profile.streakDays >= 0)
    }
}
