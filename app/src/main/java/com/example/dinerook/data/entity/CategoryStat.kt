package com.example.dinerook.data.entity

/**
 * Modelo de datos para estadísticas por categoría (no es entidad Room)
 */
data class CategoryStat(
    val categoria: String,
    val total: Double,
    val count: Int,
    val percentage: Int
)

