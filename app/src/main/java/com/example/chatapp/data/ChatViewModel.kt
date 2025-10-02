package com.example.chatapp.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.toolbox.StringRequest
import com.example.chatapp.models.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.example.chatapp.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.reflect.Method
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.example.chatapp.SupabaseStorageUtils
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import dagger.hilt.android.qualifiers.ApplicationContext


import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(@ApplicationContext val context: Context ) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val message = _messages.asStateFlow()

    private val db = Firebase.database
    private val storage = Firebase.storage



    //Send a plain text message
    fun sendMessage(channelId: String,
                    messageText: String? = null,
                    imageUrl: String? = null,
                    documentUrl: String? = null
    ) {
        val message = Message(
            id = db.reference.push().key ?: UUID.randomUUID().toString(),
            senderId = Firebase.auth.currentUser?.uid ?: "",
            message = messageText?: "",
            createdAt = System.currentTimeMillis(),
            senderName = Firebase.auth.currentUser?.displayName ?: "",
            imageUrl = null,
            documentUrl = null
        )
        db.getReference("messages").child(channelId).push().setValue(message)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    postNotificationToUsers(channelId, message.senderName, messageText.toString())
                }


        }
    }




    //Send an image message
    fun sendImageMessage(channelId: String, uri: Uri) {
        viewModelScope.launch {
            val storageUtils = SupabaseStorageUtils(context)
            val downloadUrl = storageUtils.uploadImage(uri)

            downloadUrl?.let { url ->
                sendMessage(channelId, null, url)
            }
        }


        //<===== These are for Firebase =====>

        /*val fileName = "images/${UUID.randomUUID()}.jpg"
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
        }*/
    }



    //Send a document (PDF, Word,)
    fun sendDocumentMessage(channelId: String, uri: Uri) {
        viewModelScope.launch {
            val storageUtils = SupabaseStorageUtils(context)
            val downloadUrl = storageUtils.uploadFile(uri, "documents")

            downloadUrl?.let { url ->
                sendMessage(channelId, documentUrl = url)
            }
        }


        //<=====Firebase====>
        /*

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
        * */
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
        subscribeForNotification(channelId)
        registerUserIdtoChannel(channelId)
    }



    fun getAllUserEmails(channelID: String, callback: (List<String>) -> Unit) {
        val ref = db.reference.child("channels").child(channelID).child("users")
        val userIds = mutableListOf<String>()
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    userIds.add(it.value.toString())
                }
                callback.invoke(userIds)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.invoke(emptyList())
            }
        })
    }



    fun registerUserIdtoChannel(channelId: String){
        val currentUser = Firebase.auth.currentUser
        val ref = db.reference.child("channels").child(channelId).child("users")
        ref.child(currentUser?.uid?: "").addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()){
                        ref.child(currentUser?.uid?:"").setValue(currentUser?.email)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )

    }





    private fun subscribeForNotification(channelId: String){
        FirebaseMessaging.getInstance().subscribeToTopic( "group_$channelId" ).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d("ChatViewModel", "subscribe to topic: group_$channelId")
            }else{
                Log.d("ChatViewModel", "Failed to subscribe to topic: group_$channelId")

            }

        }

    }


    //this is there since no billing plan for the firebase cloud service

    private fun postNotificationToUsers(channelId: String, senderName: String, messageContent: String) {
        val fcmUrl = "https://fcm.googleapis.com/v1/projects/chatapp-9109d/messages:send"//project-id(chatapp-9109d)
        val jsonBody = JSONObject().apply {
            put("message", JSONObject().apply {
                put("topic", "group_$channelId")
                put("notification", JSONObject().apply {
                    put("title", "new message in $channelId")
                    put("body", "$senderName: $messageContent")
                })

            })
        }
        val requestBody = jsonBody.toString()

        val request = object : StringRequest(Method.POST, fcmUrl,
            Response.Listener<String> {
                Log.d("ChatViewModel", "Notification sent successfully")
            },
            Response.ErrorListener {
                Log.e("ChatViewModel", "Failed to send notification")
            }) {
            override fun getBody(): ByteArray{
                return requestBody.toByteArray()
            }
            override fun getHeaders(): MutableMap<String, String>{
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${getAccessToken()}"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }

    private fun getAccessToken(): String {
        val inputStream = context.resources.openRawResource(R.raw.chatapp_key)
        val googleCreds = GoogleCredentials.fromStream(inputStream)
            .createScoped("https://www.googleapis.com/auth/firebase.messaging")
        return googleCreds.refreshAccessToken().tokenValue

    }




}
