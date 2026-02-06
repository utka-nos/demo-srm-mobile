package com.example.diploma

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class PushTokenRegistrar(context: Context) {

    private val appContext = context.applicationContext
    private val authManager = AuthManager(appContext)

    suspend fun registerToken(token: String) {
        val jwtToken = authManager.getToken() ?: return

        withContext(Dispatchers.IO) {
            val connection = URL(PUSH_TOKEN_URL).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $jwtToken")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val payload = JSONObject()
                    .put("token", token)
                    .put("platform", "ANDROID")

                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(payload.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    Log.w(TAG, "Token registration failed with code: $responseCode")
                } else {
                    Log.i(TAG, "Token registered successfully with code: $responseCode")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Token registration failed: ${e.message}")
            } finally {
                connection.disconnect()
            }
        }
    }

    companion object {
        // Endpoint on backend-server for saving FCM token by authenticated user.
        private const val PUSH_TOKEN_URL = "http://10.0.2.2:8081/api/mobile/push-tokens"
        private const val TAG = "PushTokenRegistrar"
    }
}
