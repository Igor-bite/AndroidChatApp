package com.klyuzhevigor.ChatApp.Services

import com.klyuzhevigor.ChatApp.Model.MessageModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatsDataProvider {
    @GET("channels?limit=100")
    suspend fun getChats(): List<String>

    @GET("channel/{chat}")
    suspend fun getMessages(@Path("chat") chat: String, @Query("lastKnownId") lastId: Long): List<MessageModel>

    @POST("messages")
    suspend fun sendMessage(
        @Body message: MessageModel
    )
}
