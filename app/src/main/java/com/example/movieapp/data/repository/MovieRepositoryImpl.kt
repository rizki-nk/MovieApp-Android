package com.example.movieapp.data.repository

import com.example.movieapp.data.mapper.toDomainModel
import com.example.movieapp.data.remote.tmdb.ApiService
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val apiService: ApiService,
    private val apiKey: String
) : MovieRepository {

    override suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getPopularMovies(
                apiKey = apiKey,
                language = "en-US"
            )
            val movieList = response.results?.map { it.toDomainModel() } ?: emptyList()
            Result.success(movieList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMovie(query: String): Result<List<Movie>> {
        return try {
            // Memanggil endpoint searchMovies yang ada di ApiService[cite: 1]
            val response = apiService.searchMovies(
                apiKey = apiKey,
                query = query,
                language = "en-US"
            )

            val movies = response.results.map {it.toDomainModel() }
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<Movie?> {
        return try{
            val response = apiService.getMovieDetails(
                movieId = movieId,
                apiKey = apiKey,
                appendToResponse = "credits,videos",
                )
            Result.success(response.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNowPlayingMovies(): Result<List<Movie>> {
        return try {
            val response = apiService.getNowPlayingMovies(
                apiKey = apiKey,
                language = "en-US"
            ) // Sesuaikan parameter
            val movies = response.results.map { it.toDomainModel() } ?: emptyList()
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}