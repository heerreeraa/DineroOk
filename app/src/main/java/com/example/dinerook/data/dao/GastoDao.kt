package com.example.dinerook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dinerook.data.entity.Gasto

/**
 * DAO para operaciones CRUD de Gastos
 */
@Dao
interface GastoDao {

    /**
     * Obtiene todos los gastos ordenados por fecha descendente
     */
    @Query("SELECT * FROM gastos ORDER BY fecha DESC")
    fun getAllGastos(): LiveData<List<Gasto>>

    /**
     * Obtiene un gasto por su ID
     */
    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun getGastoById(id: Int): Gasto?

    /**
     * Inserta un nuevo gasto
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto): Long

    /**
     * Actualiza un gasto existente
     */
    @Update
    suspend fun updateGasto(gasto: Gasto)

    /**
     * Elimina un gasto
     */
    @Delete
    suspend fun deleteGasto(gasto: Gasto)

    /**
     * Obtiene gastos por categoría
     */
    @Query("SELECT * FROM gastos WHERE categoria = :categoria ORDER BY fecha DESC")
    fun getGastosByCategoria(categoria: String): LiveData<List<Gasto>>

    /**
     * Calcula el total gastado
     */
    @Query("SELECT SUM(cantidad) FROM gastos")
    fun getTotalGastado(): LiveData<Double?>

    /**
     * Cuenta el número de gastos
     */
    @Query("SELECT COUNT(*) FROM gastos")
    fun getGastosCount(): LiveData<Int>
}

