package com.example.chatapp.models

data class Channel(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
){
    // Add empty constructor for Firebase
    constructor() : this("", "")
}