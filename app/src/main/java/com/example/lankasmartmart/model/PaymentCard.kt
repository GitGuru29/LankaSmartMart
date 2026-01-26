package com.example.lankasmartmart.model

data class PaymentCard(
    val id: String = "",
    val userId: String = "",
    val last4Digits: String = "", // Only store last 4 digits for security
    val cardHolder: String = "",
    val cardType: String = "", // Visa, Mastercard, Amex, etc.
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Temporary model for scanned card data (before saving)
 * NEVER store CVV in Firestore!
 */
data class ScannedCardData(
    val cardNumber: String = "",
    val cardHolder: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val cvv: String = "" // Only for immediate validation, never persisted
)
