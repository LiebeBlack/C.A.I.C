package com.liebeblack.isla_digital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.liebeblack.isla_digital.ui.screens.home.HomeScreen
import com.liebeblack.isla_digital.ui.screens.home.HomeViewModel
import com.liebeblack.isla_digital.ui.screens.home.HomeUiState
import com.liebeblack.isla_digital.ui.theme.IslaDigitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar Edge-to-Edge para un look moderno en 2026
        enableEdgeToEdge()
        
        val appContainer = (application as IslaDigitalApp).container
        val repository = appContainer.childProfileRepository
        
        setContent {
            IslaDigitalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val homeViewModel: HomeViewModel = viewModel(
                            factory = viewModelFactory {
                                initializer {
                                    HomeViewModel(repository)
                                }
                            }
                        )
                        
                        // collectAsStateWithLifecycle es lo recomendado para recolectar flows en UI
                        val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                        
                        when (val state = uiState) {
                            is HomeUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            is HomeUiState.Success -> {
                                HomeScreen(
                                    profile = state.profile,
                                    onNavigateToLevels = {},
                                    onNavigateToSettings = {},
                                    onNavigateToProfile = {},
                                    onNavigateToShowcase = {}
                                )
                            }
                            is HomeUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
