package com.example.movies.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movies.ui.common.EmptyView
import com.example.movies.ui.common.ErrorView
import com.example.movies.ui.common.GenreChipsRow
import com.example.movies.ui.common.LoadingView
import com.example.movies.ui.common.MovieCard
import com.example.movies.ui.common.MovieSearchBar
import com.example.movies.ui.common.NetworkBanner
import com.example.movies.ui.common.PaginationLoadingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieListViewModel,
    onMovieClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Detect when scrolling near the end to trigger infinite scroll pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleItemIndex >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Cartelera TMDB",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
                NetworkBanner(isConnected = state.isConnected)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input (Endpoint: /search/movie)
            MovieSearchBar(
                query = state.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )

            // Genre Chips (Endpoint: /genre/movie/list & /discover/movie)
            if (state.genres.isNotEmpty()) {
                GenreChipsRow(
                    genres = state.genres,
                    selectedGenreId = state.selectedGenreId,
                    onGenreSelected = viewModel::onGenreSelected
                )
            }

            // Body Content based on State
            when {
                state.isLoadingInitial -> {
                    LoadingView(message = "Obteniendo películas...")
                }
                state.errorMessage != null && state.movies.isEmpty() -> {
                    ErrorView(
                        message = state.errorMessage ?: "Error desconocido",
                        onRetry = viewModel::retry
                    )
                }
                state.movies.isEmpty() -> {
                    EmptyView(
                        message = if (state.searchQuery.isNotBlank()) {
                            "No se encontraron películas para \"${state.searchQuery}\"."
                        } else {
                            "No hay películas disponibles en esta categoría."
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.movies, key = { it.id }) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) }
                            )
                        }

                        // Infinite scroll loader indicator at the bottom
                        if (state.isLoadingMore) {
                            item {
                                PaginationLoadingItem()
                            }
                        }
                    }
                }
            }
        }
    }
}
