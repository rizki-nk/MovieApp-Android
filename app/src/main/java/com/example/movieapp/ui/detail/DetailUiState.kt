package com.example.movieapp.ui.detail

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.data.local.ReviewEntity

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val movie: Movie,
        val isFavorite: Boolean = false,
        val reviews: List<ReviewEntity> = emptyList()
    ) : DetailUiState
    data class Error (val message: String) : DetailUiState
}