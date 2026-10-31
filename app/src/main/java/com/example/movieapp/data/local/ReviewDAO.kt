package com.example.movieapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDAO {
    //Query Semua orang bisa lihat review berdasarkan movieId
    @Query("SELECT * FROM reviews WHERE movieId = :movieId ORDER BY timestamp DESC")
    fun getReviewsForMovie(movieId: Int): Flow<List<ReviewEntity>>

    //Hanya user yang bisa memasukan review
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)


    //Query mengambil ulasan aktif milik user
    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserReviews(userId: Int): Flow<List<ReviewEntity>>

    //Query Update ulasan (Rating & Comment)
    @Update
    suspend fun updateReview(review: ReviewEntity)

    //Query Delete
    @Delete
    suspend fun deleteReview(review: ReviewEntity)
}