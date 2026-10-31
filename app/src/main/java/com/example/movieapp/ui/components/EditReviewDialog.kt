package com.example.movieapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.movieapp.data.local.ReviewEntity

@Composable
fun EditReviewDialog(
    review: ReviewEntity,
    onDismiss: () -> Unit,
    onSave: (newRating: Float, newComment: String) -> Unit
){
    var editedRating by remember {mutableStateOf(review.rating)}
    var editedComment by remember { mutableStateOf(review.comment)}

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Edit Rating & Ulasan",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Menggunakan Komponen MovingStarRatingSlider dari ui/components
                MovingStarRatingSlider(
                    rating = editedRating,
                    onRatingChange = { editedRating = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = editedComment,
                    onValueChange = { editedComment = it },
                    label = { Text("Ulasan Kamu") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (editedComment.isNotBlank()) {
                                onSave(editedRating, editedComment)
                            }
                        }
                    ) {
                        Text("Simpan")
                    }
                }

            }
        }
    }
}