package com.example.movieapp.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("runtime")
    val runtime: Int?,

    @SerializedName("actors")
    val actors: List<String>?,

    @SerializedName("release_date")
    val releaseDate: String?
)
