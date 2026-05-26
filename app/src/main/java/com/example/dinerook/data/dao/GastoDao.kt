package com.example.dinerook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dinerook.data.entity.Gasto

@Dao
interface GastoDao {

    @Query("SELECT * FROM gastos WHERE userEmail = :userEmail ORDER BY fecha DESC")
    fun getAllGastosByUser(userEmail: String): LiveData<List<Gasto>>

    @Query("SELECT * FROM gastos WHERE id = :id AND userEmail = :userEmail")
    suspend fun getGastoById(id: Int, userEmail: String): Gasto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasto(gasto: Gasto): Long

    @Update
    suspend fun updateGasto(gasto: Gasto)

    @Delete
    suspend fun deleteGasto(gasto: Gasto)

    @Query("SELECT * FROM gastos WHERE categoria = :categoria AND userEmail = :userEmail ORDER BY fecha DESC")
    fun getGastosByCategoria(categoria: String, userEmail: String): LiveData<List<Gasto>>

    @Query("SELECT SUM(cantidad) FROM gastos WHERE userEmail = :userEmail")
    fun getTotalGastadoByUser(userEmail: String): LiveData<Double?>

    @Query("SELECT COUNT(*) FROM gastos WHERE userEmail = :userEmail")
    fun getGastosCountByUser(userEmail: String): LiveData<Int>
}
