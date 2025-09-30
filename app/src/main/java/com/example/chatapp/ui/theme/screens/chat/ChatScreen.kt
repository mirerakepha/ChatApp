package com.example.chatapp.ui.theme.screens.chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.chatapp.R
import com.example.chatapp.data.ChatViewModel
import com.example.chatapp.models.Message
import com.example.chatapp.ui.theme.MarPurple
import com.example.chatapp.ui.theme.Purple
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, channelId: String, channelName: String) {
    val context = LocalContext.current
    val viewModel: ChatViewModel = hiltViewModel()

    LaunchedEffect(Unit) { viewModel.listenForMessages(channelId) }
    val messages = viewModel.message.collectAsState()

    val chooseDialog = remember { mutableStateOf(false) }
    val previewImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES).first()
        val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        cameraImageUri.value = uri
        return uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) previewImageUri.value = cameraImageUri.value
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { previewImageUri.value = it }
    }

    val documentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { viewModel.sendDocumentMessage(channelId, it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(createImageUri(context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(channelName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MarPurple)

            )
        },
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            ChatMessages(
                messages = messages.value,
                onSendMessage = { viewModel.sendMessage(channelId, it) },
                onOpenCamera = {
                    chooseDialog.value = false
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(createImageUri(context))
                    } else permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onImageClicked = { chooseDialog.value = true },
                onOpenGallery = {
                    chooseDialog.value = false
                    galleryLauncher.launch("image/*")
                },
                channelName = channelName,
                onAttachDocument = {
                    chooseDialog.value = false
                    documentLauncher.launch(arrayOf("application/pdf", "application/msword"))
                }
            )

            if (chooseDialog.value) {
                ContentSelectionDialog(
                    onOpenCamera = {
                        chooseDialog.value = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(createImageUri(context))
                        } else permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    onOpenGallery = {
                        chooseDialog.value = false
                        galleryLauncher.launch("image/*")
                    },
                    onAttachDocument = {
                        chooseDialog.value = false
                        documentLauncher.launch(arrayOf("application/pdf", "application/msword"))
                    },
                    onDismiss = { chooseDialog.value = false }
                )
            }

            previewImageUri.value?.let { uri ->
                ImagePreviewDialog(
                    uri = uri,
                    onDismiss = { previewImageUri.value = null },
                    onSend = {
                        viewModel.sendImageMessage(channelId, uri)
                        previewImageUri.value = null
                    }
                )
            }
        }
    }
}

@Composable
fun ContentSelectionDialog(
    onOpenCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    onAttachDocument: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Select Content") },
        text = {
            Column {
                Text("Camera", modifier = Modifier.fillMaxWidth().clickable { onOpenCamera() }.padding(12.dp))
                Text("Gallery", modifier = Modifier.fillMaxWidth().clickable { onOpenGallery() }.padding(12.dp))
                Text("Document", modifier = Modifier.fillMaxWidth().clickable { onAttachDocument() }.padding(12.dp))
            }
        }
    )
}

@Composable
fun ImagePreviewDialog(uri: Uri, onDismiss: () -> Unit, onSend: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onSend) { Text("Send", color = Color.White) } },
        dismissButton = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Discard", tint = Color.White)
            }
        },
        text = {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Preview",
                modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(8.dp))
            )
        }
    )
}

@Composable
fun ChatMessages(
    channelName: String,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onImageClicked: () -> Unit,
    onOpenGallery: () -> Unit,
    onAttachDocument: () -> Unit
) {
    var msg by remember { mutableStateOf("") }
    val hideKeyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 72.dp)
        ) {
            items(messages) { message -> ChatBubble(message) }
        }

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
                IconButton(onClick = onImageClicked) {
                    Icon(painter = painterResource(R.drawable.attach), contentDescription = "Attach", tint = MarPurple, modifier = Modifier.size(40.dp))
                }
            }

            TextField(
                value = msg,
                onValueChange = { msg = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { hideKeyboardController?.hide() }),
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

            IconButton(
                onClick = {
                    if (msg.isNotBlank()) {
                        onSendMessage(msg.trim())
                        msg = ""
                    }
                }
            ) {
                Icon(painter = painterResource(R.drawable.send), contentDescription = "Send", tint = MarPurple, modifier = Modifier.size(40.dp))
            }
        }
    }
}

fun openDocument(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse(url), "*/*")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try { context.startActivity(intent) } catch (e: Exception) {
        Toast.makeText(context, "No app found to open document", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isCurrentUser = message.senderId == Firebase.auth.currentUser?.uid
    val bubbleColor = if (isCurrentUser) MarPurple else Purple
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val maxChars = 120
    val maxBubbleWidth: Dp = with(LocalDensity.current) {
        (0.75f * LocalContext.current.resources.displayMetrics.widthPixels.toFloat()).toDp()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isCurrentUser) {
            Icon(Icons.Default.Person, contentDescription = "Avatar", modifier = Modifier.size(28.dp).clip(CircleShape), tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(
            modifier = Modifier
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .padding(10.dp)
                .widthIn(max = maxBubbleWidth)
        ) {
            when {
                message.imageUrl != null -> ChatImageMessage(message)
                message.documentUrl != null -> Text(
                    "📄 Document",
                    color = Color.White,
                    modifier = Modifier.clickable { openDocument(context, message.documentUrl) }
                )
                else -> {
                    val shouldTruncate = message.message.length > maxChars
                    val displayText = if (!expanded && shouldTruncate) message.message.take(maxChars) + "..." else message.message

                    Column {
                        Text(displayText, color = Color.White, modifier = Modifier.clickable(enabled = shouldTruncate) { expanded = !expanded })
                        if (shouldTruncate && !expanded) Text(
                            "Read more",
                            color = Color.Cyan,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp).clickable { expanded = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatImageMessage(message: Message) {
    var expanded by remember { mutableStateOf(false) }
    val maxChars = 120
    val maxBubbleWidth: Dp = with(LocalDensity.current) {
        (0.75f * LocalContext.current.resources.displayMetrics.widthPixels.toFloat()).toDp()
    }

    Column(modifier = Modifier.widthIn(max = maxBubbleWidth)) {
        Image(
            painter = rememberAsyncImagePainter(
                message.imageUrl,
                placeholder = painterResource(R.drawable.ic_image_placeholder),
                error = painterResource(R.drawable.ic_broken_image)
            ),
            contentDescription = "Image Message",
            modifier = Modifier.height(200.dp).fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        if (message.message.isNotBlank()) {
            val shouldTruncate = message.message.length > maxChars
            val displayText = if (!expanded && shouldTruncate) message.message.take(maxChars) + "..." else message.message
            Text(displayText, color = Color.White, modifier = Modifier.padding(top = 6.dp).clickable(enabled = shouldTruncate) { expanded = !expanded })
            if (shouldTruncate && !expanded) Text(
                "Read more",
                color = Color.Cyan,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp).clickable { expanded = true }
            )
        }
    }
}
