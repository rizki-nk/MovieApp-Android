package com.example.movieapp.data.remote.tmdb

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "id-ID",
        @Query("page") page: Int = 1
    ) : MovieResponseDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String
    ) : MovieResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("append_to_response") appendToResponse: String = "credits,videos"
    ): MovieDetailResponseDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        // Catatan: Di aplikasi production, API Key sebaiknya disembunyikan di local.properties atau BuildConfig
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w50"
    }
}