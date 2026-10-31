package com.example.movieapp.data.repository

import com.example.movieapp.data.local.UserDAO
import com.example.movieapp.data.local.UserEntity
import com.example.movieapp.util.HashUtils
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDAO: UserDAO
){
    /**
     * Mendaftarkan pengguna baru dengan kata sandi ter-hash.
     * Mengembalikan true jika berhasil, atau false jika email sudah terdaftar.
     */

    suspend fun registerUser(name: String, username: String, password: String): Boolean {
        val existingUser = userDAO.getUserByEmail(username)
        if (existingUser != null) {
            return false // Cek
        }

        val hashedPassword = HashUtils.hashPassword(password)
        val newUser = UserEntity(
            name = name,
            username = username,
            password = hashedPassword
        )

        val result = userDAO.registerUser(newUser)
        return result > 0
    }

    /**
     * Melakukan autentikasi login dengan memverifikasi email dan kata sandi ter-hash.
     */

    suspend fun login(username: String, password: String): UserEntity? {
        val hashedPassword = HashUtils.hashPassword(password)
        return userDAO.login(username, hashedPassword)
    }

    /**
     * Mendapatkan data profil pengguna berdasarkan ID.
     */

    fun getUseById(userId: Int): Flow<UserEntity?>{
        return userDAO.getUserById(userId)
    }
}