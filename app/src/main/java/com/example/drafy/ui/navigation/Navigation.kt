// Navigation.kt
package com.example.drafy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drafy.ui.screen.home.HomeScreen
import com.example.drafy.ui.screen.note.NoteScreen
import com.example.drafy.ui.screen.splash.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Note : Screen("note?noteId={noteId}") {
        fun createRoute(noteId: Long? = null): String {
            return "note?noteId=$noteId"
        }
    }
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNote = { noteId ->
                    navController.navigate(Screen.Note.createRoute(noteId))
                }
            )
        }

        composable(
            route = Screen.Note.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
            NoteScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}