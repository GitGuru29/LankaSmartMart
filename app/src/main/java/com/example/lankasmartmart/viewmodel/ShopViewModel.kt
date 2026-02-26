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
    
    // Promotion State
    private val _promotions = MutableStateFlow<List<com.example.lankasmartmart.model.Promotion>>(emptyList())
    val promotions: StateFlow<List<com.example.lankasmartmart.model.Promotion>> = _promotions
    
    init {
        loadCategories()
        loadProducts()
        loadPromotions()
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
                val fetchedProducts = result.getOrElse { emptyList() }
                
                if (fetchedProducts.isNotEmpty()) {
                    _products.value = fetchedProducts
                } else {
                    // Fallback to mock data if Firestore returns empty
                    _products.value = getMockProducts()
                }
                _searchResults.value = _products.value
            } catch (e: Exception) {
                // Handle error - fallback to mock
                _products.value = getMockProducts()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Load promotions
    private fun loadPromotions() {
        _promotions.value = getMockPromotions()
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
    
    // Mock data for promotions
    private fun getMockPromotions(): List<com.example.lankasmartmart.model.Promotion> {
        return listOf(
            com.example.lankasmartmart.model.Promotion(
                id = "promo1",
                title = "Avurudu Special",
                subtitle = "Up to 40% OFF on Groceries",
                backgroundColor = "#53B175",
                actionType = "category",
                actionId = "groceries"
            ),
            com.example.lankasmartmart.model.Promotion(
                id = "promo2",
                title = "Fresh Beverages",
                subtitle = "20% OFF on all Juices",
                backgroundColor = "#F8A44C",
                actionType = "category",
                actionId = "beverages"
            ),
            com.example.lankasmartmart.model.Promotion(
                id = "promo3",
                title = "Snack Time!",
                subtitle = "Buy 2 Get 1 FREE on Biscuits",
                backgroundColor = "#D3B0E0",
                actionType = "category",
                actionId = "snacks"
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
                rating = 4.8f,
                reviewCount = 125,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_basmati_rice"
            ),
            Product(
                id = "2",
                name = "Wheat Flour",
                description = "Fine multipurpose wheat flour for baking and cooking.",
                price = 220.0,
                category = "groceries",
                stock = 150,
                unit = "1 kg",
                brand = "Prima",
                rating = 4.5f,
                reviewCount = 85,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_wheat_flour"
            ),
            Product(
                id = "3",
                name = "Red Dhal",
                description = "Split red lentils, a staple for delicious dhal curry.",
                price = 380.0,
                category = "groceries",
                stock = 80,
                unit = "1 kg",
                brand = "Fortune",
                rating = 4.6f,
                reviewCount = 92,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_red_dhal"
            ),
            
            // Vegetables
            Product(
                id = "4",
                name = "Organic Carrots",
                description = "Freshly harvested organic carrots, rich in Beta-Carotene.",
                price = 150.0,
                category = "vegetables",
                stock = 45,
                unit = "500g",
                brand = "EcoFarm",
                rating = 4.7f,
                reviewCount = 64,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_carrots"
            ),
            Product(
                id = "5",
                name = "Fresh Tomatoes",
                description = "Juicy red tomatoes, ideal for salads and curries.",
                price = 180.0,
                category = "vegetables",
                stock = 30,
                unit = "500g",
                brand = "Local",
                rating = 4.4f,
                reviewCount = 56,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_tomatoes"
            ),
            
            // Fruits
            Product(
                id = "6",
                name = "Organic Bananas",
                description = "Sweet and creamy Cavendish bananas, ripened naturally.",
                price = 120.0,
                category = "fruits",
                stock = 60,
                unit = "1 kg",
                brand = "Local",
                rating = 4.9f,
                reviewCount = 110,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_bananas"
            ),
            Product(
                id = "7",
                name = "Red Papaya",
                description = "Sweet and ripe red papaya, perfect for a healthy breakfast.",
                price = 250.0,
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
                description = "Premium full cream powdered milk, fortified with vitamins.",
                price = 1050.0,
                category = "dairy",
                stock = 40,
                unit = "400g",
                brand = "Anchor",
                rating = 4.8f,
                reviewCount = 200,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_anchor_milk"
            ),
            Product(
                id = "9",
                name = "Buffalo Curd",
                description = "Traditional rich buffalo curd in a clay pot.",
                price = 350.0,
                category = "dairy",
                stock = 35,
                unit = "1 kg",
                brand = "Pelwatte",
                rating = 4.6f,
                reviewCount = 110,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_curd"
            ),
            
            // Beverages
            Product(
                id = "10",
                name = "Ceylon Black Tea",
                description = "Pure Ceylon black tea, strong and revitalizing.",
                price = 450.0,
                category = "beverages",
                stock = 80,
                unit = "250g",
                brand = "Dilmah",
                rating = 4.9f,
                reviewCount = 250,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_ceylon_tea"
            ),
            Product(
                id = "11",
                name = "Mango Nectar",
                description = "Rich and thick mango juice made from local sun-ripened mangoes.",
                price = 280.0,
                originalPrice = 320.0,
                category = "beverages",
                stock = 50,
                unit = "1 L",
                brand = "Kist",
                isOnSale = true,
                discount = 12,
                rating = 4.6f,
                reviewCount = 95,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_mango_juice"
            ),
            
            // Snacks
            Product(
                id = "12",
                name = "Cream Crackers",
                description = "Classic crispy cream crackers from Munchee.",
                price = 230.0,
                category = "snacks",
                stock = 120,
                unit = "190g",
                brand = "Munchee",
                rating = 4.5f,
                reviewCount = 180,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_cream_cracker"
            ),
            Product(
                id = "13",
                name = "Coconut Chips",
                description = "Crunchy toasted coconut chips, slightly sweetened.",
                price = 150.0,
                category = "snacks",
                stock = 65,
                unit = "100g",
                brand = "Local",
                rating = 4.4f,
                reviewCount = 72,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_coconut_chips"
            ),
            
            // Personal Care
            Product(
                id = "14",
                name = "Rani Sandalwood Soap",
                description = "Original sandalwood soap with natural extracts for glowing skin.",
                price = 145.0,
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
                description = "Gentle beauty soap for moisture-rich and soft skin.",
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
                description = "Powerful lime fresh liquid that cuts through grease easily.",
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
                description = "Floral fresh detergent powder for brilliant white clothes.",
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
                description = "Standard 80-page single rule exercise book for school use.",
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
                description = "Pack of 5 smooth-writing ballpoint pens, long-lasting ink.",
                price = 150.0,
                category = "stationery",
                stock = 100,
                unit = "5 pack",
                brand = "Atlas",
                rating = 4.6f,
                reviewCount = 30,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_blue_pens"
            ),
            
            // --- Additional Items ---
            
            // Groceries (Continued)
            Product(
                id = "20",
                name = "White Sugar",
                description = "Pure refined white sugar for daily use, high quality.",
                price = 260.0,
                category = "groceries",
                stock = 150,
                unit = "1 kg",
                brand = "Pelican",
                rating = 4.4f,
                reviewCount = 110,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_white_sugar"
            ),
            Product(
                id = "21",
                name = "Table Salt",
                description = "Iodized table salt, essential for health and flavor.",
                price = 110.0,
                category = "groceries",
                stock = 200,
                unit = "400g",
                brand = "Raigam",
                rating = 4.5f,
                reviewCount = 95,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_table_salt"
            ),
            Product(
                id = "22",
                name = "Coconut Oil",
                description = "100% pure coconut oil, ideal for cooking and frying.",
                price = 560.0,
                category = "groceries",
                stock = 45,
                unit = "1 L",
                brand = "Marina",
                rating = 4.7f,
                reviewCount = 130,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_coconut_oil"
            ),
            
            // Vegetables (Continued)
            Product(
                id = "23",
                name = "Potatoes",
                description = "Quality local potatoes, versatile for any dish.",
                price = 320.0,
                category = "vegetables",
                stock = 80,
                unit = "1 kg",
                brand = "Local",
                rating = 4.5f,
                reviewCount = 65,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_potatoes"
            ),
            Product(
                id = "24",
                name = "Red Onions",
                description = "Freshly harvested red onions - Grade A flavor and aroma.",
                price = 380.0,
                category = "vegetables",
                stock = 60,
                unit = "1 kg",
                brand = "Local",
                rating = 4.3f,
                reviewCount = 80,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_onions"
            ),
            Product(
                id = "25",
                name = "Green Chillies",
                description = "Spicy and fresh local green chillies, rich in flavor.",
                price = 120.0,
                category = "vegetables",
                stock = 40,
                unit = "100g",
                brand = "Local",
                rating = 4.6f,
                reviewCount = 42,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_green_chillies"
            ),
            
            // Fruits (Continued)
            Product(
                id = "26",
                name = "Sweet Mango",
                description = "Deliciously sweet Karthacolomban (KC) mangoes.",
                price = 450.0,
                category = "fruits",
                stock = 30,
                unit = "1 kg",
                brand = "Local",
                rating = 4.8f,
                reviewCount = 55,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_mango"
            ),
            Product(
                id = "27",
                name = "Mauritius Pineapple",
                description = "Spiny-leaf sweet pineapple, rich in natural juice.",
                price = 280.0,
                category = "fruits",
                stock = 20,
                unit = "per piece",
                brand = "Local",
                rating = 4.5f,
                reviewCount = 38,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_pineapple"
            ),
            Product(
                id = "28",
                name = "Butter Avocado",
                description = "Ripe and buttery avocados, perfect for smoothies or salads.",
                price = 350.0,
                category = "fruits",
                stock = 15,
                unit = "500g",
                brand = "Local",
                rating = 4.7f,
                reviewCount = 29,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_avocado"
            ),
            
            // Dairy (Continued)
            Product(
                id = "29",
                name = "Salted Butter",
                description = "Pure creamery salted butter from Pelwatte.",
                price = 780.0,
                category = "dairy",
                stock = 25,
                unit = "200g",
                brand = "Pelwatte",
                rating = 4.6f,
                reviewCount = 112,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_butter"
            ),
            Product(
                id = "30",
                name = "Set Yogurt",
                description = "Smooth and fresh set yogurt cup, high in probiotics.",
                price = 65.0,
                category = "dairy",
                stock = 120,
                unit = "1 cup",
                brand = "Highland",
                rating = 4.8f,
                reviewCount = 450,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_yogurt"
            ),
            Product(
                id = "31",
                name = "Vanilla Ice Cream",
                description = "Creamy vanilla ice cream, family-sized tub.",
                price = 850.0,
                category = "dairy",
                stock = 15,
                unit = "1 L",
                brand = "Elephant House",
                rating = 4.7f,
                reviewCount = 89,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_ice_cream"
            ),
            
            // Beverages (Continued)
            Product(
                id = "32",
                name = "Instant Coffee",
                description = "Classic instant coffee for a perfect morning boost.",
                price = 650.0,
                category = "beverages",
                stock = 40,
                unit = "50g",
                brand = "Nescafe",
                rating = 4.6f,
                reviewCount = 125,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_coffee"
            ),
            Product(
                id = "33",
                name = "Milo Powder",
                description = "Chocolate malt energy food drink for active kids.",
                price = 980.0,
                category = "beverages",
                stock = 35,
                unit = "400g",
                brand = "Nestle",
                rating = 4.9f,
                reviewCount = 310,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_milo"
            ),
            Product(
                id = "34",
                name = "Mineral Water",
                description = "Pure and fresh natural mineral water from the Knuckles range.",
                price = 120.0,
                category = "beverages",
                stock = 100,
                unit = "1.5 L",
                brand = "Knuckles",
                rating = 4.8f,
                reviewCount = 180,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_bottled_water"
            ),
            
            // Snacks (Continued)
            Product(
                id = "35",
                name = "Lemon Puff",
                description = "Crispy biscuits with a tangy lemon cream filling.",
                price = 150.0,
                category = "snacks",
                stock = 85,
                unit = "200g",
                brand = "Munchee",
                rating = 4.7f,
                reviewCount = 220,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_lemon_puff"
            ),
            Product(
                id = "36",
                name = "Cassava Chips",
                description = "Thin and spicy fried cassava chips, perfectly crunchy.",
                price = 120.0,
                category = "snacks",
                stock = 50,
                unit = "100g",
                brand = "Local",
                rating = 4.4f,
                reviewCount = 68,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_cassava_chips"
            ),
            Product(
                id = "37",
                name = "Milk Chocolate",
                description = "Creamy milk chocolate bar, rich and smooth.",
                price = 280.0,
                category = "snacks",
                stock = 40,
                unit = "100g",
                brand = "Kandos",
                rating = 4.6f,
                reviewCount = 145,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_chocolate"
            ),
            
            // Personal Care (Continued)
            Product(
                id = "38",
                name = "Signal Toothpaste",
                description = "Total cavity protection with fluoride and calcium.",
                price = 220.0,
                category = "personal_care",
                stock = 75,
                unit = "120g",
                brand = "Signal",
                rating = 4.7f,
                reviewCount = 130,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_toothpaste"
            ),
            Product(
                id = "39",
                name = "Sunsilk Shampoo",
                description = "Smooth and manageable shampoo for silky soft hair.",
                price = 480.0,
                category = "personal_care",
                stock = 50,
                unit = "180ml",
                brand = "Sunsilk",
                rating = 4.5f,
                reviewCount = 75,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_shampoo"
            ),
            Product(
                id = "40",
                name = "Hand Wash",
                description = "Gentle antibacterial hand wash for complete hygiene.",
                price = 350.0,
                category = "personal_care",
                stock = 60,
                unit = "200ml",
                brand = "Lifebuoy",
                rating = 4.8f,
                reviewCount = 92,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_handwash"
            ),
            
            // Household (Continued)
            Product(
                id = "41",
                name = "Floor Cleaner",
                description = "Citrus fresh liquid for deep cleaning any floor surface.",
                price = 680.0,
                category = "household",
                stock = 30,
                unit = "500ml",
                brand = "Lysol",
                rating = 4.7f,
                reviewCount = 55,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_floor_cleaner"
            ),
            Product(
                id = "42",
                name = "Mosquito Coils",
                description = "Effective protection against mosquitoes, pack of 10 coils.",
                price = 150.0,
                category = "household",
                stock = 120,
                unit = "10 pack",
                brand = "Ninja",
                rating = 4.4f,
                reviewCount = 140,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_mosquito_coil"
            ),
            Product(
                id = "43",
                name = "Toilet Cleaner",
                description = "Deep acting toilet cleaner that kills 99.9% of germs.",
                price = 450.0,
                category = "household",
                stock = 40,
                unit = "500ml",
                brand = "Harpic",
                rating = 4.8f,
                reviewCount = 165,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_toilet_cleaner"
            ),
            
            // Stationery (Continued)
            Product(
                id = "44",
                name = "Black Pens Pack",
                description = "Pack of 5 black ballpoint pens, perfect for office and school work.",
                price = 150.0,
                category = "stationery",
                stock = 90,
                unit = "5 pack",
                brand = "Atlas",
                rating = 4.6f,
                reviewCount = 50,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_black_pens"
            ),
            Product(
                id = "45",
                name = "CR Notebook",
                description = "High-quality 120-page single rule CR notebook, durable binding.",
                price = 220.0,
                category = "stationery",
                stock = 65,
                unit = "120 pgs",
                brand = "Atlas",
                rating = 4.7f,
                reviewCount = 42,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_notebook"
            ),
            Product(
                id = "46",
                name = "Eraser & Sharpener Set",
                description = "Essential school set with a dust-free eraser and a sharp sharpener.",
                price = 85.0,
                category = "stationery",
                stock = 150,
                unit = "set",
                brand = "Maped",
                rating = 4.5f,
                reviewCount = 25,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_eraser_sharpener"
            ),
            
            // --- Newly Added Items ---
            Product(
                id = "47",
                name = "Creamy Mayonnaise",
                description = "Rich and creamy mayonnaise, the perfect dip for snacks and sandwiches.",
                price = 320.0,
                category = "groceries",
                stock = 45,
                unit = "250g",
                brand = "Heinz",
                rating = 4.6f,
                reviewCount = 54,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_mayonnaise"
            ),
            Product(
                id = "48",
                name = "Fresh Bellpepper",
                description = "Crisp and colorful bell peppers, ideal for stir-fries and salads.",
                price = 180.0,
                category = "vegetables",
                stock = 60,
                unit = "250g",
                brand = "Local",
                rating = 4.5f,
                reviewCount = 32,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_bellpaper"
            ),
            Product(
                id = "49",
                name = "Fresh Farm Eggs",
                description = "Large farm-fresh white eggs, rich in protein and nutrients.",
                price = 45.0,
                category = "dairy",
                stock = 200,
                unit = "per piece",
                brand = "Local",
                rating = 4.8f,
                reviewCount = 120,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_egg"
            ),
            Product(
                id = "50",
                name = "Yellow Egg Noodles",
                description = "High-quality egg noodles, quick to cook and delicious to eat.",
                price = 240.0,
                category = "groceries",
                stock = 55,
                unit = "400g",
                brand = "Maggi",
                rating = 4.6f,
                reviewCount = 67,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_eggnoodles"
            ),
            Product(
                id = "51",
                name = "Local Fresh Ginger",
                description = "Aromatic and spicy local ginger, perfect for tea and cooking.",
                price = 150.0,
                category = "vegetables",
                stock = 30,
                unit = "250g",
                brand = "Local",
                rating = 4.4f,
                reviewCount = 28,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_ginger"
            ),
            Product(
                id = "52",
                name = "Vegetable Cooking Oil",
                description = "Healthy cholesterol-free vegetable oil for high-heat cooking.",
                price = 480.0,
                category = "groceries",
                stock = 40,
                unit = "1 L",
                brand = "Fortune",
                rating = 4.5f,
                reviewCount = 89,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_oil"
            ),
            Product(
                id = "53",
                name = "Penne Pasta",
                description = "Durum wheat semolina penne pasta, stays firm after cooking.",
                price = 350.0,
                category = "groceries",
                stock = 50,
                unit = "500g",
                brand = "Bari",
                rating = 4.7f,
                reviewCount = 45,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_pasta"
            ),
            Product(
                id = "54",
                name = "Whole Watermelon",
                description = "Sweet and hydrating local watermelon, the ultimate summer fruit.",
                price = 160.0,
                category = "fruits",
                stock = 25,
                unit = "1 kg",
                brand = "Local",
                rating = 4.8f,
                reviewCount = 76,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_watermelon"
            ),
            Product(
                id = "55",
                name = "Munchee Chocolate Biscuit",
                description = "Crunchy biscuits with a rich chocolate coating.",
                price = 130.0,
                category = "snacks",
                stock = 80,
                unit = "100g",
                brand = "Munchee",
                rating = 4.7f,
                reviewCount = 112,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_chocolate_biscuit"
            ),
            Product(
                id = "56",
                name = "Maliban Chocolate Puff",
                description = "Light and airy puffs filled with smooth chocolate cream.",
                price = 130.0,
                category = "snacks",
                stock = 70,
                unit = "100g",
                brand = "Maliban",
                rating = 4.6f,
                reviewCount = 94,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_chocolate_puff"
            ),
            Product(
                id = "57",
                name = "Choc Shock Snacks",
                description = "Bursting with chocolate flavor, the favorite snack for kids.",
                price = 290.0,
                category = "snacks",
                stock = 45,
                unit = "150g",
                brand = "Munchee",
                rating = 4.8f,
                reviewCount = 56,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_choc_shock"
            ),
            Product(
                id = "58",
                name = "Kalo Dark Biscuit",
                description = "Intense dark chocolate biscuits for a premium snack experience.",
                price = 280.0,
                category = "snacks",
                stock = 60,
                unit = "120g",
                brand = "Munchee",
                rating = 4.5f,
                reviewCount = 34,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_kalo_biscuit"
            ),
            Product(
                id = "59",
                name = "Milk Cream Sandwich",
                description = "Smooth milk cream layered between two crunchy biscuits.",
                price = 280.0,
                category = "snacks",
                stock = 65,
                unit = "120g",
                brand = "Maliban",
                rating = 4.6f,
                reviewCount = 88,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_milk_cream_biscuit"
            ),
            Product(
                id = "60",
                name = "Tiffin Crispy Biscuit",
                description = "Classic crispy biscuits, perfect with a cup of tea.",
                price = 230.0,
                category = "snacks",
                stock = 90,
                unit = "100g",
                brand = "Munchee",
                rating = 4.4f,
                reviewCount = 42,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_tiffin_biscuit"
            ),
            Product(
                id = "61",
                name = "Instant Curry Noodles",
                description = "Spicy curry flavor instant noodles, ready in 2 minutes.",
                price = 280.0,
                category = "groceries",
                stock = 120,
                unit = "400g",
                brand = "Maggi",
                rating = 4.5f,
                reviewCount = 150,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_noodles"
            ),
            Product(
                id = "62",
                name = "Wild Strawberries",
                description = "Small, sweet wild strawberries harvested from the hills.",
                price = 500.0,
                category = "fruits",
                stock = 15,
                unit = "250g",
                brand = "Local",
                rating = 4.9f,
                reviewCount = 20,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_stewberry"
            ),
            Product(
                id = "63",
                name = "Purple Grapes",
                description = "Seedless purple grapes, sweet and bursting with juice.",
                price = 290.0,
                category = "fruits",
                stock = 40,
                unit = "500g",
                brand = "Local",
                rating = 4.7f,
                reviewCount = 45,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/img_grapes"
            ),
            Product(
                id = "64",
                name = "Soft Sandwich Bread",
                description = "Freshly baked soft white sandwich bread, sliced for convenience.",
                price = 180.0,
                category = "bakery",
                stock = 30,
                unit = "450g",
                brand = "Local",
                rating = 4.8f,
                reviewCount = 95,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/bakery"
            ),
            Product(
                id = "65",
                name = "Fresh Broiler Chicken",
                description = "Cleaned and dressed whole broiler chicken, farm fresh.",
                price = 1200.0,
                category = "meat",
                stock = 25,
                unit = "1 kg",
                brand = "Bairaha",
                rating = 4.7f,
                reviewCount = 68,
                imageUrl = "android.resource://com.example.lankasmartmart/drawable/meet"
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
        val subtotal = _cartItems.value.sumOf { it.product.discountedPrice * it.quantity }
        val delivery = if (subtotal >= 2000.0 || _cartItems.value.isEmpty()) 0.0 else 150.0
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
    
    // Product Review Functions
    fun addProductReview(productId: String, rating: Int, comment: String) {
        val currentProducts = _products.value.toMutableList()
        val productIndex = currentProducts.indexOfFirst { it.id == productId }
        
        if (productIndex != -1) {
            val product = currentProducts[productIndex]
            val newReview = com.example.lankasmartmart.model.Review(
                id = java.util.UUID.randomUUID().toString(),
                userId = "user_123",
                userName = "Kaveesha", // For demo purposes
                rating = rating,
                comment = comment
            )
            val updatedReviews = product.reviews + newReview
            val newTotalReviews = product.reviewCount + 1
            // Simple new average calculation
            val newRating = (product.rating * product.reviewCount + rating) / newTotalReviews
            
            currentProducts[productIndex] = product.copy(
                reviews = updatedReviews,
                reviewCount = newTotalReviews,
                rating = String.format("%.1f", newRating).toFloat()
            )
            _products.value = currentProducts
            
            // Refresh search results to reflect updated product data
            performSearch(_searchQuery.value)
        }
    }
}
