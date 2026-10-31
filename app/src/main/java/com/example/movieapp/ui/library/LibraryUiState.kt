package com.example.movieapp.ui.library

import com.example.movieapp.data.local.FavoriteMovieEntity

sealed interface LibraryUiState {
    object Loading: LibraryUiState
    object Empty: LibraryUiState
    data class Success(val favorites: List<FavoriteMovieEntity>): LibraryUiState
}