package com.liebeblack.isla_digital.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import com.liebeblack.isla_digital.domain.model.DigitalPhase
import com.liebeblack.isla_digital.ui.components.PhaseIndicator
import com.liebeblack.isla_digital.ui.theme.PhaseColorPalette
import com.liebeblack.isla_digital.ui.theme.PhaseColors

/**
 * Pantalla de Onboarding con 4 pasos:
 * 0: Bienvenida / Splash
 * 1: Ingreso de nombre
 * 2: Selección de edad (con detección automática de fase)
 * 3: Selección de avatar
 */
@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onAgeChange: (Int) -> Unit,
    onAvatarSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val phase = uiState.detectedPhase
    val phaseColors = PhaseColors.forPhase(phase)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        phaseColors.backgroundGradientStart,
                        phaseColors.backgroundGradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Indicador de progreso
            StepIndicator(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                phaseColors = phaseColors
            )

            // Contenido del paso actual
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        (fadeIn(tween(300)) + slideInHorizontally { it / 3 }) togetherWith
                                (fadeOut(tween(200)) + slideOutHorizontally { -it / 3 })
                    },
                    label = "onboardingStep"
                ) { step ->
                    when (step) {
                        0 -> WelcomeStep(phaseColors)
                        1 -> NameStep(
                            name = uiState.name,
                            onNameChange = onNameChange,
                            phaseColors = phaseColors
                        )
                        2 -> AgeStep(
                            age = uiState.age,
                            phase = uiState.detectedPhase,
                            onAgeChange = onAgeChange,
                            phaseColors = phaseColors
                        )
                        3 -> AvatarStep(
                            selectedAvatar = uiState.selectedAvatar,
                            onAvatarSelect = onAvatarSelect,
                            phaseColors = phaseColors
                        )
                    }
                }
            }

            // Botones de navegación
            NavigationButtons(
                currentStep = uiState.currentStep,
                totalSteps = uiState.totalSteps,
                isValid = uiState.isValid,
                isLoading = uiState.isLoading,
                onNext = onNext,
                onBack = onBack,
                onComplete = onComplete,
                phaseColors = phaseColors
            )
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    phaseColors: PhaseColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(6.dp)
                    .width(if (index == currentStep) 32.dp else 12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (isActive) phaseColors.primary
                        else phaseColors.onBackground.copy(alpha = 0.2f)
                    )
            )
        }
    }
}

@Composable
private fun WelcomeStep(phaseColors: PhaseColorPalette) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Public,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = phaseColors.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Bienvenido a",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = phaseColors.onBackground.copy(alpha = 0.7f)
            )
        )
        Text(
            text = "ISLA DIGITAL",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                color = phaseColors.onBackground
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tu plataforma de digitalización humana",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = phaseColors.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun NameStep(
    name: String,
    onNameChange: (String) -> Unit,
    phaseColors: PhaseColorPalette
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = phaseColors.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¿Cómo te llamas?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = phaseColors.onBackground
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Tu nombre...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = phaseColors.primary,
                cursorColor = phaseColors.primary,
                focusedTextColor = phaseColors.onBackground,
                unfocusedTextColor = phaseColors.onBackground
            )
        )
    }
}

@Composable
private fun AgeStep(
    age: Int,
    phase: DigitalPhase,
    onAgeChange: (Int) -> Unit,
    phaseColors: PhaseColorPalette
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "¿Cuántos años tienes?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = phaseColors.onBackground
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Selector de edad
        Text(
            text = age.toString(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = phaseColors.primary
            )
        )
        Text(
            text = "años",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = phaseColors.onBackground.copy(alpha = 0.6f)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            value = age.toFloat(),
            onValueChange = { onAgeChange(it.toInt()) },
            valueRange = 3f..20f,
            steps = 16,
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = SliderDefaults.colors(
                thumbColor = phaseColors.primary,
                activeTrackColor = phaseColors.primary,
                inactiveTrackColor = phaseColors.onBackground.copy(alpha = 0.15f)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Detección automática de fase
        PhaseIndicator(phase = phase, compact = false)
    }
}

@Composable
private fun AvatarStep(
    selectedAvatar: String,
    onAvatarSelect: (String) -> Unit,
    phaseColors: PhaseColorPalette
) {
    val avatars = listOf(
        "avatar_explorer" to Icons.Rounded.Explore,
        "avatar_star" to Icons.Rounded.Star,
        "avatar_rocket" to Icons.Rounded.RocketLaunch,
        "avatar_nature" to Icons.Rounded.Park,
        "avatar_music" to Icons.Rounded.MusicNote,
        "avatar_science" to Icons.Rounded.Science
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Elige tu avatar",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = phaseColors.onBackground
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            items(avatars) { (avatarId, icon) ->
                val isSelected = avatarId == selectedAvatar
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .then(
                            if (isSelected) Modifier.border(
                                3.dp, phaseColors.primary, CircleShape
                            ) else Modifier
                        )
                        .clickable { onAvatarSelect(avatarId) },
                    color = if (isSelected) phaseColors.primary.copy(alpha = 0.15f)
                    else phaseColors.surfaceVariant.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = avatarId,
                            tint = if (isSelected) phaseColors.primary
                            else phaseColors.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    currentStep: Int,
    totalSteps: Int,
    isValid: Boolean,
    isLoading: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    phaseColors: PhaseColorPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón atrás
        if (currentStep > 0) {
            TextButton(onClick = onBack) {
                Text(
                    "Atrás",
                    color = phaseColors.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }

        // Botón siguiente / completar
        if (currentStep < totalSteps - 1) {
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = phaseColors.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Siguiente")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Button(
                onClick = onComplete,
                enabled = isValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = phaseColors.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("¡Comenzar aventura!")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Rounded.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
