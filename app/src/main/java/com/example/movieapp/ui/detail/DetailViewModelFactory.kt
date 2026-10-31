package com.example.movieapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.data.repository.FavoriteRepository
import com.example.movieapp.domain.usecase.GetMovieByIdUseCase
import com.example.movieapp.data.repository.ReviewRepository
class DetailViewModelFactory(
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(getMovieByIdUseCase, favoriteRepository, reviewRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}