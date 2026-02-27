package com.example.lankasmartmart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val productImageUrl: String = "",
    val quantity: Int = 1,
    val category: String = "",
    val unit: String = "",
    val brand: String = "",
    val discount: Int = 0,
    val isOnSale: Boolean = false,
    val originalPrice: Double = 0.0,
    val addedAt: Long = System.currentTimeMillis()
)
