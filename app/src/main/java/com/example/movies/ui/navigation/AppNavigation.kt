package com.example.movies.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.movies.MovieApplication
import com.example.movies.ui.screens.detail.MovieDetailScreen
import com.example.movies.ui.screens.detail.MovieDetailViewModel
import com.example.movies.ui.screens.list.MovieListScreen
import com.example.movies.ui.screens.list.MovieListViewModel

sealed class Screen(val route: String) {
    data object MovieList : Screen("movie_list")
    data object MovieDetail : Screen("movie_detail") {
        const val ARG_MOVIE_ID = "movieId"
        val routeWithArgs = "$route/{$ARG_MOVIE_ID}"
        fun createRoute(movieId: Int) = "$route/$movieId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val appContainer = (context.applicationContext as MovieApplication).container

    NavHost(
        navController = navController,
        startDestination = Screen.MovieList.route
    ) {
        composable(Screen.MovieList.route) {
            val viewModel: MovieListViewModel = viewModel(
                factory = MovieListViewModel.provideFactory(
                    repository = appContainer.movieRepository,
                    connectivityObserver = appContainer.connectivityObserver
                )
            )
            MovieListScreen(
                viewModel = viewModel,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                }
            )
        }

        composable(
            route = Screen.MovieDetail.routeWithArgs,
            arguments = listOf(
                navArgument(Screen.MovieDetail.ARG_MOVIE_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(Screen.MovieDetail.ARG_MOVIE_ID) ?: 0
            val viewModel: MovieDetailViewModel = viewModel(
                factory = MovieDetailViewModel.provideFactory(
                    movieId = movieId,
                    repository = appContainer.movieRepository,
                    connectivityObserver = appContainer.connectivityObserver
                )
            )
            MovieDetailScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
