package com.example.chatapp.models

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderImage: String? = null,
    val imageUrl: String? = null,
    val documentUrl: String? = null,
)
