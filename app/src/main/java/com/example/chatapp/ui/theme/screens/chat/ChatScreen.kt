package com.example.chatapp.ui.theme.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chatapp.R
import com.example.chatapp.data.ChatViewModel
import com.example.chatapp.models.Message
import com.example.chatapp.ui.theme.MarPurple
import com.example.chatapp.ui.theme.Purple
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// 🔹 Chat Screen
@Composable
fun ChatScreen(navController: NavController, channelId: String) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val viewModel: ChatViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                viewModel.listenForMessages(channelId)
            }

            val messages = viewModel.message.collectAsState()

            ChatMessages(
                messages = messages.value,
                onSendMessage = { message ->
                    viewModel.sendMessage(channelId, message)
                },
                onOpenCamera = { /* handle camera */ },
                onOpenGallery = { /* handle gallery */ },
                onAttachDocument = { /* handle documents */ }
            )
        }
    }
}

// 🔹 Chat Message List + Input Bar
@Composable
fun ChatMessages(
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onAttachDocument: () -> Unit,
) {
    var msg by remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Messages
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message = message)
            }
        }

        // Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .background(Color.DarkGray, RoundedCornerShape(32.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (msg.isEmpty()) {
                IconButton(onClick = onOpenCamera) {
                    Icon(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = "Camera",
                        tint = MarPurple,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            TextField(
                value = msg,
                onValueChange = { msg = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { hideKeyboardController?.hide() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MarPurple,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (msg.isEmpty()) {
                IconButton(onClick = onAttachDocument) {
                    Icon(
                        painter = painterResource(R.drawable.attach),
                        contentDescription = "Attach",
                        tint = MarPurple,
                        modifier = Modifier.size(50.dp)
                    )
                }
                IconButton(onClick = onOpenGallery) {
                    Icon(
                        painter = painterResource(R.drawable.gallery),
                        contentDescription = "Gallery",
                        tint = MarPurple,
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        if (msg.isNotBlank()) {
                            onSendMessage(msg.trim())
                            msg = ""
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send),
                        contentDescription = "Send",
                        tint = MarPurple,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) MarPurple else Purple

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isCurrentUser) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Sender Avatar",
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .padding(10.dp)
        ) {
            Text(text = message.message, color = Color.White)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatMessages(
        messages = emptyList(),
        onSendMessage = {},
        onOpenCamera = {},
        onOpenGallery = {},
        onAttachDocument = {}
    )
}
