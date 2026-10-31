package com.example.movieapp.ui.components.old

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieapp.ui.components.shimmerEffect

@Composable
fun MovieItemShimmer(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skeleton Poster Gambar
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(12.dp))

            //Skeleton Teks (Judul, Deskripsi, Cast)
            Column(modifier = Modifier.weight(1f)) {
                //Judul
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Sinopsis Baris 1
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Sinopsis Baris 2
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(14.dp)
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Cast
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .shimmerEffect()
                )
            }
        }
    }
}