package com.example.movies.domain.repository

import com.example.movies.core.util.Resource
import com.example.movies.domain.model.Genre
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MovieDetail

interface MovieRepository {
    suspend fun getPopularMovies(page: Int = 1): Resource<List<Movie>>
    suspend fun searchMovies(query: String, page: Int = 1): Resource<List<Movie>>
    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): Resource<List<Movie>>
    suspend fun getGenres(): Resource<List<Genre>>
    suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail>
}
