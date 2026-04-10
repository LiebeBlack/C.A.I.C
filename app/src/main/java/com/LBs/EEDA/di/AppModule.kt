package com.LBs.EEDA.di

import android.content.Context
import android.content.SharedPreferences
import com.LBs.EEDA.data.repository.*
import com.LBs.EEDA.domain.repository.*
import com.LBs.EEDA.domain.usecase.educational.*
import kotlinx.serialization.json.Json

/**
 * Módulo de Inyección de Dependencias Manual para E.E.D.A
 * Proporciona todas las dependencias de la aplicación
 */
object AppModule {

    // ═══════════════════════════════════════════════════════════════
    // SERIALIZATION
    // ═══════════════════════════════════════════════════════════════

    val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            prettyPrint = false
            isLenient = true
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // SHARED PREFERENCES
    // ═══════════════════════════════════════════════════════════════

    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("eeda_prefs", Context.MODE_PRIVATE)
    }

    fun provideSecurePreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("eeda_secure", Context.MODE_PRIVATE)
    }

    // ═══════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════

    fun provideChildProfileRepository(context: Context): ChildProfileRepository {
        return ChildProfileRepositoryImpl(
            prefs = provideSharedPreferences(context),
            jsonSerializer = json
        )
    }

    fun provideHardwareRepository(context: Context): HardwareRepository {
        return HardwareRepositoryImpl(context)
    }

    fun provideMinigameRepository(context: Context): MinigameRepository {
        return MinigameRepositoryImpl(
            context = context,
            json = json
        )
    }

    fun provideSandboxRepository(context: Context): SandboxRepository {
        return SandboxRepositoryImpl(
            context = context,
            json = json
        )
    }

    fun provideAssessmentRepository(context: Context): AssessmentRepository {
        return AssessmentRepositoryImpl(
            context = context,
            json = json
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // USE CASES
    // ═══════════════════════════════════════════════════════════════

    fun provideGetProfileUseCase(repository: ChildProfileRepository): GetProfileUseCase {
        return GetProfileUseCase(repository)
    }

    fun provideSaveProfileUseCase(repository: ChildProfileRepository): SaveProfileUseCase {
        return SaveProfileUseCase(repository)
    }

    fun provideCompleteLessonUseCase(repository: ChildProfileRepository): CompleteLessonUseCase {
        return CompleteLessonUseCase(repository)
    }

    fun provideUnlockSkillUseCase(repository: ChildProfileRepository): UnlockSkillUseCase {
        return UnlockSkillUseCase(repository)
    }

    fun provideAddXpUseCase(repository: ChildProfileRepository): AddXpUseCase {
        return AddXpUseCase(repository)
    }

    // ═══════════════════════════════════════════════════════════════
    // ASSESSMENT ENGINE
    // ═══════════════════════════════════════════════════════════════

    fun provideAssessmentEngine(assessmentId: String): AssessmentEngine {
        // Create assessment based on ID
        val assessment = AssessmentFactory.createScienceAssessment(10) // Default
        return AssessmentEngine(
            assessment = assessment,
            userProgress = emptyMap()
        )
    }

    // ═══════════════════════════════════════════════════════════════
    // NOTIFICATIONS
    // ═══════════════════════════════════════════════════════════════

    fun provideNotificationHelper(context: Context): com.LBs.EEDA.util.NotificationHelper {
        return com.LBs.EEDA.util.NotificationHelper(context)
    }

    // ═══════════════════════════════════════════════════════════════
    // ANALYTICS
    // ═══════════════════════════════════════════════════════════════

    fun provideAnalyticsEngine(context: Context): com.LBs.EEDA.domain.model.analytics.AnalyticsEngine {
        val analytics = com.LBs.EEDA.domain.model.analytics.AnalyticsFactory.createInitialAnalytics("user_default")
        return com.LBs.EEDA.domain.model.analytics.AnalyticsEngine(analytics)
    }

    // ═══════════════════════════════════════════════════════════════
    // SECURITY
    // ═══════════════════════════════════════════════════════════════

    fun provideSecurityUtils(): com.LBs.EEDA.util.SecurityUtils {
        return com.LBs.EEDA.util.SecurityUtils
    }
}

// ═══════════════════════════════════════════════════════════════
// USE CASE CLASSES
// ═══════════════════════════════════════════════════════════════

class GetProfileUseCase(private val repository: ChildProfileRepository) {
    operator fun invoke() = repository.getProfile()
}

class SaveProfileUseCase(private val repository: ChildProfileRepository) {
    suspend operator fun invoke(profile: com.LBs.EEDA.domain.model.ChildProfile) =
        repository.saveProfile(profile)
}

class CompleteLessonUseCase(private val repository: ChildProfileRepository) {
    suspend operator fun invoke(lessonId: String) {
        repository.getProfile().collect { result ->
            result.onSuccess { profile ->
                profile?.let {
                    val updated = it.completeLesson(lessonId)
                    repository.saveProfile(updated)
                }
            }
        }
    }
}

class UnlockSkillUseCase(private val repository: ChildProfileRepository) {
    suspend operator fun invoke(skillId: String) {
        repository.getProfile().collect { result ->
            result.onSuccess { profile ->
                profile?.let {
                    val updated = it.completeSkill(skillId)
                    repository.saveProfile(updated)
                }
            }
        }
    }
}

class AddXpUseCase(private val repository: ChildProfileRepository) {
    suspend operator fun invoke(amount: Long) {
        repository.getProfile().collect { result ->
            result.onSuccess { profile ->
                profile?.let {
                    val updated = it.addXp(amount)
                    repository.saveProfile(updated)
                }
            }
        }
    }
}
