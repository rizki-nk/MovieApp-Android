package com.example.movieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movieapp.ui.auth.AuthUiState
import com.example.movieapp.ui.auth.AuthViewModel
import com.example.movieapp.ui.auth.LoginScreen
import com.example.movieapp.ui.auth.RegisterScreen
import com.example.movieapp.ui.detail.DetailScreen
import com.example.movieapp.ui.detail.DetailViewModel
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.library.LibraryUiState
import com.example.movieapp.ui.library.LibraryViewModel
import com.example.movieapp.ui.main.MainScreen
import com.example.movieapp.ui.screen.SplashScreen
import com.example.movieapp.ui.seeall.SeeAllScreen

@Composable
fun MovieNavGraph(
    homeViewModel: HomeViewModel,
    detailViewModel: DetailViewModel,
    authViewModel: AuthViewModel,
    libraryViewModel: LibraryViewModel
) {
    val navController = rememberNavController()

    // State Autentikasi
    val authUiState by authViewModel.uiState.collectAsState()
    val currentUserId by authViewModel.currentUserId.collectAsState()
    val isLoggedIn = authUiState is AuthUiState.Success || currentUserId != -1
    val currentUser by authViewModel.currentUser.collectAsState()

    // Hitung jumlah favorit & review untuk profil secara reaktif
    val favoriteList by remember(currentUserId){
        libraryViewModel.getFavorites(currentUserId)
    }.collectAsState(initial = LibraryUiState.Loading)

    val reviewList by remember(currentUserId) {
        libraryViewModel.getUserReviews(currentUserId)
    }.collectAsState(initial = emptyList())

    val favCount = (favoriteList as? LibraryUiState.Success)?.favorites?.size ?: 0
    val revCount = reviewList.size

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true } // Hapus splash dari backstack
                    }
                }
            )
        }
        // Halaman Utama
        composable("main") {
            MainScreen(
                homeViewModel = homeViewModel,
                libraryViewModel = libraryViewModel,
                currentUserId = currentUserId,
                currentUser = currentUser,
                username = currentUser?.name ?: currentUser?.username ?: "Guest",
                favoriteCount = favCount,
                reviewCount = revCount,
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onSeeAllClick = {
                    navController.navigate("see_all")
                },
                onLogoutClick = {
                    authViewModel.logout()
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }
        // Halaman See All
        composable("see_all") {
            SeeAllScreen(
                viewModel = homeViewModel,
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Halaman Detail Film
        composable(
            route = "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType})
        ) { backStackEntry ->
            val movieIdArg = backStackEntry.arguments?.get("movieId")
            val movieId = when (movieIdArg) {
                is Int -> movieIdArg
                is String -> movieIdArg.toIntOrNull() ?:0
                else -> 0
            }

            DetailScreen(
                movieId = movieId,
                currentUserId = currentUserId,
                viewModel = detailViewModel,
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }


        // Halaman Login
        composable("login") {
            LoginScreen(
                uiState = authUiState,
                onLoginClick = { username, password ->
                    authViewModel.login(username, password)
                },
                onLoginSuccess = {
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate("register")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }


        // Halaman Register (Rute Baru)
        composable("register") {
            LaunchedEffect(Unit) {
                authViewModel.resetState()
            }

            RegisterScreen(
                uiState = authUiState,
                onRegisterClick = { name, username, password ->
                    authViewModel.register(name, username, password)
                },
                onRegisterSuccess = {
                    authViewModel.resetState()
                    navController.popBackStack("main", false)
                },
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}