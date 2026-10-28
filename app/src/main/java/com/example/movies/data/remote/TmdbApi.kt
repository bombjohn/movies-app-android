package com.example.movies.data.remote

import com.example.movies.data.remote.dto.GenreListResponseDto
import com.example.movies.data.remote.dto.MovieDetailDto
import com.example.movies.data.remote.dto.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1
    ): MovieListResponseDto

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponseDto

    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") movieId: Int
    ): MovieDetailDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"
    }
}
