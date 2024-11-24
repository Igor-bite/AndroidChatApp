package com.klyuzhevigor.ChatApp.Services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ChatsRepository {
    suspend fun getChats(): List<String>
}

class NetworkChatsRepository(
    private val chatsDataProvider: ChatsDataProvider
) : ChatsRepository {
    override suspend fun getChats(): List<String> {
        return withContext(Dispatchers.IO) {
            chatsDataProvider.getChats()
        }
    }
}
