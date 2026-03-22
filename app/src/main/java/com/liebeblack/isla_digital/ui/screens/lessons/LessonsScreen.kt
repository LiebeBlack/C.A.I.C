package com.liebeblack.isla_digital.ui.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.liebeblack.isla_digital.domain.model.DigitalPhase
import com.liebeblack.isla_digital.ui.components.AdaptiveCard
import com.liebeblack.isla_digital.ui.components.MiniProgressRing
import com.liebeblack.isla_digital.ui.theme.IslaAdaptiveTheme

data class LessonModule(
    val id: String, val title: String, val description: String,
    val icon: ImageVector, val phase: DigitalPhase,
    val totalLessons: Int, val completedLessons: Int = 0,
    val estimatedMinutes: Int = 15
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    viewModel: LessonsViewModel,
    onNavigateBack: () -> Unit
) {
    val colors = IslaAdaptiveTheme.colors
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("📚 Lecciones", color = colors.onBackground, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver", tint = colors.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is LessonsUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            is LessonsUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = colors.error) }
            }
            is LessonsUiState.Success -> {
                val modules = remember(state.phase) { getLessonsForPhase(state.phase) }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AdaptiveCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("${state.phase.emoji} Módulos — ${state.phase.displayName}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colors.onSurface))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(state.phase.description, style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.7f)))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${modules.size} módulos de aprendizaje adaptativo", style = MaterialTheme.typography.labelSmall.copy(color = colors.primary, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                    items(modules) { module -> 
                        val completedCount = if (module.id in state.completedLessonIds) module.totalLessons else 0
                        LessonModuleCard(
                            module = module.copy(completedLessons = completedCount),
                            onComplete = { viewModel.completeLesson(module.id) }
                        ) 
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LessonModuleCard(
    module: LessonModule,
    onComplete: () -> Unit
) {
    val colors = IslaAdaptiveTheme.colors
    val config = IslaAdaptiveTheme.typoConfig
    val progress = if (module.totalLessons > 0) module.completedLessons.toFloat() / module.totalLessons else 0f

    AdaptiveCard(modifier = Modifier.fillMaxWidth().clickable { onComplete() }) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(48.dp), shape = RoundedCornerShape(config.borderRadius.dp / 2), color = colors.primary.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) { Icon(module.icon, null, tint = colors.primary, modifier = Modifier.size(26.dp)) }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(module.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onSurface), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(module.description, style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.6f)), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, Modifier.size(14.dp), tint = colors.onSurface.copy(alpha = 0.5f))
                    Spacer(Modifier.width(4.dp))
                    Text("${module.estimatedMinutes} min · ${module.completedLessons}/${module.totalLessons}", style = MaterialTheme.typography.labelSmall.copy(color = colors.onSurface.copy(alpha = 0.5f)))
                }
            }
            MiniProgressRing(progress = progress, size = 36.dp)
        }
    }
}

private fun getLessonsForPhase(phase: DigitalPhase): List<LessonModule> = when (phase) {
    DigitalPhase.SENSORIAL -> listOf(
        // 3-5 años
        LessonModule("s_01", "Toque y Gesto", "Interactúa con la pantalla táctil de forma divertida", Icons.Rounded.TouchApp, phase, 4, estimatedMinutes = 10),
        LessonModule("s_02", "Colores Digitales", "Reconoce los colores y formas en tu dispositivo", Icons.Rounded.Palette, phase, 3, estimatedMinutes = 8),
        LessonModule("s_07", "La Pausa Mágica", "Uso saludable y tiempo de descanso", Icons.Rounded.SelfImprovement, phase, 3, estimatedMinutes = 8),
        // 6-7 años
        LessonModule("s_03", "Contraseña Secreta", "Tu primera clave de seguridad", Icons.Rounded.Lock, phase, 3, estimatedMinutes = 10),
        LessonModule("s_05", "Iconos de Poder", "Qué significan el WiFi y la batería", Icons.Rounded.Apps, phase, 5, estimatedMinutes = 10),
        LessonModule("s_04", "Amigos o Extraños", "Seguridad básica en la red", Icons.Rounded.Warning, phase, 4, estimatedMinutes = 12),
        LessonModule("s_08", "Ayuda de Gigantes", "Cuándo llamar a un adulto de confianza", Icons.Rounded.SupportAgent, phase, 2, estimatedMinutes = 8)
    )
    DigitalPhase.CREATIVE -> listOf(
        // 8-10 años
        LessonModule("c_08", "Foto y Creatividad", "Toma y edita fotos increíbles", Icons.Rounded.PhotoCamera, phase, 4, estimatedMinutes = 15),
        LessonModule("c_04", "Aventura de Lógica", "Resuelve problemas paso a paso", Icons.Rounded.Psychology, phase, 6, estimatedMinutes = 25),
        LessonModule("c_01", "Escudo Digital", "Detecta trampas y phishing", Icons.Rounded.Security, phase, 5, estimatedMinutes = 20),
        // 11-12 años
        LessonModule("c_10", "Soy Buen Ciudadano", "Respeto y ética en internet", Icons.Rounded.People, phase, 4, estimatedMinutes = 15),
        LessonModule("c_12", "Super Buscador", "Encuentra información real rápidamente", Icons.Rounded.Search, phase, 4, estimatedMinutes = 15),
        LessonModule("c_09", "Mini Director", "Crea y edita tus propios videos", Icons.Rounded.Videocam, phase, 5, estimatedMinutes = 20),
        // 13-14 años
        LessonModule("c_11", "Mi Mochila en la Nube", "Sincroniza y guarda tus archivos", Icons.Rounded.Cloud, phase, 4, estimatedMinutes = 15),
        LessonModule("c_03", "Maestro de Claves", "Uso avanzado de gestores de contraseñas", Icons.Rounded.Key, phase, 4, estimatedMinutes = 15)
    )
    DigitalPhase.PROFESSIONAL -> listOf(
        // 15-16 años
        LessonModule("p_01", "Mundo de IA", "Cómo funciona la inteligencia artificial", Icons.Rounded.SmartToy, phase, 6, estimatedMinutes = 30),
        LessonModule("p_07", "Oficina Pro", "Domina documentos y hojas de cálculo", Icons.Rounded.Description, phase, 8, estimatedMinutes = 40),
        LessonModule("p_04", "Banca y Ahorro", "Gestiona tu dinero digitalmente", Icons.Rounded.AccountBalance, phase, 5, estimatedMinutes = 25),
        // 17-18 años
        LessonModule("p_09", "Mi Marca Digital", "LinkedIn y portafolio profesional", Icons.Rounded.Badge, phase, 5, estimatedMinutes = 25),
        LessonModule("p_02", "IA para Trabajar", "Productividad con herramientas de IA", Icons.Rounded.AutoAwesome, phase, 8, estimatedMinutes = 45),
        LessonModule("p_11", "Mis Derechos", "Privacidad y ley de datos (GDPR)", Icons.Rounded.Policy, phase, 5, estimatedMinutes = 25),
        // 19-20 años
        LessonModule("p_06", "Futuro del Dinero", "Blockchain y criptomonedas", Icons.Rounded.CurrencyBitcoin, phase, 6, estimatedMinutes = 30),
        LessonModule("p_08", "Líder de Proyectos", "Gestión ágil de equipos", Icons.Rounded.Dashboard, phase, 6, estimatedMinutes = 30),
        LessonModule("p_10", "Fortaleza Digital", "2FA, biometría y seguridad total", Icons.Rounded.VerifiedUser, phase, 5, estimatedMinutes = 25)
    )
    DigitalPhase.INNOVATOR -> listOf(
        LessonModule("i_01", "Design Thinking", "Metodología para resolver problemas complejos", Icons.Rounded.Lightbulb, phase, 5, estimatedMinutes = 30),
        LessonModule("i_02", "Prototipado Lab", "Crea tu primer producto digital mínimo", Icons.Rounded.Construction, phase, 6, estimatedMinutes = 45),
        LessonModule("i_03", "Liderazgo 4.0", "Gestión de equipos en la era digital", Icons.Rounded.Groups, phase, 4, estimatedMinutes = 30)
    )
}
