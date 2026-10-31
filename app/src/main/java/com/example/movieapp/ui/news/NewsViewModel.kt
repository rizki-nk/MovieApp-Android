package com.example.movieapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.remote.news.ArticleDto
import com.example.movieapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel (
    private val newsRepository: NewsRepository
): ViewModel() {

    private val _newsList = MutableStateFlow<List<ArticleDto>>(emptyList())
    val newsList: StateFlow<List<ArticleDto>> = _newsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isloading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchMovieNews()
    }

    fun fetchMovieNews(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val articles = newsRepository.getMovieNews()
                _newsList.value = articles
            } catch (e: Exception) {
                e.printStackTrace()
                _newsList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


}