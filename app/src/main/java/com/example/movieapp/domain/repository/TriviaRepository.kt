package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.Trivia

interface TriviaRepository {
    suspend fun getMovieTrivia() : List<Trivia>
}