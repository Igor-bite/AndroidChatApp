package com.klyuzhevigor.ChatApp.ChatsList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.klyuzhevigor.ChatApp.ui.theme.ChatAppTheme

@Composable
fun PopupBox( onClickOutside: () -> Unit, content:@Composable () -> Unit) {
    // full screen background
    Dialog(
        onDismissRequest = {} ,
        DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
                .zIndex(10F),
            contentAlignment = Alignment.Center
        ) {
            Dialog(onDismissRequest = onClickOutside) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PopupPreview() {
    var showPopup by rememberSaveable {
        mutableStateOf(false)
    }
    ChatAppTheme {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .background(Color.Yellow)
            ) {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(Color.Green)
                        .clickable { showPopup = true }
                ) {
                    if (showPopup) {
                        PopupBox(onClickOutside = { showPopup = false }) {
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                                    .background(Color.Green)
                            )
                        }
                    }
                }
            }
        }
    }
}