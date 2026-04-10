package com.LBs.EEDA.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.LBs.EEDA.domain.usecase.educational.AssessmentRepository
import com.LBs.EEDA.domain.usecase.educational.AdaptiveAssessment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Implementación robusta del repositorio de evaluaciones usando SharedPreferences.
 * Correcciones de violaciones de null safety y manejo de errores.
 */
class AssessmentRepositoryImpl(
    context: Context,
    private val json: Json
) : AssessmentRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("assessments", Context.MODE_PRIVATE)

    override suspend fun getAssessment(id: String): AdaptiveAssessment? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val assessmentsJson = prefs.getString("assessments_list", null)
                    ?: return@withContext null
                
                if (assessmentsJson.isBlank() || assessmentsJson == "null") {
                    return@withContext null
                }
                
                val assessments = json.decodeFromString<List<AdaptiveAssessment>>(assessmentsJson)
                assessments.find { it.id == id }
            }.getOrNull()
        }
    }

    override suspend fun saveAssessment(assessment: AdaptiveAssessment) {
        withContext(Dispatchers.IO) {
            runCatching {
                val currentJson = prefs.getString("assessments_list", null) ?: "[]"
                
                if (currentJson.isBlank() || currentJson == "null") {
                    prefs.edit { putString("assessments_list", json.encodeToString(listOf(assessment))) }
                    return@withContext
                }
                
                val assessments = json.decodeFromString<MutableList<AdaptiveAssessment>>(currentJson)
                
                val index = assessments.indexOfFirst { it.id == assessment.id }
                if (index >= 0) {
                    assessments[index] = assessment
                } else {
                    assessments.add(assessment)
                }
                
                prefs.edit { putString("assessments_list", json.encodeToString(assessments)) }
            }.onFailure {
                // Log error and try to recover by saving only the new assessment
                prefs.edit { 
                    putString("assessments_list", json.encodeToString(listOf(assessment)))
                }
            }
        }
    }

    override suspend fun saveResponse(
        assessmentId: String,
        questionId: String,
        answer: Any,
        isCorrect: Boolean
    ) {
        withContext(Dispatchers.IO) {
            runCatching {
                val currentJson = prefs.getString("responses_list", null) ?: "[]"
                
                if (currentJson.isBlank() || currentJson == "null") {
                    val newResponse = ResponseRecord(
                        timestamp = System.currentTimeMillis(),
                        assessmentId = assessmentId,
                        questionId = questionId,
                        answer = answer.toString(),
                        isCorrect = isCorrect
                    )
                    prefs.edit { putString("responses_list", json.encodeToString(listOf(newResponse))) }
                    updateUserStats(isCorrect)
                    return@withContext
                }
                
                val responses = json.decodeFromString<MutableList<ResponseRecord>>(currentJson)
                
                responses.add(ResponseRecord(
                    timestamp = System.currentTimeMillis(),
                    assessmentId = assessmentId,
                    questionId = questionId,
                    answer = answer.toString(),
                    isCorrect = isCorrect
                ))
                
                prefs.edit { putString("responses_list", json.encodeToString(responses)) }
                updateUserStats(isCorrect)
            }.onFailure {
                // Silent failure - don't crash the app
            }
        }
    }

    suspend fun getUserStats(): UserAssessmentStats {
        return withContext(Dispatchers.IO) {
            runCatching {
                val statsJson = prefs.getString("user_stats", null)
                if (statsJson.isNullOrBlank() || statsJson == "null") {
                    return@withContext UserAssessmentStats()
                }
                json.decodeFromString<UserAssessmentStats>(statsJson)
            }.getOrDefault(UserAssessmentStats())
        }
    }

    private fun updateUserStats(isCorrect: Boolean) {
        runCatching {
            val statsJson = prefs.getString("user_stats", null)
            val stats = if (!statsJson.isNullOrBlank() && statsJson != "null") {
                json.decodeFromString<UserAssessmentStats>(statsJson)
            } else {
                UserAssessmentStats()
            }
            
            val updatedStats = stats.copy(
                totalQuestions = stats.totalQuestions + 1,
                correctAnswers = stats.correctAnswers + if (isCorrect) 1 else 0,
                streakCurrent = if (isCorrect) stats.streakCurrent + 1 else 0,
                streakMax = kotlin.math.max(stats.streakMax, if (isCorrect) stats.streakCurrent + 1 else stats.streakMax),
                lastUpdated = System.currentTimeMillis()
            )
            
            prefs.edit { putString("user_stats", json.encodeToString(updatedStats)) }
        }
    }

    override suspend fun getResponsesForAssessment(assessmentId: String): List<ResponseRecord> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val responsesJson = prefs.getString("responses_list", null)
                    ?: return@withContext emptyList()
                
                if (responsesJson.isBlank() || responsesJson == "null") {
                    return@withContext emptyList()
                }
                
                val allResponses = json.decodeFromString<List<ResponseRecord>>(responsesJson)
                allResponses.filter { it.assessmentId == assessmentId }
            }.getOrDefault(emptyList())
        }
    }
}

@kotlinx.serialization.Serializable
data class ResponseRecord(
    val timestamp: Long,
    val assessmentId: String,
    val questionId: String,
    val answer: String,
    val isCorrect: Boolean
)

@kotlinx.serialization.Serializable
data class UserAssessmentStats(
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val streakCurrent: Int = 0,
    val streakMax: Int = 0,
    val lastUpdated: Long = 0
) {
    val accuracyPercentage: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0
}
