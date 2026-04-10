package com.LBs.EEDA.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.LBs.EEDA.ui.components.AdaptiveBottomBar
import com.LBs.EEDA.ui.components.BottomNavItem
import com.LBs.EEDA.ui.navigation.Screen
import com.LBs.EEDA.ui.theme.EedaAdaptiveTheme

/**
 * Pantalla principal que actúa como contenedor (Shell) para la navegación con BottomBar de E.E.D.A.
 * Se muestra después del onboarding.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // Definir los items de navegación
    val navItems = listOf(
        BottomNavItem(Screen.Home.route, "Inicio", Icons.Rounded.Home),
        BottomNavItem(Screen.SkillTree.route, "Habilidades", Icons.Rounded.AccountTree),
        BottomNavItem(Screen.Lessons.route, "Lecciones", Icons.AutoMirrored.Rounded.MenuBook),
        BottomNavItem(Screen.Profile.route, "Perfil", Icons.Rounded.Person)
    )

    // Solo mostrar BottomBar en rutas principales
    val showBottomBar = currentRoute in navItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AdaptiveBottomBar(
                    items = navItems,
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
