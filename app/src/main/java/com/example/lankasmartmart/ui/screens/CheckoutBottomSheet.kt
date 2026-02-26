package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.ui.theme.GroceryTextDark
import com.example.lankasmartmart.ui.theme.GroceryTextGrey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
    totalCost: Double,
    onClose: () -> Unit,
    onPlaceOrder: () -> Unit,
    onDeliveryClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onPromoClick: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        containerColor = Color.White,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Checkout",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryTextDark
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = GroceryTextDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)

            // Delivery Row
            CheckoutRow(
                label = "Delivery",
                value = "Select Method",
                onClick = onDeliveryClick
            )

            // Payment Row
            CheckoutRow(
                label = "Payment",
                onClick = onPaymentClick,
                content = {
                    // Try to find a card icon or use a generic one
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        // Using a simple colored box as placeholder if card icon is missing
                        // or better, if there's a mastercard icon in resources
                        // For now, let's use a generic icon or empty
                        Canvas(modifier = Modifier.size(24.dp, 16.dp)) {
                            drawRect(color = Color(0xFFEB001B), size = size.copy(width = size.width / 2))
                            drawRect(
                                color = Color(0xFFF79E1B),
                                size = size.copy(width = size.width / 2),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width / 2, 0f)
                            )
                        }
                    }
                }
            )

            // Promo Code Row
            CheckoutRow(
                label = "Promo Code",
                value = "Pick discount",
                onClick = onPromoClick
            )

            // Total Cost Row
            CheckoutRow(
                label = "Total Cost",
                value = "LKR ${String.format("%.0f", totalCost)}",
                isBold = true,
                onClick = {} // Cost is usually not clickable
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Terms and Conditions
            Text(
                text = buildAnnotatedString {
                    append("By placing an order you agree to our ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = GroceryTextDark)) {
                        append("Terms")
                    }
                    append(" And ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = GroceryTextDark)) {
                        append("Conditions")
                    }
                },
                fontSize = 14.sp,
                color = GroceryTextGrey,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Place Order Button
            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(67.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5383EC) // Blue from design
                )
            ) {
                Text(
                    text = "Place Order",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CheckoutRow(
    label: String,
    value: String? = null,
    isBold: Boolean = false,
    onClick: () -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = GroceryTextGrey,
                modifier = Modifier.weight(1f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                content?.invoke()
                
                if (value != null) {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                        color = GroceryTextDark
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = GroceryTextDark
                )
            }
        }
        HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)
    }
}
