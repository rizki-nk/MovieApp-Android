package com.example.movieapp.domain.model


data class Movie(
    val id: Int,
    val title: String,
    val description: String,
    val posterCard: String,      // Gambar vertikal (untuk Grid)
    val bannerRes: String = posterCard, // Gambar horizontal (untuk Slide Show)
    val rating: Float = 0f,             // Contoh: 4.8
    val genre: List<String> = emptyList(),
    val duration: String,
    val cast: List<Cast>,
    val reviews: List<Review> = emptyList(),
    val isFeatured: Boolean = false,
    val youtubeVideoId: String = "",
    val releaseDate: String
)
