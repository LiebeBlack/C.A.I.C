package com.LBs.EEDA.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.domain.model.ChildProfile
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.model.CertificationType
import com.LBs.EEDA.domain.model.PerformanceTier
import com.LBs.EEDA.ui.components.*
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme
import com.LBs.EEDA.ui.theme.LocalHardwareProfile

@Suppress("DEPRECATION")
@Composable
fun HomeScreen(
    profile: ChildProfile?,
    onNavigateToLevels: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToShowcase: () -> Unit,
    onNavigateToSkillTree: () -> Unit = {},
    onNavigateToLessons: () -> Unit = {},
    onNavigateToCertifications: () -> Unit = {},
    onNavigateToEducationalModules: () -> Unit = {}
) {
    val colors = EedaAdaptiveTheme.colors
    val hardware = LocalHardwareProfile.current
    val phase = EedaAdaptiveTheme.phase
    val name = profile?.name ?: "Explorador"
    val badges = profile?.earnedBadges?.size ?: 0

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { AdaptiveTopBar(name, badges, phase, onNavigateToProfile, onNavigateToSettings) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Optimización: Usar color sólido en lugar de degradado en gama baja
                .background(
                    brush = if (hardware.tier == PerformanceTier.LOW) {
                        Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientStart))
                    } else {
                        Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd))
                    }
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { AdaptiveWelcomeHero(phase) }
                item { 
                    val phaseProgress = remember(profile) {
                        if (profile == null) 0f else {
                            val phaseCerts = CertificationType.entries.filter { it.requiredPhase == profile.currentPhase }
                            val earnedPhaseCerts = profile.earnedCertifications.count { it.type.requiredPhase == profile.currentPhase }
                            if (phaseCerts.isEmpty()) 0f else earnedPhaseCerts.toFloat() / phaseCerts.size
                        }
                    }
                    PhaseIndicator(phase = phase, compact = false, progress = phaseProgress) 
                }
                item { QuickStatsRow(profile) }
                item { MainActionsGrid(phase, onNavigateToLevels, onNavigateToSkillTree, onNavigateToLessons, onNavigateToCertifications, onNavigateToShowcase, onNavigateToEducationalModules) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun AdaptiveTopBar(name: String, badges: Int, phase: DigitalPhase, onProfile: () -> Unit, onSettings: () -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    val config = EedaAdaptiveTheme.typoConfig
    val hardware = LocalHardwareProfile.current
    
    // Optimización: Desactivar entrada animada en gama baja para ahorrar ciclos de CPU al iniciar
    var visible by remember { mutableStateOf(hardware.tier == PerformanceTier.LOW) }
    LaunchedEffect(Unit) { if (!visible) visible = true }

    val content = @Composable {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AdaptiveGlassCard(modifier = Modifier.weight(1f).clickable { onProfile() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(40.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.2f)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Face, null, tint = colors.primary, modifier = Modifier.size(24.dp)) }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = when (phase) { 
                                DigitalPhase.SENSORIAL -> "¡HOLA!"
                                DigitalPhase.CREATIVE -> "Hey!"
                                DigitalPhase.PROFESSIONAL -> "Bienvenido"
                                DigitalPhase.INNOVATOR -> "Saludos"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(color = colors.onBackground.copy(alpha = 0.6f))
                        )
                        Text(name.uppercase(), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onBackground), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Surface(color = colors.primary.copy(alpha = 0.12f), shape = RoundedCornerShape(config.borderRadius.dp)) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, null, tint = colors.accent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("$badges", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = colors.onBackground))
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface(modifier = Modifier.size(44.dp).clickable { onSettings() }, shape = CircleShape, color = colors.glassOverlay) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Settings, "Ajustes", tint = colors.onBackground) }
            }
        }
    }

    if (hardware.tier == PerformanceTier.LOW) {
        content()
    } else {
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(600)) + slideInVertically { -it / 5 }) {
            content()
        }
    }
}

@Composable
private fun AdaptiveWelcomeHero(phase: DigitalPhase) {
    val colors = EedaAdaptiveTheme.colors
    val icon = when (phase) {
        DigitalPhase.SENSORIAL -> Icons.Rounded.Park
        DigitalPhase.CREATIVE -> Icons.Rounded.Palette
        DigitalPhase.PROFESSIONAL -> Icons.Rounded.Work
        DigitalPhase.INNOVATOR -> Icons.Rounded.RocketLaunch
    }
    val subtitle = when (phase) {
        DigitalPhase.SENSORIAL -> "¡TU AVENTURA COMIENZA AQUÍ!"
        DigitalPhase.CREATIVE -> "CREA, APRENDE, CONECTA"
        DigitalPhase.PROFESSIONAL -> "TU FUTURO DIGITAL"
        DigitalPhase.INNOVATOR -> "LIDERAZGO Y FUTURO"
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, Modifier.size(100.dp), tint = colors.primary)
        Text("E.E.D.A", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = colors.onBackground))
        Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(color = colors.onBackground.copy(alpha = 0.6f)))
    }
}

@Composable
private fun QuickStatsRow(profile: ChildProfile?) {
    if (profile == null) return
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        QuickStat("Nivel", "${profile.currentLevel}", Icons.AutoMirrored.Rounded.TrendingUp)
        QuickStat("Medallas", "${profile.earnedBadges.size}", Icons.Rounded.Star)
        QuickStat("Tiempo", "${profile.totalPlayTimeMinutes}m", Icons.Rounded.Timer)
    }
}

@Composable
private fun QuickStat(label: String, value: String, icon: ImageVector) {
    val colors = EedaAdaptiveTheme.colors
    AdaptiveGlassCard {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 12.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = colors.primary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onBackground))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = colors.onBackground.copy(alpha = 0.6f)))
        }
    }
}

@Composable
private fun MainActionsGrid(
    phase: DigitalPhase, onLevels: () -> Unit, onSkillTree: () -> Unit,
    onLessons: () -> Unit, onCerts: () -> Unit, onShowcase: () -> Unit,
    onEducationalModules: () -> Unit = {}
) {
    val colors = EedaAdaptiveTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BigButton(
            icon = Icons.Rounded.PlayArrow, 
            label = when (phase) { 
                DigitalPhase.SENSORIAL -> "JUGAR"
                DigitalPhase.CREATIVE -> "EXPLORAR"
                DigitalPhase.PROFESSIONAL -> "INICIAR"
                DigitalPhase.INNOVATOR -> "LIDERAR"
            }, 
            color = colors.primary, 
            onClick = onLevels
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigButton(icon = Icons.Rounded.AccountTree, label = "HABILIDADES", color = colors.secondary, onClick = onSkillTree, modifier = Modifier.weight(1f))
            BigButton(icon = Icons.AutoMirrored.Rounded.MenuBook, label = "LECCIONES", color = colors.accent, onClick = onLessons, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigButton(icon = Icons.Rounded.EmojiEvents, label = "CERTIFICADOS", color = colors.success, onClick = onCerts, modifier = Modifier.weight(1f))
            BigButton(icon = Icons.Rounded.Image, label = "ÁLBUM", color = colors.info, onClick = onShowcase, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigButton(icon = Icons.Rounded.School, label = "EDUCATIVO 2026", color = colors.primary.copy(alpha = 0.8f), onClick = onEducationalModules, modifier = Modifier.fillMaxWidth())
        }
    }
}
