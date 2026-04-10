package com.LBs.EEDA.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.LBs.EEDA.domain.model.DigitalPhase
import com.LBs.EEDA.domain.repository.ChildProfileRepository
import com.LBs.EEDA.ui.screens.home.HomeScreen
import com.LBs.EEDA.ui.screens.home.HomeUiState
import com.LBs.EEDA.ui.screens.home.HomeViewModel
import com.LBs.EEDA.ui.screens.levels.LevelsScreen
import com.LBs.EEDA.ui.screens.settings.SettingsScreen
import com.LBs.EEDA.ui.screens.profile.ProfileScreen
import com.LBs.EEDA.ui.screens.showcase.ShowcaseScreen
import com.LBs.EEDA.ui.screens.showcase.ShowcaseViewModel
import com.LBs.EEDA.ui.screens.onboarding.OnboardingScreen
import com.LBs.EEDA.ui.screens.onboarding.OnboardingViewModel
import com.LBs.EEDA.ui.screens.skilltree.SkillTreeScreen
import com.LBs.EEDA.ui.screens.skilltree.SkillTreeViewModel
import com.LBs.EEDA.ui.screens.certifications.CertificationsScreen
import com.LBs.EEDA.ui.screens.lessons.LessonsScreen
import com.LBs.EEDA.ui.screens.lessons.LessonsViewModel
import com.LBs.EEDA.ui.screens.parent.ParentDashboardScreen
import com.LBs.EEDA.ui.screens.parent.ParentViewModel
import com.LBs.EEDA.ui.screens.main.MainScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import com.LBs.EEDA.EedaApp
import com.LBs.EEDA.data.repository.MinigameRepositoryImpl
import com.LBs.EEDA.data.repository.SandboxRepositoryImpl
import com.LBs.EEDA.data.repository.AssessmentRepositoryImpl
import com.LBs.EEDA.ui.screens.educational.EducationalModulesScreen
import com.LBs.EEDA.ui.screens.educational.MinigameScreen
import com.LBs.EEDA.ui.screens.educational.MinigameViewModel
import com.LBs.EEDA.ui.screens.educational.CodeSandboxScreen
import com.LBs.EEDA.ui.screens.educational.AssessmentScreen
import com.LBs.EEDA.ui.screens.educational.EducationalViewModel
import com.LBs.EEDA.ui.screens.educational.CodeSandboxViewModel
import com.LBs.EEDA.ui.screens.educational.AssessmentViewModel
import androidx.navigation.navArgument
import androidx.navigation.NavType

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
    
    // Pantallas Educativas 2026
    data object EducationalModules : Screen("educational_modules")
    data object Minigame : Screen("minigame/{minigameId}") {
        fun createRoute(minigameId: String) = "minigame/$minigameId"
    }
    data object CodeSandbox : Screen("sandbox/{sandboxId}") {
        fun createRoute(sandboxId: String) = "sandbox/$sandboxId"
    }
    data object Assessment : Screen("assessment/{assessmentId}") {
        fun createRoute(assessmentId: String) = "assessment/$assessmentId"
    }
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
                                onNavigateToCertifications = { navController.navigate(Screen.Certifications.route) },
                                onNavigateToEducationalModules = { navController.navigate(Screen.EducationalModules.route) }
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
                val showcaseViewModel: ShowcaseViewModel = viewModel(
                    factory = viewModelFactory { initializer { ShowcaseViewModel(repository) } }
                )
                ShowcaseScreen(
                    viewModel = showcaseViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
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
            
            // === Pantallas Educativas 2026 ===
            composable(Screen.EducationalModules.route) {
                val context = LocalContext.current
                val educationalViewModel: EducationalViewModel = viewModel(
                    factory = viewModelFactory { 
                        initializer { 
                            EducationalViewModel(
                                minigameRepository = (context.applicationContext as EedaApp).container.minigameRepository,
                                sandboxRepository = (context.applicationContext as EedaApp).container.sandboxRepository,
                                assessmentRepository = (context.applicationContext as EedaApp).container.assessmentRepository
                            ) 
                        } 
                    }
                )
                EducationalModulesScreen(
                    viewModel = educationalViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMinigame = { minigameId ->
                        navController.navigate(Screen.Minigame.createRoute(minigameId))
                    },
                    onNavigateToSandbox = { sandboxId ->
                        navController.navigate(Screen.CodeSandbox.createRoute(sandboxId))
                    },
                    onNavigateToAssessment = { assessmentId ->
                        navController.navigate(Screen.Assessment.createRoute(assessmentId))
                    }
                )
            }
            
            composable(
                route = Screen.Minigame.route,
                arguments = listOf(navArgument("minigameId") { type = NavType.StringType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val minigameId = backStackEntry.arguments?.getString("minigameId") ?: ""
                val minigameViewModel: MinigameViewModel = viewModel(
                    factory = viewModelFactory { 
                        initializer { 
                            MinigameViewModel(
                                minigameRepository = (context.applicationContext as EedaApp).container.minigameRepository,
                                profileRepository = (context.applicationContext as EedaApp).container.childProfileRepository
                            ) 
                        } 
                    }
                )
                MinigameScreen(
                    minigameId = minigameId,
                    viewModel = minigameViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.CodeSandbox.route,
                arguments = listOf(navArgument("sandboxId") { type = NavType.StringType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val sandboxId = backStackEntry.arguments?.getString("sandboxId") ?: ""
                val sandboxViewModel: CodeSandboxViewModel = viewModel(
                    factory = viewModelFactory { 
                        initializer { 
                            CodeSandboxViewModel(
                                sandboxRepository = (context.applicationContext as EedaApp).container.sandboxRepository
                            ) 
                        } 
                    }
                )
                CodeSandboxScreen(
                    sandboxId = sandboxId,
                    viewModel = sandboxViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.Assessment.route,
                arguments = listOf(navArgument("assessmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val context = LocalContext.current
                val assessmentId = backStackEntry.arguments?.getString("assessmentId") ?: ""
                val assessmentViewModel: AssessmentViewModel = viewModel(
                    factory = viewModelFactory { 
                        initializer { 
                            AssessmentViewModel(
                                assessmentRepository = (context.applicationContext as EedaApp).container.assessmentRepository
                            ) 
                        } 
                    }
                )
                AssessmentScreen(
                    assessmentId = assessmentId,
                    viewModel = assessmentViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
}
