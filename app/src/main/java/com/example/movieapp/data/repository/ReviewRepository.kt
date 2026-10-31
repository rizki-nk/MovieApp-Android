package com.example.movieapp.data.repository

import com.example.movieapp.data.local.ReviewDAO
import com.example.movieapp.data.local.ReviewEntity
import kotlinx.coroutines.flow.Flow

class ReviewRepository(private val reviewDAO: ReviewDAO) {
    fun getReviewsForMovie(movieId: Int): Flow<List<ReviewEntity>> {
        return reviewDAO.getReviewsForMovie(movieId)
    }

    suspend fun addReview(
        movieId: Int,
        userId: Int,
        username: String,
        movieName: String,
        comment: String,
        rating: Float
    ) {
        val review = ReviewEntity(
            movieId = movieId,
            userId = userId,
            username = username,
            movieName = movieName,
            comment = comment,
            rating = rating
        )
        reviewDAO.insertReview(review)
    }

    //Ambil Ulasan User
    fun getUserReview(userId: Int): Flow<List<ReviewEntity>> =
        reviewDAO.getUserReviews(userId)

    //Edit Ulasan
    suspend fun updateReview(review: ReviewEntity) {
        reviewDAO.updateReview(review)
    }

    //Hapus Ulasan
    suspend fun deleteReview(review: ReviewEntity) {
        reviewDAO.deleteReview(review)
    }
}