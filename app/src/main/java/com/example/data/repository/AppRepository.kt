package com.example.data.repository

import com.example.data.SupabaseApi
import com.example.data.model.FinanceLog
import com.example.data.model.Product
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import android.content.Context
import android.content.SharedPreferences

class AppRepository(private val context: Context) {
    val client = SupabaseApi.client
    private val prefs: SharedPreferences = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
    
    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    val allProducts: Flow<List<Product>> = _allProducts

    val lowStockProducts: Flow<List<Product>> = _allProducts.map { list -> 
        list.filter { it.stock < 10 } 
    }

    private val _allFinanceLogs = MutableStateFlow<List<FinanceLog>>(emptyList())
    val allFinanceLogs: Flow<List<FinanceLog>> = _allFinanceLogs

    init {
        // Init happens explicitly from ViewModel now to avoid races
    }

    suspend fun initializeSession(): Boolean {
        return try {
            client.auth.awaitInitialization()
            restoreSession()
            val isLoggedIn = client.auth.currentUserOrNull() != null
            if (isLoggedIn) {
                fetchProducts()
                fetchFinanceLogs()
            }
            isLoggedIn
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun restoreSession() {
        val token = prefs.getString("access_token", null)
        val refreshToken = prefs.getString("refresh_token", null)
        if (token != null && refreshToken != null) {
            try {
                client.auth.importAuthToken(token, refreshToken, autoRefresh = false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveSession() {
        val session = client.auth.currentSessionOrNull()
        if (session != null) {
            prefs.edit()
                .putString("access_token", session.accessToken)
                .putString("refresh_token", session.refreshToken)
                .apply()
        } else {
            prefs.edit().clear().apply()
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

    private suspend fun getCurrentUserId(): String {
        client.auth.awaitInitialization()
        return client.auth.currentUserOrNull()?.id ?: ""
    }

    suspend fun login(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        saveSession()
        fetchProducts()
        fetchFinanceLogs()
    }

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        saveSession()
        fetchProducts()
        fetchFinanceLogs()
    }

    suspend fun logout() {
        client.auth.signOut()
        saveSession()
        _allProducts.value = emptyList()
        _allFinanceLogs.value = emptyList()
    }

    @kotlinx.serialization.Serializable
    private data class ProductInsert(
        @kotlinx.serialization.SerialName("user_id") val userId: String,
        val name: String,
        val category: String,
        val price: Double,
        val stock: Int,
        val notes: String,
        @kotlinx.serialization.SerialName("image_uri") val imageUri: String
    )

    suspend fun insertProduct(product: Product, imageBytes: ByteArray? = null, extension: String? = null) {
        val userId = getCurrentUserId()
        var finalImageUri = product.imageUri
        
        if (imageBytes != null) {
            try {
                val bucket = client.storage["products"]
                val filename = "${userId}_${System.currentTimeMillis()}${extension ?: ".jpg"}"
                bucket.upload(filename, imageBytes) {
                    upsert = true
                }
                finalImageUri = bucket.publicUrl(filename)
            } catch (e: Exception) {
                e.printStackTrace()
                // Keep the local imageUri as a fallback
            }
        }
        
        val newProduct = ProductInsert(
            userId = userId,
            name = product.name,
            category = product.category,
            price = product.price,
            stock = product.stock,
            notes = product.notes,
            imageUri = finalImageUri
        )
        client.postgrest["products"].insert(newProduct)
        fetchProducts() // Refresh
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

    @kotlinx.serialization.Serializable
    private data class FinanceLogInsert(
        @kotlinx.serialization.SerialName("user_id") val userId: String,
        val type: String,
        val amount: Double,
        val title: String,
        val category: String,
        val timestamp: Long
    )

    suspend fun insertFinanceLog(log: FinanceLog) {
        try {
            val userId = getCurrentUserId()
            val newLog = FinanceLogInsert(
                userId = userId,
                type = log.type,
                amount = log.amount,
                title = log.title,
                category = log.category,
                timestamp = log.timestamp
            )
            client.postgrest["finance_logs"].insert(newLog)
            fetchFinanceLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
