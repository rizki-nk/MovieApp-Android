package com.example.movieapp.data.local

import androidx.room.Entity

@Entity(
    tableName = "favorite_movies",
    primaryKeys = ["id", "userId"] // Composite key agar favorit terpisah per user
)
data class FavoriteMovieEntity(
    val id: Int,
    val userId: Int,
    val title: String,
    val posterCard: String,
    val rating: Double
)