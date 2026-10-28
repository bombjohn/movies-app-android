package com.example.movies.data.mapper

import com.example.movies.data.remote.TmdbApi
import com.example.movies.data.remote.dto.GenreDto
import com.example.movies.data.remote.dto.MovieDetailDto
import com.example.movies.data.remote.dto.MovieDto
import com.example.movies.domain.model.Genre
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MovieDetail

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterUrl = posterPath?.let { "${TmdbApi.IMAGE_BASE_URL}$it" },
        backdropUrl = backdropPath?.let { "${TmdbApi.BACKDROP_BASE_URL}$it" },
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterUrl = posterPath?.let { "${TmdbApi.IMAGE_BASE_URL}$it" },
        backdropUrl = backdropPath?.let { "${TmdbApi.BACKDROP_BASE_URL}$it" },
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtime = runtime,
        tagline = tagline,
        status = status.orEmpty(),
        originalLanguage = originalLanguage.orEmpty().uppercase(),
        genres = genres?.map { it.toDomain() } ?: emptyList()
    )
}
