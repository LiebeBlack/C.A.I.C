package com.liebeblack.isla_digital.ui.screens.levels

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.liebeblack.isla_digital.domain.model.DigitalPhase
import com.liebeblack.isla_digital.domain.repository.ChildProfileRepository
import com.liebeblack.isla_digital.ui.components.AdaptiveCard
import com.liebeblack.isla_digital.ui.theme.IslaAdaptiveTheme

data class Level(
    val id: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val minLevel: Int,
    val requiredSkillId: String? = null,
    val xpReward: Long = 150L,
    val difficulty: Int = 1 // 1: Fácil, 2: Medio, 3: Difícil
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(
    repository: ChildProfileRepository,
    onNavigateBack: () -> Unit
) {
    val levelsViewModel: LevelsViewModel = viewModel(
        factory = viewModelFactory { initializer { LevelsViewModel(repository) } }
    )
    val uiState by levelsViewModel.uiState.collectAsState()
    val colors = IslaAdaptiveTheme.colors
    val phase = IslaAdaptiveTheme.phase

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Aventuras", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background.copy(alpha = 0.8f),
                    titleContentColor = colors.onBackground,
                    navigationIconContentColor = colors.onBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd)))
                .padding(padding)
        ) {
            when (val state = uiState) {
                is LevelsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }
                is LevelsUiState.Success -> {
                    val allLevels = remember(phase) { getLevelsForPhase(phase) }
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            PhaseHeader(phase, state.currentLevel)
                        }

                        items(allLevels) { level ->
                            val skillRequirementMet = level.requiredSkillId == null || state.completedSkillIds.contains(level.requiredSkillId)
                            val isLocked = state.currentLevel < level.minLevel || !skillRequirementMet
                            val isCompleted = state.completedMapLevelIds.contains(level.id)
                            
                            LevelItem(
                                level = level,
                                isLocked = isLocked,
                                isCompleted = isCompleted,
                                requirementText = if (!skillRequirementMet) "Requiere habilidad específica" else if (state.currentLevel < level.minLevel) "Nivel ${level.minLevel} necesario" else null,
                                onClick = { if (!isLocked) levelsViewModel.completeLevel(level) }
                            )
                        }
                        
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
                is LevelsUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = colors.error)
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseHeader(phase: DigitalPhase, currentLevel: Int) {
    val colors = IslaAdaptiveTheme.colors
    AdaptiveCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(12.dp)) {
            Text(
                text = phase.emoji,
                fontSize = 48.sp
            )
            Text(
                text = phase.displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colors.primary
            )
            Text(
                text = "Nivel Actual: $currentLevel",
                style = MaterialTheme.typography.labelLarge,
                color = colors.secondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = phase.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = colors.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun LevelItem(
    level: Level,
    isLocked: Boolean,
    isCompleted: Boolean,
    requirementText: String?,
    onClick: () -> Unit
) {
    val colors = IslaAdaptiveTheme.colors
    val config = IslaAdaptiveTheme.typoConfig
    
    val backgroundBrush = if (isLocked) {
        SolidColor(colors.onSurface.copy(alpha = 0.05f))
    } else {
        Brush.horizontalGradient(listOf(colors.surface.copy(alpha = 0.9f), colors.surface.copy(alpha = 0.7f)))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(config.borderRadius.dp))
                .background(backgroundBrush)
                .clickable(enabled = !isLocked) { onClick() }
                .padding(config.cardPadding.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(config.iconSize.dp * 2),
                shape = CircleShape,
                color = when {
                    isLocked -> colors.onSurface.copy(alpha = 0.1f)
                    isCompleted -> colors.success.copy(alpha = 0.2f)
                    else -> colors.primary.copy(alpha = 0.2f)
                },
                border = if (!isLocked) BorderStroke(2.dp, colors.primary.copy(alpha = 0.3f)) else null
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isLocked) Icons.Rounded.Lock else level.icon,
                        contentDescription = null,
                        tint = when {
                            isLocked -> colors.onSurface.copy(alpha = 0.4f)
                            isCompleted -> colors.success
                            else -> colors.primary
                        },
                        modifier = Modifier.size(config.iconSize.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = level.title,
                        style = config.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) colors.onSurface.copy(alpha = 0.4f) else colors.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    repeat(level.difficulty) {
                        Icon(Icons.Rounded.Star, null, Modifier.size(12.dp), tint = if(isLocked) colors.onSurface.copy(alpha = 0.2f) else colors.accent)
                    }
                }
                Text(
                    text = level.description,
                    style = config.typography.bodySmall,
                    color = if (isLocked) colors.onSurface.copy(alpha = 0.3f) else colors.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                if (isLocked && requirementText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = requirementText,
                        style = config.typography.labelSmall,
                        color = colors.error.copy(alpha = 0.7f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            if (isCompleted) {
                Icon(Icons.Rounded.CheckCircle, null, tint = colors.success, modifier = Modifier.size(28.dp))
            }
        }
    }
}

fun getLevelsForPhase(phase: DigitalPhase): List<Level> = when (phase) {
    DigitalPhase.SENSORIAL -> listOf(
        Level(101, "El Despertar Táctil", "Explora la isla tocando y deslizando los elementos mágicos", Icons.Rounded.TouchApp, 1, difficulty = 1),
        Level(102, "Cazador de Colores", "Clasifica las gemas digitales por su color vibrante", Icons.Rounded.Palette, 2, requiredSkillId = "dev_01", difficulty = 1),
        Level(103, "La Pausa del Guerrero", "Aprende cuándo es momento de descansar de la pantalla", Icons.Rounded.SelfImprovement, 3, requiredSkillId = "well_01", difficulty = 1),
        Level(104, "El Cofre de las Llaves", "Crea una contraseña mágica para proteger tus tesoros", Icons.Rounded.VpnKey, 4, requiredSkillId = "sec_01", difficulty = 2),
        Level(105, "Navegante de Símbolos", "Identifica el WiFi, la batería y otros iconos del dispositivo", Icons.Rounded.Apps, 5, requiredSkillId = "web_02", difficulty = 2),
        Level(106, "El Bosque de los Mensajes", "Aprende a reconocer a los amigos de los desconocidos", Icons.AutoMirrored.Rounded.Chat, 6, requiredSkillId = "sec_02", difficulty = 2),
        Level(107, "S.O.S. Guardianes", "Pide ayuda a los adultos cuando algo extraño aparezca", Icons.Rounded.SupportAgent, 7, requiredSkillId = "sec_03", difficulty = 3)
    )
    DigitalPhase.CREATIVE -> listOf(
        Level(201, "Ojo de Fotógrafo", "Captura la esencia de la isla y edita tu primera obra", Icons.Rounded.PhotoCamera, 1, requiredSkillId = "cont_01", difficulty = 1),
        Level(202, "Laberinto Lógico", "Resuelve acertijos usando el pensamiento computacional", Icons.Rounded.Psychology, 3, requiredSkillId = "prog_01", difficulty = 2),
        Level(203, "Escudo de Identidad", "Protege tu información personal de los piratas digitales", Icons.Rounded.Shield, 5, requiredSkillId = "sec_04", difficulty = 3),
        Level(204, "Puente de la Empatía", "Construye una comunidad respetuosa en la red", Icons.Rounded.Groups, 8, requiredSkillId = "cont_03", difficulty = 2),
        Level(205, "Buscadores de la Verdad", "Distingue entre información real y noticias falsas", Icons.Rounded.TravelExplore, 10, requiredSkillId = "web_04", difficulty = 2),
        Level(206, "Cineastas del Mañana", "Dirige y edita un video que cuente una gran historia", Icons.Rounded.VideoCameraBack, 12, requiredSkillId = "cont_02", difficulty = 3),
        Level(207, "Arquitectos de Datos", "Organiza y protege tus creaciones en la nube", Icons.Rounded.CloudQueue, 15, requiredSkillId = "web_03", difficulty = 2),
        Level(208, "El Guardián de Llaves", "Domina la seguridad avanzada y la verificación en dos pasos", Icons.Rounded.Password, 18, requiredSkillId = "sec_06", difficulty = 3)
    )
    DigitalPhase.PROFESSIONAL -> listOf(
        Level(301, "Susurrador de IA", "Aprende los secretos para conversar con la IA", Icons.Rounded.SmartToy, 1, requiredSkillId = "ai_01", difficulty = 2),
        Level(302, "Maestro de la Productividad", "Domina las herramientas esenciales del mundo profesional", Icons.Rounded.WorkOutline, 5, requiredSkillId = "prod_01", difficulty = 2),
        Level(303, "Banquero Digital", "Gestiona tus ahorros y transacciones con total seguridad", Icons.Rounded.AccountBalance, 8, requiredSkillId = "fin_01", difficulty = 3),
        Level(304, "Marca Personal", "Crea un portafolio que muestre tus mejores talentos", Icons.Rounded.Badge, 12, requiredSkillId = "prod_03", difficulty = 3),
        Level(305, "Ética en el Algoritmo", "Decide el futuro de la IA con responsabilidad social", Icons.Rounded.Balance, 15, requiredSkillId = "ai_03", difficulty = 3),
        Level(306, "Ciber-Detective", "Defiende tus derechos digitales y los de los demás", Icons.Rounded.Gavel, 18, requiredSkillId = "sec_08", difficulty = 3),
        Level(307, "Visionario de Blockchain", "Explora la nueva economía de la confianza digital", Icons.Rounded.CurrencyBitcoin, 22, requiredSkillId = "fin_03", difficulty = 3),
        Level(308, "Automatizador Supremo", "Crea flujos de trabajo inteligentes que trabajen por ti", Icons.Rounded.AutoFixHigh, 25, requiredSkillId = "prod_04", difficulty = 3)
    )
    DigitalPhase.INNOVATOR -> listOf(
        Level(401, "El Canvas del Innovador", "Diseña tu primer modelo de negocio digital", Icons.Rounded.Draw, 1, requiredSkillId = "inn_01", difficulty = 2),
        Level(402, "Arquitecto de Nubes", "Diseña una infraestructura escalable y segura", Icons.Rounded.CloudDone, 5, requiredSkillId = "inn_02", difficulty = 3),
        Level(403, "Liderazgo de Impacto", "Gestiona una crisis digital con ética y empatía", Icons.Rounded.AutoGraph, 10, requiredSkillId = "inn_03", difficulty = 3)
    )
}
