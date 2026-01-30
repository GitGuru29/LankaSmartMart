package com.example.lankasmartmart.data

import android.util.Log
import com.example.lankasmartmart.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * One-time seeder to upload mock products to Firestore
 * Call seedProducts() once from Splash/Home screen then remove the call
 */
class ProductSeeder {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")
    
    suspend fun seedProducts(): Result<Unit> {
        return try {
            val products = getMockProducts()
            
            // Upload each product
            products.forEach { product ->
                productsCollection.document(product.id).set(product).await()
                Log.d("ProductSeeder", "Uploaded: ${product.name}")
            }
            
            Log.d("ProductSeeder", "Successfully seeded ${products.size} products!")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ProductSeeder", "Error seeding products", e)
            Result.failure(e)
        }
    }
    
    private fun getMockProducts(): List<Product> {
        return listOf(
            // Groceries
            Product(
                id = "1",
                name = "Basmati Rice",
                description = "Premium quality Basmati rice",
                price = 450.0,
                originalPrice = 500.0,
                category = "groceries",
                stock = 50,
                unit = "1 kg",
                brand = "Araliya",
                isOnSale = true,
                discount = 10,
                rating = 4.5f,
                reviewCount = 120,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_basmati_rice"
            ),
            Product(
                id = "2",
                name = "Red Lentils (Dhal)",
                description = "Fresh red lentils",
                price = 180.0,
                category = "groceries",
                stock = 100,
                unit = "500g",
                brand = "Local",
                rating = 4.3f,
                reviewCount = 85,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_red_dhal"
            ),
            Product(
                id = "3",
                name = "Wheat Flour",
                description = "Fine wheat flour for roti",
                price = 120.0,
                category = "groceries",
                stock = 75,
                unit = "1 kg",
                brand = "Prima",
                rating = 4.2f,
                reviewCount = 60,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_wheat_flour"
            ),
            
            // Vegetables
            Product(
                id = "4",
                name = "Carrots",
                description = "Fresh organic carrots",
                price = 150.0,
                category = "vegetables",
                stock = 30,
                unit = "500g",
                brand = "Farm Fresh",
                rating = 4.6f,
                reviewCount = 45,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_carrots"
            ),
            Product(
                id = "5",
                name = "Tomatoes",
                description = "Red ripe tomatoes",
                price = 200.0,
                originalPrice = 250.0,
                category = "vegetables",
                stock = 40,
                unit = "1 kg",
                brand = "Farm Fresh",
                isOnSale = true,
                discount = 20,
                rating = 4.4f,
                reviewCount = 90,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_tomatoes"
            ),
            
            // Fruits
            Product(
                id = "6",
                name = "Bananas",
                description = "Ambul banana - Locally grown",
                price = 180.0,
                category = "fruits",
                stock = 60,
                unit = "1 kg",
                brand = "Local",
                rating = 4.7f,
                reviewCount = 150,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_bananas"
            ),
            Product(
                id = "7",
                name = "Papaya",
                description = "Sweet red papaya",
                price = 120.0,
                category = "fruits",
                stock = 25,
                unit = "per piece",
                brand = "Local",
                rating = 4.5f,
                reviewCount = 78,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_papaya"
            ),
            
            // Dairy
            Product(
                id = "8",
                name = "Anchor Full Cream Milk",
                description = "Full cream fresh milk",
                price = 280.0,
                category = "dairy",
                stock = 40,
                unit = "1 L",
                brand = "Anchor",
                rating = 4.8f,
                reviewCount = 200,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_anchor_milk"
            ),
            Product(
                id = "9",
                name = "Curd",
                description = "Traditional buffalo curd",
                price = 150.0,
                category = "dairy",
                stock = 35,
                unit = "400g",
                brand = "Pelwatte",
                rating = 4.6f,
                reviewCount = 110,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_curd"
            ),
            
            // Beverages
            Product(
                id = "10",
                name = "Ceylon Tea",
                description = "Premium Ceylon black tea",
                price = 350.0,
                category = "beverages",
                stock = 80,
                unit = "200g",
                brand = "Dilmah",
                rating = 4.9f,
                reviewCount = 250,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_ceylon_tea"
            ),
            Product(
                id = "11",
                name = "Mango Juice",
                description = "Pure mango nectar",
                price = 220.0,
                originalPrice = 250.0,
                category = "beverages",
                stock = 50,
                unit = "1 L",
                brand = "Kist",
                isOnSale = true,
                discount = 12,
                rating = 4.4f,
                reviewCount = 95,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_mango_juice"
            ),
            
            // Snacks
            Product(
                id = "12",
                name = "Cream Crackers",
                description = "Crispy cream crackers",
                price = 85.0,
                category = "snacks",
                stock = 120,
                unit = "190g",
                brand = "Munchee",
                rating = 4.3f,
                reviewCount = 180,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_cream_cracker"
            ),

            Product(
                id = "13",
                name = "Coconut Chips",
                description = "Crunchy coconut chips",
                price = 120.0,
                category = "snacks",
                stock = 65,
                unit = "100g",
                brand = "Ritzbury",
                rating = 4.5f,
                reviewCount = 72,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_coconut_chips"
            ),
            
            // Personal Care
            Product(
                id = "14",
                name = "Rani Sandalwood Soap",
                description = "Original sandalwood soap",
                price = 120.0,
                category = "personal_care",
                stock = 150,
                unit = "100g",
                brand = "Rani",
                rating = 4.6f,
                reviewCount = 88,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_rani_soap"
            ),
            Product(
                id = "15",
                name = "Misumi Beauty Soap",
                description = "Momo beauty soap",
                price = 450.0,
                category = "personal_care",
                stock = 45,
                unit = "100g",
                brand = "Misumi",
                rating = 4.4f,
                reviewCount = 56,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_misumi_soap"
            ),
            
            // Household
            Product(
                id = "16",
                name = "Dish Wash Liquid",
                description = "Lime fresh dish wash",
                price = 280.0,
                category = "household",
                stock = 80,
                unit = "500ml",
                brand = "Sunlight",
                rating = 4.7f,
                reviewCount = 134,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_sunlight_dishwash"
            ),
            Product(
                id = "17",
                name = "Washing Powder",
                description = "Floral fresh detergent powder",
                price = 420.0,
                category = "household",
                stock = 60,
                unit = "1kg",
                brand = "Rin",
                rating = 4.5f,
                reviewCount = 92,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_rin_powder"
            ),
            
            // Stationery
            Product(
                id = "18",
                name = "Exercise Book",
                description = "80 pages single rule book",
                price = 120.0,
                category = "stationery",
                stock = 200,
                unit = "80 pgs",
                brand = "Atlas",
                rating = 4.8f,
                reviewCount = 45,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_atlas_book"
            ),
            Product(
                id = "19",
                name = "Blue Pens Pack",
                description = "Pack of 5 ballpoint pens",
                price = 150.0,
                category = "stationery",
                stock = 100,
                unit = "5 pack",
                brand = "Atlas",
                rating = 4.6f,
                reviewCount = 30,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_blue_pens"
            )
        )
    }
}
