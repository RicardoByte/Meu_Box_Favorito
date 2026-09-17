package com.boxel.meuboxfavorito.data.repository

import com.boxel.meuboxfavorito.data.remote.Moviefmt
import com.boxel.meuboxfavorito.data.remote.RetrofitInstance

class MovieRepository {
    suspend fun getPopularMovies(apiKey: String): List<Moviefmt> {
        return RetrofitInstance.api.getPopularMovies(apiKey).results
    }
}