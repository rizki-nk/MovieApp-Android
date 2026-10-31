package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository

class GetPopularMoviesUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(): Result<List<Movie>> {
        return repository.getPopularMovies()
    }
}