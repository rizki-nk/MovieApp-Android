package com.example.movieapp.ui.auth

import com.example.movieapp.data.local.UserEntity

sealed interface AuthUiState {
    object Idle: AuthUiState
    object loading: AuthUiState
    data class Success(val userEntity: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
}