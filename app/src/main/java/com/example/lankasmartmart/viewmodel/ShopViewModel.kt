package com.example.lankasmartmart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lankasmartmart.model.Category
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShopViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // Cart State
    private val _cartItems = MutableStateFlow<List<com.example.lankasmartmart.model.CartItem>>(emptyList())
    val cartItems: StateFlow<List<com.example.lankasmartmart.model.CartItem>> = _cartItems
    
    val cartItemCount: StateFlow<Int> = MutableStateFlow(0)
    val cartSubtotal: StateFlow<Double> = MutableStateFlow(0.0)
    val deliveryFee: StateFlow<Double> = MutableStateFlow(150.0)
    val cartTotal: StateFlow<Double> = MutableStateFlow(0.0)
    
    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults
    
    init {
        loadCategories()
        loadProducts()
        loadMockCartData() // Add some test items
        updateCartCalculations()
    }
    
    // Load categories from Firestore (or use mock data)
    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // For now, using mock data. Later we'll fetch from Firestore
                _categories.value = getMockCategories()
            } catch (e: Exception) {
                // Handle error
                _categories.value = getMockCategories() // Fallback to mock
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load products from Firestore
    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = productRepository.getAllProducts()
                _products.value = result.getOrElse { emptyList() }
                _searchResults.value = _products.value
            } catch (e: Exception) {
                // Handle error - keep empty list
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Get products by category
    fun getProductsByCategory(categoryId: String): List<Product> {
        return _products.value.filter { it.category == categoryId }
    }
    
    // Mock data for categories (Sri Lankan context)
    private fun getMockCategories(): List<Category> {
        return listOf(
            Category(
                id = "groceries",
                name = "Groceries",
                icon = "🛒",
                description = "Rice, Dal, Flour & more"
            ),
            Category(
                id = "vegetables",
                name = "Vegetables",
                icon = "🥬",
                description = "Fresh vegetables"
            ),
            Category(
                id = "fruits",
                name = "Fruits",
                icon = "🍎",
                description = "Fresh fruits"
            ),
            Category(
                id = "dairy",
                name = "Dairy",
                icon = "🥛",
                description = "Milk, Curd, Cheese"
            ),
            Category(
                id = "beverages",
                name = "Beverages",
                icon = "☕",
                description = "Tea, Coffee, Juice"
            ),
            Category(
                id = "snacks",
                name = "Snacks",
                icon = "🍿",
                description = "Chips, Biscuits & more"
            ),
            Category(
                id = "personal_care",
                name = "Personal Care",
                icon = "🧴",
                description = "Soap, Shampoo, Toiletries"
            ),
            Category(
                id = "household",
                name = "Household",
                icon = "🧹",
                description = "Cleaning supplies"
            ),
            Category(
                id = "stationery",
                name = "Stationery",
                icon = "✏️",
                description = "Books, Pens, Paper"
            )
        )
    }
    
    // Mock data for products (Sri Lankan products)
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
                reviewCount = 120
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
                reviewCount = 85
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
                reviewCount = 60
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
                reviewCount = 45
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
                reviewCount = 90
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
                reviewCount = 150
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
                reviewCount = 78
            ),
            
            // Dairy
            Product(
                id = "8",
                name = "Fresh Milk",
                description = "Full cream fresh milk",
                price = 280.0,
                category = "dairy",
                stock = 40,
                unit = "1 L",
                brand = "Anchor",
                rating = 4.8f,
                reviewCount = 200
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
                reviewCount = 110
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
                reviewCount = 250
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
                reviewCount = 95
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
                reviewCount = 180
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
                reviewCount = 72
            )
        )
    }
    
    // Cart Management Functions
    fun addToCart(product: Product, quantity: Int = 1) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }
        
        if (existingItem != null) {
            // Update quantity if already in cart
            val index = currentCart.indexOf(existingItem)
            currentCart[index] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            // Add new item
            currentCart.add(com.example.lankasmartmart.model.CartItem(product = product, quantity = quantity))
        }
        
        _cartItems.value = currentCart
        updateCartCalculations()
    }
    
    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        updateCartCalculations()
    }
    
    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity < 1) {
            removeFromCart(productId)
            return
        }
        
        val currentCart = _cartItems.value.toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.product.id == productId }
        
        if (itemIndex != -1) {
            currentCart[itemIndex] = currentCart[itemIndex].copy(quantity = newQuantity)
            _cartItems.value = currentCart
            updateCartCalculations()
        }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
        updateCartCalculations()
    }
    
    private fun updateCartCalculations() {
        val subtotal = _cartItems.value.sumOf { it.product.price * it.quantity }
        val delivery = if (subtotal >= 2000.0) 0.0 else 150.0
        val total = subtotal + delivery
        
        (cartItemCount as MutableStateFlow).value = _cartItems.value.sumOf { it.quantity }
        (cartSubtotal as MutableStateFlow).value = subtotal
        (deliveryFee as MutableStateFlow).value = delivery
        (cartTotal as MutableStateFlow).value = total
    }
    
    // Load mock cart data for testing
    private fun loadMockCartData() {
        val mockProducts = getMockProducts()
        _cartItems.value = listOf(
            com.example.lankasmartmart.model.CartItem(
                product = mockProducts.first { it.id == "1" }, // Rice
                quantity = 2
            ),
            com.example.lankasmartmart.model.CartItem(
                product = mockProducts.first { it.id == "8" }, // Milk
                quantity = 1
            ),
            com.example.lankasmartmart.model.CartItem(
                product = mockProducts.first { it.id == "12" }, // Crackers
                quantity = 3
            )
        )
    }
    
    // Search Functions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch(query)
    }
    
    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = _products.value
        } else {
            _searchResults.value = _products.value.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                product.brand.contains(query, ignoreCase = true) ||
                product.description.contains(query, ignoreCase = true)
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = _products.value
    }
}
