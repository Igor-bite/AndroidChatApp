package com.klyuzhevigor.ChatApp.Auth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.klyuzhevigor.ChatApp.R
import com.klyuzhevigor.ChatApp.navigateAndClean


@Composable
fun LoginScreen(viewModel: LoginScreenViewModel) {
    var nickname by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.welcome_to_chat), fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(value = nickname, onValueChange = {
            nickname = it
        }, label = {
            Text(stringResource(R.string.nickname))
        })

        Spacer(Modifier.height(20.dp))

        Button({
            viewModel.login(nickname)
        }) {
            Text(stringResource(R.string.login))
        }
    }
}

class LoginScreenViewModel(val navController: NavHostController, val auth: AuthManager) {
    fun login(nickname: String) {
        if (nickname.isEmpty()) {
            return
        }
        auth.login(nickname)
        navController.navigateAndClean("main")
    }
}
