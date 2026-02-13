package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.viewmodel.ShopViewModel

@Composable
fun ProductDetailsScreen(
    productId: String,
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCartClick: () -> Unit = {}
) {
    val products by shopViewModel.products.collectAsState()
    val product = products.find { it.id == productId }
    
    var quantity by remember { mutableIntStateOf(1) }
    var showAddedToast by remember { mutableStateOf(false) }
    
    if (product == null) {
        // Product not found
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Product not found")
        }
        return
    }
    
    Scaffold(
        containerColor = Color(0xFFF8FFFE),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            ProductDetailsTopBar(
                productName = product.name,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AddToCartBottomBar(
                quantity = quantity,
                price = product.price,
                onAddToCart = {
                    shopViewModel.addToCart(product, quantity)
                    showAddedToast = true
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Product Hero Section
            item {
                ProductHeroSection(product = product)
            }
            
            // Product Info Card
            item {
                ProductInfoCard(product = product)
            }
            
            // Quantity Selector
            item {
                QuantitySelectorCard(
                    quantity = quantity,
                    maxQuantity = product.stock,
                    onQuantityChange = { newQuantity ->
                        quantity = newQuantity.coerceIn(1, product.stock)
                    }
                )
            }
            
            // Description
            item {
                DescriptionCard(description = product.description)
            }
            
            // Ratings & Reviews
            item {
                RatingsReviewsCard(
                    rating = product.rating,
                    reviewCount = product.reviewCount
                )
            }
        }
        
        // Toast/Snackbar for added to cart
        if (showAddedToast) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showAddedToast = false
            }
        }
    }
    
    if (showAddedToast) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Added to cart!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailsTopBar(
    productName: String,
    onBackClick: () -> Unit
) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            .padding(top = statusBarHeight)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = productName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProductHeroSection(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Large Product Icon
            Surface(
                shape = CircleShape,
                color = getCategoryColor(product.category),
                modifier = Modifier
                    .size(140.dp)
                    .shadow(8.dp, CircleShape)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getCategoryEmoji(product.category),
                        fontSize = 64.sp
                    )
                }
            }
            
            // Sale Badge
            if (product.isOnSale) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "SALE ${product.discount}% OFF",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProductInfoCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Product Name & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.weight(1f)
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(java.util.Locale.getDefault(), "%.1f", product.rating),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Brand & Unit
            Text(
                text = "${product.brand} • ${product.unit}",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LKR ${String.format(java.util.Locale.getDefault(), "%.2f", product.price)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (product.isOnSale && product.originalPrice != null) {
                    Text(
                        text = "LKR ${String.format(java.util.Locale.getDefault(), "%.2f", product.originalPrice)}",
                        fontSize = 16.sp,
                        color = Color(0xFF757575),
                        textDecoration = TextDecoration.LineThrough
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "-${product.discount}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stock Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (product.stock > 10) MaterialTheme.colorScheme.primary else Color(0xFFFFA726),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (product.stock > 10) "In Stock (${product.stock} available)" else "Only ${product.stock} left!",
                    fontSize = 14.sp,
                    color = if (product.stock > 10) MaterialTheme.colorScheme.primary else Color(0xFFFFA726),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun QuantitySelectorCard(
    quantity: Int,
    maxQuantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Decrease Button
                Surface(
                    shape = CircleShape,
                    color = if (quantity > 1) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = quantity > 1) {
                            onQuantityChange(quantity - 1)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Decrease",
                            tint = if (quantity > 1) Color.White else Color(0xFF9E9E9E),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Quantity
                Text(
                    text = "$quantity",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                
                // Increase Button
                Surface(
                    shape = CircleShape,
                    color = if (quantity < maxQuantity) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(enabled = quantity < maxQuantity) {
                            onQuantityChange(quantity + 1)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = if (quantity < maxQuantity) Color.White else Color(0xFF9E9E9E),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DescriptionCard(description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF616161),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun RatingsReviewsCard(
    rating: Float,
    reviewCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ratings & Reviews",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                
                Text(
                    text = "($reviewCount reviews)",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Star Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating.toInt()) Color(0xFFFFA726) else Color(0xFFE0E0E0),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format(java.util.Locale.getDefault(), "%.1f/5.0", rating),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sample Reviews
            SampleReviewItem("John D.", 5f, "Excellent quality! Highly recommended.", "2 days ago")
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SampleReviewItem("Sarah M.", 4f, "Good product, fast delivery.", "1 week ago")
        }
    }
}

@Composable
fun SampleReviewItem(
    userName: String,
    rating: Float,
    comment: String,
    date: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121)
            )
            Text(
                text = date,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < rating.toInt()) Color(0xFFFFA726) else Color(0xFFE0E0E0),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = comment,
            fontSize = 13.sp,
            color = Color(0xFF616161),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun AddToCartBottomBar(
    quantity: Int,
    price: Double,
    onAddToCart: () -> Unit
) {
    Surface(
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add to Cart ($quantity)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    
                    Text(
                        text = "LKR ${String.format(java.util.Locale.getDefault(), "%.2f", price * quantity)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
