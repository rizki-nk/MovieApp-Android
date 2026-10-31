package com.example.movieapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.FavoriteMovieEntity
import com.example.movieapp.data.local.ReviewEntity
import com.example.movieapp.data.repository.FavoriteRepository
import com.example.movieapp.data.repository.ReviewRepository
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.GetMovieByIdUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class DetailViewModel(
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DetailUiEvent>()
    val eventFlow: SharedFlow<DetailUiEvent> = _eventFlow.asSharedFlow()

    // Variabel untuk menyimpan job agar bisa dibatalkan jika load dipanggil ulang
    private var detailJob: kotlinx.coroutines.Job? = null

    fun loadMovieDetail(movieId: Int, userId: Int) {
        detailJob?.cancel()

        detailJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            getMovieByIdUseCase(movieId)
                .onSuccess { movie ->
                    if (movie != null) {
                        // Menggabungkan Flow reaktif dari Room dengan data Movie API
                        combine(
                            favoriteRepository.isFavorite(movieId, userId),
                            reviewRepository.getReviewsForMovie(movieId)
                        ){ isFav, reviews ->
                            DetailUiState.Success(
                                movie = movie,
                                isFavorite = isFav,
                                reviews = reviews
                            )
                        }.collect { combinedState ->
                            _uiState.value = combinedState
                        }
                    } else {
                        _uiState.value = DetailUiState.Error("Film tidak ditemukan")
                    }
                }
                .onFailure {
                    _uiState.value = DetailUiState.Error(it.message ?: "Terjadi kesalahan")
                }
        }
    }

    /**
     * Mengamati status favorit film berdasarkan userId secara reaktif dari Room Database
     */

    fun toogleFavorite(movie: Movie, userId: Int, isCurrentlyFavorite: Boolean) {
        if (userId == -1) {
            viewModelScope.launch {
                _eventFlow.emit(DetailUiEvent.ShowSnackBar("Silahkan login terlebih dahulu"))
            }
            return
        }

        viewModelScope.launch {
            val favoriteEntity = FavoriteMovieEntity(
                id = movie.id,
                userId = userId,
                title = movie.title,
                posterCard = movie.posterCard,
                rating = movie.rating.toDouble()
            )

            if (isCurrentlyFavorite) {
                favoriteRepository.removeFavorite(favoriteEntity)
                _eventFlow.emit(
                    DetailUiEvent.ShowSnackBar(
                        message = "'${movie.title}' dihapus dari favorit",
                        actionLabel = "Batal",
                        movie = movie,
                        userId = userId,
                        wasFavorite = true
                    )
                )
            } else {
                favoriteRepository.addFavorite(favoriteEntity)
                _eventFlow.emit(
                    DetailUiEvent.ShowSnackBar(
                        message = "'${movie.title}' ditambahkan ke favorit",
                        actionLabel = "Batal",
                        movie = movie,
                        userId = userId,
                        wasFavorite = false
                    )
                )
            }
        }
    }


    /**
     * Fungsi kirim review
     */

    fun submitReview(movieId: Int, userId: Int, username: String, movieTitle: String, comment: String, rating: Float) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            reviewRepository.addReview(
                movieId,
                userId,
                username,
                movieTitle,
                comment,
                rating)
            _eventFlow.emit(DetailUiEvent.ShowSnackBar("Ulasan Berhasil dikirim!"))
        }
    }
}