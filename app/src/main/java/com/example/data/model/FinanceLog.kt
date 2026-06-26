package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "finance_logs")
data class FinanceLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("user_id") val userId: String = "",
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val title: String, // "Sale", "Electricity Bill", etc.
    val category: String, // "Supplier", "Utilities", "Supplies", "Sales"
    val timestamp: Long = System.currentTimeMillis()
)
