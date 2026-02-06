package com.example.diploma

import android.content.Context

class PushTokenStore(context: Context) {

    private val prefs = context.getSharedPreferences("push_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_token"
    }
}
