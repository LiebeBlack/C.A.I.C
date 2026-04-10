package com.LBs.EEDA.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.domain.usecase.educational.*
import com.LBs.EEDA.util.DateUtils
import com.LBs.EEDA.util.Logger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class QuizUiState(
    val questions: List<AdaptiveQuestion> = emptyList(),
    val currentQuestion: AdaptiveQuestion? = null,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedAnswers: List<String> = emptyList(),
    val feedback: AnswerFeedback? = null,
    val hintsUsed: Int = 0,
    val score: Int = 0,
    val correctAnswers: Int = 0,
    val xpEarned: Long = 0,
    val grade: Grade = Grade.F,
    val timeRemainingSeconds: Int = 0,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null
)

class QuizViewModel(
    private val assessmentEngine: AssessmentEngine,
    private val repository: ChildProfileRepository,
    private val assessmentId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var currentSession: AssessmentSession? = null
    private val userAnswers = mutableMapOf<String, UserAnswer>()

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                currentSession = assessmentEngine.startSession("user_${System.currentTimeMillis()}")
                val firstQuestion = currentSession?.let { assessmentEngine.getNextQuestion(it) }
                val timeLimit = assessmentEngine.assessment.timeLimitMinutes?.times(60) ?: 0

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentQuestion = firstQuestion,
                        currentQuestionIndex = 0,
                        totalQuestions = assessmentEngine.assessment.config.questionsCount,
                        timeRemainingSeconds = timeLimit,
                        selectedAnswers = emptyList(),
                        feedback = null,
                        hintsUsed = 0
                    )
                }

                if (timeLimit > 0) {
                    startTimer(timeLimit)
                }
            } catch (e: Exception) {
                Logger.e(Logger.Category.DATA, "Error loading quiz", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar la evaluación")
                }
            }
        }
    }

    private fun startTimer(totalSeconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0 && !uiState.value.isComplete) {
                _uiState.update { it.copy(timeRemainingSeconds = remaining) }
                delay(1000)
                remaining--
            }
            if (remaining <= 0 && !uiState.value.isComplete) {
                completeQuiz()
            }
        }
    }

    fun selectAnswer(answerId: String) {
        if (uiState.value.feedback != null) return // Already submitted

        val question = uiState.value.currentQuestion ?: return

        val newAnswers = when (question.type) {
            QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE -> listOf(answerId)
            QuestionType.MULTIPLE_CHOICE -> {
                val current = uiState.value.selectedAnswers
                if (answerId in current) current - answerId else current + answerId
            }
            QuestionType.FILL_IN_BLANK -> listOf(answerId)
            else -> listOf(answerId)
        }

        _uiState.update { it.copy(selectedAnswers = newAnswers) }
    }

    fun submitAnswer() {
        val question = uiState.value.currentQuestion ?: return
        val answers = uiState.value.selectedAnswers

        if (answers.isEmpty()) return

        viewModelScope.launch {
            val userAnswer = createUserAnswer(question, answers)
            val session = currentSession ?: return@launch
            val feedback = assessmentEngine.submitAnswer(
                session,
                question,
                userAnswer
            )

            userAnswers[question.id] = userAnswer

            val isCorrect = feedback.isCorrect
            val currentCorrect = uiState.value.correctAnswers + if (isCorrect) 1 else 0

            _uiState.update {
                it.copy(
                    feedback = feedback,
                    correctAnswers = currentCorrect
                )
            }

            // Update profile XP
            if (feedback.xpEarned > 0) {
                repository.getProfile().firstOrNull()?.onSuccess { profile ->
                    profile?.let {
                        val updated = it.addXp(feedback.xpEarned.toLong())
                        repository.saveProfile(updated)
                    }
                }
            }
        }
    }

    private fun createUserAnswer(question: AdaptiveQuestion, answers: List<String>): UserAnswer {
        return when (question.type) {
            QuestionType.SINGLE_CHOICE, QuestionType.TRUE_FALSE, QuestionType.MULTIPLE_CHOICE ->
                UserAnswer(
                    questionId = question.id,
                    selectedOptions = answers,
                    timestamp = DateUtils.getCurrentTimestamp()
                )
            QuestionType.FILL_IN_BLANK ->
                UserAnswer(
                    questionId = question.id,
                    textAnswer = answers.firstOrNull() ?: "",
                    timestamp = DateUtils.getCurrentTimestamp()
                )
            else ->
                UserAnswer(
                    questionId = question.id,
                    selectedOptions = answers,
                    timestamp = DateUtils.getCurrentTimestamp()
                )
        }
    }

    fun useHint() {
        val currentHints = uiState.value.hintsUsed

        if (currentHints == 0) {
            _uiState.update { it.copy(hintsUsed = 1) }
        }
    }

    fun nextQuestion() {
        viewModelScope.launch {
            val session = currentSession ?: return@launch
            val nextQuestion = assessmentEngine.getNextQuestion(session)

            if (nextQuestion != null && uiState.value.currentQuestionIndex < uiState.value.totalQuestions - 1) {
                _uiState.update {
                    it.copy(
                        currentQuestion = nextQuestion,
                        currentQuestionIndex = it.currentQuestionIndex + 1,
                        selectedAnswers = emptyList(),
                        feedback = null,
                        hintsUsed = 0
                    )
                }
            } else {
                completeQuiz()
            }
        }
    }

    private fun completeQuiz() {
        timerJob?.cancel()

        viewModelScope.launch {
            val session = currentSession ?: return@launch
            val result = assessmentEngine.completeSession(session)

            // Save assessment completion to profile
            repository.getProfile().firstOrNull()?.onSuccess { profile ->
                profile?.let {
                    val timeSpent = uiState.value.timeRemainingSeconds
                    val totalTime = assessmentEngine.assessment.timeLimitMinutes?.times(60) ?: 0
                    val timeSpentMinutes = (totalTime - timeSpent) / 60
                    
                    val updatedProfile = it.copy(
                        assessmentResults = it.assessmentResults + com.LBs.EEDA.domain.model.AssessmentResult(
                            assessmentId = assessmentId,
                            timestamp = DateUtils.getCurrentTimestamp().toString(),
                            conceptId = assessmentEngine.assessment.discipline.name,
                            score = result.score,
                            totalQuestions = uiState.value.totalQuestions,
                            correctAnswers = uiState.value.correctAnswers,
                            hintsUsed = uiState.value.hintsUsed,
                            timeSpentMinutes = timeSpentMinutes.coerceAtLeast(1)
                        )
                    )
                    repository.saveProfile(updatedProfile)
                }
            }

            _uiState.update {
                it.copy(
                    isComplete = true,
                    score = result.score,
                    xpEarned = result.xpEarned,
                    grade = result.grade
                )
            }

            Logger.logEvent(Logger.Category.ANALYTICS, "quiz_completed", mapOf(
                "score" to result.score,
                "grade" to result.grade.name,
                "xp" to result.xpEarned
            ))
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
