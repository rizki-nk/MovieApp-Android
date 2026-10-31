package com.example.movieapp.ui.screen



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.MovieUiState
import com.example.movieapp.ui.MovieViewModel
import com.example.movieapp.ui.components.old.ItemEmptySearch
import com.example.movieapp.ui.components.old.MovieItemShimmer
import com.example.movieapp.ui.components.old.MovieListContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit
) {
    // Membaca StateFlow dari ViewModel secara real-time
    val uiState by viewModel.uiState.collectAsState()

    // State untuk mengontrol apakah mode pencarian sedang aktif
    var isSearchActive by remember { mutableStateOf(false) }

    // State untuk menyimpan kueri pencarian teks
    var searchQuery by remember { mutableStateOf("") }

    // State untuk pulling Refresh
    val isRefreshing = uiState is MovieUiState.Loading
    val pullToRefreshState = rememberPullToRefreshState()

    // State untuk menentukan listView / gridView
    var isGridView by remember { mutableStateOf(false) }

    // Picu refresh saat user menarik layar
    if(pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.fetchMovie()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        // TAMPILAN 1: Kolom Input Pencarian (saat tombol search aktif)
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari Film...")},
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // TAMPILAN 2: Judul Default
                        Text(text = "Movie Showcase")
                    }
                },
                actions = {
                    if (isSearchActive) {
                        IconButton(
                            onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Pencarian"
                            )
                        }
                    } else {
                        //Tombol Grid / List
                        IconButton(
                            onClick = { isGridView = !isGridView }
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.AutoMirrored.Filled.List else Icons.Default.GridView,
                                contentDescription = if (isGridView) "Tampilan List" else "Tampilan Grid"
                            )
                        }

                        //Tombol Kaca Pembesar
                        IconButton(
                            onClick = { isSearchActive = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cari Film"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            // Reaksi UI berdasarkan status saat ini
            when (val state = uiState) {
                is MovieUiState.Loading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(5) {
                            MovieItemShimmer()
                        }
                    }
                }
                is MovieUiState.Success -> {
                    //Filtering berdasarkan Pencarian

                    val filteredMovies = if (searchQuery.isBlank()) {
                        state.movies
                    } else {
                        state.movies.filter { movie ->
                            movie.title.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredMovies.isEmpty()){
                        ItemEmptySearch(searchQuery = searchQuery)
                    } else {
                MovieListContent(
                    movies = filteredMovies,
                    isGridView = isGridView,
                    onMovieClick = onMovieClick
                )
            }

                }
                is MovieUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}