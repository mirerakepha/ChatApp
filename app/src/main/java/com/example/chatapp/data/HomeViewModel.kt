package com.example.chatapp.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.models.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel(){
    private val firebaseDatabase = Firebase.database
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()
    init {
        getChannels()
    }
    private fun getChannels(){
        firebaseDatabase.getReference("Channel").get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach {
                data ->
                val channel = Channel(data.key!!, data.value.toString())
                list.add(channel)
            }
            _channels.value = list

        }

    }
    fun addChannel(name: String){
        val key = firebaseDatabase.getReference("channel").push().key
        firebaseDatabase.getReference("channel").child(key!!).setValue(name).addOnSuccessListener {
            getChannels()
        }

    }
}