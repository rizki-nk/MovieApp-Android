package com.example.movieapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.local.UserEntity
import com.example.movieapp.data.local.UserSessionManager
import com.example.movieapp.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class AuthViewModel (
    private val userRepository: UserRepository,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Menyimpan ID user yang sedang aktif terautentikasi (-1 jika belum login)
    private val _currentUserId = MutableStateFlow<Int>(-1)
    val currentUserId: StateFlow<Int> = sessionManager.userIdFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = -1
        )

    val currentUser: StateFlow<UserEntity?> = currentUserId.flatMapLatest { id ->
        if (id != -1) userRepository.getUseById(id) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Username dan password tidak boleh kosong !")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.loading
            val user = userRepository.login(username, password)
            if (user != null) {
               // _currentUserId.value = user.id -> Pindah ke Datastore
                sessionManager.saveUserSession(user.id)

                _uiState.value = AuthUiState.Success(user)
            } else {
                _uiState.value = AuthUiState.Error("Username atau password Salah !")
            }
        }
    }

    fun register(name: String, username: String, password: String) {
        if (name.isBlank() || username.isBlank() || password.isBlank()) {
             _uiState.value = AuthUiState.Error("Semua field harus diisi !")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.loading
            val isSuccess = userRepository.registerUser(name, username, password)
            if (isSuccess) {
                // Otomatis login setelah pendaftaran berhasil
                val user = userRepository.login(username, password)
                if (user != null) {
                    _currentUserId.value = user.id
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    _uiState.value = AuthUiState.Error("Registrasi berhasil, silahkan login")
                }
            } else {
                _uiState.value = AuthUiState.Error("Username sudah digunakan")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
          //  _currentUserId.value = -1 -> Berganti ke session Manager
            sessionManager.clearSession()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

}