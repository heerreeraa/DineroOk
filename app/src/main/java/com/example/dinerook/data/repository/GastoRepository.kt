package com.example.dinerook.data.repository

import androidx.lifecycle.LiveData
import com.example.dinerook.data.dao.GastoDao
import com.example.dinerook.data.entity.Gasto

/**
 * Repository - Intermediario entre ViewModel y DAO para operaciones de Gastos
 * Abstrae la fuente de datos del ViewModel
 */
class GastoRepository(private val gastoDao: GastoDao) {

    fun getAllGastosByUser(userEmail: String): LiveData<List<Gasto>> =
        gastoDao.getAllGastosByUser(userEmail)

    fun getTotalGastadoByUser(userEmail: String): LiveData<Double?> =
        gastoDao.getTotalGastadoByUser(userEmail)

    fun getGastosCountByUser(userEmail: String): LiveData<Int> =
        gastoDao.getGastosCountByUser(userEmail)

    suspend fun insertGasto(gasto: Gasto): Long =
        gastoDao.insertGasto(gasto)

    suspend fun updateGasto(gasto: Gasto) =
        gastoDao.updateGasto(gasto)

    suspend fun deleteGasto(gasto: Gasto) =
        gastoDao.deleteGasto(gasto)

    suspend fun getGastoById(id: Int, userEmail: String): Gasto? =
        gastoDao.getGastoById(id, userEmail)

    fun getGastosByCategoria(categoria: String, userEmail: String): LiveData<List<Gasto>> =
        gastoDao.getGastosByCategoria(categoria, userEmail)
}

