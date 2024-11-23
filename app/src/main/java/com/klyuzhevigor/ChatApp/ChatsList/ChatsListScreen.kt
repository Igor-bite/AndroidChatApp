package com.klyuzhevigor.ChatApp.ChatsList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klyuzhevigor.ChatApp.Auth.AuthManager

@Composable
fun ChatsListScreen() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Text("Chats", fontSize = 44.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(20.dp))

        LazyColumn() {
            items(listOf("1", "2")) { el ->
                ChatCell(name = el)
            }
        }
    }
}

@Composable
fun ChatCell(name: String) {
    Text("Hello " + name, fontSize = 28.sp, modifier = Modifier.fillMaxWidth())
}

class ChatsListViewModel(val authManager: AuthManager) {

}