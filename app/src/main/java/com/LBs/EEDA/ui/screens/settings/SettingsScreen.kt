package com.LBs.EEDA.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.LBs.EEDA.ui.components.AdaptiveCard
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToParentMode: () -> Unit = {}
) {
    val colors = EedaAdaptiveTheme.colors
    var soundEnabled by remember { mutableStateOf(true) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Configuración", color = colors.onBackground, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Volver", tint = colors.onBackground) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // General
            item {
                AdaptiveCard(Modifier.fillMaxWidth()) {
                    Column {
                        Text("General", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onSurface))
                        Spacer(Modifier.height(8.dp))
                        SettingsToggle(Icons.Rounded.VolumeUp, "Sonido", soundEnabled) { soundEnabled = it }
                        SettingsToggle(Icons.Rounded.Vibration, "Vibración háptica", hapticEnabled) { hapticEnabled = it }
                        SettingsToggle(Icons.Rounded.Notifications, "Notificaciones", notificationsEnabled) { notificationsEnabled = it }
                    }
                }
            }

            // Idioma
            item {
                AdaptiveCard(Modifier.fillMaxWidth()) {
                    SettingsNavRow(Icons.Rounded.Language, "Idioma", "Español")
                }
            }

            // Modo Acompañante
            item {
                AdaptiveCard(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToParentMode() }
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.FamilyRestroom, null, Modifier.size(24.dp), tint = colors.primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Modo Acompañante", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onSurface))
                            Text("Panel parental con controles de tiempo y contenido", style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.6f)))
                        }
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, Modifier.size(20.dp), tint = colors.primary)
                    }
                }
            }

            // Info
            item {
                AdaptiveCard(Modifier.fillMaxWidth()) {
                    Column {
                        Text("Acerca de", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = colors.onSurface))
                        Spacer(Modifier.height(8.dp))
                        SettingsNavRow(Icons.Rounded.Info, "Versión", "26.4.6")
                        SettingsNavRow(Icons.Rounded.Code, "Desarrollado por", "Yoangel Gómez (Liebe Black)")
                        SettingsNavRow(Icons.Rounded.Public, "Isla de Margarita", "Venezuela 🇻🇪")
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun SettingsToggle(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = colors.primary)
        Spacer(Modifier.width(12.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface))
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = colors.primary, checkedTrackColor = colors.primary.copy(alpha = 0.3f)))
    }
}

@Composable
private fun SettingsNavRow(icon: ImageVector, label: String, value: String) {
    val colors = EedaAdaptiveTheme.colors
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(20.dp), tint = colors.primary)
        Spacer(Modifier.width(12.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium.copy(color = colors.onSurface))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurface.copy(alpha = 0.6f)))
    }
}
