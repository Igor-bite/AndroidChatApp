package com.klyuzhevigor.ChatApp.database

import com.klyuzhevigor.ChatApp.database.entities.ChannelEntity
import com.klyuzhevigor.ChatApp.database.entities.MessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DBChatsRepository(private val chatsDao: ChatsDao) {
    suspend fun insertChannels(channels: List<ChannelEntity>) {
        withContext(Dispatchers.IO) {
            chatsDao.insertChannels(channels)
        }
    }

    suspend fun insertMessages(messages: List<MessageEntity>) {
        withContext(Dispatchers.IO) {
            chatsDao.insertMessages(messages)
        }
    }

    suspend fun getMessages(channelName: String): List<MessageEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext chatsDao.getMessages(channelName)
        }
    }

    suspend fun getAllChats(): List<ChannelEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext chatsDao.getAllChats()
        }
    }
}