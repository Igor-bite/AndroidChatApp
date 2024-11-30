package com.klyuzhevigor.ChatApp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klyuzhevigor.ChatApp.database.entities.ChannelEntity
import com.klyuzhevigor.ChatApp.database.entities.MessageEntity

@Database(
    version = 1,
    entities = [
        MessageEntity::class,
        ChannelEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getChatsDao(): ChatsDao
}