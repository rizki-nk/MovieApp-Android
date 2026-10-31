package com.example.movieapp.domain.repository

import com.example.movieapp.data.remote.news.ArticleDto

interface NewsRepository {
    suspend fun getMovieNews(): List<ArticleDto>
}