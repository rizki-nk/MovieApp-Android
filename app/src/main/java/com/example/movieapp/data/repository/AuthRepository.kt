package com.example.movieapp.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    fun login(user: String, password: String): Boolean {
        // Validasi manual username & password admin
        return if (user == "admin" && password == "admin") {
            _username.value = user
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _username.value = ""
    }
}