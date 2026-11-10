package com.example.movieapp.ui.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.movieapp.data.model.Genre
import com.example.movieapp.ui.components.*
import org.koin.androidx.compose.koinViewModel
import com.example.movieapp.ui.news.NewsViewModel
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.LocalContext
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.ui.trivia.TriviaViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    username: String,
    isLoggedIn: Boolean = false,
    onMovieClick: (Int) -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onLibraryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
){
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val searchResults by viewModel.searchResult.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Ambil state data On Now Playing dari ViewModel
    val nowPlayingMovies by viewModel.nowPlayingMovies.collectAsState()
    var selectedMovieForPreview by remember { mutableStateOf<Movie?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    HomeShimmerLoading()
                }
                is HomeUiState.Error -> {
                    ErrorStateView(
                        errorMessage = state.message,
                        onRetryClick = {
                            viewModel.loadHomeScreenData()
                        }
                    )
                }
                is HomeUiState.Success -> {
                    val newsViewModel: NewsViewModel = koinViewModel()
                    val newsList by newsViewModel.newsList.collectAsState()

                    val triviaViewModel: TriviaViewModel = koinViewModel ()
                    val triviaList by triviaViewModel.triviaList.collectAsState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Tombol Header & Pemicu Search Overlay
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Halo, $username",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(onClick = { isSearchActive = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Buka Pencarian",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        // 2. Slide Show / Featured Banner
                        item {
                            SlideShowSection(
                                movies = state.featuredMovies,
                                onMovieClick = { movie -> onMovieClick(movie.id)}
                            )
                        }

                        // 3. Filter Genre
                        item {
                            GenreChipGroup(
                                selectedGenre = selectedGenre,
                                onGenreSelected = { genre -> viewModel.onGenreSelected(genre)
                                }
                            )
                        }

                        // 4. Horizontal Grid Section (Popular Movies)
                        item {
                            HorizontalGridSection(
                                title = if (selectedGenre == Genre.ALL) "Popular Movies" else "Film ${selectedGenre.displayName}",
                                movies = state.movies,
                                onMovieClick = { movie ->
                                    selectedMovieForPreview = movie
                                     },
                                onSeeAllClick = onSeeAllClick
                            )
                        }

                        // 5. On Now Playing Section (Baru ditambahkan)
                        item {
                            HorizontalGridSection(
                                title = "On Now Playing",
                                movies = nowPlayingMovies,
                                onMovieClick = { movie ->
                                    selectedMovieForPreview = movie
                                },
                                onSeeAllClick = {}
                            )
                        }

                        // 6. News Section
                        item {
                            NewsSection(
                                newsList = newsList,
                                onNewsClick = { article ->
                                    article.url?.let { url ->
                                        val builder = CustomTabsIntent.Builder()
                                        val customTabsIntent = builder.build()
                                        customTabsIntent.launchUrl(context, Uri.parse(url))
                                    }
                                }
                            )
                        }

                        // 7. Trivia Section (Placeholder dulu sampai model/usecase Trivia dibuat)
                        item { TriviaSection(triviaList = triviaList) }
                    }
                }
            }
            // Tampilkan SearchOverlay di atas segalanya ketika isSearchActive bernilai true
            if (isSearchActive) {
                searchOverlay(
                    searchQuery = searchQuery,
                    searchResults = searchResults,
                    onQueryChanged = { newQuery ->
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    onCloseClick = {
                        isSearchActive = false
                        viewModel.onSearchQueryChanged("")
                    },
                    onMovieClick = { movieId ->
                        isSearchActive = false
                        onMovieClick(movieId)
                    }
                )
            }

            MovieQuickPreviewSheet(
                movie = selectedMovieForPreview,
                lifecycleOwner = lifecycleOwner,
                onDismiss = { selectedMovieForPreview = null },
                onviewDetailClick = { movieId ->
                    selectedMovieForPreview = null
                    onMovieClick(movieId)
                }
            )
        }
    }


// Komponen Skeleton Shimmer untuk Section Home
@Composable
fun HomeShimmerLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Row Film Populer Shimmer
        Row (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(190.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}