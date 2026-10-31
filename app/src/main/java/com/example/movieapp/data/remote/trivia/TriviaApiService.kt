package com.example.movieapp.data.remote.trivia

import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    // Category 11 = Entertainment: Film / Movie
    @GET("api.php")
    suspend fun getMovieTrivia(
        @Query("amount") amount: Int = 20,
        @Query("category") category: Int = 11
    ) : OpenTdbResponse
}