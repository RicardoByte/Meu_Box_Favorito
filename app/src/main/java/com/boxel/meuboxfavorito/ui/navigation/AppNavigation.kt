package com.boxel.meuboxfavorito.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.boxel.meuboxfavorito.ui.screen.auth.AuthScreen
import com.boxel.meuboxfavorito.ui.screen.auth.RegisterScreen
import com.boxel.meuboxfavorito.ui.screen.boxdetail.BoxDetailScreen
import com.boxel.meuboxfavorito.ui.screen.discussion.DiscussionScreen
import com.boxel.meuboxfavorito.ui.screen.feed.CommunityFeedScreen
import com.boxel.meuboxfavorito.ui.screen.movie.MovieDetailScreen
import com.boxel.meuboxfavorito.ui.screen.myboxes.CreateBoxScreen
import com.boxel.meuboxfavorito.ui.screen.myboxes.MyBoxesScreen
import com.boxel.meuboxfavorito.ui.screen.profile.ProfileScreen

object AppRoutes {

    // Bottom Nav
    const val EXPLORE   = "explore"
    const val MY_BOXES  = "my_boxes"
    const val PROFILE   = "profile"

    // Auth
    const val AUTH      = "auth"
    const val REGISTER  = "register"

    // Com argumentos
    const val BOX_DETAIL    = "box_detail/{boxId}"
    const val MOVIE_DETAIL  = "movie_detail/{movieId}"
    const val DISCUSSION    = "discussion/{boxId}"
    const val CREATE_BOX    = "create_box"

    // Helpers para montar a rota com argumento
    fun boxDetail(boxId: Int)    = "box_detail/$boxId"
    fun movieDetail(movieId: Int) = "movie_detail/$movieId"
    fun discussion(boxId: Int)   = "discussion/$boxId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = AppRoutes.AUTH
    ) {

        // ──────────────────────────────
        // AUTH
        // ──────────────────────────────
        composable(AppRoutes.AUTH) {

            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.EXPLORE) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onExploreWithoutAccount = {
                    navController.navigate(AppRoutes.EXPLORE) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        // ──────────────────────────────
        // CADASTRO / REGISTRO
        // ──────────────────────────────
        composable(AppRoutes.REGISTER) {

            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.EXPLORE) {
                        popUpTo(AppRoutes.AUTH) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // ──────────────────────────────
        // BOTTOM NAV
        // ──────────────────────────────
        composable(AppRoutes.EXPLORE) {

            CommunityFeedScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBoxClick = { boxId ->
                    navController.navigate(AppRoutes.boxDetail(boxId))
                }
            )
        }

        composable(AppRoutes.MY_BOXES) {

            MyBoxesScreen(
                onNavigate = { route -> navController.navigate(route) },
                onBoxClick = { boxId ->
                    navController.navigate(AppRoutes.boxDetail(boxId))
                },
                onCreateBoxClick = {
                    navController.navigate(AppRoutes.CREATE_BOX)
                }
            )
        }

        composable(AppRoutes.PROFILE) {

            ProfileScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // ──────────────────────────────
        // CRIAR BOX
        // ──────────────────────────────
        composable(AppRoutes.CREATE_BOX) {

            CreateBoxScreen(
                onBack = { navController.popBackStack() },
                onBoxCreated = { boxId ->
                    navController.navigate(AppRoutes.boxDetail(boxId)) {
                        popUpTo(AppRoutes.CREATE_BOX) { inclusive = true }
                    }
                }
            )
        }

        // ──────────────────────────────
        // DETALHE DA BOX (Agora tela de discussão / box proprietário)
        // ──────────────────────────────
        composable(
            route = AppRoutes.BOX_DETAIL,
            arguments = listOf(
                navArgument("boxId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val boxId = backStackEntry.arguments?.getInt("boxId") ?: return@composable

            BoxDetailScreen(
                boxId = boxId,
                onBack = { navController.popBackStack() },
                onMovieClick = { movieId ->
                    navController.navigate(AppRoutes.movieDetail(movieId))
                },
                onDiscussionClick = {
                    navController.navigate(AppRoutes.discussion(boxId))
                },
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        // ──────────────────────────────
        // DETALHE DO FILME
        // ──────────────────────────────
        composable(
            route = AppRoutes.MOVIE_DETAIL,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable

            MovieDetailScreen(
                movieId = movieId,
                onBack = { navController.popBackStack() }
            )
        }

        // ──────────────────────────────
        // DISCUSSÃO
        // ──────────────────────────────
        composable(
            route = AppRoutes.DISCUSSION,
            arguments = listOf(
                navArgument("boxId") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val boxId = backStackEntry.arguments?.getInt("boxId") ?: return@composable

            DiscussionScreen(
                boxId = boxId,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
    }
}