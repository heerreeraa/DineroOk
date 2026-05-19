package com.example.dinerook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room - Representa un gasto del usuario
 */
@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val cantidad: Double,
    val categoria: String,
    val fecha: String,
    val userEmail: String // Vincula el gasto con su propietario
)

