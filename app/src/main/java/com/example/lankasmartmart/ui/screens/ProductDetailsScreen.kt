package com.example.lankasmartmart.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.R
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.ui.theme.GroceryGreen
import com.example.lankasmartmart.ui.theme.GroceryTextDark
import com.example.lankasmartmart.ui.theme.GroceryTextGrey
import com.example.lankasmartmart.viewmodel.ShopViewModel
import com.example.lankasmartmart.utils.*

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
    var isFavourite by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isReviewsExpanded by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showAddedToast by remember { mutableStateOf(false) }
    
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found")
        }
        return
    }
    
    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp)) {
                Button(
                    onClick = {
                        shopViewModel.addToCart(product, quantity)
                        showAddedToast = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(67.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5383EC)
                    )
                ) {
                    Text(
                        text = "Add To Basket",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            ProductHeroSection(
                product = product,
                onBackClick = onBackClick,
                onShareClick = { /* Share logic */ }
            )

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(30.dp))

                // Name & Favourite row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GroceryTextDark
                    )
                    IconButton(onClick = { isFavourite = !isFavourite }) {
                        Icon(
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favourite",
                            tint = if (isFavourite) Color.Red else GroceryTextGrey,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "${product.unit}, Price",
                    fontSize = 16.sp,
                    color = GroceryTextGrey
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Quantity & Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = GroceryTextGrey,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { if (quantity > 1) quantity-- }
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(17.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$quantity",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GroceryTextDark
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = GroceryGreen,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { quantity++ }
                        )
                    }

                    Text(
                        text = "LKR ${(product.discountedPrice * quantity).toInt()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GroceryTextDark
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)

                // Product Detail Section
                ExpandableDetailRow(
                    label = "Product Detail",
                    isExpanded = isDescriptionExpanded,
                    onToggle = { isDescriptionExpanded = !isDescriptionExpanded }
                ) {
                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = GroceryTextGrey,
                        lineHeight = 21.sp
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)

                // Nutritions Row
                ClickableInfoRow(
                    label = "Nutritions",
                    value = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFFEBEBEB))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "100g",
                                fontSize = 9.sp,
                                color = GroceryTextGrey
                            )
                        }
                    }
                )

                HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)

                // Review Section
                ExpandableDetailRow(
                    label = "Review",
                    isExpanded = isReviewsExpanded,
                    onToggle = { isReviewsExpanded = !isReviewsExpanded }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < product.rating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFF3603F),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${product.rating} (${product.reviewCount} reviews)",
                                    fontSize = 14.sp,
                                    color = GroceryTextDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "Write a review",
                                fontSize = 14.sp,
                                color = GroceryGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showReviewDialog = true }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (product.reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first to review!",
                                fontSize = 13.sp,
                                color = GroceryTextGrey,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            product.reviews.forEach { review ->
                                ReviewItem(review = review)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
    }
}
    
    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                shopViewModel.addProductReview(product.id, rating, comment)
                showReviewDialog = false
            }
        )
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
private fun ProductHeroSection(
    product: Product,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    val imageRes = remember(product.name) {
        getProductImageRes(context, product.name)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(370.dp)
            .clip(RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp))
            .background(Color(0xFFF2F3F2))
    ) {
        // Status bar icons (simplified as an image/box overlay in real implementation)
        // Back & Share Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() },
                tint = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Share, // Fixed: Using standard Share icon
                contentDescription = "Share",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onShareClick() },
                tint = Color.Black
            )
        }

        // Product Image
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.size(300.dp, 200.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = getCategoryEmoji(product.category),
                    fontSize = 120.sp
                )
            }
        }

        // Carousel Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp, 3.dp)
                    .clip(CircleShape)
                    .background(GroceryGreen)
            )
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3B3B3))
            )
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3B3B3))
            )
        }
    }
}

@Composable
private fun ClickableInfoRow(
    label: String,
    value: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = GroceryTextDark
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            value()
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GroceryTextDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ExpandableDetailRow(
    label: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = GroceryTextDark
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GroceryTextDark,
                modifier = Modifier.size(24.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ReviewItem(review: com.example.lankasmartmart.model.Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = review.userName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryTextDark
            )
            Text(
                text = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(review.timestamp)),
                fontSize = 12.sp,
                color = GroceryTextGrey
            )
        }
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = Color(0xFFF3603F),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Text(
            text = review.comment,
            fontSize = 13.sp,
            color = GroceryTextGrey,
            lineHeight = 18.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Rating", fontSize = 14.sp, color = GroceryTextGrey)
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFF3603F),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index + 1 }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = { Text("Your comments...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreen)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White
    )
}

// ─── Helper Functions (Copied from CartScreen or shared) ───────────────────────



