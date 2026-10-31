package com.example.movieapp.data.remote.news

import com.example.movieapp.BuildConfig
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

    // --- Interface Service Retrofit ---
    interface NewsApiService {
        @GET("v2/everything")
        suspend fun getMovieNews(
            @Query("q") query: String = "movies OR cinema OR Hollywood OR box office",
            @Query("sortBy") sortBy: String = "publishedAt",
            @Query("language") language: String = "en",
            @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
        ): NewsResponse
}