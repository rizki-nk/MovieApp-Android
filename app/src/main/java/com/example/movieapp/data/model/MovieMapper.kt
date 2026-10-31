package com.example.movieapp.data.model

import com.example.movieapp.domain.model.Movie

fun MovieResponse.toDomain(): Movie{
    //Format menit ke format Jam & Menit
    val totalMinutes = this.runtime ?:0
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val formattedDuration = if (hours > 60) "${hours} Jam ${minutes} menit" else "${minutes} menit"

    // Ambil path poster dari API, berikan string kosong jika null
    val posterUrl = this.posterPath ?: ""

    return Movie(
        id = this.id,
        title = this.title,
        description = this.overview,
        posterCard = posterUrl,
        bannerRes = posterUrl,
        rating = 0f,
        genre = emptyList(),
        duration = formattedDuration,
        cast = emptyList(),
        reviews = emptyList(),
        isFeatured = true,
        releaseDate = this.releaseDate ?: "-"
    )
}