package com.example.dinerook.utils

import com.example.dinerook.R

/**
 * Utilidad para manejar categorías de gastos - iconos y colores
 */
object CategoryHelper {

    const val ALIMENTACION = "Alimentación"
    const val TRANSPORTE = "Transporte"
    const val SALUD = "Salud"
    const val ENTRETENIMIENTO = "Entretenimiento"
    const val SERVICIOS = "Servicios"
    const val OTROS = "Otros"

    fun getCategories(): List<String> = listOf(
        ALIMENTACION, TRANSPORTE, SALUD, ENTRETENIMIENTO, SERVICIOS, OTROS
    )

    fun getCategoryIcon(categoria: String): Int = when (categoria) {
        ALIMENTACION -> R.drawable.ic_food
        TRANSPORTE -> R.drawable.ic_transport
        SALUD -> R.drawable.ic_calendar
        ENTRETENIMIENTO -> R.drawable.ic_entertainment
        SERVICIOS -> R.drawable.ic_list
        OTROS -> R.drawable.ic_other
        else -> R.drawable.ic_money
    }

    fun getCategoryColor(categoria: String): Int = when (categoria) {
        ALIMENTACION -> R.color.cat_alimentacion
        TRANSPORTE -> R.color.cat_transporte
        SALUD -> R.color.cat_salud
        ENTRETENIMIENTO -> R.color.cat_entretenimiento
        SERVICIOS -> R.color.cat_servicios
        OTROS -> R.color.cat_otros
        else -> R.color.primary
    }
}

