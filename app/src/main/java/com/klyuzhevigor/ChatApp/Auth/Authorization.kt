package com.klyuzhevigor.ChatApp.Auth

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Authorization {
    @POST("addusr")
    @Headers("Content-type: text/plain")
    suspend fun addUser(
        @Body nickname: RequestBody
    ): ResponseBody

    @POST("login")
    suspend fun login(
        @Body user: UserModel
    ): ResponseBody
}