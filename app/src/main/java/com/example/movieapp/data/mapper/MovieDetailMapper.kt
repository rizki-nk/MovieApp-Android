package com.example.movieapp.data.mapper

import com.example.movieapp.data.remote.tmdb.MovieDetailResponseDto
import com.example.movieapp.domain.model.Cast
import com.example.movieapp.domain.model.Movie

fun MovieDetailResponseDto.toDomainModel(): Movie {
    val overviewText = if (!this.overview.isNullOrBlank()){
        this.overview
    } else {
        "Sinopsis belum tersedia dalam bahasa Indonesia."
    }

    val castList = this.credits?.cast?.map {
        Cast(
            name = it.name ?: "Unknown",
            character = it.character ?: "-",
            profileUrl = if (!it.profilePath.isNullOrBlank()) "https://image.tmdb.org/t/p/w185${it.profilePath}" else ""
        )
    }.orEmpty()

    val videosList = this.videos?.results ?: emptyList()

    val youtubeKey = videosList.firstOrNull {
        it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Trailer", ignoreCase = true)
    }?.key
        ?: videosList.firstOrNull {
            it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Teaser", ignoreCase = true)
        }?.key
        ?: videosList.firstOrNull {
            it.site.equals("YouTube", ignoreCase = true)
        }?.key
        ?: ""

    android.util.Log.d("TRAILER_DEBUG", "Judul Film: ${this.title} | YouTube Key: $youtubeKey")

    return Movie(
        id = this.id,
        title = this.title ?: "",
        description = overviewText,
        posterCard = this.posterPath ?: "",
        bannerRes = this.backdropPath ?: this.posterPath ?: "",
        rating = this.voteAverage?.toFloat() ?: 0f,
        duration = "${this.runtime ?: 0} menit", // Durasi
        genre = this.genres?.map { it.name } ?: emptyList(),
        cast = castList, // Bisa diisi nanti jika ada data cast
        youtubeVideoId = youtubeKey, // Bisa diisi nanti jika ada data trailer
        releaseDate = this.releaseDate ?: ""
    )
}
