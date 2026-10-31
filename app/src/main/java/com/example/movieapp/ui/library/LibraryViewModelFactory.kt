package com.example.movieapp.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.data.repository.FavoriteRepository
import com.example.movieapp.data.repository.ReviewRepository

class LibraryViewModelFactory (
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LibraryViewModel(favoriteRepository, reviewRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}