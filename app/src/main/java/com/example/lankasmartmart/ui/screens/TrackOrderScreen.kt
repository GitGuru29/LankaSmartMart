package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.model.Order
import java.text.SimpleDateFormat
import java.util.*

data class OrderStatus(
    val status: String,
    val title: String,
    val icon: ImageVector,
    val isCompleted: Boolean,
    val isActive: Boolean,
    val timestamp: Long? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    order: Order,
    onBackClick: () -> Unit = {}
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    
    val statusList = remember(order.status) {
        val current = order.status.lowercase()
        val statuses = listOf(
            "pending" to "Order Placed",
            "confirmed" to "Order Confirmed",
            "processing" to "Processing",
            "out_for_delivery" to "Out for Delivery",
            "delivered" to "Delivered"
        )
        
        val currentIndex = statuses.indexOfFirst { it.first == current }
        val isCancelled = current == "cancelled"
        
        statuses.mapIndexed { index, (statusKey, title) ->
            OrderStatus(
                status = statusKey,
                title = title,
                icon = when (statusKey) {
                    "pending" -> Icons.Default.ShoppingCart
                    "confirmed" -> Icons.Default.CheckCircle
                    "processing" -> Icons.Default.Refresh
                    "out_for_delivery" -> Icons.Default.ShoppingCart
                    "delivered" -> Icons.Default.Check
                    else -> Icons.Default.Info
                },
                isCompleted = if (isCancelled) false else index < currentIndex,
                isActive = if (isCancelled) false else index == currentIndex,
                timestamp = if (index == currentIndex) order.updatedAt else null
            )
        } + if (isCancelled) {
            listOf(
                OrderStatus(
                    status = "cancelled",
                    title = "Order Cancelled",
                    icon = Icons.Default.Close,
                    isCompleted = false,
                    isActive = true,
                    timestamp = order.updatedAt
                )
            )
        } else emptyList()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Track Order",
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Order #${order.id.take(8).uppercase()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = order.deliveryAddress?.city ?: "City",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            // Timeline
            statusList.forEach { status ->
                OrderStatusItem(
                    status = status,
                    dateFormat = dateFormat,
                    isLast = status == statusList.last()
                )
            }
            
            // Delivery Address
            if (order.status != "cancelled") {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delivery Address",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = order.deliveryAddress?.name ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = order.deliveryAddress?.addressLine1 ?: "",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        if (!order.deliveryAddress?.addressLine2.isNullOrEmpty()) {
                            Text(
                                text = order.deliveryAddress?.addressLine2 ?: "",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "${order.deliveryAddress?.city ?: ""}, ${order.deliveryAddress?.postalCode ?: ""}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusItem(
    status: OrderStatus,
    dateFormat: SimpleDateFormat,
    isLast: Boolean
) {
    val iconColor = when {
        status.isActive -> MaterialTheme.colorScheme.primary
        status.isCompleted -> MaterialTheme.colorScheme.primary
        status.status == "cancelled" -> Color(0xFFE53935)
        else -> Color.Gray
    }
    
    val lineColor = if (status.isCompleted) MaterialTheme.colorScheme.primary else Color.LightGray
    
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Icon column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = if (status.isActive || status.isCompleted || status.status == "cancelled") {
                    iconColor.copy(alpha = 0.2f)
                } else {
                    Color.LightGray.copy(alpha = 0.3f)
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Timeline line
            if (!isLast) {
                Canvas(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                ) {
                    drawLine(
                        color = lineColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 4f,
                        pathEffect = if (status.isCompleted) null else PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = status.title,
                fontSize = 16.sp,
                fontWeight = if (status.isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (status.isActive || status.isCompleted || status.status == "cancelled") {
                    Color(0xFF212121)
                } else {
                    Color.Gray
                }
            )
            if (status.timestamp != null) {
                Text(
                    text = dateFormat.format(Date(status.timestamp)),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (!isLast) {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
