package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.CartItem
import com.example.lankasmartmart.ui.theme.GroceryGreen
import com.example.lankasmartmart.ui.theme.GroceryGreenDark
import com.example.lankasmartmart.ui.theme.GroceryTextDark
import com.example.lankasmartmart.ui.theme.GroceryTextGrey
import com.example.lankasmartmart.viewmodel.ShopViewModel

@Composable
fun CartScreen(
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCheckoutClick: () -> Unit = {}
) {
    val cartItems by shopViewModel.cartItems.collectAsState()
    val cartTotal by shopViewModel.cartTotal.collectAsState()

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CartTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            CartBottomBar(
                total = cartTotal,
                onCheckoutClick = onCheckoutClick,
                showCheckout = cartItems.isNotEmpty()
            )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(cartItems, key = { it.product.id }) { cartItem ->
                    CartItemRow(
                        cartItem = cartItem,
                        onQuantityChange = { newQuantity ->
                            shopViewModel.updateQuantity(cartItem.product.id, newQuantity)
                        },
                        onRemoveClick = {
                            shopViewModel.removeFromCart(cartItem.product.id)
                        }
                    )
                    HorizontalDivider(
                        color = Color(0xFFE2E2E2),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}

// ─── Top Bar ────────────────────────────────────────────────────────────────────

@Composable
private fun CartTopBar(onBackClick: () -> Unit) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarHeight)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = GroceryTextDark
                )
            }

            // Title
            Text(
                text = "My Cart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryTextDark,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// ─── Cart Item Row ──────────────────────────────────────────────────────────────

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveClick: () -> Unit
) {
    val context = LocalContext.current

    // Resolve drawable resource from product name
    val imageRes = remember(cartItem.product.name) {
        getProductImageRes(context, cartItem.product.name)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = cartItem.product.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Fallback emoji
                Text(
                    text = getCategoryEmoji(cartItem.product.category),
                    fontSize = 32.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Product details + quantity controls
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Name & remove button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItem.product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GroceryTextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${cartItem.product.unit}, Price",
                        fontSize = 13.sp,
                        color = GroceryTextGrey
                    )
                }

                // Remove (X) button
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color(0xFFB3B3B3),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemoveClick() }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity controls + price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Minus button
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.5.dp, Color(0xFFE2E2E2), RoundedCornerShape(14.dp))
                            .clickable {
                                if (cartItem.quantity > 1) {
                                    onQuantityChange(cartItem.quantity - 1)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = GroceryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Count
                    Text(
                        text = "${cartItem.quantity}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GroceryTextDark
                    )

                    // Plus button
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .border(1.5.dp, Color(0xFFE2E2E2), RoundedCornerShape(14.dp))
                            .clickable {
                                onQuantityChange(cartItem.quantity + 1)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = GroceryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Price
                Text(
                    text = "LKR ${String.format("%.0f", cartItem.product.price * cartItem.quantity)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryTextDark
                )
            }
        }
    }
}

// ─── Bottom Bar with Checkout ───────────────────────────────────────────────────

@Composable
private fun CartBottomBar(
    total: Double,
    onCheckoutClick: () -> Unit,
    showCheckout: Boolean
) {
    if (!showCheckout) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GroceryGreen
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Empty spacer for centering
                    Spacer(modifier = Modifier.width(80.dp))

                    Text(
                        text = "Go to Checkout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    // Price badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF489E67)
                    ) {
                        Text(
                            text = "LKR ${String.format("%.2f", total)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─── Empty Cart ─────────────────────────────────────────────────────────────────

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
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Empty Cart",
            tint = Color(0xFFE2E2E2),
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your cart is empty",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = GroceryTextDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add items to get started",
            fontSize = 14.sp,
            color = GroceryTextGrey
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartShoppingClick,
            shape = RoundedCornerShape(19.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GroceryGreen
            ),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text(
                text = "Start Shopping",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─── Helper: Resolve product image drawable ─────────────────────────────────────

private fun getProductImageRes(context: android.content.Context, productName: String): Int? {
    // Convert product name to drawable resource name: "Organic Bananas" -> "img_bananas"
    val simpleName = productName
        .lowercase()
        .replace("organic ", "")
        .replace("fresh ", "")
        .replace("ceylon ", "ceylon_")
        .replace("basmati ", "basmati_")
        .replace("wheat ", "wheat_")
        .replace("anchor ", "anchor_")
        .replace("mango ", "mango_")
        .replace("red ", "red_")
        .replace("rin ", "rin_")
        .replace("rani ", "rani_")
        .replace("misumi ", "misumi_")
        .replace("cream ", "cream_")
        .replace("coconut ", "coconut_")
        .replace("sunlight ", "sunlight_")
        .replace("blue ", "blue_")
        .replace("atlas ", "atlas_")
        .trim()
        .replace(" ", "_")

    val resName = "img_$simpleName"
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) resId else null
}

// Helper function for category icons (kept for fallback)
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

// Helper function for category colors (kept for other usage)
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
