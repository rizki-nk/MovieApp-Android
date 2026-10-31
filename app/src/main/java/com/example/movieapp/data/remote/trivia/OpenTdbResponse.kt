package com.example.movieapp.data.remote.trivia

import com.google.gson.annotations.SerializedName

data class OpenTdbResponse(
    @SerializedName("results")
    val results: List<OpenTdbResults>?
)

data class OpenTdbResults(
    @SerializedName("question")
    val question: String,
    @SerializedName("correct_answer")
    val correctAnswer: String
)