package com.klyuzhevigor.ChatApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.Auth.LoginScreen
import com.klyuzhevigor.ChatApp.Auth.LoginScreenViewModel
import com.klyuzhevigor.ChatApp.ChatsList.ChatsListScreen
import com.klyuzhevigor.ChatApp.ui.theme.ChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val auth = AuthManager()
                    NavHost(navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(viewModel = LoginScreenViewModel(navController, auth))
                        }
                        composable("main") {
                            ChatsListScreen()
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