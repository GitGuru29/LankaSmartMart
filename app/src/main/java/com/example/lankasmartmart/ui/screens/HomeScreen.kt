package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.viewmodel.ShopViewModel
import androidx.compose.ui.platform.LocalContext

// Data Models
data class ProductItem(
    val id: Int,
    val name: String,
    val imageRes: Int?,
    val rating: Float,
    val reviewCount: Int,
    val price: Int,
    val fallbackColor: Color = Color(0xFFF5F5F5)
)

data class CategorySection(
    val title: String,
    val products: List<ProductItem>
)

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val isSelected: Boolean = false
)

// Colors
private val PrimaryBlue = Color(0xFF3F7BFA)
private val LightBannerBlue = Color(0xFFE6EEFF)
private val BannerBlueBackground = Color(0xFFD6E4FF)
private val TextGray = Color(0xFF6B7280)
private val GreenPrimary = Color(0xFF4CAF50)
private val OrangeAccent = Color(0xFFFF9800)
private val StarYellow = Color(0xFFFFC107)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    shopViewModel: ShopViewModel? = null,
    onCategoryClick: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onProductClick: (ProductItem) -> Unit = {},
    onAddToCart: (ProductItem) -> Unit = {},
    onSeeAllClick: (String) -> Unit = {},
    onShopNowClick: () -> Unit = {},
    onBottomNavClick: (String) -> Unit = {}
) {
    val categories = remember {
        listOf(
            CategorySection(
                title = "Fruits",
                products = listOf(
                    ProductItem(
                        id = 1,
                        name = "Banana",
                        imageRes = null,
                        rating = 4.8f,
                        reviewCount = 287,
                        price = 250,
                        fallbackColor = Color(0xFFFFF9E6)
                    ),
                    ProductItem(
                        id = 2,
                        name = "Pepper",
                        imageRes = null,
                        rating = 4.8f,
                        reviewCount = 287,
                        price = 420,
                        fallbackColor = Color(0xFFFFE6E6)
                    )
                )
            ),
            CategorySection(
                title = "Detergent",
                products = listOf()
            )
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem("Shop", Icons.Default.Store, true),
                    BottomNavItem("Explore", Icons.Default.Search, false),
                    BottomNavItem("Cart", Icons.Default.ShoppingCart, false),
                    BottomNavItem("Favourite", Icons.Default.Favorite, false),
                    BottomNavItem("Account", Icons.Default.Person, false)
                ),
                onItemClick = { label ->
                    when (label) {
                        "Explore" -> onSearchClick()
                        "Cart" -> onCartClick()
                        "Account" -> onProfileClick()
                    }
                    onBottomNavClick(label)
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TopLogoHeader()
            }

            item {
                PromoBanner(
                    onShopNowClick = onShopNowClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(categories) { category ->
                Column {
                    SectionHeader(
                        title = category.title,
                        onSeeAllClick = { onSeeAllClick(category.title) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (category.products.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(category.products) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { onProductClick(product) },
                                    onAddClick = { onAddToCart(product) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TopLogoHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo placeholder - can be replaced with painterResource(R.drawable.logo)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp),
                tint = GreenPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lanka ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Text(
                text = "Smart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OrangeAccent
            )
            Text(
                text = "Mart",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
        }
    }
}

@Composable
fun PromoBanner(
    modifier: Modifier = Modifier,
    onShopNowClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BannerBlueBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Up to 30% offer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Enjoy our big offer",
                    fontSize = 14.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onShopNowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Shop Now",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Right image placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBasket,
                    contentDescription = "Grocery basket",
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "See all",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PrimaryBlue,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
fun ProductCard(
    product: ProductItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Product image with add button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Product image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(product.fallbackColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageRes != null) {
                        // Use actual image if available
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = product.name,
                            modifier = Modifier.size(50.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = product.name,
                            modifier = Modifier.size(50.dp),
                            tint = Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }

                // Add button
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape
                        )
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product name
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = StarYellow,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = "${product.rating} (${product.reviewCount})",
                    fontSize = 12.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = "LKR ${product.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                BottomNavItemView(
                    item = item,
                    onClick = { onItemClick(item.label) }
                )
            }
        }
    }
}

@Composable
fun BottomNavItemView(
    item: BottomNavItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (item.isSelected) PrimaryBlue else TextGray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.label,
            fontSize = 12.sp,
            color = if (item.isSelected) PrimaryBlue else TextGray,
            fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}
