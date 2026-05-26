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

class GastoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GastoRepository
    private val sessionManager: SessionManager = SessionManager(application)
    private val currentUserEmail = MutableLiveData<String>()

    val allGastos: LiveData<List<Gasto>>
    val totalGastado: LiveData<Double?>
    val gastosCount: LiveData<Int>

    init {
        val gastoDao = AppDatabase.getDatabase(application).gastoDao()
        repository = GastoRepository(gastoDao)

        currentUserEmail.value = sessionManager.getUserEmail() ?: ""

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

    fun refreshCurrentUser() {
        currentUserEmail.value = sessionManager.getUserEmail() ?: ""
    }

    fun insertGasto(gasto: Gasto) = viewModelScope.launch {
        val userEmail = sessionManager.getUserEmail() ?: return@launch
        repository.insertGasto(gasto.copy(userEmail = userEmail))
    }

    fun updateGasto(gasto: Gasto) = viewModelScope.launch {
        val userEmail = sessionManager.getUserEmail() ?: return@launch
        repository.updateGasto(gasto.copy(userEmail = userEmail))
    }

    fun deleteGasto(gasto: Gasto) = viewModelScope.launch {
        repository.deleteGasto(gasto)
    }

    fun getGastosByCategoria(categoria: String): LiveData<List<Gasto>> {
        val userEmail = sessionManager.getUserEmail() ?: ""
        return repository.getGastosByCategoria(categoria, userEmail)
    }

    suspend fun getGastoById(id: Int): Gasto? {
        val userEmail = sessionManager.getUserEmail() ?: return null
        return repository.getGastoById(id, userEmail)
    }
}
