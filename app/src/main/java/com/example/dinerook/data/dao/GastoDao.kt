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
     * Obtiene todos los gastos de un usuario ordenados por fecha descendente
     */
    @Query("SELECT * FROM gastos WHERE userEmail = :userEmail ORDER BY fecha DESC")
    fun getAllGastosByUser(userEmail: String): LiveData<List<Gasto>>

    /**
     * Obtiene un gasto por su ID y usuario
     */
    @Query("SELECT * FROM gastos WHERE id = :id AND userEmail = :userEmail")
    suspend fun getGastoById(id: Int, userEmail: String): Gasto?

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
     * Obtiene gastos por categoría y usuario
     */
    @Query("SELECT * FROM gastos WHERE categoria = :categoria AND userEmail = :userEmail ORDER BY fecha DESC")
    fun getGastosByCategoria(categoria: String, userEmail: String): LiveData<List<Gasto>>

    /**
     * Calcula el total gastado por un usuario
     */
    @Query("SELECT SUM(cantidad) FROM gastos WHERE userEmail = :userEmail")
    fun getTotalGastadoByUser(userEmail: String): LiveData<Double?>

    /**
     * Cuenta el número de gastos de un usuario
     */
    @Query("SELECT COUNT(*) FROM gastos WHERE userEmail = :userEmail")
    fun getGastosCountByUser(userEmail: String): LiveData<Int>
}

