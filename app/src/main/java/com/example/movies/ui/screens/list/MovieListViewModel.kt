package com.example.movies.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movies.core.util.ConnectivityObserver
import com.example.movies.core.util.Resource
import com.example.movies.domain.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val movieRepository: MovieRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    init {
        observeConnectivity()
        loadGenres()
        loadMovies(page = 1, reset = true)
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityObserver.observe().collect { isOnline ->
                _uiState.update { it.copy(isConnected = isOnline) }
                // If connection just came back and current list is empty or errored, retry
                if (isOnline && (_uiState.value.movies.isEmpty() || _uiState.value.errorMessage != null)) {
                    retry()
                }
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            when (val result = movieRepository.getGenres()) {
                is Resource.Success -> {
                    _uiState.update { it.copy(genres = result.data) }
                }
                is Resource.Error -> {
                    // Non-fatal, genres chips can be retried or empty
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery, selectedGenreId = null) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce 500ms
            loadMovies(page = 1, reset = true)
        }
    }

    fun onGenreSelected(genreId: Int?) {
        searchJob?.cancel()
        _uiState.update { it.copy(selectedGenreId = genreId, searchQuery = "") }
        loadMovies(page = 1, reset = true)
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingInitial || state.isLoadingMore || !state.canPaginate) {
            return
        }
        loadMovies(page = state.currentPage + 1, reset = false)
    }

    fun retry() {
        if (_uiState.value.genres.isEmpty()) {
            loadGenres()
        }
        loadMovies(page = 1, reset = true)
    }

    private fun loadMovies(page: Int, reset: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            if (reset) {
                _uiState.update {
                    it.copy(
                        isLoadingInitial = true,
                        errorMessage = null,
                        currentPage = 1,
                        canPaginate = true
                    )
                }
            } else {
                _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            }

            val query = _uiState.value.searchQuery.trim()
            val genreId = _uiState.value.selectedGenreId

            val result = when {
                query.isNotBlank() -> movieRepository.searchMovies(query, page)
                genreId != null -> movieRepository.getMoviesByGenre(genreId, page)
                else -> movieRepository.getPopularMovies(page)
            }

            when (result) {
                is Resource.Success -> {
                    val newMovies = result.data
                    val updatedList = if (reset) {
                        newMovies
                    } else {
                        val existingIds = _uiState.value.movies.map { it.id }.toSet()
                        _uiState.value.movies + newMovies.filter { it.id !in existingIds }
                    }

                    _uiState.update {
                        it.copy(
                            movies = updatedList,
                            isLoadingInitial = false,
                            isLoadingMore = false,
                            currentPage = page,
                            canPaginate = newMovies.isNotEmpty(),
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            isLoadingMore = false,
                            errorMessage = if (reset) result.message else null
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: MovieRepository,
            connectivityObserver: ConnectivityObserver
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieListViewModel(repository, connectivityObserver) as T
            }
        }
    }
}
