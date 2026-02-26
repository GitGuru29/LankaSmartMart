package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.viewmodel.ShopViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun SearchScreen(
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onProductClick: (Product) -> Unit = {}
) {
    val searchQuery by shopViewModel.searchQuery.collectAsState()
    val searchResults by shopViewModel.searchResults.collectAsState()
    val focusRequester = remember { FocusRequester() }
    
    // Auto-focus search field on screen open
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        shopViewModel.clearSearch() // Show all products initially
    }
    
    Scaffold(
        containerColor = Color(0xFFF8FFFE),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onQueryChange = { shopViewModel.updateSearchQuery(it) },
                onClearClick = { shopViewModel.clearSearch() },
                onBackClick = onBackClick,
                focusRequester = focusRequester
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Results Header
            if (searchQuery.isNotBlank()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Found ${searchResults.size} product${if (searchResults.size != 1) "s" else ""}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF757575),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }
            
            // Search Results or Empty State
            when {
                searchQuery.isBlank() -> {
                    // Initial state - show all products
                    SearchResultsList(
                        products = searchResults,
                        onProductClick = onProductClick
                    )
                }
                searchResults.isEmpty() -> {
                    // No results found
                    NoResultsFound(query = searchQuery)
                }
                else -> {
                    // Show results
                    SearchResultsList(
                        products = searchResults,
                        onProductClick = onProductClick
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTopBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    focusRequester: FocusRequester
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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Search Input Field
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(Color.White),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search products...",
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            innerTextField()
                        }
                    )
                    
                    // Clear Button
                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    if (products.isEmpty()) {
        EmptySearchState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products, key = { it.id }) { product ->
                SearchProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
fun SearchProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Icon
            Surface(
                shape = CircleShape,
                color = getCategoryColor(product.category),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getCategoryEmoji(product.category),
                        fontSize = 32.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${product.brand} • ${product.unit}",
                    fontSize = 13.sp,
                    color = Color(0xFF757575)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "LKR ${String.format("%.2f", product.price)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (product.isOnSale) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "-${product.discount}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            // Arrow Icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}

@Composable
fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🔍",
                fontSize = 80.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Search for products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Find what you need quickly",
                fontSize = 14.sp,
                color = Color(0xFF757575)
            )
        }
    }
}

@Composable
fun NoResultsFound(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "😔",
                fontSize = 80.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No products found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "No results for \"$query\"",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Try different keywords",
                fontSize = 13.sp,
                color = Color(0xFF9E9E9E)
            )
        }
    }
}
