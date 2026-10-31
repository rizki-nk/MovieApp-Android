package com.example.movieapp.ui.home

import android.os.Message
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.News
import com.example.movieapp.domain.model.Trivia

sealed interface HomeUiState{
    object Loading : HomeUiState
    data class Success(
        val movies: List<Movie>,
        val featuredMovies: List<Movie>,
        val news: List<News> = emptyList(),
        val trivia: List<Trivia> = emptyList()
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}