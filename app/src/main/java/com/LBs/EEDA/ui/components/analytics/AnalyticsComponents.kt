package com.LBs.EEDA.ui.components.analytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.educational.analytics.*

/**
 * Componentes UI para el Sistema de Análisis Previo y Auto-Evaluación EEDA 2026
 * Desarrollado por Yoangel Gómez con tecnología de LB's
 */

/**
 * Tarjeta de perfil cognitivo del usuario
 */
@Composable
fun CognitiveProfileCard(
    profile: CognitiveProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Perfil Cognitivo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Métricas cognitivas
            CognitiveMetricRow(
                icon = Icons.Default.Speed,
                label = "Velocidad de Procesamiento",
                value = profile.processingSpeed.name.replace("_", " "),
                color = getCognitiveColor(profile.processingSpeed)
            )
            
            CognitiveMetricRow(
                icon = Icons.Default.Memory,
                label = "Capacidad de Memoria",
                value = profile.memoryCapacity.name.replace("_", " "),
                color = getMemoryColor(profile.memoryCapacity)
            )
            
            CognitiveMetricRow(
                icon = Icons.Default.Lightbulb,
                label = "Razonamiento Lógico",
                value = profile.logicalReasoning.name.replace("_", " "),
                color = getReasoningColor(profile.logicalReasoning)
            )
            
            CognitiveMetricRow(
                icon = Icons.Default.Timer,
                label = "Tiempo Promedio de Respuesta",
                value = "${profile.averageResponseTime / 1000}s",
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Estilo de aprendizaje
            LearningStyleChip(profile.learningStyle)
        }
    }
}

@Composable
private fun CognitiveMetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
private fun LearningStyleChip(style: LearningStyle) {
    val (icon, label, color) = when (style) {
        LearningStyle.VISUAL -> Triple(Icons.Default.Visibility, "Visual", Color(0xFF2196F3))
        LearningStyle.AUDITORY -> Triple(Icons.Default.Hearing, "Auditivo", Color(0xFF9C27B0))
        LearningStyle.KINESTHETIC -> Triple(Icons.Default.SportsHandball, "Kinestésico", Color(0xFF4CAF50))
        LearningStyle.READING -> Triple(Icons.Default.Book, "Lectura", Color(0xFFFF9800))
        LearningStyle.MULTIMODAL -> Triple(Icons.Default.Dashboard, "Multimodal", Color(0xFFE91E63))
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Estilo: $label",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

/**
 * Visualización de brechas de conocimiento
 */
@Composable
fun KnowledgeGapsPanel(
    gaps: List<KnowledgeGap>,
    onGapSelected: (KnowledgeGap) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Áreas de Mejora Identificadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (gaps.isEmpty()) {
                Text(
                    text = "¡Excelente! No se detectaron brechas significativas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                gaps.forEach { gap ->
                    KnowledgeGapItem(gap, onGapSelected)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun KnowledgeGapItem(
    gap: KnowledgeGap,
    onClick: (KnowledgeGap) -> Unit
) {
    val severityColor = when (gap.severity) {
        GapSeverity.CRITICAL -> Color(0xFFD32F2F)
        GapSeverity.SIGNIFICANT -> Color(0xFFFF9800)
        GapSeverity.MODERATE -> Color(0xFFFFC107)
        GapSeverity.MINOR -> Color(0xFF4CAF50)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(gap) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(severityColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = gap.conceptId.replace("_", " ").capitalize(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                GapSeverityChip(gap.severity)
            }
            
            if (gap.foundationalImpact) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⚠️ Concepto fundamental - impacta en otros temas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gap.recommendedRemediation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GapSeverityChip(severity: GapSeverity) {
    val (color, text) = when (severity) {
        GapSeverity.CRITICAL -> Pair(Color(0xFFD32F2F), "Crítica")
        GapSeverity.SIGNIFICANT -> Pair(Color(0xFFFF9800), "Significativa")
        GapSeverity.MODERATE -> Pair(Color(0xFFFFC107), "Moderada")
        GapSeverity.MINOR -> Pair(Color(0xFF4CAF50), "Menor")
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Panel de resultados de auto-evaluación
 */
@Composable
fun AssessmentResultsPanel(
    results: AssessmentResults,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = results.percentage.toFloat(),
        animationSpec = tween(durationMillis = 1000)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono y título
            val (icon, title, color) = when {
                results.percentage >= 80 -> Triple(Icons.Default.EmojiEvents, "¡Excelente Desempeño!", Color(0xFF4CAF50))
                results.percentage >= 60 -> Triple(Icons.Default.Star, "¡Buen Trabajo!", MaterialTheme.colorScheme.primary)
                results.percentage >= 40 -> Triple(Icons.AutoMirrored.Filled.TrendingUp, "En Progreso", Color(0xFFFF9800))
                else -> Triple(Icons.Default.School, "Sigue Aprendiendo", Color(0xFF2196F3))
            }
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = color
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = color
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Score circular
            CircularScoreDisplay(
                percentage = animatedPercentage,
                correct = results.correctAnswers,
                total = results.totalQuestions,
                color = color
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Tiempo
            InfoChip(
                icon = Icons.Default.Schedule,
                text = "${results.timeSpentMinutes} minutos"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Rendimiento por concepto
            Text(
                text = "Rendimiento por Concepto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            results.conceptPerformance.forEach { performance ->
                ConceptPerformanceBar(performance)
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recomendaciones
            if (results.recommendations.isNotEmpty()) {
                Text(
                    text = "Recomendaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                results.recommendations.forEach { recommendation ->
                    RecommendationItem(recommendation)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text("Continuar")
            }
        }
    }
}

@Composable
private fun CircularScoreDisplay(
    percentage: Float,
    correct: Int,
    total: Int,
    color: Color
) {
    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Fondo circular
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = color.copy(alpha = 0.2f),
            strokeWidth = 12.dp
        )
        
        // Progreso
        CircularProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 12.dp
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = "$correct / $total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConceptPerformanceBar(performance: ConceptPerformance) {
    val percentage = performance.correct.toFloat() / performance.total.coerceAtLeast(1)
    val color = when {
        percentage >= 0.8 -> Color(0xFF4CAF50)
        percentage >= 0.6 -> Color(0xFFFFC107)
        else -> Color(0xFFFF9800)
    }
    
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = performance.conceptId.replace("_", " ").capitalize(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${performance.correct}/${performance.total}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun RecommendationItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

// Funciones auxiliares de color
private fun getCognitiveColor(level: CognitiveLevel): Color = when (level) {
    CognitiveLevel.BELOW_AVERAGE -> Color(0xFFFF9800)
    CognitiveLevel.AVERAGE -> Color(0xFF2196F3)
    CognitiveLevel.ABOVE_AVERAGE -> Color(0xFF4CAF50)
    CognitiveLevel.SUPERIOR -> Color(0xFF9C27B0)
}

private fun getMemoryColor(level: MemoryLevel): Color = when (level) {
    MemoryLevel.LIMITED -> Color(0xFFFF9800)
    MemoryLevel.SHORT_TERM -> Color(0xFFFFC107)
    MemoryLevel.WORKING -> Color(0xFF2196F3)
    MemoryLevel.LONG_TERM -> Color(0xFF4CAF50)
}

private fun getReasoningColor(level: ReasoningLevel): Color = when (level) {
    ReasoningLevel.CONCRETE -> Color(0xFFFF9800)
    ReasoningLevel.TRANSITIONAL -> Color(0xFFFFC107)
    ReasoningLevel.ABSTRACT -> Color(0xFF2196F3)
    ReasoningLevel.FORMAL -> Color(0xFF4CAF50)
}

private fun String.capitalize(): String =
    this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
