package com.example.movieapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.Review
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel (
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    // 1. Private State (Hanya bisa diubah di dalam ViewModel)
    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)

    // 2. Public State (Hanya bisa dibaca oleh Jetpack Compose UI)
    val uiState: StateFlow<MovieUiState> = _uiState

    init {
        // Otomatis mengambil data saat ViewModel dibuat
        fetchMovie()
    }

    fun fetchMovie() {
        viewModelScope.launch {
            // Ubah status ke Loading
            _uiState.value = MovieUiState.Loading

            // Panggil Use Case (menggunakan operator invoke)
            val result = getPopularMoviesUseCase()

            //Penanganan Hasil
            result.onSuccess { movieList ->
                _uiState.value = MovieUiState.Success(movieList)
            }.onFailure { exception ->
                _uiState.value = MovieUiState.Error(
                    exception.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun addReview(movieId: Int, author: String, movieName: String, comment: String, rating: Float) {
        val currentState = _uiState.value
        if (currentState is MovieUiState.Success) {
            val updatedList = currentState.movies.map { movie ->
                if (movie.id == movieId) {
                    val newReview = Review(
                        author = author,
                        comment = comment,
                        rating = rating,
                        movieName = movieName)
                    // Buat objek Movie baru dengan list review yang ditambah
                    movie.copy(reviews = movie.reviews + newReview)
                } else {
                    movie
                }
            }
            _uiState.value = MovieUiState.Success(updatedList)
        }
    }

    fun deleteReview(movieId: Int, reviewToDelete: Review) {
        // Pastikan ulasan masih dalam batas waktu 30 menit (validasi keamanan)
        if (!reviewToDelete.isEditable) return

        val currentState = _uiState.value
        if (currentState is MovieUiState.Success) {
            val updatedMovies = currentState.movies.map { movie ->
                if (movie.id == movieId) {
                    // Filter dan hapus ulasan yang cocok berdasarkan ID-nya
                    val updatedReviews = movie.reviews.filterNot { it.id == reviewToDelete.id }
                    movie.copy(reviews = updatedReviews)
                } else {
                    movie
                }
            }

            // Update state agar UI otomatis me-render ulang daftar ulasan terbaru
            _uiState.value = MovieUiState.Success(updatedMovies)
        }
    }



}