package com.klyuzhevigor.ChatApp.ChatsList

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.klyuzhevigor.ChatApp.Auth.AuthManager

@Composable
fun ChatsListScreen() {
    Text("ChatsListScreen")
}

class ChatsListViewModel(val authManager: AuthManager) {

}