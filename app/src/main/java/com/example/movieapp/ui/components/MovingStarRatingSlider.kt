package com.example.movieapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MovingStarRatingSlider(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier
){
    // Ukuran bintang membesar seiring nilai rating naik (1.0 -> 5.0)
    val animatedStarSize by animateDpAsState(
        targetValue = (16 + (rating * 6)).dp,
        label = "StarSizeAnimation"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Menghitung posisi horizontal bintang agar mengikuti Slider Thumb
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val width = this.maxWidth
                val fraction = (rating - 1f) / (5f - 1f)

                // POSISI INSET SLIDER THUMB BISA DISESUAIKAN:
                // Slider thumb bawaan Material3 memiliki inset horizontal internal ~10.dp.
                // Lebar komponen bintang (bintang + teks) saat membesar di rating 5 mencapai ~68.dp.

                val sliderPadding = 10.dp
                val contentWidth = 68.dp // Estimasi lebar total bintang + teks "5.0"

                val usableWidth = width - (sliderPadding * 2) - contentWidth
                val xOffset = sliderPadding + (usableWidth * fraction)

                Row(
                   verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .offset(x = xOffset)
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(animatedStarSize)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", rating), // PERBAIKAN: %.1f
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Slider(
                value = rating,
                onValueChange = onRatingChange,
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}