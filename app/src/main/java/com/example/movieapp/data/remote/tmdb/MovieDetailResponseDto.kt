package com.example.movieapp.data.remote.tmdb

import com.google.gson.annotations.SerializedName

data class MovieDetailResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("genres")
    val genres: List<GenreDto>?,
    @SerializedName("credits")
    val credits: CreditsDto,
    @SerializedName("videos")
    val videos: MovieVideoResponse?,
    @SerializedName("release_date")
    val releaseDate: String?
)

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class CreditsDto(
    @SerializedName("cast")
    val cast: List<CastMemberDTO>?
)

data class CastMemberDTO(
    @SerializedName("name")
    val name: String?,
    @SerializedName("character")
    val character: String?,
    @SerializedName("profile_path")
    val profilePath: String?
)

data class MovieVideoResponse(
    @SerializedName("results")
    val results: List<VideoDto>?
)

data class VideoDto(
    @SerializedName("key")
    val key: String?,   // Contoh: "k64V0SWVEp0" (ID video YouTube)
    @SerializedName("site")
    val site: String?,  // Contoh: "YouTube"
    @SerializedName("type")
    val type: String?   // Contoh: "Trailer"
)