package com.example.movieapp.ui.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.movieapp.ui.components.MovingStarRatingSlider
import com.example.movieapp.ui.components.YouTubePlayerSection
import kotlinx.coroutines.flow.collectLatest
import com.example.movieapp.ui.components.ErrorStateView


import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.movieapp.ui.components.CastItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movieId: Int,
    currentUserId: Int,
    currentUsername: String = "",
    viewModel: DetailViewModel,
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // State untuk mengontrol tampilan Fullscreen Image
    var showFullImage by remember { mutableStateOf(false) }

    // Memuat detail film saat movieId berubah
    LaunchedEffect(movieId, currentUserId) {
        viewModel.loadMovieDetail(movieId, currentUserId)
    }

    // Mengambil daftar review
    var userComment by remember { mutableStateOf("") }
    var userRating by remember { mutableFloatStateOf(5.0f)}

    // Snackbar Host State
    val snackbarHostState = remember { SnackbarHostState() }

    // Ambil lifecycleOwner
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observasi Event dari ViewModel untuk Snackbar (Favorit & Review)
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is DetailUiEvent.ShowSnackBar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed && event.movie != null) {
                        viewModel.toogleFavorite(
                            movie = event.movie,
                            userId = event.userId,
                            isCurrentlyFavorite = !event.wasFavorite
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            TopAppBar(
                title = { Text("Detail Film") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                // Tombol Favorit (diambil langsung dari state Success jika ada)
                    if (uiState is DetailUiState.Success) {
                        val successState = uiState as DetailUiState.Success
                        val movie = successState.movie
                        val isFavorite = successState.isFavorite

                        IconButton(
                            onClick = {
                                if (currentUserId == -1) {
                                    Toast.makeText(context, "Silakan login terlebih dahulu untuk menyimpan favorit", Toast.LENGTH_SHORT).show()
                                    onNavigateToLogin()
                                } else {
                                    viewModel.toogleFavorite(
                                        movie = movie,
                                        userId = currentUserId,
                                        isCurrentlyFavorite = isFavorite
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Hapus dari favorite" else "Tambah ke favorite",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                   ErrorStateView(
                       errorMessage = state.message,
                       onRetryClick = {
                           viewModel.loadMovieDetail(movieId, currentUserId)
                       }
                   )
                }
                is DetailUiState.Success -> {
                    val movie = state.movie
                    val reviewList = state.reviews

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // 1. Banner & Header Gradient (Klik untuk Fullscreen)
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clickable { showFullImage = true }
                            ) {
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
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    MaterialTheme.colorScheme.background
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        // 2. Judul, Rating & Meta Info
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = movie.title,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            tint = Color(0xFFFFC107),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = movie.rating.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "•  ${movie.duration}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = movie.genre.joinToString(" • "),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // 3. Sinopsis & youtube player
                        item {
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Sinopsis",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = movie.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Text (
                                    text = "Trailer Resmi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                YouTubePlayerSection(
                                    videoId = movie.youtubeVideoId,
                                    lifecycleOwner = lifecycleOwner
                                )
                            }
                        }

                        // 4. Daftar Pemeran (Cast)
                        item {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Pemeran",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ){
                                    items(movie.cast) { castMember ->
                                        CastItem(
                                            name = castMember.name,
                                            character = castMember.character,
                                            profileUrl = castMember.profileUrl
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Section Ulasan & Rating
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                                Text(
                                    text = "Ulasan & Rating",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // FORM INPUT ULASAN (Hanya muncul jika user sudah LOGIN)
                                if (currentUserId != -1) {
                                    OutlinedTextField(
                                        value = userComment,
                                        onValueChange = { userComment = it },
                                        placeholder = { Text("Tulis ulasan kamu tentang film ini...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))
                                    // 2. Memanggil Komponen Rating Slider yang Baru
                                    MovingStarRatingSlider(
                                        rating = userRating,
                                        onRatingChange = { userRating = it }
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Button(
                                        onClick = {
                                            if (userComment.isNotBlank()) {
                                                viewModel.submitReview(
                                                    movieId = movieId,
                                                    userId = currentUserId,
                                                    username = currentUsername,
                                                    movieTitle = movie.title,
                                                    comment = userComment,
                                                    rating = userRating
                                                )
                                                userComment = "" // Reset input teks
                                                userRating = 5.0f
                                                Toast.makeText(context, "Ulasan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Kirim Ulasan")
                                    }
                                } else {
                                    // Peringatan bagi pengguna yang belum login
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Silakan login terlebih dahulu untuk memberikan ulasan.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // DAFTAR ULASAN (Bisa dilihat oleh SEMUA ORANG)
                                if (reviewList.isEmpty()) {
                                    Text(
                                        text = "Belum ada ulasan untuk film ini.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                } else {
                                    reviewList.forEach { itemReview ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = itemReview.username.ifEmpty { "Pengguna" },
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.labelLarge
                                                    )
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = Color(0xFFFFC107),
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text(
                                                            text = "${itemReview.rating}",
                                                            style = MaterialTheme.typography.labelMedium
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = itemReview.comment,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    }

                    // --- DIALOG FULLSCREEN IMAGE VIEWER ---
                    if (showFullImage) {
                        Dialog(
                            onDismissRequest = { showFullImage = false },
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .clickable { showFullImage = false}
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("https://image.tmdb.org/t/p/w500${movie.posterCard}")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = movie.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentScale = ContentScale.Fit
                                )
                                IconButton(
                                    onClick = { showFullImage = false},
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.White

                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}