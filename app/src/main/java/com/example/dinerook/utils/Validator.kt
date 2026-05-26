package com.example.dinerook.utils

import android.util.Patterns

object Validator {

    fun isFieldEmpty(text: String?): Boolean = text.isNullOrBlank()

    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidAmount(amount: String?): Boolean {
        if (amount.isNullOrBlank()) return false
        return try {
            amount.toDouble() > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValidPassword(password: String?): Boolean {
        if (password.isNullOrBlank()) return false
        return password.length >= 6
    }
}
