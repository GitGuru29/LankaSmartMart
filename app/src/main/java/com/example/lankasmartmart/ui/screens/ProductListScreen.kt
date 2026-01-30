package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.viewmodel.ShopViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.ColorPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    categoryId: String,
    categoryName: String,
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    val products = remember(categoryId) {
        shopViewModel.getProductsByCategory(categoryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = categoryName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (products.isEmpty()) {
                Text(
                    text = "No products available",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Product Name
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                // Brand
                if (product.brand.isNotEmpty()) {
                    Text(
                        text = product.brand,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Unit
                Text(
                    text = product.unit,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rating
                if (product.rating > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${product.rating} (${product.reviewCount})",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${String.format("%.2f", product.discountedPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (product.isOnSale && product.originalPrice > product.price) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rs. ${String.format("%.2f", product.originalPrice)}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                
                // Discount Badge
                if (product.isOnSale && product.discount > 0) {
                    Text(
                        text = "${product.discount}% OFF",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(
                                color = Color(0xFFE53935),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            
            // Add to Cart Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Product Image
                AsyncImage(
                    model = product.imageUrl.ifEmpty { "https://placehold.co/400x400/png?text=${product.name}" }, // Fallback if empty
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    error = androidx.compose.ui.graphics.painter.ColorPainter(Color.LightGray) // Simple error placeholder
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(width = 100.dp, height = 36.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", fontSize = 12.sp)
                }
                
                // Stock info
                if (product.stock < 10) {
                    Text(
                        text = "Only ${product.stock} left!",
                        fontSize = 10.sp,
                        color = Color(0xFFE53935),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
