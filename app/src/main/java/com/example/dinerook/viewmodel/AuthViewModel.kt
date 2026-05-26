package com.example.dinerook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dinerook.data.database.AppDatabase
import com.example.dinerook.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    private val _loginResult = MutableLiveData<AuthResult>()
    val loginResult: LiveData<AuthResult> = _loginResult

    private val _registerResult = MutableLiveData<AuthResult>()
    val registerResult: LiveData<AuthResult> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val user = repository.login(email, password)
            _isLoading.value = false
            _loginResult.value = if (user != null) {
                AuthResult.Success
            } else {
                AuthResult.Error("Email o contraseña incorrectos")
            }
        }
    }

    fun register(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = repository.registerUser(email, password)
            _isLoading.value = false
            _registerResult.value = if (success) {
                AuthResult.Success
            } else {
                AuthResult.Error("Este email ya está registrado")
            }
        }
    }
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}
