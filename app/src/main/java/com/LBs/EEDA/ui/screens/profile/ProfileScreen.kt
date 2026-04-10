package com.LBs.EEDA.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: ChildProfile?,
    onNavigateBack: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            if (profile == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = colors.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        // Avatar y Nombre
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = colors.primary.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Face, null, modifier = Modifier.size(80.dp), tint = colors.primary)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = profile.name.uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = colors.onBackground)
                        )
                        Text(
                            text = profile.highestTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.primary, fontWeight = FontWeight.Bold)
                        )
                    }

                    item {
                        // Stats Principales
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ProfileStatCard("Nivel", profile.currentLevel.toString(), Icons.AutoMirrored.Rounded.TrendingUp, colors.primary)
                            ProfileStatCard("XP Total", profile.totalXp.toString(), Icons.Rounded.Bolt, colors.accent)
                            ProfileStatCard("Racha", "${profile.streakDays} d", Icons.Rounded.LocalFireDepartment, colors.error)
                        }
                    }

                    item {
                        // Sección de Progreso
                        Text(
                            text = "Progreso de Habilidades",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = colors.onBackground)
                        )
                        Spacer(Modifier.height(8.dp))
                        ProgressSection("Lógica y Programación", profile.logicProgress / 100f, colors.primary)
                        ProgressSection("Creatividad Digital", profile.creativityProgress / 100f, colors.secondary)
                        ProgressSection("Seguridad y Ética", profile.problemSolvingProgress / 100f, colors.accent)
                    }

                    item {
                        // Resumen de Actividad
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.glassOverlay)
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Resumen de Aventura", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(Modifier.height(12.dp))
                                ActivityRow("Tiempo de juego", "${profile.totalPlayTimeMinutes} min", Icons.Rounded.Timer)
                                ActivityRow("Habilidades", "${profile.completedSkillIds.size} completadas", Icons.Rounded.AccountTree)
                                ActivityRow("Certificados", "${profile.earnedCertifications.size} obtenidos", Icons.Rounded.EmojiEvents)
                                ActivityRow("Lecciones", "${profile.completedLessonIds.size} terminadas", Icons.AutoMirrored.Rounded.MenuBook)
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(label: String, value: String, icon: ImageVector, color: Color) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.glassOverlay)
    ) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, Modifier.size(24.dp), tint = color)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = colors.onBackground.copy(alpha = 0.6f)))
        }
    }
}

@Composable
private fun ProgressSection(label: String, progress: Float, color: Color) {
    val colors = EedaAdaptiveTheme.colors
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium.copy(color = colors.onBackground.copy(alpha = 0.8f)))
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun ActivityRow(label: String, value: String, icon: ImageVector) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(18.dp), tint = EedaAdaptiveTheme.colors.primary)
        Spacer(Modifier.width(12.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
    }
}
