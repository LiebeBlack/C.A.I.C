package com.LBs.EEDA.ui.screens.educational

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.LBs.EEDA.data.repository.MinigameRepositoryImpl
import com.LBs.EEDA.data.repository.SandboxRepositoryImpl
import com.LBs.EEDA.data.repository.AssessmentRepositoryImpl
import com.LBs.EEDA.domain.model.educational.Minigame
import com.LBs.EEDA.domain.model.educational.CodeSandbox
import com.LBs.EEDA.domain.model.educational.modules.UniversalKnowledgeModules
import com.LBs.EEDA.domain.model.educational.modules.Discipline
import com.LBs.EEDA.domain.model.educational.modules.EducationalModule
import com.LBs.EEDA.domain.model.educational.modules.Topic
import com.LBs.EEDA.domain.model.educational.modules.MasteryChallenge
import com.LBs.EEDA.domain.model.educational.modules.scienceModule
import com.LBs.EEDA.domain.model.educational.modules.historyModule
import com.LBs.EEDA.domain.model.educational.modules.financeModule
import com.LBs.EEDA.domain.model.educational.modules.aiModule
import com.LBs.EEDA.domain.model.educational.modules.geopoliticsModule
import com.LBs.EEDA.domain.model.educational.modules.artModule
import com.LBs.EEDA.domain.model.educational.modules.discipline
import com.LBs.EEDA.domain.model.educational.modules.ageRange
import com.LBs.EEDA.domain.model.educational.modules.ExpandedKnowledgeModules
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════
//  ViewModel
// ══════════════════════════════════════════════════════════════

class EducationalViewModel(
    private val minigameRepository: MinigameRepositoryImpl,
    private val sandboxRepository: SandboxRepositoryImpl,
    private val assessmentRepository: AssessmentRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<EducationalUiState>(EducationalUiState.Loading)
    val uiState: StateFlow<EducationalUiState> = _uiState

    private val _selectedDiscipline = MutableStateFlow<Discipline?>(null)
    val selectedDiscipline: StateFlow<Discipline?> = _selectedDiscipline

    private val _topicProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val topicProgress: StateFlow<Map<String, Float>> = _topicProgress

    private val _quizScores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val quizScores: StateFlow<Map<String, Int>> = _quizScores

    init { loadModules() }

    fun loadModules(age: Int = 10, interests: List<String> = emptyList()) {
        viewModelScope.launch {
            _uiState.value = EducationalUiState.Loading
            try {
                val expandedModules = ExpandedKnowledgeModules.getAllExpandedModules(age)
                _uiState.value = EducationalUiState.Success(expandedModules)
            } catch (e: Exception) {
                try {
                    val basic = UniversalKnowledgeModules.getAllModules(age)
                    _uiState.value = EducationalUiState.Success(basic)
                } catch (_: Exception) {
                    _uiState.value = EducationalUiState.Error("Error cargando módulos: ${e.message}")
                }
            }
        }
    }

    fun selectDiscipline(discipline: Discipline?) { _selectedDiscipline.value = discipline }

    fun recordTopicProgress(topicId: String, progress: Float) {
        _topicProgress.value = _topicProgress.value + (topicId to progress)
    }

    fun recordQuizScore(topicId: String, score: Int) {
        _quizScores.value = _quizScores.value + (topicId to score)
    }

    fun getSampleMinigames(): List<Minigame> = listOf(
        createMinigame("minigame_password_001", "Construye una Contraseña Fuerte", "Ordena los elementos para crear una contraseña segura", 5, 100),
        createMinigame("minigame_encrypt_001", "Cifrado César", "Descifra el mensaje desplazando las letras", 8, 150),
        createMinigame("minigame_algo_001", "Ordena los Pasos", "Secuencia correcta de un algoritmo", 3, 50),
        createMinigame("minigame_network_001", "Conecta Dispositivos", "Une cada dispositivo con su función de red", 6, 120)
    )

    private fun createMinigame(id: String, title: String, desc: String, duration: Int, xp: Long): Minigame {
        return Minigame(
            id = id, type = com.LBs.EEDA.domain.model.educational.MinigameType.SEQUENCE_PUZZLE,
            title = title, description = desc, conceptId = "general",
            difficulty = com.LBs.EEDA.domain.model.educational.ContentDifficulty.ELEMENTARY,
            ageBracket = com.LBs.EEDA.domain.model.educational.AgeBracket.CHILDHOOD,
            estimatedDurationMinutes = duration, xpReward = xp,
            stages = emptyList(), config = com.LBs.EEDA.domain.model.educational.MinigameConfig()
        )
    }
}

sealed class EducationalUiState {
    data object Loading : EducationalUiState()
    data class Success(val disciplines: List<EducationalModule>) : EducationalUiState()
    data class Error(val message: String) : EducationalUiState()
}

// ══════════════════════════════════════════════════════════════
//  Paleta de colores por disciplina
// ══════════════════════════════════════════════════════════════

private data class DisciplinePalette(
    val primary: Color, val secondary: Color,
    val surface: Color, val emoji: String, val gradient: List<Color>
)

private fun disciplinePalette(d: Discipline) = when (d) {
    Discipline.SCIENCE     -> DisciplinePalette(Color(0xFF0D47A1), Color(0xFF42A5F5), Color(0xFFE3F2FD), "🔬", listOf(Color(0xFF0D47A1), Color(0xFF42A5F5)))
    Discipline.HISTORY     -> DisciplinePalette(Color(0xFF6A1B9A), Color(0xFFAB47BC), Color(0xFFF3E5F5), "📜", listOf(Color(0xFF6A1B9A), Color(0xFFCE93D8)))
    Discipline.FINANCE     -> DisciplinePalette(Color(0xFF1B5E20), Color(0xFF66BB6A), Color(0xFFE8F5E9), "💰", listOf(Color(0xFF1B5E20), Color(0xFF81C784)))
    Discipline.AI          -> DisciplinePalette(Color(0xFFE65100), Color(0xFFFFA726), Color(0xFFFFF3E0), "🧠", listOf(Color(0xFFE65100), Color(0xFFFFB74D)))
    Discipline.GEOPOLITICS -> DisciplinePalette(Color(0xFFB71C1C), Color(0xFFE57373), Color(0xFFFFEBEE), "🌍", listOf(Color(0xFFB71C1C), Color(0xFFEF9A9A)))
    Discipline.ART         -> DisciplinePalette(Color(0xFF006064), Color(0xFF4DD0E1), Color(0xFFE0F7FA), "🎨", listOf(Color(0xFF006064), Color(0xFF80DEEA)))
    Discipline.TECHNOLOGY  -> DisciplinePalette(Color(0xFF004D40), Color(0xFF009688), Color(0xFFE0F2F1), "💻", listOf(Color(0xFF004D40), Color(0xFF26A69A)))
    Discipline.WELLBEING   -> DisciplinePalette(Color(0xFF311B92), Color(0xFF673AB7), Color(0xFFEDE7F6), "🧘", listOf(Color(0xFF311B92), Color(0xFF9575CD)))
    Discipline.SUSTAINABILITY -> DisciplinePalette(Color(0xFFF57F17), Color(0xFFFBC02D), Color(0xFFFFF9C4), "🌱", listOf(Color(0xFFF57F17), Color(0xFFFFF176)))
    Discipline.LEADERSHIP  -> DisciplinePalette(Color(0xFF263238), Color(0xFF546E7A), Color(0xFFECEFF1), "👑", listOf(Color(0xFF263238), Color(0xFF78909C)))
    Discipline.SPACE       -> DisciplinePalette(Color(0xFF001233), Color(0xFF002855), Color(0xFFCAF0F8), "🚀", listOf(Color(0xFF001233), Color(0xFF002855)))
}

// ══════════════════════════════════════════════════════════════
//  Pantalla principal
// ══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalModulesScreen(
    viewModel: EducationalViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMinigame: (String) -> Unit,
    onNavigateToSandbox: (String) -> Unit,
    onNavigateToAssessment: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDiscipline by viewModel.selectedDiscipline.collectAsState()
    val topicProgress by viewModel.topicProgress.collectAsState()
    val quizScores by viewModel.quizScores.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedDiscipline == null) "Universo Educativo" else "",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedDiscipline != null) viewModel.selectDiscipline(null)
                        else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is EducationalUiState.Loading -> LoadingAnimation(Modifier.align(Alignment.Center))
                is EducationalUiState.Success -> {
                    AnimatedContent(
                        targetState = selectedDiscipline,
                        transitionSpec = {
                            (fadeIn(tween(300)) + slideInHorizontally { it / 3 }) togetherWith
                            (fadeOut(tween(200)) + slideOutHorizontally { -it / 3 })
                        },
                        label = "discipline_nav"
                    ) { disc ->
                        if (disc == null) {
                            DisciplineHub(
                                disciplines = state.disciplines,
                                topicProgress = topicProgress,
                                onDisciplineClick = { viewModel.selectDiscipline(it) }
                            )
                        } else {
                            DisciplineDetailScreen(
                                discipline = disc,
                                topicProgress = topicProgress,
                                quizScores = quizScores,
                                onMinigameClick = onNavigateToMinigame,
                                onSandboxClick = onNavigateToSandbox,
                                onAssessmentClick = onNavigateToAssessment,
                                onQuizAnswer = { tid, score -> viewModel.recordQuizScore(tid, score) },
                                onTopicComplete = { tid -> viewModel.recordTopicProgress(tid, 1f) },
                                sampleMinigames = viewModel.getSampleMinigames()
                            )
                        }
                    }
                }
                is EducationalUiState.Error -> ErrorState(state.message) { viewModel.loadModules() }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Loading / Error
// ══════════════════════════════════════════════════════════════

@Composable
private fun LoadingAnimation(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "pulse"
    )
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🧠", fontSize = 64.sp, modifier = Modifier.scale(scale))
        Spacer(Modifier.height(16.dp))
        Text("Cargando universo educativo…", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text(message, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Reintentar") }
    }
}

// ══════════════════════════════════════════════════════════════
//  Discipline Hub (Grid de disciplinas)
// ══════════════════════════════════════════════════════════════

@Composable
private fun DisciplineHub(
    disciplines: List<EducationalModule>,
    topicProgress: Map<String, Float>,
    onDisciplineClick: (Discipline) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroHeader() }

        item {
            Text(
                "Explora las Disciplinas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(disciplines) { module ->
            DisciplineHubCard(
                module = module,
                progress = calculateModuleProgress(module, topicProgress),
                onClick = { onDisciplineClick(module.discipline) }
            )
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun HeroHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000), RepeatMode.Reverse), label = "shimmer"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF1A237E).copy(alpha = 0.9f + shimmer * 0.1f),
                            Color(0xFF4A148C).copy(alpha = 0.9f + shimmer * 0.1f),
                            Color(0xFF0D47A1)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text("🌌", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Universo del Conocimiento",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ciencia · Historia · Finanzas · IA · Geopolítica · Arte",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun DisciplineHubCard(
    module: EducationalModule,
    progress: Float,
    onClick: () -> Unit
) {
    val palette = disciplinePalette(module.discipline)
    var isPressed by remember { mutableStateOf(false) }
    val animScale by animateFloatAsState(
        if (isPressed) 0.97f else 1f, spring(dampingRatio = 0.6f), label = "press"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(palette.gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(module.emoji, fontSize = 28.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        module.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = palette.primary
                    )
                    Text(
                        "${module.topics.size} temas · ${module.ageRange} años",
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.primary.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null, tint = palette.primary
                )
            }

            if (progress > 0f) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = palette.primary, trackColor = palette.primary.copy(alpha = 0.15f)
                )
                Text(
                    "${(progress * 100).toInt()}% completado",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Preview de temas
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(module.topics.take(4)) { topic ->
                    Surface(
                        color = palette.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            topic.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Discipline Detail
// ══════════════════════════════════════════════════════════════

@Composable
private fun DisciplineDetailScreen(
    discipline: Discipline,
    topicProgress: Map<String, Float>,
    quizScores: Map<String, Int>,
    onMinigameClick: (String) -> Unit,
    onSandboxClick: (String) -> Unit,
    onAssessmentClick: (String) -> Unit,
    onQuizAnswer: (String, Int) -> Unit,
    onTopicComplete: (String) -> Unit,
    sampleMinigames: List<Minigame>
) {
    val module = remember(discipline) {
        UniversalKnowledgeModules.getAllModules(10).find { it.discipline == discipline }
            ?: scienceModule()
    }
    val palette = disciplinePalette(discipline)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header de disciplina
        item { DisciplineHeader(module, palette) }

        // Sección: Temas de aprendizaje
        item { SectionTitle("📖 Temas de Aprendizaje", palette.primary) }

        itemsIndexed(module.topics) { index, topic ->
            InteractiveTopicCard(
                topic = topic,
                index = index,
                palette = palette,
                progress = topicProgress[topic.id] ?: 0f,
                quizScore = quizScores[topic.id],
                onQuizAnswer = { score -> onQuizAnswer(topic.id, score) },
                onMinigameClick = { onMinigameClick(topic.id) },
                onComplete = { onTopicComplete(topic.id) }
            )
        }

        // Sección: Desafío Maestro
        item { SectionTitle("🏆 Desafío Maestro", palette.primary) }
        item { MasteryChallengeCard(module.masteryChallenge, palette) }

        // Sección: Minijuegos
        item { SectionTitle("🎮 Minijuegos Interactivos", palette.primary) }

        items(sampleMinigames) { mg ->
            MinigameListItem(minigame = mg, palette = palette, onClick = { onMinigameClick(mg.id) })
        }

        // Botón de evaluación
        item {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { onAssessmentClick("assessment_${discipline.name.lowercase()}") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
            ) {
                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Iniciar Evaluación Completa", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

// ══════════════════════════════════════════════════════════════
//  Componentes de detalle
// ══════════════════════════════════════════════════════════════

@Composable
private fun DisciplineHeader(module: EducationalModule, palette: DisciplinePalette) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(palette.gradient))
                .padding(24.dp)
        ) {
            Column {
                Text(module.emoji, fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(module.title, style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(4.dp))
                Text("${module.topics.size} temas · Rango: ${module.ageRange} años",
                    color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String, color: Color) {
    Text(text, style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold, color = color,
        modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun InteractiveTopicCard(
    topic: Topic, index: Int, palette: DisciplinePalette,
    progress: Float, quizScore: Int?,
    onQuizAnswer: (Int) -> Unit, 
    onMinigameClick: () -> Unit,
    onComplete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showQuiz by remember { mutableStateOf(false) }
    var quizAnswered by remember { mutableStateOf(quizScore != null) }
    var selectedOption by remember { mutableIntStateOf(-1) }

    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(index * 80L)
        appear.animateTo(1f, tween(400))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = appear.value; translationX = (1f - appear.value) * 60f }
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (expanded) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(palette.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", fontWeight = FontWeight.Bold, color = palette.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(topic.description, fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis)
                }
                if (progress >= 1f) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null, tint = palette.primary
                )
            }

            // Minigame badge
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VideogameAsset, null, Modifier.size(14.dp), tint = palette.secondary)
                Spacer(Modifier.width(4.dp))
                Text("Minijuego: ${topic.minigame.name.replace('_', ' ')}",
                    fontSize = 11.sp, color = palette.secondary)
            }

            // Expanded content
            AnimatedVisibility(expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Metáfora visual
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = palette.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 24.sp)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Metáfora", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = palette.primary)
                                Text(topic.metaphor, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Objetivos de aprendizaje
                    Text("🎯 Objetivos:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = palette.primary)
                    topic.learningObjectives.forEach { obj ->
                        Row(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                            Text("•", color = palette.secondary, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Text(obj, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Lanzar Minijuego
                    Button(
                        onClick = onMinigameClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
                    ) {
                        Icon(Icons.Default.VideogameAsset, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Misión: ${topic.minigame.name.replace('_', ' ')}", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Auto-evaluación rápida
                    if (!showQuiz && !quizAnswered) {
                        OutlinedButton(
                            onClick = { showQuiz = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, palette.primary)
                        ) {
                            Icon(Icons.Default.Quiz, null, tint = palette.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Auto-evaluación rápida", color = palette.primary)
                        }
                    }

                    // Inline Quiz
                    AnimatedVisibility(showQuiz && !quizAnswered) {
                        InlineQuiz(
                            topic = topic,
                            palette = palette,
                            onAnswer = { correct ->
                                quizAnswered = true
                                showQuiz = false
                                onQuizAnswer(if (correct) 100 else 0)
                                if (correct) onComplete()
                            }
                        )
                    }

                    // Quiz Result
                    if (quizAnswered) {
                        val score = quizScore ?: 0
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (score >= 50) Color(0xFF4CAF50).copy(alpha = 0.1f)
                                else Color(0xFFF44336).copy(alpha = 0.1f)
                            )
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(if (score >= 50) "✅" else "📚", fontSize = 24.sp)
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    if (score >= 50) "¡Excelente! Tema dominado." else "Sigue practicando. ¡Tú puedes!",
                                    fontWeight = FontWeight.Medium, fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Inline Quiz
// ══════════════════════════════════════════════════════════════

@Composable
private fun InlineQuiz(topic: Topic, palette: DisciplinePalette, onAnswer: (Boolean) -> Unit) {
    val questions = remember(topic.id) { com.LBs.EEDA.domain.model.educational.modules.EedaQuestionBank.getQuestionsForTopic(topic.id) }
    val q = questions.firstOrNull() ?: return
    
    val question = q.text
    val options = q.options
    val correctIndex = q.correctAnswerIndex
    
    var selected by remember { mutableIntStateOf(-1) }
    var answered by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📝 Auto-evaluación", fontWeight = FontWeight.Bold, color = palette.primary)
            Spacer(Modifier.height(8.dp))
            Text(question, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))

            options.forEachIndexed { i, opt ->
                val bgColor = when {
                    !answered -> if (selected == i) palette.primary.copy(alpha = 0.15f) else Color.Transparent
                    i == correctIndex -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    selected == i -> Color(0xFFF44336).copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !answered) { selected = i },
                    color = bgColor,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, if (selected == i) palette.primary else Color.Gray.copy(alpha = 0.3f)
                    )
                ) {
                    Text(opt, modifier = Modifier.padding(12.dp), fontSize = 13.sp,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!answered && selected >= 0) {
                        answered = true
                        onAnswer(selected == correctIndex)
                    }
                },
                enabled = selected >= 0 && !answered,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
            ) { Text("Confirmar respuesta", color = Color.White) }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Mastery Challenge
// ══════════════════════════════════════════════════════════════

@Composable
private fun MasteryChallengeCard(challenge: MasteryChallenge, palette: DisciplinePalette) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.linearGradient(
                    listOf(palette.primary.copy(alpha = 0.85f), palette.secondary.copy(alpha = 0.9f)))
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(challenge.title, fontWeight = FontWeight.Bold,
                            color = Color.White, fontSize = 18.sp)
                        Text("Dificultad: ${challenge.difficulty.name}",
                            color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(challenge.description, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(challenge.disciplines) { disc ->
                        Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                            Text(disc, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
//  Minigame Item
// ══════════════════════════════════════════════════════════════

@Composable
private fun MinigameListItem(minigame: Minigame, palette: DisciplinePalette = disciplinePalette(Discipline.SCIENCE), onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(palette.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Games, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(minigame.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(minigame.description, fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("⏱ ${minigame.estimatedDurationMinutes} min", fontSize = 11.sp, color = palette.secondary)
                    Text("⭐ ${minigame.xpReward} XP", fontSize = 11.sp, color = palette.primary)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = palette.primary)
        }
    }
}

// Badge utilitario
@Composable
fun Badge(text: String) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun TopicCard(topic: Topic) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(topic.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(topic.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VideogameAsset, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(4.dp))
                Text("Minijuego: ${topic.minigame.name.replace('_', ' ')}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

// Utilidad
private fun calculateModuleProgress(module: EducationalModule, progress: Map<String, Float>): Float {
    if (module.topics.isEmpty()) return 0f
    val total = module.topics.sumOf { (progress[it.id] ?: 0f).toDouble() }
    return (total / module.topics.size).toFloat()
}
