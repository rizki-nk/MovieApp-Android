package com.example.movieapp.ui.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.model.Review
import kotlinx.coroutines.delay

@Composable
fun MovieReviewTab(
    movie: Movie,
    onAddReview: (author: String, comment: String, rating: Float) -> Unit,
    onDeleteReview: (Review) -> Unit,
    modifier: Modifier = Modifier
) {
    var authorName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }
    var selectedRating by remember { mutableFloatStateOf(5.0f) }

    // State Loading untuk efek jeda/delay saat kirim ulasan
    var isLoading by remember { mutableStateOf(false) }

    // Efek delay simulasi request server
    if (isLoading) {
        LaunchedEffect(Unit) {
            delay(1500)
            onAddReview(authorName, reviewComment, selectedRating)

            //Reset form setelah berhasil
            authorName = ""
            reviewComment = ""
            selectedRating = 5.0f
            isLoading = false
        }
    }

    Column(modifier = Modifier) {
        Text(
            text = "Tulis Ulasan Kamu",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Nama Kamu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Rating: ⭐ ${selectedRating.toInt()} / 5",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = selectedRating,
            onValueChange = { selectedRating = it },
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = reviewComment,
            onValueChange = { reviewComment = it },
            label = { Text("Ulasan / Pendapat") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (authorName.isNotBlank() && reviewComment.isNotBlank()) {
                    isLoading = true
                }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = !isLoading && authorName.isNotBlank() && reviewComment.isNotBlank()
        ) {
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mengirim...")
                }
            } else {
                Text("Kirim Ulasan")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ulasan Pengguna (${movie.reviews.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (movie.reviews.isEmpty()) {
            Text(
                text = "Belum ada ulasan. Jadilah yang pertama memberikan ulasan!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            movie.reviews.forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = review.author,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⭐ ${review.rating.toInt()}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (review.isEditable) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = { onDeleteReview(review) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Ulasan",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = review.comment,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}