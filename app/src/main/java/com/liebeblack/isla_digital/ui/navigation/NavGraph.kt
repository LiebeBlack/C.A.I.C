package com.liebeblack.isla_digital.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.liebeblack.isla_digital.domain.model.DigitalPhase
import com.liebeblack.isla_digital.domain.repository.ChildProfileRepository
import com.liebeblack.isla_digital.ui.screens.home.HomeScreen
import com.liebeblack.isla_digital.ui.screens.home.HomeUiState
import com.liebeblack.isla_digital.ui.screens.home.HomeViewModel
import com.liebeblack.isla_digital.ui.screens.levels.LevelsScreen
import com.liebeblack.isla_digital.ui.screens.settings.SettingsScreen
import com.liebeblack.isla_digital.ui.screens.profile.ProfileScreen
import com.liebeblack.isla_digital.ui.screens.showcase.ShowcaseScreen
import com.liebeblack.isla_digital.ui.screens.onboarding.OnboardingScreen
import com.liebeblack.isla_digital.ui.screens.onboarding.OnboardingViewModel
import com.liebeblack.isla_digital.ui.screens.skilltree.SkillTreeScreen
import com.liebeblack.isla_digital.ui.screens.skilltree.SkillTreeViewModel
import com.liebeblack.isla_digital.ui.screens.certifications.CertificationsScreen
import com.liebeblack.isla_digital.ui.screens.lessons.LessonsScreen
import com.liebeblack.isla_digital.ui.screens.lessons.LessonsViewModel
import com.liebeblack.isla_digital.ui.screens.parent.ParentDashboardScreen
import com.liebeblack.isla_digital.ui.screens.parent.ParentViewModel
import com.liebeblack.isla_digital.ui.screens.main.MainScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Levels : Screen("levels")
    data object Profile : Screen("profile")
    data object Showcase : Screen("showcase")
    data object Onboarding : Screen("onboarding")
    data object SkillTree : Screen("skilltree")
    data object Certifications : Screen("certifications")
    data object Lessons : Screen("lessons")
    data object ParentDashboard : Screen("parent_dashboard")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: ChildProfileRepository,
    currentPhase: DigitalPhase = DigitalPhase.SENSORIAL
) {
    // El Shell de la aplicación con la BottomBar adaptativa
    MainScreen(navController = navController) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = viewModelFactory { initializer { HomeViewModel(repository) } }
                )
                val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

                when (val state = uiState) {
                    is HomeUiState.Success -> {
                        if (state.profile == null) {
                            androidx.compose.runtime.LaunchedEffect(Unit) {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        } else {
                            HomeScreen(
                                profile = state.profile,
                                onNavigateToLevels = { navController.navigate(Screen.Levels.route) },
                                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                                onNavigateToShowcase = { navController.navigate(Screen.Showcase.route) },
                                onNavigateToSkillTree = { navController.navigate(Screen.SkillTree.route) },
                                onNavigateToLessons = { navController.navigate(Screen.Lessons.route) },
                                onNavigateToCertifications = { navController.navigate(Screen.Certifications.route) }
                            )
                        }
                    }
                    is HomeUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    }
                    is HomeUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: ${state.message}") }
                    }
                }
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToParentMode = { navController.navigate(Screen.ParentDashboard.route) }
                )
            }

            composable(Screen.Levels.route) {
                LevelsScreen(
                    repository = repository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = viewModelFactory { initializer { HomeViewModel(repository) } }
                )
                val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val profile = (uiState as? HomeUiState.Success)?.profile
                ProfileScreen(profile = profile, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Showcase.route) {
                ShowcaseScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Onboarding.route) {
                val onboardingViewModel: OnboardingViewModel = viewModel(
                    factory = viewModelFactory { initializer { OnboardingViewModel(repository) } }
                )
                val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
                OnboardingScreen(
                    uiState = uiState,
                    onNameChange = onboardingViewModel::updateName,
                    onAgeChange = onboardingViewModel::updateAge,
                    onAvatarSelect = onboardingViewModel::selectAvatar,
                    onNext = onboardingViewModel::nextStep,
                    onBack = onboardingViewModel::previousStep,
                    onComplete = {
                        onboardingViewModel.completeOnboarding {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(Screen.SkillTree.route) {
                val skillTreeViewModel: SkillTreeViewModel = viewModel(
                    factory = viewModelFactory { initializer { SkillTreeViewModel(repository) } }
                )
                val uiState by skillTreeViewModel.uiState.collectAsStateWithLifecycle()
                SkillTreeScreen(
                    uiState = uiState,
                    onCategorySelected = skillTreeViewModel::selectCategory,
                    onSkillSelected = skillTreeViewModel::selectSkill,
                    onUnlockSkill = skillTreeViewModel::unlockSkill,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Certifications.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = viewModelFactory { initializer { HomeViewModel(repository) } }
                )
                val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                val profile = (uiState as? HomeUiState.Success)?.profile
                CertificationsScreen(
                    earnedCertifications = profile?.earnedCertifications ?: emptyList(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Lessons.route) {
                val lessonsViewModel: LessonsViewModel = viewModel(
                    factory = viewModelFactory { initializer { LessonsViewModel(repository) } }
                )
                LessonsScreen(
                    viewModel = lessonsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ParentDashboard.route) {
                val parentViewModel: ParentViewModel = viewModel(
                    factory = viewModelFactory { initializer { ParentViewModel(repository) } }
                )
                ParentDashboardScreen(
                    viewModel = parentViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
}
