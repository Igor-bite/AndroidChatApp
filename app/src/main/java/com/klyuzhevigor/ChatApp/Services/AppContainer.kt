package com.klyuzhevigor.ChatApp.Services

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.database.AppDatabase
import com.klyuzhevigor.ChatApp.database.DBChatsRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


class DefaultAppContainer {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }

    private val baseUrl = "https://faerytea.name:8008"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    private val chatsDataProvider: ChatsDataProvider by lazy {
        retrofit.create(ChatsDataProvider::class.java)
    }

    private val networkChatsRepo: NetworkChatsRepository by lazy {
        NetworkChatsRepository(chatsDataProvider)
    }

    val auth: AuthManager by lazy {
        AuthManager()
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database.db")
            .build()
    }

    private val dbChatsRepository: DBChatsRepository by lazy { DBChatsRepository(appDatabase.getChatsDao()) }

    val chatsRepo: ChatsRepository by lazy { MainChatsRepository(networkChatsRepo, dbChatsRepository, connectivityManager) }

    private val connectivityManager: ConnectivityManager by lazy { applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
}
