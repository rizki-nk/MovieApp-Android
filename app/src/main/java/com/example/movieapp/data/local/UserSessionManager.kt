package com.example.movieapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.datastore by preferencesDataStore(name = "user_session")

class UserSessionManager(private val context: Context) {

    companion object {
        private val KEY_USER_ID = intPreferencesKey("current_user_id")
    }

    // Mengambil userId yang tersimpan secara reaktif (default: -1 jika belum login)
    val userIdFlow: Flow<Int> = context.datastore.data.map { preferences ->
        preferences[KEY_USER_ID] ?: -1
    }

    // Menyimpan userId saat login berhasil
    suspend fun saveUserSession(userId: Int){
        context.datastore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    suspend fun clearSession() {
        context.datastore.edit { preferences ->
            preferences.clear()
        }
    }
}