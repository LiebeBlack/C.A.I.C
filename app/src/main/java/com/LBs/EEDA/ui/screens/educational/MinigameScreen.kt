package com.LBs.EEDA.ui.screens.educational

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.educational.*
import com.LBs.EEDA.domain.model.educational.games.MinigameCollection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.LBs.EEDA.domain.model.educational.MinigameUiState
import com.LBs.EEDA.domain.usecase.educational.MinigameRepository
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.domain.model.educational.games.MinigameFactory
import com.LBs.EEDA.domain.model.ChildProfile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull


// Minigame definitions moved to MinigameUiState.kt


class MinigameViewModel(
    private val minigameRepository: MinigameRepository,
    private val profileRepository: ChildProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MinigameUiState())
    val uiState: StateFlow<MinigameUiState> = _uiState

    fun loadMinigame(minigameId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Intentar obtener del catálogo dinámico primero si no está en el repo fijo
            val profile = profileRepository.getChildProfile().firstOrNull()
            val age = profile?.age ?: 10
            
            val minigame = minigameRepository.getMinigame(minigameId) 
                ?: MinigameCollection.getGamesForAge(age).find { it.id == minigameId }
                ?: MinigameFactory.createInfiniteCatalog(age).find { it.id == minigameId }

            if (minigame != null) {
                _uiState.value = _uiState.value.copy(
                    minigame = minigame,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Minijuego no encontrado"
                )
            }
        }
    }

    fun submitStage(success: Boolean, xp: Int) {
        val currentState = _uiState.value
        val minigame = currentState.minigame ?: return
        
        viewModelScope.launch {
            if (success) {
                val nextIndex = currentState.currentStageIndex + 1
                if (nextIndex < minigame.stages.size) {
                    _uiState.value = currentState.copy(
                        currentStageIndex = nextIndex,
                        accumulatedXp = currentState.accumulatedXp + xp
                    )
                } else {
                    // Fin del juego con éxito
                    _uiState.value = currentState.copy(
                        accumulatedXp = currentState.accumulatedXp + xp,
                        gameState = "SUCCESS"
                    )
                    saveProgress(currentState.accumulatedXp + xp)
                }
            } else {
                val nextLives = currentState.lives - 1
                if (nextLives <= 0) {
                    _uiState.value = currentState.copy(
                        lives = 0,
                        gameState = "GAME_OVER"
                    )
                } else {
                    _uiState.value = currentState.copy(lives = nextLives)
                }
            }
        }
    }

    private suspend fun saveProgress(finalScore: Int) {
        val profile = profileRepository.getChildProfile().firstOrNull() ?: return
        val minigame = _uiState.value.minigame ?: return
        
        minigameRepository.saveAttempt(
            MinigameAttempt(
                userId = profile.id,
                minigameId = minigame.id,
                score = finalScore,
                timestamp = System.currentTimeMillis(),
                stageNumber = 1,
                attemptsCount = 1,
                successful = true,
                timeSpentSeconds = 0,
                errorsMade = emptyList(),
                hintsUsed = emptyList(),
                finalSolution = null,
                xpEarned = finalScore.toLong()
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinigameScreen(
    minigameId: String,
    viewModel: MinigameViewModel,
    onNavigateBack: () -> Unit
) {
    val stateFlow = viewModel.uiState.collectAsState()
    val state = stateFlow.value

    LaunchedEffect(minigameId) {
        viewModel.loadMinigame(minigameId)
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentGame = state.minigame ?: return

    val feedback = remember { mutableStateOf<String?>(null) }
    val isError = remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val currentStage = currentGame.stages.getOrElse(state.currentStageIndex) { currentGame.stages.last() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentGame.title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Dificultad: ${currentGame.difficulty}", fontSize = 10.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                        Text("❤️ ${state.lives}", fontWeight = FontWeight.Bold, color = Color.Red)
                        Spacer(Modifier.width(12.dp))
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
                            Text("XP: ${state.accumulatedXp}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GameBackground(currentGame.type)

            AnimatedContent(targetState = state.gameState, label = "game_state") { gameState ->
                when (gameState) {
                    "PLAYING" -> {
                        GameEngineView(
                            minigameType = currentGame.type,
                            stage = currentStage,
                            onAction = { success, xpBonus, msg ->
                                viewModel.submitStage(success, xpBonus)
                                feedback.value = msg
                                isError.value = !success
                                scope.launch {
                                    delay(2000)
                                    feedback.value = null
                                }
                            }
                        )
                    }
                    "SUCCESS" -> {
                        SuccessOverlay(state.accumulatedXp, onNavigateBack)
                    }
                    "GAME_OVER" -> {
                        GameOverOverlay(onRetry = {
                            viewModel.loadMinigame(minigameId)
                        }, onNavigateBack)
                    }
                }
            }

            // Global Toast
            AnimatedVisibility(visible = feedback.value != null, modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)) {
                Card(colors = CardDefaults.cardColors(if (isError.value) MaterialTheme.colorScheme.errorContainer else Color(0xFF4CAF50).copy(alpha = 0.9f))) {
                    Row(Modifier.padding(16.dp)) { Icon(if (isError.value) Icons.Default.Cancel else Icons.Default.Public, null); Spacer(Modifier.width(8.dp)); Text(feedback.value ?: "") }
                }
            }
        }
    }
}

@Composable
fun GameEngineView(minigameType: MinigameType, stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stage.instruction, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        Box(Modifier.weight(1f)) {
            when (minigameType) {
                MinigameType.STRATEGY -> StrategyWarRoom(stage, onAction)
                MinigameType.SPACE_COLONY_SIM -> SpaceColonyView(stage, onAction)
                MinigameType.MARKET_SIMULATOR -> MarketSimView(stage, onAction)
                MinigameType.KERNEL_WAR_RTS -> KernelWarEngine(stage, onAction)
                MinigameType.DEBUGGER_QUEST -> DebuggerQuestEngine(stage, onAction)
                MinigameType.BINARY_BRIDGE -> BinaryBridgeEngine(stage, onAction)
                MinigameType.AI_TRAINING_SIM -> AILabEngine(stage, onAction)
                MinigameType.SEQUENCE_PUZZLE -> SequencePuzzleEngine(stage, onAction)
                MinigameType.ENCRYPTION_DECRYPT -> EncryptionEngine(stage, onAction)
                MinigameType.LOGIC_GATE_SIM -> LogicGateEngine(stage, onAction)
                MinigameType.MATCHING -> MatchingEngine(stage, onAction)
                MinigameType.DRAG_DROP_SORTING -> MatchingEngine(stage, onAction) // Usando matching por ahora
                else -> StandardPuzzleEngine(stage, onAction)
            }
        }
    }
}

@Composable
fun SequencePuzzleEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    var items by remember { mutableStateOf(stage.elements.shuffled()) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(items) { element ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("☰", Modifier.padding(end = 12.dp))
                        Text(element.label)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = { 
            val isCorrect = items.map { it.id } == stage.correctSolution.orderedElements
            onAction(isCorrect, if (isCorrect) 150 else 0, if (isCorrect) "¡Secuencia Correcta!" else "Orden incorrecto")
        }) { Text("Verificar Orden") }
    }
}

@Composable
fun EncryptionEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    var userInput by remember { mutableStateOf("") }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Mensaje Cifrado:", color = Color.Gray)
        Text(stage.elements.firstOrNull()?.value ?: "???", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Cyan)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Respuesta") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { 
            val isCorrect = userInput.equals(stage.correctSolution.correctValue, ignoreCase = true)
            onAction(isCorrect, if (isCorrect) 200 else 0, if (isCorrect) "¡Descifrado!" else "Código incorrecto")
        }) { Text("Desencriptar") }
    }
}

@Composable
fun LogicGateEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    var input1 by remember { mutableStateOf(false) }
    var input2 by remember { mutableStateOf(false) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = input1, onCheckedChange = { input1 = it })
            Spacer(Modifier.width(16.dp))
            Text("Entrada A: ${if (input1) "1" else "0"}")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = input2, onCheckedChange = { input2 = it })
            Spacer(Modifier.width(16.dp))
            Text("Entrada B: ${if (input2) "1" else "0"}")
        }
        Spacer(Modifier.height(24.dp))
        Icon(Icons.Default.SettingsInputComponent, null, modifier = Modifier.size(64.dp))
        Text("Compuerta AND", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Button(onClick = { 
            val isCorrect = input1 && input2
            onAction(isCorrect, 100, if (isCorrect) "¡Señal Activa!" else "Circuito incompleto")
        }) { Text("Probar Circuito") }
    }
}

@Composable
fun MatchingEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    // Simplificado para MVP
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Une los conceptos", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        stage.elements.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Card(Modifier.weight(1f).padding(4.dp)) { Text(pair[0].label, Modifier.padding(8.dp)) }
                Text("↔️")
                Card(Modifier.weight(1f).padding(4.dp)) { Text(pair.getOrNull(1)?.label ?: "?", Modifier.padding(8.dp)) }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = { onAction(true, 100, "¡Relación correcta!") }) { Text("Confirmar") }
    }
}

@Composable
fun BinaryBridgeEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🌁 Puente Binario", color = Color.White)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { onAction(true, 100, "¡Conexión establecida!") }) { Text("0") }
            Button(onClick = { onAction(true, 100, "¡Conexión establecida!") }) { Text("1") }
        }
    }
}

@Composable
fun AILabEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🧠 Laboratorio de IA", color = Color.Magenta)
        Spacer(Modifier.height(24.dp))
        Slider(value = 0.5f, onValueChange = {}, modifier = Modifier.fillMaxWidth())
        Button(onClick = { onAction(true, 200, "Modelo entrenado.") }) { Text("Entrenar") }
    }
}

// ══════════════════════════════════════════════════════════════
//  Vistas de Juegos Específicos
// ══════════════════════════════════════════════════════════════

@Composable
fun StrategyWarRoom(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Adjust, null, modifier = Modifier.size(100.dp).rotate(45f), tint = Color.Red)
        Text("Centro de Comando Geopolítico", style = MaterialTheme.typography.labelSmall)
        Spacer(Modifier.height(24.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(listOf("Negociación", "Embargo", "Alerta Roja", "Diplomacia")) { act ->
                Button(onClick = { onAction(act == "Diplomacia", 200, "¡Paz alcanzada!") }, shape = RoundedCornerShape(8.dp)) { Text(act) }
            }
        }
    }
}

@Composable
fun SpaceColonyView(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🛰️ Nivel de Oxigeno: 84%", color = Color.Cyan)
        LinearProgressIndicator(progress = { 0.84f }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape))
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            arrayOf("Reparar Filtro", "Abrir Válvula", "Reciclar H2O").forEach { task ->
                OutlinedButton(onClick = { onAction(true, 150, "Sistemas Estables.") }) { Text(task) }
            }
        }
    }
}

@Composable
fun MarketSimView(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column {
        Text("Tendencia de Mercado: ALCISTA 💹", color = Color.Green, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().height(200.dp), colors = CardDefaults.cardColors(Color.Black)) {
            // Mock de gráfico
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("📈 GRAFICO EN TIEMPO REAL", color = Color.Green) }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { onAction(true, 300, "¡Vendido en el pico!") }, colors = ButtonDefaults.buttonColors(Color(0xFFE91E63))) { Text("Vender") }
            Button(onClick = { onAction(false, 0, "Mercado sobrecomprado.") }, colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))) { Text("Comprar") }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Motores de Apoyo
// ══════════════════════════════════════════════════════════════

@Composable
fun KernelWarEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.fillMaxSize()) {
        items(16) { i -> 
            Box(Modifier.padding(4.dp).aspectRatio(1f).background(if(i==5) Color.Red else Color.DarkGray).clickable { 
                if(i==5) onAction(true, 200, "Celda Hex 0x05 Liberada")
            })
        }
    }
}

@Composable
fun DebuggerQuestEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Column {
        Text("fun main() { \n   val data = null \n   println(data!!.length) \n}", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAction(true, 150, "Puntero nulo corregido.") }) { Text("Parchear NPE") }
    }
}

@Composable
fun StandardPuzzleEngine(stage: MinigameStage, onAction: (Boolean, Int, String?) -> Unit) {
    Button(onClick = { onAction(true, 50, "Reto superado.") }) { Text("Resolver") }
}

@Composable
fun GameBackground(type: MinigameType) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF000428), Color(0xFF004E92)))).graphicsLayer { alpha = 0.5f })
}

@Composable
fun SuccessOverlay(score: Int, onFinish: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🏆", fontSize = 100.sp); Text("¡ VICTORIA !", fontSize = 32.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(32.dp)); Button(onClick = onFinish) { Text("Reclamar +$score XP") }
    }
}

@Composable
fun GameOverOverlay(onRetry: () -> Unit, onQuit: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("💥", fontSize = 100.sp); Text("SISTEMA DAÑADO", color = Color.Red, fontSize = 32.sp)
        Row { Button(onClick = onRetry) { Text("Reiniciar") }; Spacer(Modifier.width(8.dp)); OutlinedButton(onClick = onQuit) { Text("Salir") } }
    }
}
