package com.example.chatapp.ui.theme.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.R
import com.example.chatapp.BuildConfig
import com.example.chatapp.data.HomeViewModel
import com.example.chatapp.ui.theme.MarPurple
import com.google.firebase.auth.FirebaseAuth

import com.example.chatapp.MainActivity
import com.example.chatapp.ui.theme.screens.chat.CallButton
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current as MainActivity

    // Initialize Zego once when HomeScreen is entered
     LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.let {
            context.initZegoService(
                appID = BuildConfig.ZEGO_APP_ID,
                appSign = BuildConfig.ZEGO_APP_SIGN,
                userID = it.email!!,
                userName = it.email!!
            )
        }
    }

    val viewModel: HomeViewModel = viewModel()
    val channels = viewModel.channels.collectAsState()
    val showAddChannelDialog = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn {
                // Header
                item {
                    Text(
                        text = "Messages",
                        color = MarPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Search Bar Row with Floating Button
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Floating Add Button with Glow
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(
                                    elevation = 48.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    clip = false,
                                    ambientColor = Color(0xFF1976D2),
                                    spotColor = Color(0xFF42A5F5)
                                )
                                .shadow(
                                    elevation = 32.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    clip = false,
                                    ambientColor = Color(0xFF2196F3).copy(alpha = 0.9f),
                                    spotColor = Color(0xFF64B5F6).copy(alpha = 0.7f)
                                )
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(18.dp),
                                    clip = false,
                                    ambientColor = Color(0xFF90CAF9).copy(alpha = 0.5f),
                                    spotColor = Color(0xFFBBDEFB).copy(alpha = 0.3f)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MarPurple)
                                    .clickable { showAddChannelDialog.value = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.generative),
                                    contentDescription = "Add Channel",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        // Search Bar
                        var query by remember { mutableStateOf("") }
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search...", color = Color.Gray) },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MarPurple
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MarPurple,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = MarPurple,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                // Channel List
                items(channels.value) { channel ->
                    ChannelItem(
                        channelName = channel.name,
                        onClick = { navController.navigate("chat/${channel.id}&${channel.name}") },
                        onCall = {}
                    )
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }

            // Add Channel Dialog
            if (showAddChannelDialog.value) {
                ModalBottomSheet(
                    onDismissRequest = { showAddChannelDialog.value = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 16.dp
                ) {
                    AddChannelDialog { newChannel ->
                        if (newChannel.isNotBlank()) {
                            viewModel.addChannel(newChannel)
                        }
                        showAddChannelDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelItem(
    channelName: String,
    onClick: () -> Unit,
    shouldShowCallButtons: Boolean = false,
    onCall: (ZegoSendCallInvitationButton) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Channel Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MarPurple.copy(alpha = 0.1f))
                .padding(12.dp),
            tint = MarPurple
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = channelName,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            modifier = Modifier.weight(1f)
        )

        // Call buttons float to the end automatically
        if (shouldShowCallButtons) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CallButton(isVideoCall = true, onCall)
                CallButton(isVideoCall = false, onCall)
            }
        }
    }
}

@Composable
fun AddChannelDialog(onAddChannel: (String) -> Unit) {
    var channelName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create New Channel",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MarPurple
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = channelName,
            onValueChange = {
                channelName = it
                isError = false
            },
            label = { Text("Channel Name") },
            singleLine = true,
            isError = isError,
            supportingText = {
                if (isError) {
                    Text("Channel name cannot be empty")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MarPurple,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = MarPurple,
                focusedLabelColor = MarPurple
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onAddChannel("") }
            ) {
                Text("Cancel", color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (channelName.isBlank()) {
                        isError = true
                    } else {
                        onAddChannel(channelName)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MarPurple),
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        clip = false,
                        ambientColor = MarPurple.copy(alpha = 0.4f)
                    )
            ) {
                Text("Create Channel", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}
