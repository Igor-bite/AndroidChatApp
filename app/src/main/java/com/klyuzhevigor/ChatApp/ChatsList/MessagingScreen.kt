package com.klyuzhevigor.ChatApp.ChatsList

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil3.compose.AsyncImage
import com.klyuzhevigor.ChatApp.Auth.AuthManager
import com.klyuzhevigor.ChatApp.ChatsApplication
import com.klyuzhevigor.ChatApp.Model.MessageModel
import com.klyuzhevigor.ChatApp.R
import com.klyuzhevigor.ChatApp.Services.ChatsRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


@Composable
fun MessagingScreen(uiState: MessagingUiState, retryAction: () -> Unit) {
    when (uiState) {
        is MessagingUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is MessagingUiState.Success -> {
            if (uiState.messages.isEmpty()) {
                EmptyView()
            } else {
                MessagesColumn(uiState.messages)
            }
        }
        is MessagingUiState.Error -> ErrorScreen(retryAction, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun MessagesColumn(chats: List<MessageModel>) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Text("Messages", fontSize = 44.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

        Spacer(Modifier.height(20.dp))

        var showPopup by rememberSaveable {
            mutableStateOf(false)
        }

        LazyColumn() {
            items(chats) { el ->
                el.data.text?.let {
                    MessageCell(
                        text = it.text
                    )
                }
                el.data.image?.let {
                    Box(
                        modifier = Modifier.clickable { showPopup = true }
                    ) {
                        AsyncImage(
                            model = "https://faerytea.name:8008/thumb/" + it.link,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                        if (showPopup) {
                            PopupBox(onClickOutside = { showPopup = false }) {
                                AsyncImage(
                                    model = "https://faerytea.name:8008/img/" + it.link,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageCell(text: String) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .clip(
                RoundedCornerShape(
                    size = 20.dp
                )
            ),
        tonalElevation = 16.dp,
        shadowElevation = 8.dp
    ) {
        Text(text, fontSize = 28.sp, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp))
    }
}

@Composable
fun EmptyView() {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.no_messages_in_this_chat_yet),
            modifier = Modifier.padding(32.dp),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

sealed interface MessagingUiState {
    data class Success(val messages: List<MessageModel>) : MessagingUiState
    data object Error : MessagingUiState
    data object Loading : MessagingUiState
}

class MessagesListViewModel(
    private val authManager: AuthManager,
    private val chatsRepository: ChatsRepository,
    private val chat: String
) : ViewModel() {
    var uiState: MessagingUiState by mutableStateOf(MessagingUiState.Loading)
        private set

    override fun onCleared() {
        super.onCleared()
    }

    init {
        getMessages()
    }

    fun getMessages() {
        viewModelScope.launch {
            uiState = MessagingUiState.Loading
            uiState = try {
                MessagingUiState.Success(chatsRepository.getMessages(chat))
            } catch (e: IOException) {
                MessagingUiState.Error
            } catch (e: HttpException) {
                MessagingUiState.Error
            }
        }
    }

    companion object {
        val CHAT_NAME_KEY = object : CreationExtras.Key<String> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY] as ChatsApplication)
                val chat = checkNotNull(this[CHAT_NAME_KEY] as String)

                MessagesListViewModel(
                    application.container.auth,
                    application.container.chatsRepo,
                    chat
                )
            }
        }
    }
}