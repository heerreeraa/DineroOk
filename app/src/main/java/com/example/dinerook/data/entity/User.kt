package com.example.dinerook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad User para Room Database
 * Almacena los usuarios registrados
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val password: String
)

