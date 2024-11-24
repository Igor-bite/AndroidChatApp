package com.klyuzhevigor.ChatApp.Services

import com.klyuzhevigor.ChatApp.Model.MessageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ChatsRepository {
    suspend fun getChats(): List<String>
    suspend fun getMessages(chat: String): List<MessageModel>
}

class NetworkChatsRepository(
    private val chatsDataProvider: ChatsDataProvider
) : ChatsRepository {
    override suspend fun getChats(): List<String> {
        return withContext(Dispatchers.IO) {
            chatsDataProvider.getChats()
        }
    }

    override suspend fun getMessages(chat: String): List<MessageModel> {
        return withContext(Dispatchers.IO) {
            chatsDataProvider.getMessages(chat)
        }
    }
}
