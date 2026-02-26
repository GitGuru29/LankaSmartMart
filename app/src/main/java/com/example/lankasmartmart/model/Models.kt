package com.example.lankasmartmart.model

data class Category(
    val id: String = "",
    val name: String = "",
    val icon: String = "", // Emoji or icon name
    val description: String = "",
    val imageUrl: String = ""
)

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0, // For showing discounts
    val category: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val unit: String = "", // kg, g, ml, L, pieces, etc.
    val brand: String = "",
    val isOnSale: Boolean = false,
    val discount: Int = 0, // Percentage
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val reviews: List<Review> = emptyList()
) {
    val discountedPrice: Double
        get() = if (isOnSale && discount > 0) {
            price * (100 - discount) / 100.0
        } else {
            price
        }
}

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Promotion(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val backgroundColor: String = "#53B175", // Hex color
    val actionType: String = "product", // product, category, search
    val actionId: String = "" // productId or categoryId
)

data class CartItem(
    val productId: String = "",
    val product: Product = Product(),
    var quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.discountedPrice * quantity
}

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}

enum class PaymentMethod {
    CASH_ON_DELIVERY,
    CARD_PAYMENT,
    ONLINE_BANKING
}
