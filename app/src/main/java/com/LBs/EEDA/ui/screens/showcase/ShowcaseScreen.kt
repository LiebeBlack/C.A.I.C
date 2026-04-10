package com.LBs.EEDA.ui.screens.showcase

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.LBs.EEDA.domain.model.educational.gamification.GamificationSystem
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowcaseScreen(
    viewModel: ShowcaseViewModel,
    onNavigateBack: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(
                            "Tu Galería Digital", 
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        uiState.userLevel?.let { level ->
                            Text(
                                "${level.title} • Nivel ${level.currentLevel}",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar")
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
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sección de Logros
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Text(
                                text = "Logros (${uiState.achievements.count { it.isUnlocked }}/${uiState.achievements.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            // Barra de progreso
                            LinearProgressIndicator(
                                progress = { 
                                    if (uiState.achievements.isNotEmpty()) {
                                        uiState.achievements.count { it.isUnlocked }.toFloat() / uiState.achievements.size
                                    } else 0f
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = colors.primary,
                                trackColor = colors.primary.copy(alpha = 0.2f)
                            )
                        }
                    }
                    
                    items(uiState.achievements) { item ->
                        AchievementCard(item = item)
                    }
                    
                    // Sección de Certificaciones
                    if (uiState.certifications.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Text(
                                text = "Certificaciones",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        
                        items(uiState.certifications) { cert ->
                            CertificationCard(cert = cert)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(item: ShowcaseItem) {
    val colors = EedaAdaptiveTheme.colors
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isPressed) 0.95f else 1f, 
        spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val cardColor = when {
        !item.isUnlocked -> colors.glassOverlay.copy(alpha = 0.5f)
        item.rarity == GamificationSystem.AchievementRarity.LEGENDARY -> 
            Color(0xFFFFD700).copy(alpha = 0.15f)
        item.rarity == GamificationSystem.AchievementRarity.EPIC -> 
            Color(0xFF9C27B0).copy(alpha = 0.15f)
        item.rarity == GamificationSystem.AchievementRarity.RARE -> 
            Color(0xFF2196F3).copy(alpha = 0.15f)
        else -> colors.glassOverlay
    }
    
    val borderColor = when {
        !item.isUnlocked -> colors.onSurface.copy(alpha = 0.1f)
        item.rarity == GamificationSystem.AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
        item.rarity == GamificationSystem.AchievementRarity.EPIC -> Color(0xFF9C27B0)
        item.rarity == GamificationSystem.AchievementRarity.RARE -> Color(0xFF2196F3)
        else -> colors.primary.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .aspectRatio(0.85f)
            .scale(scale)
            .clickable { isPressed = !isPressed },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(if (item.isUnlocked) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icono del logro
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (item.isUnlocked) {
                            when (item.rarity) {
                                GamificationSystem.AchievementRarity.LEGENDARY -> Color(0xFFFFD700).copy(alpha = 0.2f)
                                GamificationSystem.AchievementRarity.EPIC -> Color(0xFF9C27B0).copy(alpha = 0.2f)
                                GamificationSystem.AchievementRarity.RARE -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                else -> colors.primary.copy(alpha = 0.15f)
                            }
                        } else colors.onSurface.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.iconEmoji,
                    fontSize = if (item.isUnlocked) 48.sp else 32.sp,
                    modifier = Modifier.alpha(if (item.isUnlocked) 1f else 0.4f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Título
            Text(
                item.title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = if (item.isUnlocked) FontWeight.Bold else FontWeight.Normal,
                color = if (item.isUnlocked) colors.onBackground else colors.onBackground.copy(alpha = 0.5f),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            
            // Rareza
            Text(
                item.level, 
                style = MaterialTheme.typography.labelSmall,
                color = when (item.rarity) {
                    GamificationSystem.AchievementRarity.LEGENDARY -> Color(0xFFFFD700)
                    GamificationSystem.AchievementRarity.EPIC -> Color(0xFF9C27B0)
                    GamificationSystem.AchievementRarity.RARE -> Color(0xFF2196F3)
                    else -> colors.onBackground.copy(alpha = 0.6f)
                }
            )
            
            // Estado
            if (!item.isUnlocked) {
                Text(
                    "🔒 Bloqueado",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onBackground.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun CertificationCard(cert: CertificationItem) {
    val colors = EedaAdaptiveTheme.colors
    
    Card(
        modifier = Modifier
            .aspectRatio(0.85f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (cert.tier) {
                3 -> Color(0xFFFFD700).copy(alpha = 0.15f) // Oro
                2 -> Color(0xFFC0C0C0).copy(alpha = 0.15f) // Plata
                else -> Color(0xFFCD7F32).copy(alpha = 0.15f) // Bronce
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            when (cert.tier) {
                3 -> Color(0xFFFFD700)
                2 -> Color(0xFFC0C0C0)
                else -> Color(0xFFCD7F32)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (cert.tier) {
                            3 -> Color(0xFFFFD700).copy(alpha = 0.2f)
                            2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                            else -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(cert.emoji, fontSize = 48.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                cert.title, 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.Bold,
                color = colors.onBackground,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            
            Text(
                when (cert.tier) {
                    3 -> "🥇 Oro"
                    2 -> "🥈 Plata"
                    else -> "🥉 Bronce"
                },
                style = MaterialTheme.typography.labelSmall,
                color = when (cert.tier) {
                    3 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    else -> Color(0xFFCD7F32)
                }
            )
        }
    }
}
