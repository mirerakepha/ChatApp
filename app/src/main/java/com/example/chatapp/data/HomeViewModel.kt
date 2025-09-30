package com.example.chatapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.models.Channel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val channel = snapshot.getValue(Channel::class.java)
            channel?.let {
                _channels.value = _channels.value + it
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val updatedChannel = snapshot.getValue(Channel::class.java)
            updatedChannel?.let { updated ->
                _channels.value = _channels.value.map { channel ->
                    if (channel.id == updated.id) updated else channel
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val removedChannel = snapshot.getValue(Channel::class.java)
            removedChannel?.let { removed ->
                _channels.value = _channels.value.filter { it.id != removed.id }
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    init {
        setupRealtimeListener()
    }

    private fun setupRealtimeListener() {
        firebaseDatabase.getReference("channels").addChildEventListener(childEventListener)
    }

    fun addChannel(name: String) {
        viewModelScope.launch {
            try {
                val key = firebaseDatabase.getReference("channels").push().key
                val channel = Channel(id = key ?: "", name = name)
                firebaseDatabase.getReference("channels").child(key!!).setValue(channel)
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firebaseDatabase.getReference("channels").removeEventListener(childEventListener)
    }
}