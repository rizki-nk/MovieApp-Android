package com.example.movieapp.domain.model

import androidx.annotation.DrawableRes

data class News(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    @DrawableRes val imageRes: Int
)