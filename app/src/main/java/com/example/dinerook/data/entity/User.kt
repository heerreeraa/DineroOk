package com.example.dinerook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room - Representa un usuario registrado
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val password: String
)

