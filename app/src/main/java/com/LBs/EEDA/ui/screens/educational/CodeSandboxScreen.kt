package com.LBs.EEDA.ui.screens.educational

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.data.repository.SandboxRepositoryImpl
import com.LBs.EEDA.domain.model.educational.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el Code Sandbox.
 */
class CodeSandboxViewModel(
    private val sandboxRepository: SandboxRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow(SandboxUiState())
    val uiState: StateFlow<SandboxUiState> = _uiState

    private val _events = MutableStateFlow<SandboxEvent?>(null)
    val events: StateFlow<SandboxEvent?> = _events

    fun loadSandbox(sandboxId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val sandbox = sandboxRepository.getSandbox(sandboxId)
            if (sandbox != null) {
                _uiState.value = _uiState.value.copy(
                    sandbox = sandbox,
                    code = sandbox.initialCode,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Sandbox no encontrado",
                    isLoading = false
                )
            }
        }
    }

    fun updateCode(newCode: String) {
        _uiState.value = _uiState.value.copy(code = newCode)
    }

    fun executeCode() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isExecuting = true,
                consoleOutput = "Ejecutando...",
                errorMessage = null,
                errorLine = null
            )

            // Simulación de ejecución
            kotlinx.coroutines.delay(1000)

            val currentCode = _uiState.value.code
            val result = simulateExecution(currentCode)

            _uiState.value = _uiState.value.copy(
                isExecuting = false,
                consoleOutput = result.output,
                showSuccess = result.success,
                totalXp = if (result.success) _uiState.value.totalXp + 50 else _uiState.value.totalXp
            )

            if (result.success) {
                _events.emit(SandboxEvent.ExecutionSuccess(
                    output = result.output,
                    xpEarned = 50,
                    nextLevelUnlocked = true
                ))
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.error
                )
            }
        }
    }

    private fun simulateExecution(code: String): ExecutionSimulation {
        return when {
            code.contains("print(\"Hola, Mundo!\")") -> {
                ExecutionSimulation(true, "Hola, Mundo!", null)
            }
            code.contains("print") -> {
                val regex = """print\(["'](.+)["']\)""".toRegex()
                val match = regex.find(code)
                ExecutionSimulation(true, match?.groupValues?.get(1) ?: "Output", null)
            }
            else -> {
                ExecutionSimulation(false, "", "Error: No se encontró comando print")
            }
        }
    }

    fun getHint() {
        val hint = _uiState.value.sandbox?.hints?.firstOrNull()
        _uiState.value = _uiState.value.copy(showHint = hint)
    }

    fun clearEvent() {
        _events.value = null
    }
}

data class SandboxUiState(
    val sandbox: CodeSandbox? = null,
    val code: String = "",
    val isLoading: Boolean = false,
    val isExecuting: Boolean = false,
    val consoleOutput: String = "",
    val errorMessage: String? = null,
    val errorLine: Int? = null,
    val showSuccess: Boolean = false,
    val showHint: CodeHint? = null,
    val totalXp: Long = 0,
    val objectivesMet: List<String> = emptyList()
)

sealed class SandboxEvent {
    data class ExecutionSuccess(
        val output: String,
        val xpEarned: Long,
        val nextLevelUnlocked: Boolean
    ) : SandboxEvent()
    
    data class ExecutionError(
        val message: String,
        val lineNumber: Int?,
        val hint: CodeHint?
    ) : SandboxEvent()
}

private data class ExecutionSimulation(
    val success: Boolean,
    val output: String,
    val error: String?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeSandboxScreen(
    sandboxId: String,
    viewModel: CodeSandboxViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(sandboxId) {
        viewModel.loadSandbox(sandboxId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.sandbox?.title ?: "Code Sandbox") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    Text(
                        text = "${uiState.totalXp} XP",
                        modifier = Modifier.padding(end = 16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Instrucciones
                uiState.sandbox?.let { sandbox ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = sandbox.instructions,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Editor de código
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E)
                    )
                ) {
                    BasicTextField(
                        value = uiState.code,
                        onValueChange = viewModel::updateCode,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        textStyle = TextStyle(
                            color = Color(0xFFD4D4D4),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp
                        ),
                        enabled = !uiState.isExecuting
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.executeCode() },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isExecuting
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (uiState.isExecuting) "Ejecutando..." else "Ejecutar")
                    }
                    
                    OutlinedButton(
                        onClick = { viewModel.getHint() },
                        enabled = !uiState.isExecuting
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Consola de salida
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 150.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0D0D0D)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Consola",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.consoleOutput.ifEmpty { "// La salida aparecerá aquí" },
                            color = if (uiState.consoleOutput.isEmpty()) Color.Gray else Color(0xFF4CAF50),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                        
                        uiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Pista
                uiState.showHint?.let { hint ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = hint.message,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
