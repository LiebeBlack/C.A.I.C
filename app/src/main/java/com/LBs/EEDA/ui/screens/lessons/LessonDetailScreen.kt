package com.LBs.EEDA.ui.screens.lessons

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.Lesson
import com.LBs.EEDA.ui.components.BigButton
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import com.LBs.EEDA.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lesson: Lesson?,
    isCompleted: Boolean,
    onNavigateBack: () -> Unit,
    onStartLesson: () -> Unit,
    onMarkComplete: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors

    if (lesson == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = colors.primary)
        }
        return
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Lección", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                ),
                actions = {
                    if (isCompleted) {
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.CheckCircle,
                                    null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Completada",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = colors.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isCompleted) {
                        Button(
                            onClick = onStartLesson,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.PlayArrow, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar Lección")
                        }
                    } else {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Volver al listado")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with gradient
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    colors.primary.copy(alpha = 0.8f),
                                    colors.secondary.copy(alpha = 0.6f)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        // Category badge
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = lesson.category.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = lesson.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.description,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Meta info
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MetaItem(
                                icon = Icons.Rounded.Schedule,
                                text = "${lesson.estimatedMinutes} min"
                            )
                            MetaItem(
                                icon = Icons.Rounded.Bolt,
                                text = "+${lesson.xpReward} XP"
                            )
                        }
                    }
                }
            }

            // Content section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Contenido",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = lesson.content,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = colors.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Objectives
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Objetivos de aprendizaje",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sample objectives based on category
                        val objectives = getObjectivesForCategory(lesson.category)

                        objectives.forEachIndexed { index, objective ->
                            ObjectiveItem(
                                number = index + 1,
                                text = objective,
                                isCompleted = isCompleted
                            )

                            if (index < objectives.size - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Activities
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Actividades incluidas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val activities = getActivitiesForCategory(lesson.category)

                        activities.forEach { activity ->
                            ActivityItem(activity = activity)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Completion status
            if (isCompleted) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "¡Lección completada!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                                Text(
                                    text = "Has ganado ${lesson.xpReward} XP",
                                    fontSize = 14.sp,
                                    color = colors.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun MetaItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun ObjectiveItem(number: Int, text: String, isCompleted: Boolean) {
    val colors = EedaAdaptiveTheme.colors

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = if (isCompleted) Color(0xFF4CAF50).copy(alpha = 0.2f)
            else colors.primary.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = number.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            color = colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActivityItem(activity: String) {
    val colors = EedaAdaptiveTheme.colors

    Surface(
        color = colors.primary.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = activity,
                fontSize = 15.sp,
                color = colors.onSurface
            )
        }
    }
}

private fun getObjectivesForCategory(category: com.LBs.EEDA.domain.model.SkillCategory): List<String> {
    return when (category) {
        com.LBs.EEDA.domain.model.SkillCategory.DEVICE_TOOLS -> listOf(
            "Comprender el funcionamiento básico de dispositivos",
            "Aplicar buenas prácticas de cuidado",
            "Navegar con gestos eficientes"
        )
        com.LBs.EEDA.domain.model.SkillCategory.SECURITY -> listOf(
            "Identificar amenazas digitales",
            "Proteger información personal",
            "Responder ante situaciones de riesgo"
        )
        com.LBs.EEDA.domain.model.SkillCategory.COMMUNICATION -> listOf(
            "Comunicarse efectivamente online",
            "Entender lenguaje digital",
            "Mantener etiqueta digital"
        )
        else -> listOf(
            "Comprender conceptos fundamentales",
            "Aplicar conocimientos prácticos",
            "Desarrollar habilidades relevantes"
        )
    }
}

private fun getActivitiesForCategory(category: com.LBs.EEDA.domain.model.SkillCategory): List<String> {
    return when (category) {
        com.LBs.EEDA.domain.model.SkillCategory.DEVICE_TOOLS -> listOf(
            "Simulación de gestos",
            "Juego de cuidado de dispositivo",
            "Práctica de navegación"
        )
        com.LBs.EEDA.domain.model.SkillCategory.SECURITY -> listOf(
            "Identificación de amenazas",
            "Quiz de seguridad",
            "Escenarios de práctica"
        )
        else -> listOf(
            "Lectura interactiva",
            "Quiz de comprensión",
            "Ejercicio práctico"
        )
    }
}
