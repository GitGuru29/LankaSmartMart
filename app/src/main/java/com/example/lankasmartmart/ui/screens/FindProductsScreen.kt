package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R

// Data model for categories
data class CategoryItem(
    val id: String,
    val name: String,
    val imageRes: Int,
    val backgroundColor: Color,
    val borderColor: Color
)

// Colors
private val PrimaryBlue = Color(0xFF5B7FFA)
private val TextGray = Color(0xFF6B7280)
private val SearchBarGrey = Color(0xFFEDEDED)
private val BackgroundGrey = Color(0xFFF5F5F5)

@Composable
fun FindProductsScreen(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onBottomNavClick: (String) -> Unit = {}
) {
    val categories = remember {
        listOf(
            CategoryItem(
                id = "fruits_veg",
                name = "Fresh Fruits\n& Vegetable",
                imageRes = R.drawable.img_carrots,
                backgroundColor = Color(0xFFE8F5E9),
                borderColor = Color(0xFF4CAF50)
            ),
            CategoryItem(
                id = "cooking_oil",
                name = "Cooking Oil\n& Ghee",
                imageRes = R.drawable.img_basmati_rice, // Fallback
                backgroundColor = Color(0xFFFFF8E1),
                borderColor = Color(0xFFFFC107)
            ),
            CategoryItem(
                id = "meat_fish",
                name = "Meat & Fish",
                imageRes = R.drawable.img_red_dhal, // Fallback
                backgroundColor = Color(0xFFFCE4EC),
                borderColor = Color(0xFFE91E63)
            ),
            CategoryItem(
                id = "bakery",
                name = "Bakery & Snacks",
                imageRes = R.drawable.img_cream_cracker,
                backgroundColor = Color(0xFFF3E5F5),
                borderColor = Color(0xFF9C27B0)
            ),
            CategoryItem(
                id = "dairy",
                name = "Dairy & Eggs",
                imageRes = R.drawable.img_curd,
                backgroundColor = Color(0xFFFFFDE7),
                borderColor = Color(0xFFFFC107)
            ),
            CategoryItem(
                id = "beverages",
                name = "Beverages",
                imageRes = R.drawable.img_mango_juice,
                backgroundColor = Color(0xFFE1F5FE),
                borderColor = Color(0xFF2196F3)
            )
        )
    }

    Scaffold(
        topBar = {
            FindProductsTopBar()
        },
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem("Shop", Icons.Outlined.Store, false),
                    BottomNavItem("Explore", Icons.Outlined.Search, true),
                    BottomNavItem("Cart", Icons.Outlined.ShoppingCart, false),
                    BottomNavItem("Favourite", Icons.Outlined.FavoriteBorder, false),
                    BottomNavItem("Account", Icons.Outlined.Person, false)
                ),
                onItemClick = { label ->
                    when (label) {
                        "Cart" -> onCartClick()
                        "Account" -> onProfileClick()
                    }
                    onBottomNavClick(label)
                }
            )
        },
        containerColor = BackgroundGrey
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            SearchBar(
                onSearchClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindProductsTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = "Find Products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = modifier
    )
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onSearchClick() },
        shape = RoundedCornerShape(30.dp),
        color = SearchBarGrey
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Search Store",
                fontSize = 16.sp,
                color = TextGray,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: CategoryItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, category.borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Category Image
            Image(
                painter = painterResource(id = category.imageRes),
                contentDescription = category.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Name
            Text(
                text = category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}
