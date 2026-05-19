package com.example.dinerook.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión - Guarda el estado de login en SharedPreferences
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "GestorGastosPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_EMAIL = "userEmail"
    }

    fun saveSession(email: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun logout() {
        prefs.edit().clear().apply()
    }
}

