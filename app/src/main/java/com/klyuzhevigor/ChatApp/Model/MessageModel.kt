package com.klyuzhevigor.ChatApp.Model

import com.klyuzhevigor.ChatApp.database.entities.ChannelEntity
import com.klyuzhevigor.ChatApp.database.entities.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageModel(
    var id: Long,
    var from: String,
    var to: String,
    var data: MessageData,
    var time: Long
) {
    fun toDbEntity(): MessageEntity = MessageEntity(
        id = id,
        from = from,
        to = to,
        text = data.text?.text,
        image = data.image?.link,
        time = time
    )
}

@Serializable
data class MessageData(
    @SerialName("Text") var text: MessageDataText? = null,
    @SerialName("Image") var image: MessageDataImage? = null
)

@Serializable
data class MessageDataText(
    var text: String
)

@Serializable
data class MessageDataImage(
    var link: String
)

@Serializable
data class Chat(
    var name: String
) {
    fun toDbEntity(): ChannelEntity = ChannelEntity(
        name = name
    )
}