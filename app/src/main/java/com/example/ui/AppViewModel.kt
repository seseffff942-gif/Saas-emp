package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.FinanceLog
import com.example.data.model.Product
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.github.jan.supabase.auth.auth

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.client.auth.awaitInitialization()
                val user = repository.client.auth.currentUserOrNull()
                if (user != null) {
                    _authState.value = AuthState.Success
                }
            } catch (e: Exception) {
                // Not logged in or error
            }
        }
    }

    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lowStockProducts: StateFlow<List<Product>> = repository.lowStockProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allFinanceLogs: StateFlow<List<FinanceLog>> = repository.allFinanceLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun login(email: String, password: String) {
        if (email.lowercase() != "seseffff942@gmail.com") {
            _authState.value = AuthState.Error("Acceso denegado. Usuario no autorizado.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.login(email, password)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.lowercase() != "seseffff942@gmail.com") {
            _authState.value = AuthState.Error("Acceso denegado. Usuario no autorizado.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                repository.signUp(email, password)
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                repository.fetchProducts()
                repository.fetchFinanceLogs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addProduct(product: Product, imageBytes: ByteArray? = null, extension: String? = null) {
        repository.insertProduct(product, imageBytes, extension)
    }

    suspend fun updateProduct(product: Product) {
        repository.updateProduct(product)
    }

    suspend fun logFinance(log: FinanceLog) {
        repository.insertFinanceLog(log)
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
