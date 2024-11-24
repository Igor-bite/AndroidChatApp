package com.klyuzhevigor.ChatApp.Services

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException


class DefaultAppContainer {
    private val baseUrl = "https://faerytea.name:8008"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val chatsDataProvider: ChatsDataProvider by lazy {
        retrofit.create(ChatsDataProvider::class.java)
    }

    val chatsRepo: NetworkChatsRepository by lazy {
        NetworkChatsRepository(chatsDataProvider)
    }

    val auth: AuthManager by lazy {
        AuthManager()
    }
}
