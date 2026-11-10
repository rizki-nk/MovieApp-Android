package com.example.movieapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.example.movieapp.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieQuickPreviewSheet(
    movie: Movie?,
    lifecycleOwner: LifecycleOwner,
    onDismiss: () -> Unit,
    onviewDetailClick: (Int) -> Unit
) {
    if (movie != null) {

        val hasVideo = movie.youtubeVideoId.isNotBlank()
        android.util.Log.d("YOUTUBE_DEBUG", "Video ID untuk ${movie.title}: '${movie.youtubeVideoId}' (hasVideo: $hasVideo)")
        val images = listOfNotNull(
            movie.bannerRes.takeIf { it.isNotBlank() },
            movie.posterCard.takeIf { it.isNotBlank() }
        ).distinct()

        val totalPages = (if (hasVideo) 1 else 0) + images.size
        val pagerState = rememberPagerState(pageCount = { totalPages.coerceAtLeast(1) })


        ModalBottomSheet(
            onDismissRequest = onDismiss,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Bagian Atas: Pager untuk Swipe Poster / Banner / Trailer
                Box (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (totalPages > 0) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            if (hasVideo && page == 0) {
                                YouTubePlayerSection(
                                    videoId = movie.youtubeVideoId,
                                    lifecycleOwner = lifecycleOwner,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Halaman berikutnya menampilkan gambar banner/poster
                                val imageIndex = if (hasVideo) page -1 else page
                                val imageUrl = images.getOrNull(imageIndex) ?: images.firstOrNull() ?: ""
                                val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "https://image.tmdb.org/t/p/w500$imageUrl"

                                AsyncImage(
                                    model = fullUrl,
                                    contentDescription = movie.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }

                        if (totalPages > 1) {
                            Row(
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(totalPages) { iteration ->
                                    val color = if (pagerState.currentPage == iteration) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .size(6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Bagian Tengah: Informasi (Judul Film, Tahun, Genre)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )

                        val releaseYear = movie.releaseDate.takeIf { it.isNotBlank() }?.take(4) ?: "Tahun Tidak Tersedia"
                        val genreText = if (movie.genre.isNotEmpty()) movie.genre.joinToString(", ") else "Genre Tidak Tersedia"

                        Text(
                            text = "$releaseYear • $genreText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }


                // Tombol Aksi Menuju Halaman Detail Penuh
                Button(
                    onClick = { onviewDetailClick(movie.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(text = "Lihat detail Selengkapnya >>>")
                }
            }
        }
    }
}