package com.example.lankasmartmart.repository

import com.example.lankasmartmart.model.Order
import com.example.lankasmartmart.model.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }
    
    suspend fun createOrder(order: Order): String? {
        return try {
            val userId = getUserId()
            val orderId = db.collection("orders").document().id
            
            val orderWithDetails = order.copy(
                id = orderId,
                userId = userId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            db.collection("orders")
                .document(orderId)
                .set(orderWithDetails)
                .await()
            
            orderId
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getOrders(): List<Order> {
        return try {
            val userId = getUserId()
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getOrder(orderId: String): Order? {
        return try {
            val snapshot = db.collection("orders")
                .document(orderId)
                .get()
                .await()
            
            snapshot.toObject(Order::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            db.collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "status" to status,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun cancelOrder(orderId: String): Boolean {
        return updateOrderStatus(orderId, "cancelled")
    }
}
