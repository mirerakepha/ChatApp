package com.example.chatapp.data

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.models.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val message = _messages.asStateFlow()

    private val db = Firebase.database
    private val storage = Firebase.storage



    //Send a plain text message
    fun sendMessage(channelId: String, messageText: String) {
        val message = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = Firebase.auth.currentUser?.uid ?: "",
            message = messageText,
            createdAt = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            imageUrl = null,
            documentUrl = null
        )
        db.getReference("messages").child(channelId).push().setValue(message)
    }




    //Send an image message
    fun sendImageMessage(channelId: String, uri: Uri) {
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child(fileName)

        viewModelScope.launch {
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val message = Message(
                            id = db.reference.push().key ?: UUID.randomUUID().toString(),
                            senderId = Firebase.auth.currentUser?.uid ?: "",
                            message = "[Image]",
                            createdAt = System.currentTimeMillis(),
                            senderName = Firebase.auth.currentUser?.displayName ?: "",
                            imageUrl = downloadUrl.toString(),
                            documentUrl = null
                        )
                        db.getReference("messages").child(channelId).push().setValue(message)
                    }
                }
        }
    }



    //Send a document (PDF, Word,)
    fun sendDocumentMessage(channelId: String, uri: Uri) {
        val fileName = "documents/${UUID.randomUUID()}"
        val ref = storage.reference.child(fileName)

        viewModelScope.launch {
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val message = Message(
                            id = db.reference.push().key ?: UUID.randomUUID().toString(),
                            senderId = Firebase.auth.currentUser?.uid ?: "",
                            message = "[Document]",
                            createdAt = System.currentTimeMillis(),
                            senderName = Firebase.auth.currentUser?.displayName ?: "",
                            imageUrl = null,
                            documentUrl = downloadUrl.toString()
                        )
                        db.getReference("messages").child(channelId).push().setValue(message)
                    }
                }
        }
    }



    //Listen for incoming messages in real time
    fun listenForMessages(channelId: String) {
        db.getReference("messages").child(channelId).orderByChild("createdAt")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Message>()
                    snapshot.children.forEach { data ->
                        val message = data.getValue(Message::class.java)
                        message?.let { list.add(it) }
                    }
                    _messages.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error properly
                    println("Error fetching messages: ${error.message}")
                }
            }
        )
    }




}
