package com.boxel.meuboxfavorito.data.repository

import com.boxel.meuboxfavorito.data.remote.RetrofitInstance
import com.boxel.meuboxfavorito.models.Moviefmt

class MovieRepository {
    suspend fun getPopularMovies(apiKey: String): List<Moviefmt> {
        return RetrofitInstance.api.getPopularMovies(apiKey).results
    }

    suspend fun searchMovies(apiKey: String, query: String): List<Moviefmt> {
        if (query.isBlank()) return emptyList()
        return RetrofitInstance.api.searchMovies(apiKey = apiKey, query = query).results
    }

    suspend fun getMovieDetail(apiKey: String, movieId: Int): Moviefmt {
        return RetrofitInstance.api.getMovieDetail(movieId = movieId, apiKey = apiKey)
    }
}