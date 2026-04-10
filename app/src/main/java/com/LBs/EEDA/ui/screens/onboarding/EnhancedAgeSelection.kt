package com.LBs.EEDA.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.model.educational.AgeBracket
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

/**
 * Pantalla de selección de edad mejorada para EEDA.
 * Diseño interactivo y amigable que guía al usuario a seleccionar su edad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedAgeSelectionScreen(
    currentAge: Int = 5,
    onAgeSelected: (Int) -> Unit,
    onContinue: () -> Unit
) {
    var selectedAge by remember { mutableIntStateOf(currentAge) }
    var selectedPhase by remember { mutableStateOf(DigitalPhase.fromAge(selectedAge)) }
    
    // Actualizar fase cuando cambia la edad
    LaunchedEffect(selectedAge) {
        selectedPhase = DigitalPhase.fromAge(selectedAge)
    }
    
    val colors = EedaAdaptiveTheme.colors
    
    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "¿Cuántos años tienes?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Phase Indicator
            AnimatedContent(
                targetState = selectedPhase,
                transitionSpec = { fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { it / 2 } },
                label = "phase_animation"
            ) { phase ->
                PhaseDisplay(phase = phase)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Age Selector
            AgeCarousel(
                selectedAge = selectedAge,
                onAgeSelected = { 
                    selectedAge = it
                    onAgeSelected(it)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Age Bracket Info
            AgeBracketCard(age = selectedAge)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Continue Button
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PhaseDisplay(phase: DigitalPhase) {
    val colors = EedaAdaptiveTheme.colors
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (phase) {
                DigitalPhase.SENSORIAL -> Color(0xFFFFE4B5)
                DigitalPhase.CREATIVE -> Color(0xFFE0F7FA)
                DigitalPhase.PROFESSIONAL -> Color(0xFFE8F5E9)
                DigitalPhase.INNOVATOR -> Color(0xFFF3E5F5)
            }
        ),
        border = BorderStroke(
            width = 2.dp,
            color = when (phase) {
                DigitalPhase.SENSORIAL -> Color(0xFFFFA726)
                DigitalPhase.CREATIVE -> Color(0xFF26C6DA)
                DigitalPhase.PROFESSIONAL -> Color(0xFF66BB6A)
                DigitalPhase.INNOVATOR -> Color(0xFFAB47BC)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phase Emoji
            Text(
                text = phase.emoji,
                fontSize = 48.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Phase Name
            Text(
                text = phase.displayName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Phase Description
            Text(
                text = phase.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colors.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Age Range Badge
            Surface(
                color = when (phase) {
                    DigitalPhase.SENSORIAL -> Color(0xFFFFA726)
                    DigitalPhase.CREATIVE -> Color(0xFF26C6DA)
                    DigitalPhase.PROFESSIONAL -> Color(0xFF66BB6A)
                    DigitalPhase.INNOVATOR -> Color(0xFFAB47BC)
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${phase.ageRange.first}-${phase.ageRange.last} años",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun AgeCarousel(
    selectedAge: Int,
    onAgeSelected: (Int) -> Unit
) {
    val ageRange = 3..20
    val listState = remember { androidx.compose.foundation.lazy.LazyListState() }
    
    // Scroll to selected age
    LaunchedEffect(selectedAge) {
        val index = selectedAge - 3
        if (index >= 0) {
            listState.animateScrollToItem(index.coerceIn(0, ageRange.count() - 1))
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center indicator
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EedaAdaptiveTheme.colors.primary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            items(ageRange.toList()) { age ->
                val isSelected = age == selectedAge
                val distance = kotlin.math.abs(age - selectedAge)
                
                val scale = when {
                    isSelected -> 1.5f
                    distance == 1 -> 1.1f
                    distance == 2 -> 0.9f
                    else -> 0.7f
                }
                
                val alpha = when {
                    isSelected -> 1f
                    distance == 1 -> 0.7f
                    distance == 2 -> 0.4f
                    else -> 0.2f
                }
                
                AgeNumberItem(
                    age = age,
                    isSelected = isSelected,
                    scale = scale,
                    alpha = alpha,
                    onClick = { onAgeSelected(age) }
                )
            }
        }
    }
}

@Composable
private fun AgeNumberItem(
    age: Int,
    isSelected: Boolean,
    scale: Float,
    alpha: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val actualScale = if (isPressed) scale * 0.9f else scale
    
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .scale(actualScale)
            .graphicsLayer { this.alpha = alpha }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 32.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // Selected background
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(EedaAdaptiveTheme.colors.primary)
            )
        }
        
        Text(
            text = age.toString(),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else EedaAdaptiveTheme.colors.onBackground
            )
        )
    }
}

@Composable
private fun AgeBracketCard(age: Int) {
    val bracket = remember(age) {
        AgeBracket.entries.find { age in it.range } ?: AgeBracket.YOUNG_ADULT
    }
    
    val cognitiveLevelName = when (bracket.cognitiveLevel) {
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.PREOPERATIONAL -> "Pensamiento Simbólico"
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.CONCRETE_EARLY -> "Lógica Concreta Inicial"
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.CONCRETE_MATURE -> "Lógica Concreta Avanzada"
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.FORMAL_EARLY -> "Pensamiento Abstracto Inicial"
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.FORMAL_MATURE -> "Pensamiento Abstracto Maduro"
        com.LBs.EEDA.domain.model.educational.CognitiveLevel.ABSTRACT -> "Pensamiento Crítico"
    }
    
    val contentDescription = when (bracket) {
        AgeBracket.EARLY_CHILDHOOD -> "Contenido con analogías simples, juegos táctiles y feedback inmediato"
        AgeBracket.CHILDHOOD -> "Aprendizaje lúdico con desafíos graduales y recompensas visuales"
        AgeBracket.PRE_ADOLESCENT -> "Conceptos técnicos introductorios con ejemplos del mundo real"
        AgeBracket.ADOLESCENT_EARLY -> "Análisis crítico y problemas de aplicación práctica"
        AgeBracket.ADOLESCENT_LATE -> "Abstracción compleja y conexiones interdisciplinarias"
        AgeBracket.YOUNG_ADULT -> "Profundización técnica y desarrollo de expertise"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EedaAdaptiveTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Psychology,
                    contentDescription = null,
                    tint = EedaAdaptiveTheme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nivel Cognitivo",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EedaAdaptiveTheme.colors.onSurface
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = cognitiveLevelName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = EedaAdaptiveTheme.colors.primary
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            HorizontalDivider(
                color = EedaAdaptiveTheme.colors.onSurface.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = contentDescription,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = EedaAdaptiveTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

/**
 * Pantalla de selección rápida de edad para onboarding simplificado.
 */
@Composable
fun QuickAgeSelectionScreen(
    onAgeSelected: (Int) -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selecciona tu grupo de edad",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Grupos de edad rápidos
        AgeGroupButton(
            ageRange = 3..5,
            label = "Pequeños exploradores",
            emoji = "🧸",
            color = Color(0xFFFFCC80),
            onClick = { onAgeSelected(4) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AgeGroupButton(
            ageRange = 6..8,
            label = "Curiosos en acción",
            emoji = "🎨",
            color = Color(0xFF80DEEA),
            onClick = { onAgeSelected(7) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AgeGroupButton(
            ageRange = 9..11,
            label = "Jóvenes científicos",
            emoji = "🔬",
            color = Color(0xFFA5D6A7),
            onClick = { onAgeSelected(10) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AgeGroupButton(
            ageRange = 12..14,
            label = "Mentes analíticas",
            emoji = "💻",
            color = Color(0xFFCE93D8),
            onClick = { onAgeSelected(13) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AgeGroupButton(
            ageRange = 15..17,
            label = "Expertos digitales",
            emoji = "🚀",
            color = Color(0xFFEF9A9A),
            onClick = { onAgeSelected(16) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        AgeGroupButton(
            ageRange = 18..20,
            label = "Profesionales tech",
            emoji = "🎓",
            color = Color(0xFFBCAAA4),
            onClick = { onAgeSelected(19) }
        )
    }
}

@Composable
private fun AgeGroupButton(
    ageRange: IntRange,
    label: String,
    emoji: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.3f)
        ),
        border = BorderStroke(2.dp, color)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "${ageRange.first}-${ageRange.last} años",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = color
            )
        }
    }
}
