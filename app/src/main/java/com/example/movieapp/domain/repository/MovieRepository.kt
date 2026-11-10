package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.Movie

interface MovieRepository {
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun searchMovie(query: String): Result<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<Movie?>
    suspend fun getNowPlayingMovies(): Result<List<Movie>>
}