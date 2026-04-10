package com.LBs.EEDA.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.LBs.EEDA.domain.model.Lesson
import com.LBs.EEDA.domain.model.SkillNode
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToSkill: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = EedaAdaptiveTheme.colors

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("Buscar", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Buscar lecciones, habilidades...") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearQuery() }) {
                            Icon(Icons.Rounded.Close, null)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.search() }
                ),
                shape = MaterialTheme.shapes.large
            )

            // Filter chips
            ScrollableFilterChips(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { viewModel.onFilterChange(it) }
            )

            // Results
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }

                uiState.query.isEmpty() -> {
                    SearchSuggestions(
                        recentSearches = uiState.recentSearches,
                        popularSearches = uiState.popularSearches,
                        onRecentClick = { viewModel.onQueryChange(it) },
                        onPopularClick = { viewModel.onQueryChange(it) }
                    )
                }

                uiState.results.isEmpty() -> {
                    EmptySearchState(query = uiState.query)
                }

                else -> {
                    SearchResultsList(
                        results = uiState.results,
                        onLessonClick = { lessonResult -> onNavigateToLesson(lessonResult.id) },
                        onSkillClick = { skillResult -> onNavigateToSkill(skillResult.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScrollableFilterChips(
    selectedFilter: SearchFilter,
    onFilterSelected: (SearchFilter) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = SearchFilter.entries.indexOf(selectedFilter),
        edgePadding = 16.dp,
        containerColor = EedaAdaptiveTheme.colors.background
    ) {
        SearchFilter.entries.forEach { filter ->
            Tab(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        when (filter) {
                            SearchFilter.ALL -> "Todo"
                            SearchFilter.LESSONS -> "Lecciones"
                            SearchFilter.SKILLS -> "Habilidades"
                            SearchFilter.CERTIFICATIONS -> "Certificaciones"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchSuggestions(
    recentSearches: List<String>,
    popularSearches: List<String>,
    onRecentClick: (String) -> Unit,
    onPopularClick: (String) -> Unit
) {
    val colors = EedaAdaptiveTheme.colors

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (recentSearches.isNotEmpty()) {
            item {
                Text(
                    text = "Búsquedas recientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            items(recentSearches) { search ->
                RecentSearchItem(
                    query = search,
                    onClick = { onRecentClick(search) }
                )
            }
        }

        if (popularSearches.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Búsquedas populares",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularSearches.forEach { search ->
                        SuggestionChip(
                            onClick = { onPopularClick(search) },
                            label = { Text(search) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchItem(query: String, onClick: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = null,
                tint = colors.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = query,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp
            )

            Icon(
                imageVector = Icons.Rounded.NorthWest,
                contentDescription = null,
                tint = colors.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SearchResultsList(
    results: List<SearchResult>,
    onLessonClick: (SearchResult.LessonResult) -> Unit,
    onSkillClick: (SearchResult.SkillResult) -> Unit
) {
    val colors = EedaAdaptiveTheme.colors

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results) { result ->
            when (result) {
                is SearchResult.LessonResult -> LessonResultItem(
                    result = result,
                    onClick = { onLessonClick(result) }
                )
                is SearchResult.SkillResult -> SkillResultItem(
                    result = result,
                    onClick = { onSkillClick(result) }
                )
            }
        }
    }
}

@Composable
private fun LessonResultItem(result: SearchResult.LessonResult, onClick: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = result.description,
                    fontSize = 14.sp,
                    color = colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${result.duration} min",
                        fontSize = 12.sp,
                        color = colors.primary
                    )
                    Text(
                        text = "+${result.xpReward} XP",
                        fontSize = 12.sp,
                        color = colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun SkillResultItem(result: SearchResult.SkillResult, onClick: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colors.secondary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountTree,
                    contentDescription = null,
                    tint = colors.secondary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = result.description,
                    fontSize = 14.sp,
                    color = colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 2
                )

                if (result.isUnlocked) {
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Desbloqueada",
                            fontSize = 11.sp,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState(query: String) {
    val colors = EedaAdaptiveTheme.colors

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.SearchOff,
            contentDescription = null,
            tint = colors.onBackground.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No se encontraron resultados",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onBackground
        )

        Text(
            text = "Intenta con \"$query\"",
            fontSize = 14.sp,
            color = colors.onBackground.copy(alpha = 0.6f)
        )
    }
}

// Search result types
sealed class SearchResult {
    data class LessonResult(
        val id: String,
        val title: String,
        val description: String,
        val duration: Int,
        val xpReward: Long,
        val category: String
    ) : SearchResult()

    data class SkillResult(
        val id: String,
        val name: String,
        val description: String,
        val isUnlocked: Boolean,
        val requiredPhase: String
    ) : SearchResult()
}

enum class SearchFilter {
    ALL, LESSONS, SKILLS, CERTIFICATIONS
}
