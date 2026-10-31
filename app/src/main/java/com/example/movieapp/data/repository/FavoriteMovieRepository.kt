package com.example.movieapp.data.repository

import com.example.movieapp.data.local.FavoriteDAO
import com.example.movieapp.data.local.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

class FavoriteMovieRepository (
    private val favoriteDAO: FavoriteDAO
) {
    /**
     * Mendapatkan seluruh daftar film favorit milik pengguna tertentu.
     */

    fun getFavoritesByUser(userId: Int): Flow<List<FavoriteMovieEntity>> {
        return favoriteDAO.getFavoritesByUser(userId)
    }

    /**
     * Mengecek apakah suatu film difavoritkan oleh pengguna tertentu.
     */

    fun isFavorite(movieId: Int, userId: Int): Flow<Boolean> {
        return favoriteDAO.isFavoriteByUser(movieId, userId)
    }

    /**
     * Menambahkan film ke daftar favorit pengguna.
     */
    suspend fun addFavorite(movie: FavoriteMovieEntity) {
        favoriteDAO.inserFavorite(movie)
    }

    /**
     * Menghapus film dari daftar favorit pengguna.
     */

    suspend fun deleteFavorite(movie: FavoriteMovieEntity) {
        favoriteDAO.deleteFavorite(movie)
    }
}