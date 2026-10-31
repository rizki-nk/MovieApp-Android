package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.MovieRepository

class SearchMovieUseCase (
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String): Result<List<Movie>> {
        return repository.searchMovie(query)
    }
}