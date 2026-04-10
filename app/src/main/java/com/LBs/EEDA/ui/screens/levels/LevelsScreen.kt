package com.LBs.EEDA.ui.screens.levels

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import com.LBs.EEDA.domain.model.LevelData
import com.LBs.EEDA.domain.model.GameLevel
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(
    repository: ChildProfileRepository,
    onNavigateBack: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val currentPhase = EedaAdaptiveTheme.phase
    val levels = LevelData.getLevels().filter { it.phase == currentPhase }
    val scope = rememberCoroutineScope()

    var completedIds by remember { mutableStateOf(emptySet<Int>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar progreso inicial
    LaunchedEffect(Unit) {
        repository.getProfile().collect { result ->
            result.onSuccess { profile ->
                completedIds = profile?.completedMapLevelIds?.toSet() ?: emptySet()
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Aventuras", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Text(
                            "Supera retos para ganar XP y desbloquear el siguiente nivel.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.onBackground.copy(alpha = 0.7f))
                        )
                    }

                    items(levels) { level ->
                        val isCompleted = completedIds.contains(level.id)
                        LevelCard(
                            level = level,
                            isCompleted = isCompleted,
                            onClick = {
                                scope.launch {
                                    repository.getProfile().firstOrNull()?.onSuccess { profile ->
                                        profile?.let {
                                            val updated = it.finishMapLevel(level.id, level.xpReward)
                                            repository.saveProfile(updated)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: GameLevel, isCompleted: Boolean, onClick: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) colors.primary.copy(alpha = 0.1f) else colors.glassOverlay
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                color = if (isCompleted) colors.primary else colors.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.Verified else when(level.icon) {
                            "touch" -> Icons.Rounded.TouchApp
                            "swipe" -> Icons.Rounded.Swipe
                            "security" -> Icons.Rounded.Security
                            "code" -> Icons.Rounded.Code
                            "ai" -> Icons.Rounded.SmartToy
                            else -> Icons.Rounded.PlayArrow
                        },
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else colors.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(20.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = level.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                )
                Text(
                    text = level.description,
                    style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.6f)),
                    maxLines = 2
                )
            }
            
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                Surface(
                    color = colors.secondary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Star, null, tint = colors.accent, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${level.difficulty}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = colors.secondary)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "+${level.xpReward} XP",
                    style = MaterialTheme.typography.labelMedium.copy(color = colors.primary, fontWeight = FontWeight.Black)
                )
            }
        }
    }
}
