package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerialName("user_id") val userId: String = "",
    val name: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val notes: String = "",
    @SerialName("image_uri") val imageUri: String = ""
)
