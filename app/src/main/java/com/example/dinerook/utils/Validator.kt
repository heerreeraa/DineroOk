package com.example.dinerook.utils

import android.util.Patterns

/**
 * Clase utilitaria para validaciones de formularios
 */
object Validator {

    /**
     * Valida que un campo no esté vacío
     */
    fun isFieldEmpty(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    /**
     * Valida formato de email
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Valida que la cantidad sea mayor a 0
     */
    fun isValidAmount(amount: String?): Boolean {
        if (amount.isNullOrBlank()) return false
        return try {
            val value = amount.toDouble()
            value > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Valida contraseña (mínimo 6 caracteres)
     */
    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        return password.length >= 6
    }
}

