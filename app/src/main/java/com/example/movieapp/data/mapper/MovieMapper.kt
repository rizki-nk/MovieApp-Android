package com.example.movieapp.data.mapper

import com.example.movieapp.data.remote.tmdb.MovieDto
import com.example.movieapp.domain.model.Movie

fun MovieDto.toDomainModel(): Movie {
    return Movie(
        id = this.id, //
        title = this.title ?: "",
        description = this.overview ?: "",
        posterCard = this.posterPath ?: "",
        bannerRes = this.backdropPath ?: this.posterPath ?: "", // Gunakan backdropPath jika ada
        rating = this.voteAverage?.toFloat() ?: 0f,
        duration = "Durasi tidak tersedia", // List film tidak punya runtime dari TMDB
        genre = emptyList(), // List film dari TMDB biasanya hanya mengirim genre_ids (Int)
        cast = emptyList(),
        youtubeVideoId = "",
        releaseDate = this.releaseDate ?: ""
    )
}