package com.LBs.EEDA.ui.screens.certifications

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.domain.model.CertificationType
import com.LBs.EEDA.domain.model.EarnedCertification
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificationsScreen(
    earnedCertifications: List<EarnedCertification>,
    onNavigateBack: () -> Unit
) {
    val colors = EedaAdaptiveTheme.colors
    val currentPhase = EedaAdaptiveTheme.phase
    
    // Todas las posibles certificaciones de la fase actual
    val phaseCertTypes = CertificationType.entries.filter { it.requiredPhase == currentPhase }
    val earnedTypes = earnedCertifications.map { it.type }.toSet()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Tus Logros Digitales", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        "Completa todas las habilidades de una categoría para obtener su certificación oficial.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = colors.onBackground.copy(alpha = 0.7f))
                    )
                }

                items(phaseCertTypes) { type ->
                    val earnedCert = earnedCertifications.find { it.type == type }
                    CertificationCard(
                        type = type,
                        earnedAt = earnedCert?.earnedAt,
                        isEarned = earnedTypes.contains(type)
                    )
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun CertificationCard(type: CertificationType, earnedAt: String?, isEarned: Boolean) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEarned) Color(type.badgeColor).copy(alpha = 0.1f) else colors.glassOverlay
        ),
        border = if (isEarned) androidx.compose.foundation.BorderStroke(2.dp, Color(type.badgeColor).copy(0.3f)) else null
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Escudo/Medalla
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = if (isEarned) Color(type.badgeColor) else Color.Gray.copy(0.2f),
                shadowElevation = if (isEarned) 8.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isEarned) Icons.Rounded.WorkspacePremium else Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = if (isEarned) Color.White else Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(Modifier.width(20.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = type.displayName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 0.5.sp),
                    color = if (isEarned) Color(type.badgeColor) else colors.onSurface.copy(0.6f)
                )
                Text(
                    text = type.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface.copy(0.7f),
                    lineHeight = 16.sp
                )
                
                if (isEarned && earnedAt != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = Color(type.badgeColor).copy(0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Obtenido: ${earnedAt.take(10)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(type.badgeColor),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (!isEarned) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Requisitos: ${type.requiredSkillIds.size} habilidades",
                        style = MaterialTheme.typography.labelSmall.copy(color = colors.primary, fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
