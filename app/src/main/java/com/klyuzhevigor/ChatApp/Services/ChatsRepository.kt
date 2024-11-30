package com.klyuzhevigor.ChatApp.Services

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.klyuzhevigor.ChatApp.Model.MessageData
import com.klyuzhevigor.ChatApp.Model.MessageDataImage
import com.klyuzhevigor.ChatApp.Model.MessageDataText
import com.klyuzhevigor.ChatApp.Model.MessageModel
import com.klyuzhevigor.ChatApp.database.DBChatsRepository
import com.klyuzhevigor.ChatApp.database.entities.ChannelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress


interface ChatsRepository {
    suspend fun getChats(): List<String>
    suspend fun getMessages(chat: String): List<MessageModel>
    suspend fun sendMessage(chat: String, text: String)
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
            chatsDataProvider.getMessages(chat + "@channel")
        }
    }

    override suspend fun sendMessage(chat: String, text: String) {
        withContext(Dispatchers.IO) {
            chatsDataProvider.sendMessage(
                MessageModel(
                    0,
                    "hello_world",
                    chat,
                    MessageData(
                        MessageDataText(text),
                        null
                    ),
                    0
                )
            )
        }
    }
}

class MainChatsRepository(
    private val network: NetworkChatsRepository,
    private val db: DBChatsRepository,
    private val connectivityManager: ConnectivityManager
): ChatsRepository {
    override suspend fun getChats(): List<String> {
        if (isInternetAvailable()) {
            val chats = network.getChats()
            db.insertChannels(chats.map { ChannelEntity(it) })
            return chats
        } else {
            return db.getAllChats().map {
                it.name
            }
        }
    }

    override suspend fun getMessages(chat: String): List<MessageModel> {
        if (isInternetAvailable()) {
            val messages = network.getMessages(chat)
            db.insertMessages(messages.map { it.toDbEntity() })
            return messages
        } else {
            return db.getMessages(chat).map {
                val text: MessageDataText? = it.text?.let { it1 -> MessageDataText(it1) }
                val image: MessageDataImage? = it.image?.let { it1 -> MessageDataImage(it1) }

                MessageModel(
                    it.id,
                    it.from,
                    it.to,
                    data = MessageData(
                        text,
                        image
                    ),
                    time = it.time
                )
            }
        }
    }

    override suspend fun sendMessage(chat: String, text: String) {
        withContext(Dispatchers.IO) {
            network.sendMessage(chat, text)
        }
    }

    private fun isInternetAvailable(): Boolean {
        val info = connectivityManager.activeNetworkInfo
        return info?.isConnectedOrConnecting ?: false
    }
}
