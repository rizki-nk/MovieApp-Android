package com.example.movieapp.ui.screen.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer

import com.example.movieapp.domain.model.Review
import com.example.movieapp.domain.model.Movie
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movie: Movie,
    onBackClick: () -> Unit,
    onAddReview: (author: String, comment: String, rating: Float) -> Unit,
    onDeleteReview: (Review) -> Unit
){
    //State untuk menyimpan text input di textField
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Informasi", "Ulasan")

    // ScrollState untuk efek Parallax Header
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(movie.title, fontWeight = FontWeight.Bold)},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState) // Mengaktifkan scroll pada seluruh layar
        ) {
            // --- 1. COLLAPSING / PARALLAX HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .graphicsLayer{
                        // Efek parallax: Gambar bergerak lebih lambat dibanding scroll (0.5f)
                        translationY = scrollState.value * 0.5f
                        // Efek memudar saat di-scroll ke atas
                        alpha = (1f - (scrollState.value / 400f)).coerceIn(0f, 1f)
                    },
                contentAlignment = Alignment.Center
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. TAB ROW ---
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index},
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // --- 3. ANIMATED TAB CONTENT ---
            // Menggunakan AnimatedContent agar perpindahan Tab terasa halus (Fade In/Out)

            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "TabTransition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> MovieInfoTab(movie = movie)
                    1 -> MovieReviewTab(
                        movie = movie,
                        onAddReview = onAddReview,
                        onDeleteReview = onDeleteReview
                    )
                        }

                }
            }
        }
    }