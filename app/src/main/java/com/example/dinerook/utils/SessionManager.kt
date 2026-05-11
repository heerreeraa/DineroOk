package com.example.dinerook.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión usando SharedPreferences
 * Mantiene el estado de login del usuario
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "GestorGastosPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_EMAIL = "userEmail"
    }

    /**
     * Guarda la sesión del usuario
     */
    fun saveSession(email: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    /**
     * Verifica si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtiene el email del usuario logueado
     */
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout() {
        prefs.edit().apply {
            clear()
            apply()
        }
    }
}

