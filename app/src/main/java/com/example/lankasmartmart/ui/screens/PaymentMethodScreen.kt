package com.example.lankasmartmart.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Address
import com.example.lankasmartmart.model.Order
import com.example.lankasmartmart.model.OrderItem
import com.example.lankasmartmart.viewmodel.OrderViewModel
import com.example.lankasmartmart.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    address: Address,
    onBackClick: () -> Unit = {},
    onOrderPlaced: (String) -> Unit = {},
    shopViewModel: ShopViewModel = viewModel(),
    orderViewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val cartItems by shopViewModel.cartItems.collectAsState()
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var isPlacingOrder by remember { mutableStateOf(false) }
    
    val totalAmount = remember(cartItems) {
        cartItems.sumOf { it.totalPrice }
    }
    
    val paymentMethods = listOf(
        PaymentMethodOption("Cash on Delivery", "Pay when you receive", Icons.Default.ShoppingCart),
        PaymentMethodOption("Card Payment", "Credit/Debit card", Icons.Default.Favorite),
        PaymentMethodOption("Online Banking", "Bank transfer", Icons.Default.Settings)
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Payment Method",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    method = method,
                    isSelected = selectedPaymentMethod == method.name,
                    onSelect = { selectedPaymentMethod = method.name }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Price summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Rs. %.2f".format(totalAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Button(
                onClick = {
                    isPlacingOrder = true
                    
                    // Convert cart items to order items
                    val orderItems = cartItems.map { cartItem ->
                        OrderItem(
                            productId = cartItem.productId,
                            productName = cartItem.product.name,
                            quantity = cartItem.quantity,
                            price = cartItem.product.discountedPrice,
                            imageUrl = cartItem.product.imageUrl
                        )
                    }
                    
                    // Create order
                    val order = Order(
                        items = orderItems,
                        totalAmount = totalAmount,
                        deliveryAddress = address,
                        paymentMethod = selectedPaymentMethod,
                        status = "pending"
                    )
                    
                    orderViewModel.createOrder(
                        order,
                        onSuccess = { orderId ->
                            // Clear cart
                            shopViewModel.clearCart()
                            isPlacingOrder = false
                            onOrderPlaced(orderId)
                        },
                        onError = {
                            isPlacingOrder = false
                            Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isPlacingOrder
            ) {
                if (isPlacingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Place Order",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class PaymentMethodOption(
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun PaymentMethodCard(
    method: PaymentMethodOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = method.icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = method.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = method.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
