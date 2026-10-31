package com.example.movieapp.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Review (
    val id: String = java.util.UUID.randomUUID().toString(),
    val movieName: String,
    val author: String,
    val comment: String,
    val rating: Float,
    val createdAt: Long = System.currentTimeMillis() //simpan timestamp
) {
    // 2. Properti dinamis untuk cek apakah ulasan belum lewat 30 menit
    val isEditable: Boolean
        get() = (System.currentTimeMillis() - createdAt)<= 30 * 60 * 1000L

    // 2. Format tanggal untuk langsung ditampilkan di Card Review UI
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MM yyyy, HH:mm", Locale.getDefault())
            return sdf.format(Date(createdAt))
        }
}