package com.example.movies.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieListResponseDto(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<MovieDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class GenreListResponseDto(
    @SerializedName("genres") val genres: List<GenreDto>
)

data class MovieDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("genres") val genres: List<GenreDto>?
)
