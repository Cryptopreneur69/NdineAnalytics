package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL = "gemini-3.5-flash"
    
    // Configured with 60-second timeouts as mandated by SKILL.md
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateAnalysis(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API Key is missing or default placeholder.")
            return@withContext "Error: Gemini API Key is not configured. Please add it via the Secrets panel in AI Studio."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey"

        try {
            // Build the JSON request body
            val part = JSONObject().put("text", prompt)
            val parts = JSONArray().put(part)
            val content = JSONObject().put("parts", parts)
            val contents = JSONArray().put(content)
            
            val requestJson = JSONObject().apply {
                put("contents", contents)
            }

            val body = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: ""
                    parseTextResponse(responseBody)
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API error: ${response.code} - $errorBody")
                    "Error: API returned code ${response.code}. Details: $errorBody"
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
            "Error: ${e.localizedMessage ?: "Unknown connection error"}"
        }
    }

    private fun parseTextResponse(responseBodyString: String): String {
        return try {
            val root = JSONObject(responseBodyString)
            val candidates = root.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val firstPart = parts.getJSONObject(0)
            firstPart.getString("text")
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response JSON", e)
            "Error parsing analysis response: ${e.message}"
        }
    }
}
