package com.example.movies.ui.screens.list

import com.example.movies.domain.model.Genre
import com.example.movies.domain.model.Movie

data class MovieListUiState(
    val movies: List<Movie> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int? = null,
    val searchQuery: String = "",
    val isLoadingInitial: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val isConnected: Boolean = true,
    val canPaginate: Boolean = true,
    val currentPage: Int = 1
)
