package com.example.data.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object TelegramClient {
    private const val TAG = "TelegramClient"
    private val client = OkHttpClient()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(botToken: String, chatId: String, text: String): Boolean = withContext(Dispatchers.IO) {
        if (botToken.isBlank() || chatId.isBlank()) {
            Log.e(TAG, "Bot token or Chat ID is blank.")
            return@withContext false
        }

        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        
        try {
            val json = JSONObject().apply {
                put("chat_id", chatId)
                put("text", text)
                put("parse_mode", "Markdown")
            }

            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.d(TAG, "Message sent successfully to Telegram")
                    true
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "Failed to send message: ${response.code} - $errorBody")
                    false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending Telegram message", e)
            false
        }
    }
}
