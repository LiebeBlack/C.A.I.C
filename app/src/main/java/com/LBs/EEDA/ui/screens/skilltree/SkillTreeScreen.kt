package com.LBs.EEDA.ui.screens.skilltree

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.SkillCategory
import com.LBs.EEDA.domain.model.SkillNode
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillTreeScreen(
    uiState: SkillTreeUiState,
    onCategorySelected: (SkillCategory?) -> Unit,
    onSkillSelected: (SkillNode?) -> Unit,
    onUnlockSkill: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val currentPhase = EedaAdaptiveTheme.phase

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Árbol de Habilidades", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
                // Filtro de categorías
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { onCategorySelected(null) },
                            label = { Text("Todas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(SkillCategory.entries) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = colors.primary,
                                selectedLabelColor = Color.White
                            )
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
                            "Desbloquea tu potencial digital paso a paso.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = colors.onBackground.copy(alpha = 0.7f))
                        )
                    }

                    val phaseSkills = uiState.filteredSkills.filter { it.requiredPhase == currentPhase }
                    items(phaseSkills) { skill ->
                        val isCompleted = uiState.completedSkillIds.contains(skill.id)
                        val isUnlocked = uiState.unlockedSkillIds.contains(skill.id)

                        SkillItem(
                            skill = skill,
                            isUnlocked = isUnlocked,
                            isCompleted = isCompleted,
                            onClick = { onSkillSelected(skill) }
                        )
                    }
                    
                    if (phaseSkills.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No hay habilidades en esta categoría para tu nivel.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }
            }

            // Modal de Habilidad
            uiState.selectedSkill?.let { skill ->
                val isUnlocked = uiState.unlockedSkillIds.contains(skill.id)
                val isCompleted = uiState.completedSkillIds.contains(skill.id)

                AlertDialog(
                    onDismissRequest = { onSkillSelected(null) },
                    confirmButton = {
                        if (isUnlocked && !isCompleted) {
                            Button(
                                onClick = { onUnlockSkill(skill.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                            ) {
                                if (uiState.isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text("Completar (+${skill.xpRequired} XP)")
                                }
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onSkillSelected(null) }) {
                            Text("Cerrar")
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when(skill.category) {
                                    SkillCategory.SECURITY -> Icons.Rounded.Shield
                                    SkillCategory.PROGRAMMING -> Icons.Rounded.Code
                                    SkillCategory.CONTENT_CREATION -> Icons.Rounded.Palette
                                    else -> Icons.Rounded.Star
                                },
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(skill.name, fontWeight = FontWeight.Bold)
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = skill.category.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(skill.description, style = MaterialTheme.typography.bodyMedium)
                            
                            if (!isUnlocked) {
                                Spacer(Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Lock, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Bloqueado. Completa las habilidades previas.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
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
            
            // Error Message
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { /* dismiss handled by some logic? */ }) { Text("OK") }
                    }
                ) { Text(error) }
            }
        }
    }
}

@Composable
fun SkillItem(
    skill: SkillNode,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) colors.primary.copy(alpha = 0.15f) 
                            else if (isUnlocked) colors.glassOverlay 
                            else Color.Black.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (isCompleted) colors.primary else if (isUnlocked) colors.secondary.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Rounded.Check else if (isUnlocked) Icons.Rounded.LockOpen else Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = if (isCompleted) Color.White else if (isUnlocked) colors.secondary else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) colors.onSurface else Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = skill.category.displayName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = (if (isUnlocked) colors.onSurface else Color.Gray).copy(alpha = 0.6f)
                    )
                )
            }
            
            if (isUnlocked && !isCompleted) {
                Surface(
                    color = colors.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "+${skill.xpRequired} XP",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = colors.primary),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else if (isCompleted) {
                Icon(Icons.Rounded.Verified, null, tint = colors.primary, modifier = Modifier.size(24.dp))
            }
        }
    }
}
