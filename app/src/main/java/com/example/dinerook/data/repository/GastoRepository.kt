package com.example.dinerook.data.repository

import androidx.lifecycle.LiveData
import com.example.dinerook.data.dao.GastoDao
import com.example.dinerook.data.entity.Gasto

/**
 * Repository para gestionar operaciones de datos de Gastos
 * Actúa como intermediario entre el ViewModel y el DAO
 */
class GastoRepository(private val gastoDao: GastoDao) {

    // LiveData que se actualiza automáticamente
    val allGastos: LiveData<List<Gasto>> = gastoDao.getAllGastos()
    val totalGastado: LiveData<Double?> = gastoDao.getTotalGastado()
    val gastosCount: LiveData<Int> = gastoDao.getGastosCount()

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
     * Obtiene un gasto por ID
     */
    suspend fun getGastoById(id: Int): Gasto? {
        return gastoDao.getGastoById(id)
    }

    /**
     * Obtiene gastos por categoría
     */
    fun getGastosByCategoria(categoria: String): LiveData<List<Gasto>> {
        return gastoDao.getGastosByCategoria(categoria)
    }
}

