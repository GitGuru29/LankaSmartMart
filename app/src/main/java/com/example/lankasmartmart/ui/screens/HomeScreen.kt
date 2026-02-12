package com.example.lankasmartmart.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.R
import com.example.lankasmartmart.model.Category
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.ui.theme.*
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.example.lankasmartmart.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel(),
    onCategoryClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val categories by shopViewModel.categories.collectAsState()
    val products by shopViewModel.products.collectAsState()
    val scrollState = rememberScrollState()
    
    // Separate products for sections
    val exclusiveProducts = remember(products) { products.take(5) }
    val bestSellingProducts = remember(products) { products.takeLast(5) }
    
    // Bottom Nav State
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            GroceryBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    when (index) {
                        1 -> onSearchClick()    // Explore tab
                        2 -> onCartClick()      // Cart tab
                        4 -> onProfileClick()   // Account tab
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Carrot Icon (Emoji fallback)
                Text(
                    text = "🥕",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF4C4F4D),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Dhaka, Banassre",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4C4F4D)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp)
                    .background(Color(0xFFF2F3F2), RoundedCornerShape(15.dp))
                    .clickable { onSearchClick() },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF181B19)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search Store",
                        fontSize = 14.sp,
                        color = Color(0xFF7C7C7C)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Promo Banner
            PromoBanner()
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Exclusive Offer
            SectionHeader(title = "Exclusive Offer", onSeeAll = {})
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(exclusiveProducts) { product ->
                    GroceryProductCard(product = product)
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Best Selling
            SectionHeader(title = "Best Selling", onSeeAll = {})
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bestSellingProducts) { product ->
                    GroceryProductCard(product = product)
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Groceries Categories
            SectionHeader(title = "Groceries", onSeeAll = { onCategoryClick("all") })
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(category = category)
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Product Grid (Using FlowRow equivalent manually or fixed grid)
            // Note: LazyVerticalGrid cannot be nested in a scrollable Column easily without fixed height.
            // Using a simple Column for the grid items for this example or a manual grid.
            SectionHeader(title = "All Products", onSeeAll = {})
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                val chunkedProducts = products.chunked(2)
                chunkedProducts.forEach { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowProducts.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                GroceryProductCard(product = product, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom nav
        }
    }
}

@Composable
fun PromoBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFECF8EF), Color(0xFFF2FDF5))
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Image Placeholder
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text("🥬", fontSize = 48.sp)
            }
            
            // Right Text
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Fresh Vegetables",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF181725)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get Up To 40% OFF",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GroceryGreen
                )
            }
        }
        
        // Dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).background(GroceryGreen, CircleShape))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, CircleShape))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, CircleShape))
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF181725)
        )
        Text(
            text = "See all",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = GroceryGreen,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
fun GroceryProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(173.dp)
            .height(250.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E2E2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Using emoji/icon as placeholder if no image
                Text(text = "🍎", fontSize = 50.sp) 
                
                // Actual Image impl (commented out as context needed for coil)
                /*
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                */
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF181725),
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Unit
            Text(
                text = product.unit,
                fontSize = 14.sp,
                color = Color(0xFF7C7C7C)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Price and Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${product.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF181725)
                )
                
                Surface(
                    shape = RoundedCornerShape(17.dp),
                    color = GroceryGreen,
                    modifier = Modifier.size(45.dp).clickable { /* Add to cart */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category) {
    Row(
        modifier = Modifier
            .background(
                color = getCategoryColor(category.id).copy(alpha = 0.15f), 
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon/Image
        Text(text = category.icon, fontSize = 24.sp) // Use emoji icon from category
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = category.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3E423F)
        )
    }
}

// Helper to get pastel colors for categories
fun getCategoryColor(id: String): Color {
    return when(id) {
        "vegetables", "fruits" -> GroceryGreen
        "dairy", "beverages" -> Color(0xFFF8A44C) // Orange pastel
        "groceries", "snacks" -> Color(0xFFD3B0E0) // Purple pastel
        else -> Color(0xFF53B175)
    }
}

@Composable
fun GroceryBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 16.dp,
        modifier = Modifier.shadow(16.dp, shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
    ) {
        val items = listOf(
            Triple("Shop", Icons.Outlined.Home, 0),
            Triple("Explore", Icons.Outlined.Search, 1),
            Triple("Cart", Icons.Outlined.ShoppingCart, 2),
            Triple("Favourite", Icons.Outlined.FavoriteBorder, 3),
            Triple("Account", Icons.Outlined.Person, 4)
        )
        
        items.forEach { (label, icon, index) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = icon, 
                        contentDescription = label,
                        tint = if (isSelected) GroceryGreen else Color(0xFF181725)
                    )
                },
                label = { 
                    Text(
                        text = label, 
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) GroceryGreen else Color(0xFF181725)
                    ) 
                },
                selected = isSelected,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
