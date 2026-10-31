package com.example.movieapp.util

import java.security.MessageDigest

object HashUtils {
    /**
     * Mengubah teks kata sandi menjadi string hash SHA-256
     */

    fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it)}
    }
}