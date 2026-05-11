package com.example.dinerook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dinerook.data.database.AppDatabase
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.data.repository.GastoRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar los datos de Gastos
 * Sigue el patrón MVVM
 */
class GastoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GastoRepository

    // LiveData observables
    val allGastos: LiveData<List<Gasto>>
    val totalGastado: LiveData<Double?>
    val gastosCount: LiveData<Int>

    init {
        val gastoDao = AppDatabase.getDatabase(application).gastoDao()
        repository = GastoRepository(gastoDao)
        allGastos = repository.allGastos
        totalGastado = repository.totalGastado
        gastosCount = repository.gastosCount
    }

    /**
     * Inserta un nuevo gasto
     */
    fun insertGasto(gasto: Gasto) = viewModelScope.launch {
        repository.insertGasto(gasto)
    }

    /**
     * Actualiza un gasto existente
     */
    fun updateGasto(gasto: Gasto) = viewModelScope.launch {
        repository.updateGasto(gasto)
    }

    /**
     * Elimina un gasto
     */
    fun deleteGasto(gasto: Gasto) = viewModelScope.launch {
        repository.deleteGasto(gasto)
    }

    /**
     * Obtiene gastos por categoría
     */
    fun getGastosByCategoria(categoria: String): LiveData<List<Gasto>> {
        return repository.getGastosByCategoria(categoria)
    }
}

