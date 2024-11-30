package com.klyuzhevigor.ChatApp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.klyuzhevigor.ChatApp.database.entities.ChannelEntity
import com.klyuzhevigor.ChatApp.database.entities.MessageEntity

@Dao
interface ChatsDao {
    @Query("SELECT * FROM channels")
    fun getAllChats(): List<ChannelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChannels(channels: List<ChannelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE `to` = :channelName")
    fun getMessages(channelName: String): List<MessageEntity>
}