package com.LBs.EEDA.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.usecase.educational.AdaptiveQuestion
import com.LBs.EEDA.domain.usecase.educational.AnswerFeedback
import com.LBs.EEDA.domain.usecase.educational.QuestionType
import com.LBs.EEDA.ui.components.AnimatedLinearProgress
import com.LBs.EEDA.ui.components.LoadingSpinner
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onNavigateBack: () -> Unit,
    onQuizComplete: (score: Int, xpEarned: Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = EedaAdaptiveTheme.colors

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Evaluación", fontWeight = FontWeight.Bold)
                        Text(
                            "Pregunta ${uiState.currentQuestionIndex + 1} de ${uiState.totalQuestions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                ),
                actions = {
                    // Timer
                    Surface(
                        color = colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = formatTime(uiState.timeRemainingSeconds),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (uiState.timeRemainingSeconds < 60) colors.error else colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingSpinner()
                }
            }

            uiState.isComplete -> {
                QuizResultsScreen(
                    score = uiState.score,
                    totalQuestions = uiState.totalQuestions,
                    correctAnswers = uiState.correctAnswers,
                    xpEarned = uiState.xpEarned,
                    grade = uiState.grade,
                    onFinish = { onQuizComplete(uiState.score, uiState.xpEarned) }
                )
            }

            uiState.currentQuestion != null -> {
                QuestionContent(
                    question = uiState.currentQuestion!!,
                    selectedAnswers = uiState.selectedAnswers,
                    feedback = uiState.feedback,
                    onAnswerSelected = { viewModel.selectAnswer(it) },
                    onSubmit = { viewModel.submitAnswer() },
                    onNext = { viewModel.nextQuestion() },
                    onUseHint = { viewModel.useHint() },
                    hintsUsed = uiState.hintsUsed,
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                ErrorState(
                    message = "No se pudo cargar la evaluación",
                    onRetry = { viewModel.loadQuiz() }
                )
            }
        }

        // Progress indicator at bottom
        if (!uiState.isComplete && !uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                AnimatedLinearProgress(
                    progress = (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions,
                    color = colors.primary,
                    trackColor = colors.primary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    question: AdaptiveQuestion,
    selectedAnswers: List<String>,
    feedback: AnswerFeedback?,
    onAnswerSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onUseHint: () -> Unit,
    hintsUsed: Int,
    modifier: Modifier = Modifier
) {
    val colors = EedaAdaptiveTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Question stem
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = question.stem,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface
                )

                question.scenario?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = colors.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            color = colors.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hint button
        if (hintsUsed == 0) {
            TextButton(
                onClick = onUseHint,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Rounded.Lightbulb, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Usar pista")
            }
        }

        // Display hint if used
        if (hintsUsed > 0) {
            AnimatedVisibility(visible = true) {
                Surface(
                    color = Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFFA000)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = question.hint.text,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Answer options
        when (question.type) {
            QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE -> {
                SingleChoiceOptions(
                    options = question.options,
                    selectedId = selectedAnswers.firstOrNull(),
                    correctId = if (feedback != null) question.correctAnswer.optionIds.first() else null,
                    onSelect = { onAnswerSelected(it) },
                    enabled = feedback == null
                )
            }

            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceOptions(
                    options = question.options,
                    selectedIds = selectedAnswers,
                    correctIds = if (feedback != null) question.correctAnswer.optionIds else null,
                    onToggle = { onAnswerSelected(it) },
                    enabled = feedback == null
                )
            }

            QuestionType.FILL_IN_BLANK -> {
                FillInBlankAnswer(
                    answer = selectedAnswers.firstOrNull() ?: "",
                    onAnswerChange = { onAnswerSelected(it) },
                    correctAnswer = if (feedback != null) question.correctAnswer.textAnswer else null,
                    enabled = feedback == null
                )
            }

            else -> {
                // Default to single choice
                SingleChoiceOptions(
                    options = question.options,
                    selectedId = selectedAnswers.firstOrNull(),
                    correctId = if (feedback != null) question.correctAnswer.optionIds.first() else null,
                    onSelect = { onAnswerSelected(it) },
                    enabled = feedback == null
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Submit / Next button
        if (feedback == null) {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswers.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verificar Respuesta")
            }
        } else {
            val isCorrect = feedback.isCorrect
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect) Color(0xFF4CAF50) else colors.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isCorrect) "¡Correcto! Continuar" else "Siguiente")
            }

            if (!isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Explicación:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                        Text(
                            text = feedback.explanation.correct,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleChoiceOptions(
    options: List<com.LBs.EEDA.domain.usecase.educational.QuestionOption>,
    selectedId: String?,
    correctId: String?,
    onSelect: (String) -> Unit,
    enabled: Boolean
) {
    val colors = EedaAdaptiveTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option.id == selectedId
            val isCorrect = option.id == correctId
            val showResult = correctId != null

            val backgroundColor = when {
                showResult && isCorrect -> Color(0xFFE8F5E9)
                showResult && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                isSelected -> colors.primary.copy(alpha = 0.1f)
                else -> colors.surface
            }

            val borderColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                isSelected -> colors.primary
                else -> colors.surfaceVariant
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onSelect(option.id) },
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Radio indicator
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(
                                2.dp,
                                if (isSelected) colors.primary else colors.onSurface.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .background(
                                if (isSelected) colors.primary else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = option.text,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp
                    )

                    if (showResult) {
                        when {
                            isCorrect -> Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Correcto",
                                tint = Color(0xFF4CAF50)
                            )
                            isSelected && !isCorrect -> Icon(
                                imageVector = Icons.Rounded.Cancel,
                                contentDescription = "Incorrecto",
                                tint = Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultipleChoiceOptions(
    options: List<com.LBs.EEDA.domain.usecase.educational.QuestionOption>,
    selectedIds: List<String>,
    correctIds: List<String>?,
    onToggle: (String) -> Unit,
    enabled: Boolean
) {
    val colors = EedaAdaptiveTheme.colors

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val isSelected = option.id in selectedIds
            val isCorrect = correctIds?.contains(option.id) == true
            val showResult = correctIds != null

            val backgroundColor = when {
                showResult && isCorrect -> Color(0xFFE8F5E9)
                showResult && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                isSelected -> colors.primary.copy(alpha = 0.1f)
                else -> colors.surface
            }

            val borderColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                isSelected -> colors.primary
                else -> colors.surfaceVariant
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onToggle(option.id) },
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox indicator
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(
                                2.dp,
                                if (isSelected) colors.primary else colors.onSurface.copy(alpha = 0.3f),
                                RoundedCornerShape(4.dp)
                            )
                            .background(
                                if (isSelected) colors.primary else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = option.text,
                        modifier = Modifier.weight(1f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FillInBlankAnswer(
    answer: String,
    onAnswerChange: (String) -> Unit,
    correctAnswer: String?,
    enabled: Boolean
) {
    val colors = EedaAdaptiveTheme.colors

    OutlinedTextField(
        value = answer,
        onValueChange = onAnswerChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text("Tu respuesta") },
        isError = correctAnswer != null && answer != correctAnswer,
        colors = if (correctAnswer != null && answer == correctAnswer) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color(0xFF4CAF50)
            )
        } else {
            OutlinedTextFieldDefaults.colors()
        }
    )

    if (correctAnswer != null && answer != correctAnswer) {
        Text(
            text = "Respuesta correcta: $correctAnswer",
            color = Color(0xFF4CAF50),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun QuizResultsScreen(
    score: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    xpEarned: Long,
    grade: com.LBs.EEDA.domain.usecase.educational.Grade,
    onFinish: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val isPassed = score >= 70

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Result icon
        Text(
            text = if (isPassed) "🎉" else "📚",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isPassed) "¡Evaluación Completada!" else "Sigue practicando",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Score circle
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    if (isPassed) Color(0xFF4CAF50).copy(alpha = 0.2f)
                    else Color(0xFFFF9800).copy(alpha = 0.2f)
                )
                .border(
                    4.dp,
                    if (isPassed) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$score%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPassed) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
                Text(
                    text = "$correctAnswers/$totalQuestions",
                    fontSize = 16.sp,
                    color = colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grade badge
        Surface(
            color = if (isPassed) Color(0xFF4CAF50).copy(alpha = 0.1f)
            else Color(0xFFFF9800).copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "Calificación: ${grade.name}",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = if (isPassed) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // XP earned
        Surface(
            color = colors.primary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Bolt, null, tint = colors.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+$xpEarned XP ganados",
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Finalizar")
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.Error, null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}
