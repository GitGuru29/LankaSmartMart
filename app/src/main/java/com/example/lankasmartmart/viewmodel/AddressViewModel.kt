package com.example.lankasmartmart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lankasmartmart.model.Address
import com.example.lankasmartmart.repository.AddressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {
    private val repository = AddressRepository()
    
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress
    
    init {
        loadAddresses()
    }
    
    fun loadAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            _addresses.value = repository.getAddresses()
            
            // Auto-select default address
            _selectedAddress.value = _addresses.value.find { it.isDefault }
                ?: _addresses.value.firstOrNull()
            
            _isLoading.value = false
        }
    }
    
    fun addAddress(address: Address, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.addAddress(address)
            if (success) {
                loadAddresses()
                onSuccess()
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }
    
    fun updateAddress(address: Address, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.updateAddress(address)
            if (success) {
                loadAddresses()
                onSuccess()
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }
    
    fun deleteAddress(addressId: String, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteAddress(addressId)
            if (success) {
                loadAddresses()
                onSuccess()
            } else {
                onError()
            }
            _isLoading.value = false
        }
    }
    
    fun setDefaultAddress(addressId: String) {
        viewModelScope.launch {
            repository.setDefaultAddress(addressId)
            loadAddresses()
        }
    }
    
    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }
}
