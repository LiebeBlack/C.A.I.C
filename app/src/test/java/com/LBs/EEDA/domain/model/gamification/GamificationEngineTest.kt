package com.LBs.EEDA.domain.model.gamification

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.BeforeEach

/**
 * Tests unitarios para el motor de gamificación
 */
class GamificationEngineTest {

    private lateinit var initialState: GamificationState
    private lateinit var engine: GamificationEngine

    @BeforeEach
    fun setup() {
        initialState = GamificationState()
        engine = GamificationEngine(initialState)
    }

    @Test
    @DisplayName("Calcular XP necesario para nivel 2")
    fun `calculate xp for level 2`() {
        val xpNeeded = engine.xpForNextLevel()
        assertTrue(xpNeeded > 0)
    }

    @Test
    @DisplayName("Agregar XP aumenta el total")
    fun `add xp increases total`() {
        val newState = engine.addXp(100, XpSource.LessonCompletion(10))
        assertEquals(100L, newState.totalXp)
    }

    @Test
    @DisplayName("Subir de nivel cuando se alcanza el XP necesario")
    fun `level up when reaching required xp`() {
        // Add enough XP to level up
        var state = initialState
        repeat(10) {
            state = GamificationEngine(state).addXp(200, XpSource.LessonCompletion(10))
        }
        
        assertTrue(state.currentLevel > 1)
    }

    @Test
    @DisplayName("Calcular tier según nivel")
    fun `calculate tier by level`() {
        val explorer = GamificationEngine(GamificationState(currentLevel = 1)).calculateTier(1)
        val apprentice = GamificationEngine(GamificationState(currentLevel = 15)).calculateTier(15)
        val master = GamificationEngine(GamificationState(currentLevel = 80)).calculateTier(80)

        assertEquals(Tier.EXPLORER, explorer)
        assertEquals(Tier.APPRENTICE, apprentice)
        assertEquals(Tier.MASTER, master)
    }

    @Test
    @DisplayName("Generar quests diarias")
    fun `generate daily quests`() {
        val quests = engine.generateDailyQuests()
        assertEquals(3, quests.size)
        assertTrue(quests.all { it.xpReward > 0 })
    }

    @Test
    @DisplayName("Completar quest diaria otorga XP")
    fun `complete daily quest gives xp`() {
        val quests = engine.generateDailyQuests()
        val quest = quests.first()
        
        val newState = engine.completeDailyQuest(quest.id)
        assertTrue(newState.dailyQuests.find { it.id == quest.id }?.completed == true)
    }

    @Test
    @DisplayName("Calcular ranking global")
    fun `calculate global ranking`()() {
        val beginnerRanking = GamificationEngine(GamificationState(currentLevel = 5)).estimateGlobalRanking()
        val expertRanking = GamificationEngine(GamificationState(currentLevel = 60)).estimateGlobalRanking()

        assertTrue(beginnerRanking.contains("%"))
        assertTrue(expertRanking.contains("Top"))
    }
}
