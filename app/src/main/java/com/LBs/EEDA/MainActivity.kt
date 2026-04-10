package com.LBs.EEDA

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
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.ui.navigation.NavGraph
import com.LBs.EEDA.ui.screens.home.HomeUiState
import com.LBs.EEDA.ui.screens.home.HomeViewModel
import com.LBs.EEDA.ui.theme.EedaTheme
import com.LBs.EEDA.ui.theme.PhaseColors
import com.LBs.EEDA.ui.components.PhaseTransitionOverlay
import com.LBs.EEDA.ui.components.LevelUpOverlay
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import com.LBs.EEDA.ui.theme.HardwareSensingProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val appContainer = (application as EedaApp).container
        val repository = appContainer.childProfileRepository
        val hardwareRepository = appContainer.hardwareRepository

        setContent {
            val hardwareProfile by hardwareRepository.hardwareProfile.collectAsStateWithLifecycle()
            
            // ViewModel setup
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

            HardwareSensingProvider(profile = hardwareProfile) {
                EedaTheme(phase = currentPhase) {
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
}
