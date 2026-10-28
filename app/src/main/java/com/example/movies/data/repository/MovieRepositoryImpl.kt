package com.example.movies.data.repository

import com.example.movies.core.util.Resource
import com.example.movies.data.mapper.toDomain
import com.example.movies.data.remote.TmdbApi
import com.example.movies.domain.model.Genre
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MovieDetail
import com.example.movies.domain.repository.MovieRepository
import retrofit2.HttpException
import java.io.IOException

class MovieRepositoryImpl(
    private val api: TmdbApi
) : MovieRepository {

    override suspend fun getPopularMovies(page: Int): Resource<List<Movie>> {
        return safeApiCall {
            val response = api.getPopularMovies(page)
            response.results.map { it.toDomain() }
        }
    }

    override suspend fun searchMovies(query: String, page: Int): Resource<List<Movie>> {
        return safeApiCall {
            val response = api.searchMovies(query, page)
            response.results.map { it.toDomain() }
        }
    }

    override suspend fun getMoviesByGenre(genreId: Int, page: Int): Resource<List<Movie>> {
        return safeApiCall {
            val response = api.getMoviesByGenre(genreId, page)
            response.results.map { it.toDomain() }
        }
    }

    override suspend fun getGenres(): Resource<List<Genre>> {
        return safeApiCall {
            val response = api.getGenres()
            response.genres.map { it.toDomain() }
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail> {
        return safeApiCall {
            val response = api.getMovieDetail(movieId)
            response.toDomain()
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            val data = apiCall()
            Resource.Success(data)
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401 -> "Error 401: Token o API Key no autorizada. Verifica tu local.properties."
                404 -> "Elemento no encontrado (404)."
                500 -> "Error en el servidor de TMDB (500)."
                else -> "Error en la petición: ${e.code()} ${e.message()}"
            }
            Resource.Error(message, e)
        } catch (e: IOException) {
            Resource.Error("Sin conexión a Internet. Comprueba tu red y reintenta.", e)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Ocurrió un error inesperado.", e)
        }
    }
}
