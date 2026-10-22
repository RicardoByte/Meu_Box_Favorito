package com.boxel.meuboxfavorito.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse

    // Utilizado em CreateBoxScreen para busca de filmes
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "pt-BR"
    ): MovieResponse

    // Utilizado em MovieDetailScreen para detalhes completos do filme
    @GET("movie/{id}")
    suspend fun getMovieDetail(
        @Path("id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "pt-BR"
    ): Moviefmt
}