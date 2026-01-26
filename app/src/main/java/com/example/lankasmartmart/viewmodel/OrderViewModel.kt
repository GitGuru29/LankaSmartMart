package com.example.lankasmartmart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lankasmartmart.model.Order
import com.example.lankasmartmart.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val repository = OrderRepository()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder
    
    init {
        loadOrders()
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
                loadOrders()
                onSuccess()
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }
}
