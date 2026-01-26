package com.example.lankasmartmart.repository

import com.example.lankasmartmart.model.PaymentCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CardRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cardsCollection = db.collection("payment_cards")
    
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
    }
    
    /**
     * Add a new payment card
     */
    suspend fun addCard(card: PaymentCard): Result<String> {
        return try {
            val userId = getUserId()
            val cardWithUser = card.copy(userId = userId)
            
            // If this is the first card or set as default, unset other defaults
            if (card.isDefault) {
                setAllCardsNonDefault(userId)
            }
            
            val docRef = cardsCollection.add(cardWithUser).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all cards for current user
     */
    suspend fun getAllCards(): Result<List<PaymentCard>> {
        return try {
            val userId = getUserId()
            val snapshot = cardsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val cards = snapshot.documents.mapNotNull { doc ->
                doc.toObject(PaymentCard::class.java)?.copy(id = doc.id)
            }
            Result.success(cards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a card
     */
    suspend fun deleteCard(cardId: String): Result<Unit> {
        return try {
            cardsCollection.document(cardId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Set a card as default
     */
    suspend fun setDefaultCard(cardId: String): Result<Unit> {
        return try {
            val userId = getUserId()
            
            // Unset all other defaults
            setAllCardsNonDefault(userId)
            
            // Set this card as default
            cardsCollection.document(cardId)
                .update("isDefault", true)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Helper: Set all cards as non-default
     */
    private suspend fun setAllCardsNonDefault(userId: String) {
        val snapshot = cardsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isDefault", true)
            .get()
            .await()
        
        snapshot.documents.forEach { doc ->
            doc.reference.update("isDefault", false).await()
        }
    }
    
    /**
     * Detect card type from card number
     */
    fun detectCardType(cardNumber: String): String {
        val cleanNumber = cardNumber.replace(" ", "")
        return when {
            cleanNumber.startsWith("4") -> "Visa"
            cleanNumber.startsWith("5") -> "Mastercard"
            cleanNumber.startsWith("3") && (cleanNumber[1] == '4' || cleanNumber[1] == '7') -> "Amex"
            cleanNumber.startsWith("6") -> "Discover"
            else -> "Unknown"
        }
    }
    
    /**
     * Validate card number using Luhn algorithm
     */
    fun validateCardNumber(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace(" ", "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false
        if (!cleanNumber.all { it.isDigit() }) return false
        
        var sum = 0
        var isSecond = false
        
        for (i in cleanNumber.length - 1 downTo 0) {
            var digit = cleanNumber[i].toString().toInt()
            
            if (isSecond) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            
            sum += digit
            isSecond = !isSecond
        }
        
        return sum % 10 == 0
    }
}
