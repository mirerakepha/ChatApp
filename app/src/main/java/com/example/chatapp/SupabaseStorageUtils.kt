package com.example.chatapp

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.util.UUID

class SupabaseStorageUtils(val context: Context) {

    val supabase = createSupabaseClient(
        "https://sgbcuzldiznuakaqmjdk.supabase.co",  //URL
        ""   //Key
    ){
        install(Storage)
    }


    //method to upload images to the database
    suspend fun uploadImage(uri: Uri): String? {
        try {
            val extension = uri.path?.substringAfterLast(".")?: "jpg"
            val fileName = "${UUID.randomUUID()}.$extension"
            val inputStream = context.contentResolver.openInputStream(uri)?: return null

            //upload
            supabase.storage.from(BUCKET_NAME).upload(fileName, inputStream.readBytes())
            val publicUrl = supabase.storage.from(BUCKET_NAME).publicUrl(fileName)
            return publicUrl

        }catch (e: Exception){
            return null
        }
    }

    // generic upload
    suspend fun uploadFile(uri: Uri, folder: String = "uploads"): String? {
        return try {
            val extension = uri.path?.substringAfterLast(".") ?: "dat"
            val fileName = "$folder/${UUID.randomUUID()}.$extension"
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // upload to Supabase bucket
            supabase.storage.from(BUCKET_NAME).upload(fileName, inputStream.readBytes())

            // return public URL
            supabase.storage.from(BUCKET_NAME).publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    companion object{
        const val BUCKET_NAME = "ChatApp"
    }
}