package com.klyuzhevigor.ChatApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.Auth.LoginScreen
import com.klyuzhevigor.ChatApp.Auth.LoginScreenViewModel
import com.klyuzhevigor.ChatApp.ChatsList.ChatsListScreen
import com.klyuzhevigor.ChatApp.ChatsList.ChatsListViewModel
import com.klyuzhevigor.ChatApp.ChatsList.ChatsUiState
import com.klyuzhevigor.ChatApp.ChatsList.MessagesListViewModel
import com.klyuzhevigor.ChatApp.ChatsList.MessagingScreen
import com.klyuzhevigor.ChatApp.Model.Chat
import com.klyuzhevigor.ChatApp.Services.DefaultAppContainer
import com.klyuzhevigor.ChatApp.ui.theme.ChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) { innerPadding ->
                    val navController = rememberNavController()
                    val app = LocalContext.current.applicationContext as ChatsApplication
                    NavHost(navController, startDestination = "main") {
                        composable("login") {
                            LoginScreen(viewModel = LoginScreenViewModel(navController, app.container.auth))
                        }
                        composable("main") {
                            val vm: ChatsListViewModel =
                                viewModel(factory = ChatsListViewModel.Factory)
                            ChatsListScreen(vm.chatsUiState, retryAction = vm::getChats, onChatClick = { chat ->
                                navController.navigate(Chat(chat))
                            })
                        }
                        composable<Chat> { stackEntry ->
                            val chat = stackEntry.toRoute<Chat>().name
                            val chatName = chat.split("@")[0]
                            val extras = MutableCreationExtras().apply {
                                set(APPLICATION_KEY, (LocalContext.current.applicationContext as ChatsApplication))
                                set(MessagesListViewModel.CHAT_NAME_KEY, chatName)
                            }
                            val vm: MessagesListViewModel = viewModel(
                                factory = MessagesListViewModel.Factory,
                                extras = extras
                            )
                            MessagingScreen(vm.uiState, { vm.getMessages() })
                        }
                    }
                }
            }
        }
    }
}

fun NavHostController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}