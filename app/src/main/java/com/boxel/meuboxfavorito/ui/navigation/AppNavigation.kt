package com.boxel.meuboxfavorito.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.boxel.meuboxfavorito.ui.screen.CommunityFeedScreen
import com.boxel.meuboxfavorito.ui.screen.MyBoxesScreen

object AppRoutes {
    const val EXPLORE = "explore"
    const val MY_BOXES = "my_boxes"
    const val PROFILE = "profile"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.EXPLORE
    ) {

        composable(AppRoutes.EXPLORE) {

            CommunityFeedScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(AppRoutes.MY_BOXES) {

            MyBoxesScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(AppRoutes.PROFILE) {

            // Tela de perfil será implementada depois.
        }
    }
}