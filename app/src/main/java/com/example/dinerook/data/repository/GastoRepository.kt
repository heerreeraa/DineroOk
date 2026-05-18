package com.example.dinerook.data.repository

import androidx.lifecycle.LiveData
import com.example.dinerook.data.dao.GastoDao
import com.example.dinerook.data.entity.Gasto

/**
 * Repository para gestionar operaciones de datos de Gastos
 * Actúa como intermediario entre el ViewModel y el DAO
 */
class GastoRepository(private val gastoDao: GastoDao) {

    /**
     * Obtiene todos los gastos de un usuario
     */
    fun getAllGastosByUser(userEmail: String): LiveData<List<Gasto>> {
        return gastoDao.getAllGastosByUser(userEmail)
    }

    /**
     * Obtiene el total gastado por un usuario
     */
    fun getTotalGastadoByUser(userEmail: String): LiveData<Double?> {
        return gastoDao.getTotalGastadoByUser(userEmail)
    }

    /**
     * Obtiene el conteo de gastos de un usuario
     */
    fun getGastosCountByUser(userEmail: String): LiveData<Int> {
        return gastoDao.getGastosCountByUser(userEmail)
    }

    /**
     * Inserta un nuevo gasto
     */
    suspend fun insertGasto(gasto: Gasto): Long {
        return gastoDao.insertGasto(gasto)
    }

    /**
     * Actualiza un gasto existente
     */
    suspend fun updateGasto(gasto: Gasto) {
        gastoDao.updateGasto(gasto)
    }

    /**
     * Elimina un gasto
     */
    suspend fun deleteGasto(gasto: Gasto) {
        gastoDao.deleteGasto(gasto)
    }

    /**
     * Obtiene un gasto por ID y usuario
     */
    suspend fun getGastoById(id: Int, userEmail: String): Gasto? {
        return gastoDao.getGastoById(id, userEmail)
    }

    /**
     * Obtiene gastos por categoría y usuario
     */
    fun getGastosByCategoria(categoria: String, userEmail: String): LiveData<List<Gasto>> {
        return gastoDao.getGastosByCategoria(categoria, userEmail)
    }
}

