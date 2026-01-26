package com.example.lankasmartmart.repository

import com.example.lankasmartmart.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AddressRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }
    
    suspend fun getAddresses(): List<Address> {
        return try {
            val userId = getUserId()
            val snapshot = db.collection("addresses")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { it.toObject(Address::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun addAddress(address: Address): Boolean {
        return try {
            val userId = getUserId()
            val addressWithUser = address.copy(
                id = db.collection("addresses").document().id,
                userId = userId
            )
            
            db.collection("addresses")
                .document(addressWithUser.id)
                .set(addressWithUser)
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun updateAddress(address: Address): Boolean {
        return try {
            db.collection("addresses")
                .document(address.id)
                .set(address)
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun deleteAddress(addressId: String): Boolean {
        return try {
            db.collection("addresses")
                .document(addressId)
                .delete()
                .await()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun setDefaultAddress(addressId: String): Boolean {
        return try {
            val userId = getUserId()
            
            // Remove default from all addresses
            val addresses = getAddresses()
            addresses.forEach { address ->
                if (address.isDefault) {
                    updateAddress(address.copy(isDefault = false))
                }
            }
            
            // Set new default
            addresses.find { it.id == addressId }?.let { address ->
                updateAddress(address.copy(isDefault = true))
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getDefaultAddress(): Address? {
        return try {
            getAddresses().find { it.isDefault }
        } catch (e: Exception) {
            null
        }
    }
}
