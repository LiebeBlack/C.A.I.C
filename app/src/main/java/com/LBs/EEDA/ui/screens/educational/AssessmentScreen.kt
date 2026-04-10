package com.LBs.EEDA.ui.screens.educational

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.data.repository.AssessmentRepositoryImpl
import com.LBs.EEDA.domain.usecase.educational.Question
import com.LBs.EEDA.domain.usecase.educational.QuestionType
import com.LBs.EEDA.domain.usecase.educational.AdaptiveAssessment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de evaluación.
 */
// Modelo de Evaluación Visual Dinámica
data class SimpleAssessment(
    val id: String,
    val userId: String,
    val questions: List<Question>,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDurationMinutes: Int = 10,
    val conceptsAssessed: List<String> = emptyList(),
    val difficulty: com.LBs.EEDA.domain.model.educational.ContentDifficulty = com.LBs.EEDA.domain.model.educational.ContentDifficulty.BEGINNER
)

class AssessmentViewModel(
    private val assessmentRepository: AssessmentRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState

    fun loadAssessment(assessmentId: String) {
        viewModelScope.launch {
            _uiState.value = AssessmentUiState(isLoading = true)
            
            // Simulación de evaluación
            val sampleQuestions = listOf(
                Question(
                    id = "q1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "¿Qué es una contraseña segura?",
                    options = listOf(
                        "123456",
                        "Mi nombre + fecha",
                        "Mezcla de letras, números y símbolos",
                        "password"
                    ),
                    correctOptionIndex = 2,
                    explanation = "Una contraseña segura debe contener variedad de caracteres."
                ),
                Question(
                    id = "q2",
                    type = QuestionType.TRUE_FALSE,
                    questionText = "El cifrado de César es un método moderno de seguridad.",
                    correctAnswerBoolean = false,
                    explanation = "El cifrado de César es antiguo y no es seguro para uso moderno."
                ),
                Question(
                    id = "q3",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "¿Qué hace un firewall?",
                    options = listOf(
                        "Acelera la conexión",
                        "Filtra tráfico no autorizado",
                        "Almacena archivos",
                        "Crea contraseñas"
                    ),
                    correctOptionIndex = 1,
                    explanation = "El firewall filtra el tráfico para proteger la red."
                )
            )
            
            val assessment = SimpleAssessment(
                id = assessmentId,
                userId = "user_001",
                questions = sampleQuestions,
                createdAt = System.currentTimeMillis(),
                estimatedDurationMinutes = 10,
                conceptsAssessed = listOf("password_security", "encryption", "network"),
                difficulty = com.LBs.EEDA.domain.model.educational.ContentDifficulty.INTERMEDIATE
            )
            
            _uiState.value = AssessmentUiState(
                assessment = assessment,
                currentQuestion = assessment.questions.firstOrNull()
            )
        }
    }

    fun answerQuestion(questionId: String, answer: Any) {
        val currentState = _uiState.value
        val question = currentState.currentQuestion ?: return
        
        val isCorrect = when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> answer == question.correctOptionIndex
            QuestionType.TRUE_FALSE -> answer == question.correctAnswerBoolean
            QuestionType.TEXT_INPUT -> answer.toString().equals(question.acceptedAnswers?.firstOrNull(), ignoreCase = true)
            QuestionType.DRAG_DROP -> if (answer is List<*>) answer == question.options else answer.toString().isNotBlank()
        }
        
        viewModelScope.launch {
            assessmentRepository.saveResponse(
                assessmentId = currentState.assessment?.id ?: "",
                questionId = questionId,
                answer = answer,
                isCorrect = isCorrect
            )
        }
        
        val updatedAnswers = currentState.userAnswers + (questionId to answer)
        val updatedCorrect = if (isCorrect) currentState.correctAnswers + 1 else currentState.correctAnswers
        
        val nextQuestionIndex = currentState.assessment?.questions?.indexOfFirst { it.id == questionId }?.plus(1) ?: 0
        val nextQuestion = currentState.assessment?.questions?.getOrNull(nextQuestionIndex)
        
        _uiState.value = currentState.copy(
            userAnswers = updatedAnswers,
            correctAnswers = updatedCorrect,
            currentQuestion = nextQuestion,
            showFeedback = true,
            lastAnswerCorrect = isCorrect,
            questionExplanation = question.explanation
        )
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            showFeedback = false,
            questionExplanation = null
        )
        
        if (currentState.currentQuestion == null) {
            _uiState.value = currentState.copy(
                isCompleted = true,
                showFeedback = false
            )
        }
    }

    fun finishAssessment() {
        _uiState.value = _uiState.value.copy(isCompleted = true)
    }
}

data class AssessmentUiState(
    val assessment: SimpleAssessment? = null,
    val currentQuestion: Question? = null,
    val userAnswers: Map<String, Any> = emptyMap(),
    val correctAnswers: Int = 0,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val showFeedback: Boolean = false,
    val lastAnswerCorrect: Boolean = false,
    val questionExplanation: String? = null,
    val error: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    assessmentId: String,
    viewModel: AssessmentViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(assessmentId) {
        viewModel.loadAssessment(assessmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.isCompleted -> {
                    AssessmentResult(
                        correctAnswers = uiState.correctAnswers,
                        totalQuestions = uiState.assessment?.questions?.size ?: 0,
                        onContinue = onNavigateBack
                    )
                }
                uiState.currentQuestion != null -> {
                    QuestionCard(
                        question = uiState.currentQuestion!!,
                        questionNumber = uiState.userAnswers.size + 1,
                        totalQuestions = uiState.assessment?.questions?.size ?: 0,
                        onAnswer = { answer ->
                            viewModel.answerQuestion(uiState.currentQuestion!!.id, answer)
                        },
                        showFeedback = uiState.showFeedback,
                        lastAnswerCorrect = uiState.lastAnswerCorrect,
                        explanation = uiState.questionExplanation,
                        onNext = { viewModel.nextQuestion() }
                    )
                }
                else -> {
                    Text(
                        text = "No hay preguntas disponibles",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionCard(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    onAnswer: (Any) -> Unit,
    showFeedback: Boolean,
    lastAnswerCorrect: Boolean,
    explanation: String?,
    onNext: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var selectedBoolean by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Progreso
        LinearProgressIndicator(
            progress = { questionNumber / totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Pregunta $questionNumber de $totalQuestions",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Pregunta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = question.questionText,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Opciones
        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                question.options?.forEachIndexed { index, option ->
                    OptionCard(
                        text = option,
                        isSelected = selectedOption == index,
                        onClick = { 
                            if (!showFeedback) {
                                selectedOption = index
                                onAnswer(index)
                            }
                        },
                        showFeedback = showFeedback,
                        isCorrect = index == question.correctOptionIndex
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            QuestionType.TRUE_FALSE -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BooleanOptionCard(
                        text = "VERDADERO",
                        isSelected = selectedBoolean == true,
                        onClick = { 
                            if (!showFeedback) {
                                selectedBoolean = true
                                onAnswer(true)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        showFeedback = showFeedback,
                        isCorrect = question.correctAnswerBoolean == true
                    )
                    BooleanOptionCard(
                        text = "FALSO",
                        isSelected = selectedBoolean == false,
                        onClick = { 
                            if (!showFeedback) {
                                selectedBoolean = false
                                onAnswer(false)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        showFeedback = showFeedback,
                        isCorrect = question.correctAnswerBoolean == false
                    )
                }
            }
            else -> {
                // Para otros tipos de preguntas
                Text(
                    text = "Tipo de pregunta no soportado en esta versión",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Feedback
        if (showFeedback) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (lastAnswerCorrect) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (lastAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (lastAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lastAnswerCorrect) "¡Correcto!" else "Incorrecto",
                            fontWeight = FontWeight.Bold,
                            color = if (lastAnswerCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                    
                    explanation?.let { 
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onNext,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Continuar")
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    showFeedback: Boolean,
    isCorrect: Boolean
) {
    val backgroundColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        showFeedback && isSelected && !isCorrect -> Color(0xFFE57373).copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    val borderColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFE57373)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !showFeedback, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )
            
            when {
                showFeedback && isCorrect -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                }
                showFeedback && isSelected && !isCorrect -> {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color(0xFFE57373)
                    )
                }
                isSelected -> {
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun BooleanOptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFeedback: Boolean,
    isCorrect: Boolean
) {
    val backgroundColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.2f)
        showFeedback && isSelected && !isCorrect -> Color(0xFFE57373).copy(alpha = 0.2f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    
    Card(
        modifier = modifier
            .clickable(enabled = !showFeedback, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AssessmentResult(
    correctAnswers: Int,
    totalQuestions: Int,
    onContinue: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (percentage >= 60) Icons.Default.EmojiEvents else Icons.Default.School,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (percentage >= 60) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (percentage >= 60) "¡Felicidades!" else "Evaluación completada",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "$correctAnswers de $totalQuestions correctas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$percentage%",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                percentage >= 80 -> Color(0xFF4CAF50)
                percentage >= 60 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.error
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when {
                percentage >= 80 -> "¡Excelente trabajo!"
                percentage >= 60 -> "¡Bien hecho!"
                else -> "Sigue practicando"
            },
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Continuar")
        }
    }
}
