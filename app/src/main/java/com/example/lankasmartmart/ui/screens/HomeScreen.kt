package com.example.lankasmartmart.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Category
import com.example.lankasmartmart.data.ProductSeeder
import com.example.lankasmartmart.utils.extractName
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.example.lankasmartmart.viewmodel.ShopViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

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
    val currentUser = authViewModel.currentUser
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    
    var userCity by remember { mutableStateOf("Detecting...") }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    
    // ONE-TIME: Seed products to Firestore
    LaunchedEffect(Unit) {
        scope.launch {
            val seeder = ProductSeeder()
            seeder.seedProducts()
        }
    }
    
    // Location permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        locationPermissionGranted = isGranted
    }
    
    // Request location permission and get city
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        delay(500) // Give time for permission dialog
        
        try {
            val locationHelper = com.example.lankasmartmart.utils.LocationHelper(context)
            val city = locationHelper.getCurrentCity()
            userCity = city
        } catch (e: Exception) {
            userCity = "Sri Lanka"
        }
    }
    
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
    
    val displayName = remember(currentUser?.displayName) {
        currentUser?.displayName?.extractName() ?: "Guest"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp), // Extend behind system bars
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = {},
                onSearchClick = onSearchClick,
                onCartClick = onCartClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Beautiful Header with Gradient - Extends to top
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
                    .padding(top = statusBarHeight) // Add padding for status bar/camera
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = greeting,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = " ☀️",
                                fontSize = 20.sp
                            )
                        }
                    }
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = userCity,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Delivering to",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Premium Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .clickable { onSearchClick() },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Search groceries, vegetables, fruits...",
                        fontSize = 15.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }

            // Eye-Catching Promotional Banners - Compact
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                modifier = Modifier.height(85.dp) // Reduced height
            ) {
                items(listOf(
                    Banner("Fresh Vegetables", "20% Off", "🥬", Color(0xFF42A5F5)),  // Medium blue
                    Banner("Dairy Products", "Buy 2 Get 1", "🥛", Color(0xFF1976D2)), // Dark blue
                    Banner("Fresh Fruits", "15% Off", "🍎", Color(0xFF64B5F6))        // Light blue
                )) { banner ->
                    PremiumBannerCard(banner)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shop by Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "See All",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Beautiful Category Grid - FIXED POSITION, NO SCROLLING
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 16.dp),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false // Disable scrolling completely
            ) {
                items(categories) { category ->
                    EyeCatchingCategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumBannerCard(banner: Banner) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            banner.color,
                            banner.color.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Emoji in Circle - Smaller
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = banner.emoji,
                            fontSize = 24.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = banner.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = banner.subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EyeCatchingCategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle gradient background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF5F5F5),
                                Color.White
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with colorful background
                Surface(
                    shape = CircleShape,
                    color = getCategoryColor(category.id),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = category.icon,
                            fontSize = 32.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = category.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

// Color scheme for categories - Blue monochromatic shades
fun getCategoryColor(categoryId: String): Color {
    return when (categoryId) {
        "groceries" -> Color(0xFFE3F2FD)      // Very light blue
        "vegetables" -> Color(0xFFBBDEFB)     // Light blue
        "fruits" -> Color(0xFF90CAF9)         // Medium light blue
        "dairy" -> Color(0xFFE1F5FE)          // Pale blue
        "beverages" -> Color(0xFFB3E5FC)      // Sky blue (already blue!)
        "snacks" -> Color(0xFFCFE8FC)         // Light sky blue
        "personal_care" -> Color(0xFFBBDEFB)  // Light blue
        "household" -> Color(0xFFD1E8F5)      // Soft blue
        "stationery" -> Color(0xFFDCEEFB)     // Pale blue
        else -> Color(0xFFE3F2FD)             // Default light blue
    }
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp) // Compact height
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, "Home", modifier = Modifier.size(22.dp)) },
                label = { Text("Home", fontSize = 11.sp) },
                selected = true,
                onClick = onHomeClick,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
            
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFE53935),
                                contentColor = Color.White
                            ) {
                                Text("3", fontSize = 9.sp)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Cart", modifier = Modifier.size(22.dp))
                    }
                },
                label = { Text("Cart", fontSize = 11.sp) },
                selected = false,
                onClick = onCartClick
            )
            
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, "Profile", modifier = Modifier.size(22.dp)) },
                label = { Text("Profile", fontSize = 11.sp) },
                selected = false,
                onClick = onProfileClick
            )
        }
    }
}

data class Banner(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: Color = Color(0xFF42A5F5)  // Medium blue default
)
