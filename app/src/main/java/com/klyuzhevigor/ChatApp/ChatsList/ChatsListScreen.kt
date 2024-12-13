package com.klyuzhevigor.ChatApp.ChatsList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.ChatsApplication
import com.klyuzhevigor.ChatApp.R
import com.klyuzhevigor.ChatApp.Services.ChatsDataProvider
import com.klyuzhevigor.ChatApp.Services.ChatsRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun ChatsListScreen(chatsUiState: ChatsUiState, retryAction: () -> Unit, onChatClick: (chat: String) -> Unit, logoutAction: (() -> Unit)?) {
    when (chatsUiState) {
        is ChatsUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is ChatsUiState.Success -> ChatsColumn(chatsUiState.chats, onChatClick, logoutAction)
        is ChatsUiState.Error -> ErrorScreen(retryAction, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ChatsColumn(chats: List<String>, onChatClick: (chat: String) -> Unit, logoutAction: (() -> Unit)?) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Chats", fontSize = 44.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

            logoutAction?.let {
                Button(it, modifier = Modifier.padding(horizontal = 8.dp)) { Text("Logout") }
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn() {
            items(chats) { el ->
                ChatCell(
                    name = el,
                    onChatClick
                )
            }
        }
    }
}

@Composable
fun ChatCell(name: String, onClick: (name: String) -> Unit) {
    Surface(
        modifier = Modifier.padding(8.dp).clip(
            RoundedCornerShape(
                size = 20.dp
            )
        ).clickable { onClick(name) },
        tonalElevation = 16.dp,
        shadowElevation = 8.dp
    ) {
        Text(name, fontSize = 28.sp, modifier = Modifier.fillMaxWidth().padding(16.dp))
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

sealed interface ChatsUiState {
    data class Success(val chats: List<String>) : ChatsUiState
    data object Error : ChatsUiState
    data object Loading : ChatsUiState
}

class ChatsListViewModel(
    private val authManager: AuthManager,
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    var chatsUiState: ChatsUiState by mutableStateOf(ChatsUiState.Loading)
        private set

    override fun onCleared() {
        super.onCleared()
    }

    init {
        getChats()
    }

    fun getChats() {
        viewModelScope.launch {
            chatsUiState = ChatsUiState.Loading
            chatsUiState = try {
                ChatsUiState.Success(chatsRepository.getChats())
            } catch (e: IOException) {
                ChatsUiState.Error
            } catch (e: HttpException) {
                ChatsUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ChatsApplication)
                ChatsListViewModel(application.container.auth, application.container.chatsRepo)
            }
        }
    }
}