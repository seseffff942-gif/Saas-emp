package com.example.data.repository

import com.example.data.SupabaseApi
import com.example.data.model.FinanceLog
import com.example.data.model.Product
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppRepository {
    private val client = SupabaseApi.client
    
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: Flow<List<Product>> = _allProducts

    val lowStockProducts: Flow<List<Product>> = _allProducts.map { list -> 
        list.filter { it.stock < 10 } 
    }

    private val _allFinanceLogs = MutableStateFlow<List<FinanceLog>>(emptyList())
    val allFinanceLogs: Flow<List<FinanceLog>> = _allFinanceLogs

    init {
        // Initial fetch when repo is created
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fetchProducts()
                fetchFinanceLogs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun fetchProducts() {
        try {
            val userId = getCurrentUserId()
            if (userId.isEmpty()) return
            val products = client.postgrest["products"].select {
                filter { eq("user_id", userId) }
            }.decodeList<Product>()
            _allProducts.value = products
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchFinanceLogs() {
        try {
            val userId = getCurrentUserId()
            if (userId.isEmpty()) return
            val logs = client.postgrest["finance_logs"].select {
                filter { eq("user_id", userId) }
            }.decodeList<FinanceLog>()
            _allFinanceLogs.value = logs
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCurrentUserId(): String {
        return client.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun login(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        fetchProducts()
        fetchFinanceLogs()
    }

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        fetchProducts()
        fetchFinanceLogs()
    }

    suspend fun logout() {
        client.auth.signOut()
        _allProducts.value = emptyList()
        _allFinanceLogs.value = emptyList()
    }

    suspend fun insertProduct(product: Product) {
        try {
            val userId = getCurrentUserId()
            val newProduct = product.copy(userId = userId)
            client.postgrest["products"].insert(newProduct)
            fetchProducts() // Refresh
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateProduct(product: Product) {
        try {
            client.postgrest["products"].update(product) {
                filter { eq("id", product.id) }
            }
            fetchProducts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteProduct(id: Int) {
        try {
            client.postgrest["products"].delete {
                filter { eq("id", id) }
            }
            fetchProducts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insertFinanceLog(log: FinanceLog) {
        try {
            val userId = getCurrentUserId()
            val newLog = log.copy(userId = userId)
            client.postgrest["finance_logs"].insert(newLog)
            fetchFinanceLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
