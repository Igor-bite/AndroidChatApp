package com.klyuzhevigor.ChatApp.Auth

import android.util.Log
import com.klyuzhevigor.ChatApp.Services.AuthTokenInterceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AuthManager(private val authApiService: Authorization, private val tokenSetter: (String?) -> Unit) {
    private var nickname: String = ""
    private var password: String = ""
    var token: String = ""

    suspend fun login(nickname: String) {
        this.nickname = nickname
        fetchToken()
    }

    fun logout() {
        this.nickname = ""
        this.password = ""
        this.token = ""
        tokenSetter(null)
    }

    private suspend fun fetchToken() {
        try {
            val requestText = "name=" + nickname
            val body = requestText.toRequestBody("text/plain".toMediaTypeOrNull());
            val passStr = authApiService.addUser(body).string()
            password = passStr.split(": ")[1].replace("'", "")
            val token = authApiService.login(UserModel(name = nickname, pwd = password)).string()
            this.token = token
            tokenSetter(token)
        } catch (e: Exception) {
            Log.i("ERROR", e.toString())
        }
    }
}