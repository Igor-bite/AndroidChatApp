package com.klyuzhevigor.ChatApp.Model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Url

@Serializable
data class MessageModel(
    var id: Int,
    var from: String,
    var to: String,
    var data: MessageData,
    var time: Long
)

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
)