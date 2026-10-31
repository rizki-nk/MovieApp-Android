package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.model.News
import com.example.movieapp.domain.repository.HomeExtrasRepository

class GetMovieNewsUseCase (
    private val repository: HomeExtrasRepository
) {
    suspend operator fun invoke(): Result<List<News>>{
        return repository.getMovieNews()
    }
}