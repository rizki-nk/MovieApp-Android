package com.example.movieapp.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.FavoriteMovieEntity
import com.example.movieapp.data.repository.FavoriteRepository
import com.example.movieapp.data.local.ReviewEntity
import com.example.movieapp.data.repository.ReviewRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository
): ViewModel() {
    /**
     * Memuat daftar favorit secara reaktif berdasarkan userId pengguna yang aktif.
     */

    fun getFavorites(userId: Int): StateFlow<LibraryUiState> {
        return favoriteRepository.getFavoritesByUser(userId)
            .map { list ->
                if (list.isEmpty()) LibraryUiState.Empty else LibraryUiState.Success(list)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = LibraryUiState.Loading
            )
    }

    /**
     * Memuat daftar ulasan milik user secara reaktif
     */

    fun getUserReviews(userId: Int): Flow<List<ReviewEntity>> {
        return  reviewRepository.getUserReview(userId)
    }

    /**
     * Menghapus ulasan milik user
     */

    fun deleteReview(review: ReviewEntity) {
        viewModelScope.launch {
            reviewRepository.deleteReview(review)
        }
    }

    /**
     * Memperbarui ulasan dan rating yang diedit
     */

    fun updateReview(review: ReviewEntity, newRating: Float, newComment: String) {
        viewModelScope.launch {
            val updatedReview = review.copy(
                rating = newRating,
                comment =  newComment,
                timestamp = System.currentTimeMillis()
            )
            reviewRepository.updateReview(updatedReview)
        }
    }

    /**
     * Menghapus item favorit langsung dari halaman Library.
     */
    fun removeFavorite(movie: FavoriteMovieEntity) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(movie)
        }
    }
}