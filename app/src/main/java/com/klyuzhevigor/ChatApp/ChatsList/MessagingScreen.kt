package com.klyuzhevigor.ChatApp.ChatsList

import android.app.Application
import android.content.res.Configuration
import android.icu.text.UnicodeSet.SpanCondition
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
fun MessagingScreen(uiState: MessagingUiState, retryAction: () -> Unit, onMessageSent: (String) -> Unit, closeAction: (() -> Unit)?) {
    when (uiState) {
        is MessagingUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
        is MessagingUiState.Success -> {
            if (uiState.messages.isEmpty()) {
                EmptyView(onMessageSent)
            } else {
                MessagesColumn(uiState.messages, onMessageSent, closeAction)
            }
        }
        is MessagingUiState.Error -> ErrorScreen(retryAction, modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun MessagesColumn(chats: List<MessageModel>, onMessageSent: (String) -> Unit, closeAction: (() -> Unit)?) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Messages", fontSize = 44.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))

            closeAction?.let {
                Button(it, modifier = Modifier.padding(horizontal = 8.dp)) { Text("Close") }
            }
        }

        Spacer(Modifier.height(20.dp))

        val fraction = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) 0.9F else 0.75F
        LazyColumn(modifier = Modifier.fillMaxHeight(fraction)) {
            items(chats) { el ->
                el.data.text?.let {
                    MessageCell(
                        text = it.text
                    )
                }
                el.data.image?.let {
                    var showPopup by rememberSaveable {
                        mutableStateOf(false)
                    }
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

        InputView(onMessageSent)
    }
}

@Composable
fun InputView(onMessageSent: (String) -> Unit) {
    var newMessage by remember { mutableStateOf("") }

    Row(modifier = Modifier.height(60.dp)) {
        TextField(
            newMessage,
            onValueChange = {
                newMessage = it
            },
            modifier = Modifier
                .fillMaxWidth(fraction = 0.8F)
                .padding(horizontal = 4.dp),
            placeholder = {
                Text(stringResource(R.string.new_message))
            }
        )

        Button({
            onMessageSent(newMessage)
            newMessage = ""
        }) { Text(stringResource(R.string.send)) }
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
fun EmptyView(onMessageSent: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .padding(32.dp)
            .fillMaxHeight(fraction = 0.9F)) {
            Text(
                stringResource(R.string.no_messages_in_this_chat_yet),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        InputView(onMessageSent)
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
    private var chat: String
) : ViewModel() {
    var uiState: MessagingUiState by mutableStateOf(MessagingUiState.Loading)
        private set

    override fun onCleared() {
        super.onCleared()
    }

    init {
        getMessages()
    }

    fun setChat(selectedChat: String) {
        chat = selectedChat
        getMessages()
    }

    fun getMessages(shouldShowLoading: Boolean = true) {
        viewModelScope.launch {
            if (shouldShowLoading) {
                uiState = MessagingUiState.Loading
            }
            uiState = try {
                MessagingUiState.Success(chatsRepository.getMessages(chat))
            } catch (e: IOException) {
                MessagingUiState.Error
            } catch (e: HttpException) {
                MessagingUiState.Error
            }
        }
    }

    fun sendNewMessage(text: String) {
        viewModelScope.launch {
            try {
                chatsRepository.sendMessage(chat, text)
                getMessages(false)
            } catch(e: HttpException) {
                Log.i("MYERROR", e.message())
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