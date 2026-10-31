package com.example.movieapp.data.repository

import com.example.movieapp.R
import com.example.movieapp.domain.model.News
import com.example.movieapp.domain.model.Trivia
import com.example.movieapp.domain.repository.HomeExtrasRepository
import kotlinx.coroutines.delay

class HomeExtrasRepositoryImpl : HomeExtrasRepository {

    override suspend fun getMovieNews(): Result<List<News>> {
        return try {
            delay(500)
            val dummyNews = listOf(
                News(
                    id = 1,
                    title = "Spider-Man Brand New Day Resmi Diumumkan!",
                    description = "Marvel Studios dan Sony mengonfirmasi seri Spider-Man terbaru dengan alur cerita yang lebih emosional.",
                    date = "05 Sep 2026",
                    imageRes = R.drawable.poster_bny
                ),
                News(
                    id = 2,
                    title = "Rahasia Dibalik Multiverse Spider-Verse",
                    description = "Tim animator membeberkan teknik visual baru yang digunakan dalam penggarapan efek animasi unik.",
                    date = "03 Sep 2026",
                    imageRes = R.drawable.poster_itsv
                )
            )
            Result.success(dummyNews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieTrivia(): Result<List<Trivia>> {
        return try {
            delay(500)
            val dummyTrivia = listOf(
                Trivia(
                    id = 1,
                    question = "Siapa pemeran Spider-Man termuda di MCU?",
                    answer = "Tom Holland pertama kali memerankan Spider-Man di usia 19 tahun pada film Captain America: Civil War."
                ),
                Trivia(
                    id = 2,
                    question = "Berapa banyak gaya animasi berbeda di Spider-Verse?",
                    answer = "Ada lebih dari 6 gaya animasi berbeda yang digabungkan untuk mewakili tiap dimensi Spider-Hero."
                )
            )
            Result.success(dummyTrivia)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}