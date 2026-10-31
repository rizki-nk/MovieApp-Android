package com.example.movieapp.data.repository

import android.text.Html
import com.example.movieapp.data.remote.trivia.TriviaApiService
import com.example.movieapp.domain.model.Trivia
import com.example.movieapp.domain.repository.TriviaRepository

class TriviaRepositoryImpl(
    private val triviaApiService: TriviaApiService
) : TriviaRepository {

    override suspend fun getMovieTrivia(): List<Trivia> {
        return try {
            val response = triviaApiService.getMovieTrivia()
            val result = response.results ?: emptyList()

            // Filter pertanyaan unik, acak urutan, lalu ambil 5 item saja
            result
                .distinctBy { it.question }
                .shuffled()
                .take(5)
                .mapIndexed { index, item ->
                    Trivia(
                        id = index + 1,
                        question = parseHtmlText(item.question),
                        answer = parseHtmlText(item.correctAnswer)
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseHtmlText(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }
}