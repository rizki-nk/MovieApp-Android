package com.example.movieapp.ui.trivia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.domain.model.Trivia
import com.example.movieapp.domain.repository.TriviaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TriviaViewModel (
    private val triviaRepository: TriviaRepository
) : ViewModel() {
    private val _triviaList = MutableStateFlow<List<Trivia>>(emptyList())
    val triviaList: StateFlow<List<Trivia>> = _triviaList.asStateFlow()

    init {
        fetchTrivia()
    }

    fun fetchTrivia() {
        viewModelScope.launch {
            try {
                _triviaList.value = triviaRepository.getMovieTrivia()
            } catch (e: Exception) {
                e.printStackTrace()
                _triviaList.value = emptyList()
            }
        }
    }
}