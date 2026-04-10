package com.LBs.EEDA.domain.model.educational.analytics

import com.LBs.EEDA.domain.model.educational.ContentDifficulty
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Sistema Avanzado de Análisis Previo y Auto-Evaluación EEDA 2026
 * Proporciona análisis predictivo, diagnóstico cognitivo y recomendaciones adaptativas
 */

@Serializable
data class PreAssessmentAnalysis(
    val userId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val cognitiveProfile: CognitiveProfile,
    val knowledgeGaps: List<KnowledgeGap>,
    val recommendedDifficulty: ContentDifficulty,
    val estimatedCompletionTime: Int,
    val confidenceScore: Double,
    val adaptivePath: List<LearningNode>,
    val prerequisiteConcepts: List<String>,
    val challengeAreas: List<String>,
    val strengthAreas: List<String>
)

@Serializable
data class CognitiveProfile(
    val processingSpeed: CognitiveLevel,
    val memoryCapacity: MemoryLevel,
    val logicalReasoning: ReasoningLevel,
    val attentionSpan: AttentionLevel,
    val learningStyle: LearningStyle,
    val previousPerformanceHistory: List<PerformanceRecord>,
    val averageResponseTime: Long,
    val errorPatternAnalysis: ErrorPatternProfile
)

@Serializable
data class PerformanceRecord(
    val conceptId: String,
    val attempts: Int,
    val successRate: Double,
    val averageTimeMs: Long,
    val lastAttempt: Long,
    val improvementTrend: TrendDirection
)

enum class TrendDirection { IMPROVING, STABLE, DECLINING, INCONSISTENT }

@Serializable
data class ErrorPatternProfile(
    val commonErrorTypes: List<ErrorTypeFrequency>,
    val conceptualMisunderstandings: List<String>,
    val carelessErrors: Double,
    val persistentMistakes: List<String>,
    val hintDependency: HintDependencyLevel
)

@Serializable
data class ErrorTypeFrequency(
    val errorType: String,
    val frequency: Double,
    val lastOccurrence: Long,
    val affectedConcepts: List<String>
)

enum class HintDependencyLevel { NONE, LOW, MODERATE, HIGH, CRITICAL }

enum class CognitiveLevel { BELOW_AVERAGE, AVERAGE, ABOVE_AVERAGE, SUPERIOR }
enum class MemoryLevel { LIMITED, SHORT_TERM, WORKING, LONG_TERM }
enum class ReasoningLevel { CONCRETE, TRANSITIONAL, ABSTRACT, FORMAL }
enum class AttentionLevel { SHORT, MODERATE, SUSTAINED, EXTENDED }
enum class LearningStyle { VISUAL, AUDITORY, KINESTHETIC, READING, MULTIMODAL }

@Serializable
data class KnowledgeGap(
    val conceptId: String,
    val severity: GapSeverity,
    val foundationalImpact: Boolean,
    val recommendedRemediation: String,
    val estimatedTimeToClose: Int,
    val relatedConcepts: List<String>
)

enum class GapSeverity { MINOR, MODERATE, SIGNIFICANT, CRITICAL }

@Serializable
data class LearningNode(
    val id: String,
    val conceptId: String,
    val difficulty: ContentDifficulty,
    val estimatedTime: Int,
    val prerequisites: List<String>,
    val unlocks: List<String>,
    val alternativePaths: List<String>,
    val assessmentType: AssessmentNodeType
)

enum class AssessmentNodeType {
    DIAGNOSTIC,
    FORMATIVE,
    SUMMATIVE,
    ADAPTIVE_DRILL,
    MASTERY_CHECK,
    CONCEPT_BRIDGING
}


/**
 * Motor de Análisis Previo de Evaluación
 * Analiza el historial del usuario y genera un perfil completo antes de la evaluación
 */
class PreAssessmentAnalyzer(
    private val json: Json
) {
    /**
     * Realiza análisis completo previo a la evaluación
     */
    fun analyzeBeforeAssessment(
        userId: String,
        targetConcepts: List<String>,
        historicalData: UserHistoricalData
    ): PreAssessmentAnalysis {
        val cognitiveProfile = buildCognitiveProfile(historicalData)
        val knowledgeGaps = identifyKnowledgeGaps(targetConcepts, historicalData)
        val recommendedDifficulty = calculateOptimalDifficulty(cognitiveProfile, knowledgeGaps)
        val adaptivePath = generateAdaptivePath(targetConcepts, knowledgeGaps, cognitiveProfile)
        
        return PreAssessmentAnalysis(
            userId = userId,
            cognitiveProfile = cognitiveProfile,
            knowledgeGaps = knowledgeGaps,
            recommendedDifficulty = recommendedDifficulty,
            estimatedCompletionTime = calculateEstimatedTime(adaptivePath, cognitiveProfile),
            confidenceScore = calculateConfidenceScore(cognitiveProfile, knowledgeGaps),
            adaptivePath = adaptivePath,
            prerequisiteConcepts = extractPrerequisites(knowledgeGaps),
            challengeAreas = identifyChallengeAreas(knowledgeGaps, historicalData),
            strengthAreas = identifyStrengthAreas(historicalData, targetConcepts)
        )
    }
    
    private fun buildCognitiveProfile(data: UserHistoricalData): CognitiveProfile {
        val responseTimes = data.attemptHistory.map { it.responseTimeMs }
        val averageResponseTime = if (responseTimes.isNotEmpty()) {
            responseTimes.average().toLong()
        } else 0L
        
        val errorPatterns = analyzeErrorPatterns(data.attemptHistory)
        val performanceHistory = calculatePerformanceHistory(data.attemptHistory)
        
        return CognitiveProfile(
            processingSpeed = categorizeProcessingSpeed(averageResponseTime),
            memoryCapacity = assessMemoryCapacity(data),
            logicalReasoning = assessLogicalReasoning(data),
            attentionSpan = assessAttentionSpan(data),
            learningStyle = detectLearningStyle(data),
            previousPerformanceHistory = performanceHistory,
            averageResponseTime = averageResponseTime,
            errorPatternAnalysis = errorPatterns
        )
    }
    
    private fun analyzeErrorPatterns(history: List<AttemptRecord>): ErrorPatternProfile {
        val errorsByType = history
            .flatMap { it.errors }
            .groupBy { it.errorType }
            .map { (type, errors) ->
                ErrorTypeFrequency(
                    errorType = type,
                    frequency = errors.size.toDouble() / history.size,
                    lastOccurrence = errors.maxOfOrNull { it.timestamp } ?: 0L,
                    affectedConcepts = errors.map { it.conceptId }.distinct()
                )
            }
            .sortedByDescending { it.frequency }
        
        val conceptualErrors = errorsByType
            .filter { it.errorType in listOf("CONCEPTUAL", "MISUNDERSTANDING", "WRONG_LOGIC") }
            .flatMap { it.affectedConcepts }
            .distinct()
        
        val carelessErrorRate = errorsByType
            .find { it.errorType == "CARELESS" }?.frequency ?: 0.0
        
        val persistentMistakes = errorsByType
            .filter { it.frequency > 0.3 && it.lastOccurrence > System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000 }
            .flatMap { it.affectedConcepts }
            .distinct()
        
        val hintUsage = history.map { it.hintsUsed }.average()
        val hintDependency = when {
            hintUsage > 0.8 -> HintDependencyLevel.CRITICAL
            hintUsage > 0.6 -> HintDependencyLevel.HIGH
            hintUsage > 0.3 -> HintDependencyLevel.MODERATE
            hintUsage > 0.1 -> HintDependencyLevel.LOW
            else -> HintDependencyLevel.NONE
        }
        
        return ErrorPatternProfile(
            commonErrorTypes = errorsByType.take(5),
            conceptualMisunderstandings = conceptualErrors,
            carelessErrors = carelessErrorRate,
            persistentMistakes = persistentMistakes,
            hintDependency = hintDependency
        )
    }
    
    private fun calculatePerformanceHistory(history: List<AttemptRecord>): List<PerformanceRecord> {
        return history
            .groupBy { it.conceptId }
            .map { (conceptId, attempts) ->
                val successRate = attempts.count { it.successful }.toDouble() / attempts.size
                val avgTime = attempts.map { it.responseTimeMs }.average().toLong()
                val trend = calculateTrend(attempts.map { it.successful })
                
                PerformanceRecord(
                    conceptId = conceptId,
                    attempts = attempts.size,
                    successRate = successRate,
                    averageTimeMs = avgTime,
                    lastAttempt = attempts.maxOfOrNull { it.timestamp } ?: 0L,
                    improvementTrend = trend
                )
            }
    }
    
    private fun calculateTrend(results: List<Boolean>): TrendDirection {
        if (results.size < 3) return TrendDirection.INCONSISTENT
        
        val recent = results.takeLast(3).count { it }
        val previous = results.dropLast(3).takeLast(3).count { it }
        
        return when {
            recent > previous -> TrendDirection.IMPROVING
            recent == previous -> TrendDirection.STABLE
            else -> TrendDirection.DECLINING
        }
    }
    
    private fun categorizeProcessingSpeed(avgTimeMs: Long): CognitiveLevel {
        return when {
            avgTimeMs < 3000 -> CognitiveLevel.SUPERIOR
            avgTimeMs < 5000 -> CognitiveLevel.ABOVE_AVERAGE
            avgTimeMs < 8000 -> CognitiveLevel.AVERAGE
            else -> CognitiveLevel.BELOW_AVERAGE
        }
    }
    
    private fun assessMemoryCapacity(data: UserHistoricalData): MemoryLevel {
        val retentionRate = data.attemptHistory
            .groupBy { it.conceptId }
            .map { (_, attempts) ->
                val first = attempts.firstOrNull()?.successful ?: false
                val last = attempts.lastOrNull()?.successful ?: false
                if (!first && last) 1.0 else 0.0
            }
            .average()
        
        return when {
            retentionRate > 0.8 -> MemoryLevel.LONG_TERM
            retentionRate > 0.5 -> MemoryLevel.WORKING
            retentionRate > 0.3 -> MemoryLevel.SHORT_TERM
            else -> MemoryLevel.LIMITED
        }
    }
    
    private fun assessLogicalReasoning(data: UserHistoricalData): ReasoningLevel {
        val complexSuccessRate = data.attemptHistory
            .filter { it.complexityLevel > 3 }
            .map { it.successful }
            .let { if (it.isNotEmpty()) it.count { it }.toDouble() / it.size else 0.0 }
        
        return when {
            complexSuccessRate > 0.8 -> ReasoningLevel.FORMAL
            complexSuccessRate > 0.6 -> ReasoningLevel.ABSTRACT
            complexSuccessRate > 0.4 -> ReasoningLevel.TRANSITIONAL
            else -> ReasoningLevel.CONCRETE
        }
    }
    
    private fun assessAttentionSpan(data: UserHistoricalData): AttentionLevel {
        val sessionDuration = data.attemptHistory
            .groupBy { it.sessionId }
            .map { (_, attempts) -> attempts.size }
            .average()
        
        return when {
            sessionDuration > 20 -> AttentionLevel.EXTENDED
            sessionDuration > 12 -> AttentionLevel.SUSTAINED
            sessionDuration > 6 -> AttentionLevel.MODERATE
            else -> AttentionLevel.SHORT
        }
    }
    
    private fun detectLearningStyle(data: UserHistoricalData): LearningStyle {
        // Simplificado - en producción usaría análisis más sofisticado
        val visualSuccess = data.attemptHistory
            .filter { it.contentType == "VISUAL" }
            .let { if (it.isNotEmpty()) it.count { it.successful }.toDouble() / it.size else 0.0 }
        
        val textSuccess = data.attemptHistory
            .filter { it.contentType == "TEXT" }
            .let { if (it.isNotEmpty()) it.count { it.successful }.toDouble() / it.size else 0.0 }
        
        val interactiveSuccess = data.attemptHistory
            .filter { it.contentType == "INTERACTIVE" }
            .let { if (it.isNotEmpty()) it.count { it.successful }.toDouble() / it.size else 0.0 }
        
        return when {
            visualSuccess > textSuccess && visualSuccess > interactiveSuccess -> LearningStyle.VISUAL
            textSuccess > visualSuccess && textSuccess > interactiveSuccess -> LearningStyle.READING
            interactiveSuccess > 0.6 -> LearningStyle.KINESTHETIC
            else -> LearningStyle.MULTIMODAL
        }
    }
    
    private fun identifyKnowledgeGaps(
        targetConcepts: List<String>,
        data: UserHistoricalData
    ): List<KnowledgeGap> {
        return targetConcepts.map { conceptId ->
            val attempts = data.attemptHistory.filter { it.conceptId == conceptId }
            val successRate = if (attempts.isNotEmpty()) {
                attempts.count { it.successful }.toDouble() / attempts.size
            } else 0.0
            
            val severity = when {
                successRate < 0.2 -> GapSeverity.CRITICAL
                successRate < 0.4 -> GapSeverity.SIGNIFICANT
                successRate < 0.6 -> GapSeverity.MODERATE
                else -> GapSeverity.MINOR
            }
            
            KnowledgeGap(
                conceptId = conceptId,
                severity = severity,
                foundationalImpact = isFoundational(conceptId),
                recommendedRemediation = generateRemediation(conceptId, severity),
                estimatedTimeToClose = estimateRemediationTime(severity, attempts.size),
                relatedConcepts = findRelatedConcepts(conceptId)
            )
        }.filter { it.severity != GapSeverity.MINOR || it.foundationalImpact }
    }
    
    private fun isFoundational(conceptId: String): Boolean {
        val foundationalConcepts = listOf(
            "password_security", "algorithm", "programming_logic",
            "encryption_basics", "network_fundamentals", "security_essentials"
        )
        return conceptId in foundationalConcepts
    }
    
    private fun generateRemediation(conceptId: String, severity: GapSeverity): String {
        return when (severity) {
            GapSeverity.CRITICAL -> "Repaso completo con tutoría personalizada y ejercicios de refuerzo intensivos"
            GapSeverity.SIGNIFICANT -> "Repaso guiado con ejemplos adicionales y práctica supervisada"
            GapSeverity.MODERATE -> "Práctica adicional con feedback inmediato"
            GapSeverity.MINOR -> "Refuerzo ocasional durante contenido avanzado"
        }
    }
    
    private fun estimateRemediationTime(severity: GapSeverity, previousAttempts: Int): Int {
        val baseTime = when (severity) {
            GapSeverity.CRITICAL -> 60
            GapSeverity.SIGNIFICANT -> 40
            GapSeverity.MODERATE -> 25
            GapSeverity.MINOR -> 10
        }
        return baseTime + (previousAttempts * 2)
    }
    
    private fun findRelatedConcepts(conceptId: String): List<String> {
        val conceptRelations = mapOf(
            "password_security" to listOf("encryption", "hashing", "authentication"),
            "encryption" to listOf("password_security", "cryptography", "network"),
            "algorithm" to listOf("programming_logic", "data_structures", "complexity"),
            "network" to listOf("firewall", "protocols", "security")
        )
        return conceptRelations[conceptId] ?: emptyList()
    }
    
    private fun calculateOptimalDifficulty(
        cognitiveProfile: CognitiveProfile,
        gaps: List<KnowledgeGap>
    ): ContentDifficulty {
        val criticalGaps = gaps.count { it.severity == GapSeverity.CRITICAL }
        val processingLevel = cognitiveProfile.processingSpeed
        
        return when {
            criticalGaps > 2 -> ContentDifficulty.BEGINNER
            criticalGaps > 0 -> ContentDifficulty.ELEMENTARY
            processingLevel == CognitiveLevel.BELOW_AVERAGE -> ContentDifficulty.ELEMENTARY
            processingLevel == CognitiveLevel.AVERAGE -> ContentDifficulty.INTERMEDIATE
            processingLevel == CognitiveLevel.ABOVE_AVERAGE -> ContentDifficulty.ADVANCED
            else -> ContentDifficulty.EXPERT
        }
    }
    
    private fun generateAdaptivePath(
        targetConcepts: List<String>,
        gaps: List<KnowledgeGap>,
        cognitiveProfile: CognitiveProfile
    ): List<LearningNode> {
        val sortedGaps = gaps.sortedByDescending { 
            when (it.severity) {
                GapSeverity.CRITICAL -> 4
                GapSeverity.SIGNIFICANT -> 3
                GapSeverity.MODERATE -> 2
                GapSeverity.MINOR -> 1
            }
        }
        
        return sortedGaps.mapIndexed { index, gap ->
            LearningNode(
                id = "node_${gap.conceptId}_$index",
                conceptId = gap.conceptId,
                difficulty = when (gap.severity) {
                    GapSeverity.CRITICAL -> ContentDifficulty.BEGINNER
                    GapSeverity.SIGNIFICANT -> ContentDifficulty.ELEMENTARY
                    GapSeverity.MODERATE -> ContentDifficulty.INTERMEDIATE
                    GapSeverity.MINOR -> ContentDifficulty.INTERMEDIATE
                },
                estimatedTime = gap.estimatedTimeToClose,
                prerequisites = gap.relatedConcepts.filter { related ->
                    gaps.any { it.conceptId == related }
                },
                unlocks = targetConcepts.filter { it != gap.conceptId },
                alternativePaths = findAlternativePaths(gap.conceptId),
                assessmentType = determineAssessmentType(gap.severity, cognitiveProfile)
            )
        }
    }
    
    private fun findAlternativePaths(conceptId: String): List<String> {
        return when (conceptId) {
            "password_security" -> listOf("biometric_auth", "two_factor", "security_questions")
            "encryption" -> listOf("encoding", "hashing", "obfuscation")
            "algorithm" -> listOf("pseudocode", "flowcharts", "visual_programming")
            else -> emptyList()
        }
    }
    
    private fun determineAssessmentType(
        severity: GapSeverity,
        profile: CognitiveProfile
    ): AssessmentNodeType {
        return when {
            severity == GapSeverity.CRITICAL -> AssessmentNodeType.DIAGNOSTIC
            profile.errorPatternAnalysis.hintDependency == HintDependencyLevel.HIGH -> 
                AssessmentNodeType.ADAPTIVE_DRILL
            profile.processingSpeed == CognitiveLevel.SUPERIOR -> AssessmentNodeType.MASTERY_CHECK
            else -> AssessmentNodeType.FORMATIVE
        }
    }
    
    private fun calculateEstimatedTime(nodes: List<LearningNode>, profile: CognitiveProfile): Int {
        val baseTime = nodes.sumOf { it.estimatedTime }
        val multiplier = when (profile.processingSpeed) {
            CognitiveLevel.SUPERIOR -> 0.7
            CognitiveLevel.ABOVE_AVERAGE -> 0.85
            CognitiveLevel.AVERAGE -> 1.0
            CognitiveLevel.BELOW_AVERAGE -> 1.3
        }
        return (baseTime * multiplier).toInt()
    }
    
    private fun calculateConfidenceScore(profile: CognitiveProfile, gaps: List<KnowledgeGap>): Double {
        val gapPenalty = gaps.sumOf { 
            when (it.severity) {
                GapSeverity.CRITICAL -> 0.25
                GapSeverity.SIGNIFICANT -> 0.15
                GapSeverity.MODERATE -> 0.08
                GapSeverity.MINOR -> 0.03
            }
        }
        
        val processingBonus = when (profile.processingSpeed) {
            CognitiveLevel.SUPERIOR -> 0.2
            CognitiveLevel.ABOVE_AVERAGE -> 0.1
            else -> 0.0
        }
        
        return (0.5 - gapPenalty + processingBonus).coerceIn(0.0, 1.0)
    }
    
    private fun extractPrerequisites(gaps: List<KnowledgeGap>): List<String> {
        return gaps
            .filter { it.foundationalImpact }
            .sortedByDescending { 
                when (it.severity) {
                    GapSeverity.CRITICAL -> 4
                    GapSeverity.SIGNIFICANT -> 3
                    GapSeverity.MODERATE -> 2
                    GapSeverity.MINOR -> 1
                }
            }
            .map { it.conceptId }
    }
    
    private fun identifyChallengeAreas(gaps: List<KnowledgeGap>, data: UserHistoricalData): List<String> {
        val persistentErrors = data.attemptHistory
            .groupBy { it.conceptId }
            .filter { (_, attempts) ->
                attempts.size > 3 && attempts.count { !it.successful }.toDouble() / attempts.size > 0.5
            }
            .map { it.key }
        
        return gaps
            .filter { it.severity in listOf(GapSeverity.CRITICAL, GapSeverity.SIGNIFICANT) }
            .map { it.conceptId }
            .plus(persistentErrors)
            .distinct()
    }
    
    private fun identifyStrengthAreas(data: UserHistoricalData, targetConcepts: List<String>): List<String> {
        return data.attemptHistory
            .groupBy { it.conceptId }
            .filter { (conceptId, attempts) ->
                conceptId in targetConcepts &&
                attempts.size >= 2 &&
                attempts.count { it.successful }.toDouble() / attempts.size > 0.8
            }
            .map { it.key }
    }
}

// Datos de entrada para el análisis
@Serializable
data class UserHistoricalData(
    val userId: String,
    val attemptHistory: List<AttemptRecord>,
    val lastAssessmentTimestamp: Long = 0,
    val totalStudyTimeMinutes: Int = 0,
    val preferredStudyTimes: List<Int> = emptyList()
)

@Serializable
data class AttemptRecord(
    val conceptId: String,
    val timestamp: Long,
    val successful: Boolean,
    val responseTimeMs: Long,
    val complexityLevel: Int,
    val contentType: String,
    val hintsUsed: Int,
    val sessionId: String,
    val errors: List<ErrorRecord>
)

@Serializable
data class ErrorRecord(
    val errorType: String,
    val conceptId: String,
    val timestamp: Long,
    val userInput: String,
    val expectedPattern: String
)
