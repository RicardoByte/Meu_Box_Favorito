package com.boxel.meuboxfavorito.data.repository

import com.boxel.meuboxfavorito.data.remote.Moviefmt
import com.boxel.meuboxfavorito.data.remote.RetrofitInstance

class MovieRepository {
    suspend fun getPopularMovies(apiKey: String): List<Moviefmt> {
        return RetrofitInstance.api.getPopularMovies(apiKey).results
    }

    suspend fun searchMovies(apiKey: String, query: String): List<Moviefmt> {
        if (query.isBlank()) return emptyList()
        return RetrofitInstance.api.searchMovies(apiKey = apiKey, query = query).results
    }
}