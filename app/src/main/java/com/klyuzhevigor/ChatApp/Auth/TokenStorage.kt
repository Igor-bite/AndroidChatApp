package com.klyuzhevigor.ChatApp.Auth

import android.content.Context
import android.content.SharedPreferences

class TokenStorage(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "auth_token"
    }

    // Сохранение токена
    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    // Получение токена
    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    // Удаление токена
    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
    }
}