package com.LBs.EEDA.ui.screens.parent

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.LBs.EEDA.domain.model.ContentFilterLevel
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: ParentViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = EedaAdaptiveTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd)))
    ) {
        if (!uiState.isAuthSuccess) {
            ParentAuthScreen(
                error = uiState.error,
                onAuth = { viewModel.authenticate(it) },
                onBack = onNavigateBack
            )
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text("Panel de Control Parental", fontWeight = FontWeight.Black) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.logoutParent() }) {
                                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Cerrar sesión parental")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Resumen del Niño
                    item {
                        ChildSummaryCard(
                            name = uiState.profile?.name ?: "Usuario",
                            level = uiState.profile?.currentLevel ?: 1,
                            xp = uiState.profile?.totalXp ?: 0,
                            time = uiState.profile?.totalPlayTimeMinutes ?: 0
                        )
                    }

                    // Configuración de Tiempo
                    item {
                        SectionHeader("Gestión de Tiempo")
                        TimeLimitControl(
                            currentLimit = uiState.profile?.parentConfig?.dailyTimeLimitMinutes ?: 60,
                            onLimitChange = { viewModel.updateScreenTimeLimit(it) }
                        )
                    }

                    // Filtros de Contenido
                    item {
                        SectionHeader("Filtros de Contenido")
                        ContentFilterSelector(
                            currentLevel = uiState.profile?.parentConfig?.contentFilterLevel ?: ContentFilterLevel.MODERATE,
                            onLevelSelect = { viewModel.setContentFilterLevel(it) }
                        )
                    }

                    // Log de Actividad
                    item {
                        SectionHeader("Actividad Reciente")
                    }
                    
                    if (uiState.activityLog.isEmpty()) {
                        item {
                            Text("No hay actividad registrada aún.", color = colors.onBackground.copy(alpha = 0.5f))
                        }
                    } else {
                        items(uiState.activityLog.reversed().take(10)) { entry ->
                            ActivityLogItem(entry.action, entry.category, entry.timestamp)
                        }
                    }

                    // Zona de Peligro
                    item {
                        Spacer(Modifier.height(16.dp))
                        DangerZone(onReset = {
                            viewModel.resetProfile {
                                onNavigateBack()
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ChildSummaryCard(name: String, level: Int, xp: Long, time: Int) {
    val colors = EedaAdaptiveTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(48.dp), shape = RoundedCornerShape(12.dp), color = colors.primary.copy(0.1f)) {
                    Icon(Icons.Rounded.Person, null, tint = colors.primary, modifier = Modifier.padding(8.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Nivel $level • $xp XP Totales", style = MaterialTheme.typography.bodySmall, color = colors.primary)
                }
            }
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = colors.onSurface.copy(0.05f))
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("Tiempo hoy", "$time min", Icons.Rounded.Timer)
                SummaryItem("Fase Actual", EedaAdaptiveTheme.phase.name, Icons.Rounded.AutoGraph)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = EedaAdaptiveTheme.colors.secondary, modifier = Modifier.size(20.dp))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TimeLimitControl(currentLimit: Int, onLimitChange: (Int) -> Unit) {
    val colors = EedaAdaptiveTheme.colors
    var sliderValue by remember { mutableFloatStateOf(currentLimit.toFloat()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.glassOverlay)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Límite diario", fontWeight = FontWeight.Medium)
                Text("${sliderValue.toInt()} min", color = colors.primary, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onLimitChange(sliderValue.toInt()) },
                valueRange = 15f..180f,
                steps = 10,
                colors = SliderDefaults.colors(thumbColor = colors.primary, activeTrackColor = colors.primary)
            )
            Text(
                "El niño recibirá un aviso 5 minutos antes de que el tiempo se agote.",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ContentFilterSelector(currentLevel: ContentFilterLevel, onLevelSelect: (ContentFilterLevel) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ContentFilterLevel.entries.forEach { level ->
            val isSelected = level == currentLevel
            val colors = EedaAdaptiveTheme.colors
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLevelSelect(level) },
                shape = RoundedCornerShape(16.dp),
                color = if (isSelected) colors.primary.copy(0.1f) else colors.glassOverlay,
                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, colors.primary) else null
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isSelected, onClick = { onLevelSelect(level) })
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(level.displayName, fontWeight = FontWeight.Bold)
                        Text(level.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(action: String, category: String, time: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(EedaAdaptiveTheme.colors.secondary, RoundedCornerShape(percent = 50)))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(action, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            Text(category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(time.takeLast(5), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun DangerZone(onReset: () -> Unit) {
    var showConfirm by remember { mutableStateOf(false) }
    
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("¿Estás seguro?") },
            text = { Text("Esto eliminará todo el progreso, XP y habilidades desbloqueadas. Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(onClick = onReset, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(0.3f))
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Zona de Peligro", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { showConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Reiniciar Perfil")
            }
        }
    }
}

@Composable
fun ParentAuthScreen(error: String?, onAuth: (String) -> Unit, onBack: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    val colors = EedaAdaptiveTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.Lock, null, Modifier.size(64.dp), tint = colors.primary)
        Spacer(Modifier.height(24.dp))
        Text("Acceso Parental", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black))
        Text(
            "Introduce el PIN de seguridad para acceder a la configuración.",
            textAlign = TextAlign.Center,
            color = colors.onBackground.copy(0.6f)
        )
        Spacer(Modifier.height(32.dp))
        
        OutlinedTextField(
            value = pin,
            onValueChange = { if (it.length <= 4) pin = it },
            label = { Text("PIN de Seguridad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.width(200.dp),
            shape = RoundedCornerShape(16.dp),
            isError = error != null
        )
        
        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { onAuth(pin) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Desbloquear")
        }
        
        TextButton(onClick = onBack) {
            Text("Volver", color = colors.onBackground.copy(0.5f))
        }
        
        Spacer(Modifier.height(48.dp))
        Text("Ayuda: El PIN por defecto es 1234", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}
