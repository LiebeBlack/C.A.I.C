package com.LBs.EEDA.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

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
    val colors = EedaAdaptiveTheme.colors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Header
            Text(
                text = "E.E.D.A",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = colors.primary,
                    letterSpacing = 4.sp
                )
            )
            Text(
                text = "Tu viaje digital comienza aquí",
                style = MaterialTheme.typography.bodyMedium.copy(color = colors.onBackground.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Content based on step
            AnimatedContent(
                targetState = uiState.step,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "OnboardingStep"
            ) { step ->
                when (step) {
                    1 -> StepName(uiState.name, onNameChange)
                    2 -> StepAge(uiState.age, onAgeChange)
                    3 -> StepAvatar(uiState.avatar, onAvatarSelect)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.step > 1) {
                    TextButton(onClick = onBack) {
                        Text("Atrás", color = colors.onBackground.copy(alpha = 0.5f))
                    }
                } else {
                    Spacer(Modifier.width(64.dp))
                }

                Button(
                    onClick = if (uiState.step < 3) onNext else onComplete,
                    modifier = Modifier
                        .height(56.dp)
                        .width(160.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    enabled = !uiState.isLoading && (uiState.step != 1 || uiState.name.isNotBlank())
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = if (uiState.step < 3) "Siguiente" else "¡Empezar!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Error snackbar/message
        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            )
        }
    }
}

@Composable
private fun StepName(name: String, onNameChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.Face, null, modifier = Modifier.size(80.dp), tint = EedaAdaptiveTheme.colors.primary)
        Spacer(Modifier.height(24.dp))
        Text(
            "¿Cómo te llamas?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Tu nombre o apodo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
private fun StepAge(age: Int, onAgeChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Rounded.Cake, null, modifier = Modifier.size(80.dp), tint = EedaAdaptiveTheme.colors.secondary)
        Spacer(Modifier.height(24.dp))
        Text(
            "¿Cuántos años tienes?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "E.E.D.A se adapta a tu edad para enseñarte mejor.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onAgeChange(age - 1) }) { Icon(Icons.Rounded.Remove, null) }
            Text(
                text = age.toString(),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            IconButton(onClick = { onAgeChange(age + 1) }) { Icon(Icons.Rounded.Add, null) }
        }
    }
}

@Composable
private fun StepAvatar(selected: String, onSelect: (String) -> Unit) {
    val avatars = listOf("avatar_base", "avatar_robot", "avatar_explorer", "avatar_artist")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Elige tu avatar",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            avatars.forEach { id ->
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (selected == id) EedaAdaptiveTheme.colors.primary else Color.Gray.copy(alpha = 0.2f))
                        .clickable { onSelect(id) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(id) {
                            "avatar_robot" -> Icons.Rounded.SmartToy
                            "avatar_explorer" -> Icons.Rounded.Explore
                            "avatar_artist" -> Icons.Rounded.Palette
                            else -> Icons.Rounded.Person
                        },
                        contentDescription = null,
                        tint = if (selected == id) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}
