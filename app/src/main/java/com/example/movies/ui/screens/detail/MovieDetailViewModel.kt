package com.example.movies.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movies.core.util.ConnectivityObserver
import com.example.movies.core.util.Resource
import com.example.movies.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        observeConnectivity()
        loadMovieDetail()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { isOnline ->
                _uiState.update { it.copy(isConnected = isOnline) }
                if (isOnline && (_uiState.value.movie == null || _uiState.value.errorMessage != null)) {
                    loadMovieDetail()
                }
            }
        }
    }

    fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = movieRepository.getMovieDetail(movieId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            movie = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    companion object {
        fun provideFactory(
            movieId: Int,
            repository: MovieRepository,
            connectivityObserver: ConnectivityObserver
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieDetailViewModel(movieId, repository, connectivityObserver) as T
            }
        }
    }
}
