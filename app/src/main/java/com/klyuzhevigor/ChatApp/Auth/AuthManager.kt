package com.klyuzhevigor.ChatApp.Auth

class AuthManager {
    private var nickname: String = ""
    private var password: String = ""
    private var token: String = ""

    fun login(nickname: String) {
        this.nickname = nickname
        this.password = "helloworld"
        fetchToken()
    }

    private fun fetchToken() {
        token = "abracadabra"
    }
}