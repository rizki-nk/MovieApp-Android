package com.example.movieapp.ui.components.old

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieapp.domain.model.Movie

@Composable
fun MovieListContent(
    movies: List<Movie>,
    isGridView: Boolean,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isGridView,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ViewModeTransition"
    ) { targetIsGrid ->
        if (targetIsGrid) {
            //Tampilan Grid 2 Kolom
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieGridItem(
                        movie = movie,
                        onMovieClick = { onMovieClick(movie)}
                    )
                }
            }
        } else {
            //Tampilan List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id}) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = { onMovieClick(movie)}
                    )

                }
            }

        }

    }
}
