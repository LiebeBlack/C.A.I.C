package com.liebeblack.isla_digital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.liebeblack.isla_digital.domain.model.DigitalPhase
import com.liebeblack.isla_digital.ui.navigation.NavGraph
import com.liebeblack.isla_digital.ui.screens.home.HomeUiState
import com.liebeblack.isla_digital.ui.screens.home.HomeViewModel
import com.liebeblack.isla_digital.ui.theme.IslaDigitalTheme
import com.liebeblack.isla_digital.ui.theme.PhaseColors
import com.liebeblack.isla_digital.ui.components.PhaseTransitionOverlay
import com.liebeblack.isla_digital.ui.components.LevelUpOverlay
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val appContainer = (application as IslaDigitalApp).container
        val repository = appContainer.childProfileRepository

        setContent {
            // Obtener la fase del perfil actual para el tema adaptativo
            val homeViewModel: HomeViewModel = viewModel(
                factory = viewModelFactory {
                    initializer { HomeViewModel(repository) }
                }
            )
            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val currentPhase = remember(uiState) {
                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        state.profile?.let { DigitalPhase.fromAge(it.age) } ?: DigitalPhase.SENSORIAL
                    }
                    else -> DigitalPhase.SENSORIAL
                }
            }

            val currentLevel = remember(uiState) {
                (uiState as? HomeUiState.Success)?.profile?.currentLevel ?: 1
            }

            var previousPhase by remember { mutableStateOf<DigitalPhase?>(null) }
            var previousLevel by remember { mutableIntStateOf(-1) }
            var showEvolution by remember { mutableStateOf(false) }
            var showLevelUp by remember { mutableStateOf(false) }

            LaunchedEffect(currentPhase) {
                if (previousPhase != null && previousPhase != currentPhase) {
                    showEvolution = true
                }
                previousPhase = currentPhase
            }

            LaunchedEffect(currentLevel) {
                if (previousLevel != -1 && currentLevel > previousLevel) {
                    showLevelUp = true
                }
                previousLevel = currentLevel
            }

            IslaDigitalTheme(phase = currentPhase) {
                val phaseColors = PhaseColors.forPhase(currentPhase)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = phaseColors.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        repository = repository,
                        currentPhase = currentPhase
                    )

                    PhaseTransitionOverlay(
                        newPhase = currentPhase,
                        isVisible = showEvolution,
                        onDismiss = { showEvolution = false }
                    )

                    LevelUpOverlay(
                        level = currentLevel,
                        isVisible = showLevelUp,
                        onDismiss = { showLevelUp = false }
                    )
                }
            }
        }
    }
}
