package com.example.dinerook.data.entity

/**
 * Clase de datos para estadísticas por categoría
 */
data class CategoryStat(
    val categoria: String,
    val total: Double,
    val count: Int,
    val percentage: Int
)

