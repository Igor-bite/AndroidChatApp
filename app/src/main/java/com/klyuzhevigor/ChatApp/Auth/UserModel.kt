package com.klyuzhevigor.ChatApp.Auth

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val name: String,
    val pwd: String
)