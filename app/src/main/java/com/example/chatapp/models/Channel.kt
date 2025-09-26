package com.example.chatapp.models

data class Channel(
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)