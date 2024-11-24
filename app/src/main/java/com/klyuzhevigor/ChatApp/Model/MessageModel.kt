package com.klyuzhevigor.ChatApp.Model

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
    var Text: MessageDataText?,
    var Image: MessageDataImage?
)

@Serializable
data class MessageDataText(
    var text: String
)

@Serializable
data class MessageDataImage(
    var link: Url
)