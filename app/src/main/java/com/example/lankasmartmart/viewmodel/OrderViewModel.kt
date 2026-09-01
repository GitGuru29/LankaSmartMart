package com.example.lankasmartmart.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lankasmartmart.data.local.DatabaseProvider
import com.example.lankasmartmart.data.local.entity.TransactionEntity
import com.example.lankasmartmart.data.local.entity.TransactionItemEntity
import com.example.lankasmartmart.model.Order
import com.example.lankasmartmart.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OrderRepository()
    private val transactionDao = DatabaseProvider.getDatabase(application).transactionDao()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder

    // Local transaction history from SQLite
    val localTransactions = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        loadOrders()
        cleanupOldTransactions()
    }
    
    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _orders.value = repository.getOrders()
            _isLoading.value = false
        }
    }
    
    fun createOrder(order: Order, onSuccess: (String) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val orderId = repository.createOrder(order)
            if (orderId != null) {
                // Cache the transaction locally in SQLite
                cacheTransactionLocally(order.copy(id = orderId))
                loadOrders()
                onSuccess(orderId)
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }
    
    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentOrder.value = repository.getOrder(orderId)
            _isLoading.value = false
        }
    }
    
    fun cancelOrder(orderId: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.cancelOrder(orderId)
            if (success) {
                // Update local cache too
                transactionDao.updateTransactionStatus(orderId, "cancelled")
                loadOrders()
                onSuccess()
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }

    /**
     * Cache an order as a local transaction in SQLite for offline access / history
     */
    private suspend fun cacheTransactionLocally(order: Order) {
        val transactionEntity = TransactionEntity(
            id = order.id,
            userId = order.userId,
            totalAmount = order.totalAmount,
            deliveryAddress = order.deliveryAddress?.let {
                "${it.addressLine1}, ${it.city}"
            } ?: "",
            paymentMethod = order.paymentMethod,
            status = order.status,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt
        )

        val transactionItems = order.items.map { item ->
            TransactionItemEntity(
                transactionId = order.id,
                productId = item.productId,
                productName = item.productName,
                quantity = item.quantity,
                price = item.price,
                imageUrl = item.imageUrl
            )
        }

        transactionDao.insertTransactionWithItems(transactionEntity, transactionItems)
    }

    /**
     * Get items for a specific transaction from local DB
     */
    suspend fun getLocalTransactionItems(transactionId: String): List<TransactionItemEntity> {
        return transactionDao.getTransactionItems(transactionId)
    }

    /**
     * Clean up transactions older than 30 days
     */
    private fun cleanupOldTransactions() {
        viewModelScope.launch {
            val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
            transactionDao.deleteOldTransactions(thirtyDaysAgo)
        }
    }
}
