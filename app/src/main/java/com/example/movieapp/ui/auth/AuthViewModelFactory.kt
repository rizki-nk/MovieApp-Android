package com.example.movieapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.data.local.UserSessionManager
import com.example.movieapp.data.repository.UserRepository

class AuthViewModelFactory (
    private val userRepository: UserRepository,
    private val sessionManager: UserSessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userRepository, sessionManager) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}