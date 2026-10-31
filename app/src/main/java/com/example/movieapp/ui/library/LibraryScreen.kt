package com.example.movieapp.ui.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.movieapp.data.local.FavoriteMovieEntity
import com.example.movieapp.data.local.ReviewEntity
import com.example.movieapp.ui.components.BottomNavigationBar
import com.example.movieapp.ui.components.EditReviewDialog
import com.example.movieapp.ui.components.TopSearchBar
import com.example.movieapp.ui.components.shimmerEffect
import java.util.Locale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    currentUserId: Int,
    uiState: LibraryUiState,
    viewModel: LibraryViewModel,
    username: String = "User",
    isLoggedIn: Boolean = true,
    onMovieClick: (Int) -> Unit,
    onDeleteClick: (FavoriteMovieEntity) -> Unit,
    onHomeClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Favorit Saya", "Ulasan Saya")

    // Collect data ulasan user secara reaktif dari ViewModel
    val userReviews by viewModel.getUserReviews(currentUserId).collectAsState(initial = emptyList())

    // State untuk kontrol EditReviewDialog
    var reviewToEdit by remember { mutableStateOf<ReviewEntity?>(null) }

    // State pencarian jika ingin memfilter daftar di Library
    var searchQuery by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
       topBar = {
           Column {
               TopSearchBar(
                   query = searchQuery,
                   onQueryChange = { searchQuery = it },
                   isLoggedIn = isLoggedIn,
                   username = username,
                   onLoginClick = onLoginClick,
                   onLogoutClick = onLogoutClick
               )

               TabRow(
                   selectedTabIndex = selectedTab,
                   modifier = Modifier.fillMaxWidth()
               ) {
                   tabs.forEachIndexed { index, title ->
                       Tab (
                           selected = selectedTab == index,
                           onClick = { selectedTab = index},
                           text = {
                               Text(
                                   text = title,
                                   fontWeight = FontWeight.Bold,
                                   modifier = Modifier.padding(vertical = 8.dp)
                               )
                           }
                       )
                   }
               }
           }
       },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (selectedTab) {
                //1. Tab Favorite
                0 -> {
                    when (uiState) {
                        is LibraryUiState.Loading -> LibraryShimmerGrid()
                        is LibraryUiState.Empty -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyLibraryState(
                                    message = "Belum ada Film favorit tersimpan",
                                    onExploreClick = onHomeClick
                                )
                            }
                        }
                        is LibraryUiState.Success -> {
                            FavoriteMovieGrid(
                                movies = uiState.favorites,
                                onMovieClick = onMovieClick,
                                onDeleteClick = onDeleteClick
                            )
                        }
                    }
                }

                //1. Tab Ulasan Saya
                1 -> {
                    if (userReviews.isEmpty()) {
                       EmptyLibraryState(
                           message = "Kamu belum memberikan ulasan atau rating",
                           onExploreClick = onHomeClick
                       )
                    } else {
                        UserReviewsList(
                            reviews = userReviews,
                            onEditClick = { review -> reviewToEdit = review},
                            onDeleteClick = { review -> viewModel.deleteReview(review) }
                        )
                    }
                }
            }
        }
    }

    // TAMPILKAN EDIT DIALOG JIKA ADA ULASAN YANG DIPILIH UNTUK DIEDIT
    reviewToEdit?.let { review ->
        EditReviewDialog(
            review = review,
            onDismiss = { reviewToEdit = null },
            onSave = { newRating, newComment ->
                viewModel.updateReview(review, newRating, newComment)
                reviewToEdit = null
            }

        )
    }
}

@Composable
private fun FavoriteMovieGrid(
    movies: List<FavoriteMovieEntity>,
    onMovieClick: (Int) -> Unit,
    onDeleteClick: (FavoriteMovieEntity) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(movies, key = { it.id }) { movie ->
            FavoriteMovieItem(
                movie = movie,
                onMovieClick = { onMovieClick(movie.id)},
                onDeleteClick = { onDeleteClick(movie)}
            )
        }
    }
}

@Composable
private fun FavoriteMovieItem(
    movie: FavoriteMovieEntity,
    onMovieClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick() }
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://image.tmdb.org/t/p/w500${movie.posterCard}")
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus dari favorit",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⭐ ${movie.rating}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// UI TAB ULASAN SAYA (KOMPONEN BARU)
@Composable
private fun UserReviewsList(
    reviews: List<ReviewEntity>,
    onEditClick: (ReviewEntity) -> Unit,
    onDeleteClick: (ReviewEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(reviews, key = { it.id }) { review ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Baris Atas: Bintang + Rating, Judul Movie, dan Tombol Aksi (Edit & Hapus)
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Bagian Kiri: Bintang, Rating, dan Judul Movie
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", review.rating),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = review.movieName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Bagian Kanan: Tombol Edit & Hapus (Fungsi asli tidak diubah sama sekali)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(onClick = { onEditClick(review) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Ulasan",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { onDeleteClick(review) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Ulasan",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bagian Tengah: Hasil Review (Komentar)
                    Text(
                        text = review.comment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bagian Bawah Kanan: Timestamp Review
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {

                        Text(
                            text = review.formattedDate, // Catatan: Sesuaikan jika properti waktu di ReviewEntity kamu bernama date / createdAt
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLibraryState(
    message: String,
    onExploreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExploreClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Jelajahi Film")
        }
    }
}

// Grid Skeleton untuk Loading Shimmer
@Composable
private fun LibraryShimmerGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shimmerEffect()
                )
            }
        }
    }
}