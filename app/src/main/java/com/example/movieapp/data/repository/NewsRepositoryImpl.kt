package com.example.movieapp.data.repository

import com.example.movieapp.data.remote.news.ArticleDto
import com.example.movieapp.data.remote.news.NewsApiService
import com.example.movieapp.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val apiService: NewsApiService
) : NewsRepository {

    override suspend fun getMovieNews(): List<ArticleDto> {
        return try{
            val response = apiService.getMovieNews()
            response.articles ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}