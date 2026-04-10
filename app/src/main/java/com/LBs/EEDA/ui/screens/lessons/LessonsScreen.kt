package com.LBs.EEDA.ui.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.Lesson
import com.LBs.EEDA.domain.model.SkillCategory
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    viewModel: LessonsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = EedaAdaptiveTheme.colors
    val currentPhase = EedaAdaptiveTheme.phase
    
    // Filtramos por fase y por categoría seleccionada
    val lessons = uiState.filteredLessons.filter { it.phase == currentPhase }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Centro de Aprendizaje", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd)))
                .padding(paddingValues)
        ) {
            Column {
                // Selector de Categorías
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.currentFilterCategory == null,
                            onClick = { viewModel.filterByCategory(null) },
                            label = { Text("Todas") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colors.primary, selectedLabelColor = Color.White)
                        )
                    }
                    items(SkillCategory.entries) { category ->
                        FilterChip(
                            selected = uiState.currentFilterCategory == category.name,
                            onClick = { viewModel.filterByCategory(category.name) },
                            label = { Text(category.displayName) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colors.primary, selectedLabelColor = Color.White)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Expande tu conocimiento digital con estas guías interactivas.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.onBackground.copy(alpha = 0.7f))
                        )
                    }

                    if (lessons.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Rounded.SearchOff, null, Modifier.size(64.dp), tint = Color.Gray.copy(alpha = 0.5f))
                                    Spacer(Modifier.height(16.dp))
                                    Text("No hay lecciones disponibles en esta categoría.", textAlign = TextAlign.Center, color = Color.Gray)
                                }
                            }
                        }
                    }

                    items(lessons) { lesson ->
                        val isCompleted = uiState.completedLessonIds.contains(lesson.id)
                        LessonCard(
                            lesson = lesson,
                            isCompleted = isCompleted,
                            onClick = { selectedLesson = lesson }
                        )
                    }
                    
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), color = colors.primary)
            }

            selectedLesson?.let { lesson ->
                LessonContentDialog(
                    lesson = lesson,
                    onComplete = {
                        viewModel.completeLesson(lesson)
                        selectedLesson = null
                    },
                    onDismiss = { selectedLesson = null }
                )
            }
        }
    }
}

@Composable
fun LessonCard(lesson: Lesson, isCompleted: Boolean, onClick: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) colors.success.copy(alpha = 0.1f) else colors.glassOverlay
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isCompleted) colors.success.copy(alpha = 0.2f) else colors.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.CheckCircle else Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        tint = if (isCompleted) colors.success else colors.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                )
                Text(
                    text = "${lesson.estimatedMinutes} min • ${lesson.category.displayName}",
                    style = MaterialTheme.typography.labelSmall.copy(color = colors.onSurface.copy(alpha = 0.5f))
                )
            }
            
            if (isCompleted) {
                Icon(Icons.Rounded.Verified, null, tint = colors.success, modifier = Modifier.size(24.dp))
            } else {
                Surface(
                    color = colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "+${lesson.xpReward} XP",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = colors.primary),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LessonContentDialog(lesson: Lesson, onComplete: () -> Unit, onDismiss: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("¡He aprendido algo nuevo!")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lightbulb, null, tint = colors.accent)
                Spacer(Modifier.width(12.dp))
                Text(lesson.title, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Surface(
                    color = colors.primary.copy(0.05f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = lesson.description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = colors.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    textAlign = TextAlign.Justify
                )
                Spacer(Modifier.height(16.dp))
                // Tip adicional simulado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.accent.copy(0.1f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.TipsAndUpdates, null, tint = colors.accent, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Dato curioso: ¡Dominar esto te acerca a ser un experto digital!", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        containerColor = colors.surface,
        titleContentColor = colors.onSurface,
        textContentColor = colors.onSurface,
        shape = RoundedCornerShape(28.dp)
    )
}
