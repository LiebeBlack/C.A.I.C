package com.liebeblack.isla_digital.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.liebeblack.isla_digital.domain.model.ChildProfile
import com.liebeblack.isla_digital.ui.components.BigButton
import com.liebeblack.isla_digital.ui.components.GlassContainer
import com.liebeblack.isla_digital.ui.theme.IslaColors

/**
 * Pantalla Principal (HomeScreen) 2026.
 * Migrada de Flutter a Kotlin/Compose con animaciones nativas.
 */
@Composable
fun HomeScreen(
    profile: ChildProfile?,
    onNavigateToLevels: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToShowcase: () -> Unit
) {
    val profileName = profile?.name ?: "Explorador"
    val badgesCount = profile?.earnedBadges?.size ?: 0

    Scaffold(
        containerColor = Color.Transparent, // El fondo lo pone el IslandBackground
        topBar = {
            TopBar(
                name = profileName,
                badgesCount = badgesCount,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { WelcomeHero() }
                
                item { Spacer(modifier = Modifier.height(30.dp)) }
                
                item {
                    ActionsGrid(
                        onPlay = onNavigateToLevels,
                        onBadges = { /* Mostrar diálogo */ },
                        onAlbum = onNavigateToShowcase
                    )
                }
                
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }

            // Botón de ajustes en la esquina superior derecha (flotante)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                SettingsButton(onClick = onNavigateToSettings)
            }
            
            // Personajes en la parte inferior (Simulado, en Flutter era Lottie)
            BottomCharacters()
        }
    }
}

@Composable
fun TopBar(name: String, badgesCount: Int, onProfileClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + slideInVertically { -it / 5 }
    ) {
        Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            GlassContainer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 56.dp) // Espacio para el botón de ajustes
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onProfileClick() }
                ) {
                    AvatarCircle()
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡HOLA!",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = name.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                    BadgePill(count = badgesCount)
                }
            }
        }
    }
}

@Composable
fun AvatarCircle() {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = IslaColors.Sunflower
    ) {
        Icon(
            imageVector = Icons.Rounded.Face,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun BadgePill(count: Int) {
    Surface(
        color = Color.White.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = IslaColors.Sunflower,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = IslaColors.Charcoal
                )
            )
        }
    }
}

@Composable
fun WelcomeHero() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // En 2026 usaríamos Compose-Lottie o similar
        Icon(
            imageVector = Icons.Rounded.Home,
            contentDescription = null,
            modifier = Modifier.size(180.dp),
            tint = IslaColors.JungleGreen
        )
        Text(
            text = "ISLA DIGITAL",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "¡TU AVENTURA COMIENZA AQUÍ!",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ActionsGrid(onPlay: () -> Unit, onBadges: () -> Unit, onAlbum: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        BigButton(
            icon = Icons.Rounded.PlayArrow,
            label = "JUGAR",
            color = IslaColors.JungleGreen,
            onClick = onPlay
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            BigButton(
                icon = Icons.Rounded.Star,
                label = "LOGROS",
                color = IslaColors.Sunflower,
                onClick = onBadges,
                modifier = Modifier.weight(1f)
            )
            BigButton(
                icon = Icons.Rounded.Image,
                label = "ÁLBUM",
                color = IslaColors.OceanBlue,
                onClick = onAlbum,
                modifier = Modifier.weight(1f)
            )
        }
        
        BigButton(
            icon = Icons.Rounded.Celebration,
            label = "EVENTO ESPECIAL",
            color = IslaColors.SunsetPink,
            onClick = { /* Navegar a evento */ }
        )
    }
}

@Composable
fun SettingsButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Ajustes",
                tint = IslaColors.OceanDark
            )
        }
    }
}

@Composable
fun BottomCharacters() {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PersonajesFlotantes"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Icon(
                Icons.Rounded.Face, 
                contentDescription = "Personaje Izquierdo", 
                Modifier.size(100.dp).offset(y = offsetY.dp), 
                tint = IslaColors.TropicOrange
            )
            Icon(
                Icons.Rounded.Face, 
                contentDescription = "Personaje Derecho", 
                Modifier.size(100.dp).offset(y = -offsetY.dp), 
                tint = IslaColors.OceanBlue
            )
        }
    }
}