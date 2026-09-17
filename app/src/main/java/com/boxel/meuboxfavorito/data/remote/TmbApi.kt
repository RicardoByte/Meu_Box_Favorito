package com.boxel.meuboxfavorito.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse
}