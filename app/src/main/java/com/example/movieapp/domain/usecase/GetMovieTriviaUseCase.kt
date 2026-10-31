package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.Trivia
import com.example.movieapp.domain.repository.HomeExtrasRepository

class GetMovieTriviaUseCase(
    private val repository: HomeExtrasRepository
) {
    suspend operator fun invoke(): Result<List<Trivia>> {
        return repository.getMovieTrivia()
    }
}