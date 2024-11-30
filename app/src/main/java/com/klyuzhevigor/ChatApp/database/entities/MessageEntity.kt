package com.klyuzhevigor.ChatApp.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "messages", indices = [Index(value = ["from"]), Index(value = ["to"])])
data class MessageEntity(
    @PrimaryKey val id: Long,
    val from: String,
    val to: String,
    val image: String? = null,
    val text: String? = null,
    val time: Long
)