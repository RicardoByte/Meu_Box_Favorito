package com.boxel.meuboxfavorito.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val results: List<Moviefmt> = emptyList()
)

@Serializable
data class Moviefmt(
    val id: Int,
    val title: String = "",
    val overview: String = "",
    val vote_average: Double = 0.0,
    val poster_path: String? = null,
    val backdrop_path: String? = null,
    val release_date: String? = null,
    val genre_ids: List<Int> = emptyList()
)