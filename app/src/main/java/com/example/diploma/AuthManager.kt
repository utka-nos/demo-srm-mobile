package com.example.diploma

import android.content.Context
import android.content.SharedPreferences

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_PASSWORD = "user_id"
    }

    fun saveAuthData(token: String, login: String, userId: String) {
        prefs.edit()
            .putString(KEY_JWT_TOKEN, token)
            .putString(KEY_USER_LOGIN, login)
            .putString(KEY_USER_PASSWORD, userId)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_JWT_TOKEN, null)

    fun getLogin(): String? = prefs.getString(KEY_USER_LOGIN, null)

    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}
