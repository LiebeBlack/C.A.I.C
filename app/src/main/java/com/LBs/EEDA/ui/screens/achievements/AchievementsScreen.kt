package com.LBs.EEDA.ui.screens.achievements

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.domain.model.gamification.Achievement
import com.LBs.EEDA.domain.model.gamification.AchievementCategory
import com.LBs.EEDA.ui.components.AchievementCard
import com.LBs.EEDA.ui.components.LoadingSpinner
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = EedaAdaptiveTheme.colors

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("🏆 Logros", fontWeight = FontWeight.Bold)
                        Text(
                            "${uiState.unlockedCount} / ${uiState.totalCount} desbloqueados",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.background
                )
            )
        }
    ) { padding ->
        when (uiState) {
            is AchievementsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    LoadingSpinner()
                }
            }

            is AchievementsUiState.Success -> {
                val state = uiState as AchievementsUiState.Success

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Filtros por categoría
                    CategoryFilter(
                        selectedCategory = state.selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )

                    // Estadísticas
                    StatsRow(
                        totalXp = state.totalXpEarned,
                        nextAchievement = state.nextAchievement
                    )

                    // Lista de logros
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.filteredAchievements,
                            key = { it.id }
                        ) { achievement ->
                            AchievementCard(
                                achievement = achievement,
                                onClick = { viewModel.onAchievementClick(achievement) }
                            )
                        }
                    }
                }
            }

            is AchievementsUiState.Error -> {
                ErrorState(
                    message = (uiState as AchievementsUiState.Error).message,
                    onRetry = { viewModel.loadAchievements() }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilter(
    selectedCategory: AchievementCategory?,
    onCategorySelected: (AchievementCategory?) -> Unit
) {
    val categories = AchievementCategory.entries

    ScrollableTabRow(
        selectedTabIndex = selectedCategory?.ordinal?.plus(1) ?: 0,
        edgePadding = 16.dp,
        containerColor = EedaAdaptiveTheme.colors.background
    ) {
        Tab(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            text = { Text("Todos") }
        )

        categories.forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(getCategoryName(category)) }
            )
        }
    }
}

@Composable
private fun StatsRow(totalXp: Long, nextAchievement: String?) {
    val colors = EedaAdaptiveTheme.colors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "XP Total",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    "$totalXp",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }

        if (nextAchievement != null) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Próximo",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        nextAchievement,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text("❌", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

private fun getCategoryName(category: AchievementCategory): String {
    return when (category) {
        AchievementCategory.LEARNING -> "Aprendizaje"
        AchievementCategory.SKILL -> "Habilidades"
        AchievementCategory.SOCIAL -> "Social"
        AchievementCategory.EXPLORATION -> "Exploración"
        AchievementCategory.MASTERY -> "Maestría"
        AchievementCategory.STREAK -> "Rachas"
        AchievementCategory.SPECIAL -> "Especial"
    }
}
