package com.example.movieapp.ui

import com.example.movieapp.domain.model.Movie

sealed interface MovieUiState {
    object Loading: MovieUiState
    data class Success(val movies: List<Movie>) : MovieUiState
    data class Error(val message: String) : MovieUiState
}