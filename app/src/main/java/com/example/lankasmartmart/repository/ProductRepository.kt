package com.example.lankasmartmart.repository

import com.example.lankasmartmart.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")
    
    /**
     * Fetch all products from Firestore
     */
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a single product by ID
     */
    suspend fun getProductById(productId: String): Result<Product?> {
        return try {
            val doc = productsCollection.document(productId).get().await()
            val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get products filtered by category
     */
    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("category", categoryId)
                .get()
                .await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Search products by name (basic search)
     */
    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            // Firestore doesn't support full-text search natively
            // We'll fetch all and filter locally (for small datasets)
            val allProducts = getAllProducts().getOrThrow()
            val filtered = allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add a new product (Admin function)
     */
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = productsCollection.add(product).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update an existing product (Admin function)
     */
    suspend fun updateProduct(productId: String, product: Product): Result<Unit> {
        return try {
            productsCollection.document(productId).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete a product (Admin function)
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
