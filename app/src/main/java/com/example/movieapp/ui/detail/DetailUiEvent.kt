package com.example.movieapp.ui.detail

import com.example.movieapp.domain.model.Movie

sealed interface DetailUiEvent {
    data class ShowSnackBar(
        val message: String,
        val actionLabel: String? = null,
        val movie: Movie? = null,
        val userId: Int = -1,
        val wasFavorite: Boolean = false
    ) : DetailUiEvent
}