package com.example.dinerook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.dinerook.data.database.AppDatabase
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.data.repository.GastoRepository
import com.example.dinerook.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar los datos de Gastos
 * Sigue el patrón MVVM
 */
class GastoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GastoRepository
    private val sessionManager: SessionManager = SessionManager(application)

    // Email del usuario actual
    private val currentUserEmail = MutableLiveData<String>()

    // LiveData observables que se actualizan según el usuario
    val allGastos: LiveData<List<Gasto>>
    val totalGastado: LiveData<Double?>
    val gastosCount: LiveData<Int>

    init {
        val gastoDao = AppDatabase.getDatabase(application).gastoDao()
        repository = GastoRepository(gastoDao)

        // Establecer el email del usuario actual
        currentUserEmail.value = sessionManager.getUserEmail() ?: ""

        // Usar switchMap para que los LiveData se actualicen cuando cambie el usuario
        allGastos = currentUserEmail.switchMap { email ->
            repository.getAllGastosByUser(email)
        }

        totalGastado = currentUserEmail.switchMap { email ->
            repository.getTotalGastadoByUser(email)
        }

        gastosCount = currentUserEmail.switchMap { email ->
            repository.getGastosCountByUser(email)
        }
    }

    /**
     * Actualiza el usuario actual (útil cuando cambia la sesión)
     */
    fun refreshCurrentUser() {
        currentUserEmail.value = sessionManager.getUserEmail() ?: ""
    }

    /**
     * Inserta un nuevo gasto para el usuario actual
     */
    fun insertGasto(gasto: Gasto) = viewModelScope.launch {
        val userEmail = sessionManager.getUserEmail() ?: return@launch
        val gastoConUsuario = gasto.copy(userEmail = userEmail)
        repository.insertGasto(gastoConUsuario)
    }

    /**
     * Actualiza un gasto existente
     */
    fun updateGasto(gasto: Gasto) = viewModelScope.launch {
        val userEmail = sessionManager.getUserEmail() ?: return@launch
        val gastoConUsuario = gasto.copy(userEmail = userEmail)
        repository.updateGasto(gastoConUsuario)
    }

    /**
     * Elimina un gasto
     */
    fun deleteGasto(gasto: Gasto) = viewModelScope.launch {
        repository.deleteGasto(gasto)
    }

    /**
     * Obtiene gastos por categoría del usuario actual
     */
    fun getGastosByCategoria(categoria: String): LiveData<List<Gasto>> {
        val userEmail = sessionManager.getUserEmail() ?: ""
        return repository.getGastosByCategoria(categoria, userEmail)
    }

    /**
     * Obtiene un gasto por su ID del usuario actual
     */
    suspend fun getGastoById(id: Int): Gasto? {
        val userEmail = sessionManager.getUserEmail() ?: return null
        return repository.getGastoById(id, userEmail)
    }
}

