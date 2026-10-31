package com.example.movieapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.model.Genre
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.usecase.GetMovieNewsUseCase
import com.example.movieapp.domain.usecase.GetMovieTriviaUseCase
import com.example.movieapp.domain.usecase.GetPopularMoviesUseCase
import com.example.movieapp.domain.usecase.SearchMovieUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel (
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getMovieNewsUseCase: GetMovieNewsUseCase,
    private val getMovieTriviaUseCase: GetMovieTriviaUseCase,
    private val searchMovieUseCase: SearchMovieUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    //state untuk melakukan pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow(Genre.ALL)
    val selectedGenre: StateFlow<Genre> = _selectedGenre.asStateFlow()

    //simpan list film asli sebagai filter
    private var originalMoviesList: List<Movie> = emptyList()

    private val _searchResult = MutableStateFlow<List<Movie>>(emptyList())
    val searchResult: StateFlow<List<Movie>> = _searchResult.asStateFlow()

    init {
        loadHomeScreenData()
    }

    //fungsi memuat data
    fun loadHomeScreenData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val moviesDeferred = async { getPopularMoviesUseCase() }
            val newsDeferred = async { getMovieNewsUseCase() }
            val triviaDeferred = async { getMovieTriviaUseCase() }

            val moviesResult = moviesDeferred.await()
            val newsResult = newsDeferred.await()
            val triviaResult = triviaDeferred.await()

            if (moviesResult.isSuccess) {
                originalMoviesList = moviesResult.getOrDefault(emptyList())

                val query = _searchQuery.value.trim()
                val genre = _selectedGenre.value

                val filteredList = filterMovies(originalMoviesList, query, genre)

                _uiState.value = HomeUiState.Success(
                    movies = filteredList,
                    featuredMovies = originalMoviesList,
                    news = newsResult.getOrDefault(emptyList()),
                    trivia = triviaResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = HomeUiState.Error(
                    moviesResult.exceptionOrNull()?.message ?: "Gagal memuat data"
                )
            }
        }
    }

    //fungsi pencarian data
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery

        if (newQuery.isBlank()) {
            _searchResult.value = emptyList()
            return
        }

        viewModelScope.launch {
            val result = searchMovieUseCase(newQuery)
            if (result.isSuccess) {
                _searchResult.value = result.getOrDefault(emptyList())
            } else {
                _searchResult.value = emptyList()
            }
        }
    }

    // Handler Perubahan Genre Chip
    fun onGenreSelected(genre: Genre) {
        _selectedGenre.value = genre
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val query = _searchQuery.value.trim()
            val genre = _selectedGenre.value

            val filteredList = filterMovies(originalMoviesList, query, genre)

            _uiState.value = currentState.copy(movies = filteredList)
        }
    }

    private fun filterMovies(movies: List<Movie>, query: String, genre: Genre): List<Movie>{
        return movies.filter { movie ->
            val matchesQuery = query.isBlank() || movie.title.contains(query, ignoreCase = true)

            // Mencocokkan ID angka dari List<Int> movie.genre dengan ID pada Enum Genre
            val matchesGenre = genre == Genre.ALL || movie.genre.any {it.equals(genre.name, ignoreCase = true)}

            matchesQuery && matchesGenre
        }
    }

}