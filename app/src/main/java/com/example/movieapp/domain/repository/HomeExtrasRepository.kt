package com.example.movieapp.domain.repository

import com.example.movieapp.domain.model.News
import com.example.movieapp.domain.model.Trivia

interface HomeExtrasRepository{
    suspend fun getMovieNews(): Result<List<News>>
    suspend fun getMovieTrivia(): Result<List<Trivia>>
}