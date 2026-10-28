package com.example.movies.ui.screens.detail

import com.example.movies.domain.model.MovieDetail

data class MovieDetailUiState(
    val movie: MovieDetail? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isConnected: Boolean = true
)
