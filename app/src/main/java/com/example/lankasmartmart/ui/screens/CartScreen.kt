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
import com.example.lankasmartmart.model.CartItem
import com.example.lankasmartmart.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val cartItems by shopViewModel.cartItems.collectAsState()
    val cartSubtotal by shopViewModel.cartSubtotal.collectAsState()
    val deliveryFee by shopViewModel.deliveryFee.collectAsState()
    val cartTotal by shopViewModel.cartTotal.collectAsState()
    
    Scaffold(
        containerColor = Color(0xFFF8FFFE),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CartTopBar(
                itemCount = cartItems.size,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CheckoutButton(
                    total = cartTotal,
                    onClick = onCheckoutClick
                )
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartView(
                onStartShoppingClick = onBackClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Cart Items List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.product.id }) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                shopViewModel.updateQuantity(cartItem.product.id, newQuantity)
                            },
                            onRemoveClick = {
                                shopViewModel.removeFromCart(cartItem.product.id)
                            }
                        )
                    }
                }
                
                // Price Summary
                PriceSummaryCard(
                    subtotal = cartSubtotal,
                    deliveryFee = deliveryFee,
                    total = cartTotal
                )
            }
        }
    }
}

@Composable
fun CartTopBar(
    itemCount: Int,
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
            
            Column {
                Text(
                    text = "My Cart",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "$itemCount item${if (itemCount != 1) "s" else ""}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Emoji/Icon in Circle
            Surface(
                shape = CircleShape,
                color = getCategoryColor(cartItem.product.category),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getCategoryEmoji(cartItem.product.category),
                        fontSize = 28.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = cartItem.product.unit,
                    fontSize = 12.sp,
                    color = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "LKR ${String.format("%.2f", cartItem.product.price)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Quantity Controls
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Decrease Button
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                onQuantityChange(cartItem.quantity - 1)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Decrease",
                                tint = Color(0xFF424242),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    
                    // Quantity
                    Text(
                        text = "${cartItem.quantity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    
                    // Increase Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                onQuantityChange(cartItem.quantity + 1)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Item Total
                Text(
                    text = "LKR ${String.format("%.2f", cartItem.product.price * cartItem.quantity)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
            }
        }
        
        // Remove Button
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        
        TextButton(
            onClick = onRemoveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color(0xFFE53935),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Remove Item",
                color = Color(0xFFE53935),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun PriceSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Price Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "LKR ${String.format("%.2f", subtotal)}",
                    fontSize = 14.sp,
                    color = Color(0xFF212121)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Delivery Fee
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                if (deliveryFee == 0.0) {
                    Text(
                        text = "FREE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "LKR ${String.format("%.2f", deliveryFee)}",
                        fontSize = 14.sp,
                        color = Color(0xFF212121)
                    )
                }
            }
            
            if (subtotal < 2000.0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Free delivery on orders over LKR 2,000",
                    fontSize = 11.sp,
                    color = Color(0xFF757575),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    text = "LKR ${String.format("%.2f", total)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CheckoutButton(
    total: Double,
    onClick: () -> Unit
) {
    Surface(
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Proceed to Checkout",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LKR ${String.format("%.2f", total)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EmptyCartView(
    onStartShoppingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🛒",
            fontSize = 80.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Your cart is empty",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Add items to get started",
            fontSize = 14.sp,
            color = Color(0xFF757575)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onStartShoppingClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Start Shopping",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Helper function for category icons
internal fun getCategoryEmoji(categoryId: String): String {
    return when (categoryId) {
        "groceries" -> "🛒"
        "vegetables" -> "🥬"
        "fruits" -> "🍎"
        "dairy" -> "🥛"
        "beverages" -> "☕"
        "snacks" -> "🍿"
        "personal_care" -> "🧴"
        "household" -> "🧹"
        "stationery" -> "✏️"
        else -> "📦"
    }
}

// Helper function for category colors
internal fun getCategoryColor(categoryId: String): Color {
    return when (categoryId) {
        "groceries" -> Color(0xFFE8F5E9)
        "vegetables" -> Color(0xFFE8F5E9)
        "fruits" -> Color(0xFFFFEBEE)
        "dairy" -> Color(0xFFE3F2FD)
        "beverages" -> Color(0xFFFFF3E0)
        "snacks" -> Color(0xFFFFF9C4)
        "personal_care" -> Color(0xFFF3E5F5)
        "household" -> Color(0xFFE0F7FA)
        "stationery" -> Color(0xFFFFF8E1)
        else -> Color(0xFFECEFF1)
    }
}
