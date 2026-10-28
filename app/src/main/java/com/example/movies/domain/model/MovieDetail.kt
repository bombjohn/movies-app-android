package com.example.movies.domain.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: Int?,
    val tagline: String?,
    val status: String,
    val originalLanguage: String,
    val genres: List<Genre>
)
