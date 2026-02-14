package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.viewmodel.ShopViewModel
import kotlinx.coroutines.delay

// Data Models
data class ProductItem(
    val id: Int,
    val name: String,
    val imageRes: Int,
    val rating: Float,
    val reviewCount: Int,
    val price: Int
)

data class PromoBannerItem(
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val backgroundColor: Color,
    val buttonColor: Color,
    val icon: ImageVector
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
private val BackgroundWhite = Color(0xFFFFFFFF)
private val PrimaryBlue = Color(0xFF5B7FFA)
private val PrimaryGreen = Color(0xFF4CAF50)
private val OrangeAccent = Color(0xFFFF9800)
private val StarYellow = Color(0xFFFFC107)
private val TextGray = Color(0xFF6B7280)
private val CardGrey = Color(0xFFF5F5F5)
private val BannerBlue = Color(0xFFD6E4FF)

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
    // Promotional banners data
    val promoBanners = remember {
        listOf(
            PromoBannerItem(
                title = "Up to 30% offer",
                subtitle = "Enjoy our big offer",
                buttonText = "Shop Now",
                backgroundColor = BannerBlue,
                buttonColor = PrimaryBlue,
                icon = Icons.Default.ShoppingBasket
            ),
            PromoBannerItem(
                title = "Up to 25% off",
                subtitle = "On first buyers",
                buttonText = "Buy Now",
                backgroundColor = Color(0xFFE8F5E9),
                buttonColor = PrimaryGreen,
                icon = Icons.Default.ShoppingBag
            ),
            PromoBannerItem(
                title = "Get Same day Deliver",
                subtitle = "On orders above \$20",
                buttonText = "Shop Now",
                backgroundColor = Color(0xFFFFF9C4),
                buttonColor = OrangeAccent,
                icon = Icons.Default.LocalShipping
            )
        )
    }

    // Product categories data
    val categories = remember {
        listOf(
            CategorySection(
                title = "Fruits",
                products = listOf(
                    ProductItem(1, "Banana", R.drawable.img_bananas, 4.8f, 287, 250),
                    ProductItem(2, "Pepper", R.drawable.img_carrots, 4.8f, 287, 420),
                    ProductItem(3, "Orange", R.drawable.img_papaya, 4.8f, 287, 200),
                    ProductItem(4, "Strawberry", R.drawable.img_tomatoes, 4.8f, 287, 500),
                    ProductItem(5, "Lemon", R.drawable.img_bananas, 4.8f, 287, 330),
                    ProductItem(6, "Water lemon", R.drawable.img_papaya, 4.8f, 287, 160),
                    ProductItem(7, "Apple", R.drawable.img_tomatoes, 4.8f, 287, 230),
                    ProductItem(8, "Mango", R.drawable.img_papaya, 4.8f, 287, 140),
                    ProductItem(9, "Grapes", R.drawable.img_bananas, 4.9f, 287, 290),
                    ProductItem(10, "Paw Paw", R.drawable.img_papaya, 4.9f, 287, 280)
                )
            ),
            CategorySection(
                title = "Detergent",
                products = listOf(
                    ProductItem(11, "Purex", R.drawable.img_rin_powder, 4.8f, 287, 390),
                    ProductItem(12, "Varnish", R.drawable.img_sunlight_dishwash, 4.8f, 287, 430),
                    ProductItem(13, "Harpic", R.drawable.img_rin_powder, 4.8f, 287, 350),
                    ProductItem(14, "Harpic", R.drawable.img_sunlight_dishwash, 4.8f, 287, 420),
                    ProductItem(15, "Purex", R.drawable.img_rin_powder, 4.8f, 287, 620),
                    ProductItem(16, "Dettol", R.drawable.img_sunlight_dishwash, 4.8f, 287, 530)
                )
            ),
            CategorySection(
                title = "Biscuit",
                products = listOf(
                    ProductItem(17, "Chocolate", R.drawable.img_cream_cracker, 4.8f, 287, 130),
                    ProductItem(18, "Milk Cream", R.drawable.img_coconut_chips, 4.8f, 287, 280),
                    ProductItem(19, "Kalo", R.drawable.img_cream_cracker, 4.8f, 287, 280),
                    ProductItem(20, "ChoShock", R.drawable.img_coconut_chips, 4.8f, 287, 290),
                    ProductItem(21, "Tiffin", R.drawable.img_cream_cracker, 4.8f, 287, 230),
                    ProductItem(22, "Chocolate Puff", R.drawable.img_coconut_chips, 4.8f, 287, 130)
                )
            )
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem("Shop", Icons.Outlined.Store, true),
                    BottomNavItem("Explore", Icons.Outlined.Search, false),
                    BottomNavItem("Cart", Icons.Outlined.ShoppingCart, false),
                    BottomNavItem("Favourite", Icons.Outlined.FavoriteBorder, false),
                    BottomNavItem("Account", Icons.Outlined.Person, false)
                ),
                onItemClick = { label ->
                    when (label) {
                        "Explore" -> onSearchClick()
                        "Cart" -> onCartClick()
                        "Account" -> onProfileClick()
                        "Favourite" -> {}
                    }
                    onBottomNavClick(label)
                }
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Centered Logo Header
            item {
                CenteredLogoHeader()
            }

            // Auto-changing Promotional Banner
            item {
                AutoChangingPromoBanner(
                    banners = promoBanners,
                    onShopNowClick = onShopNowClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Category Sections
            items(categories) { category ->
                Column {
                    SectionHeader(
                        title = category.title,
                        onSeeAllClick = { onSeeAllClick(category.title) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CenteredLogoHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Lanka SmartMart Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Brand Name
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = PrimaryGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Lanka ")
                }
                withStyle(
                    style = SpanStyle(
                        color = OrangeAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Smart")
                }
                withStyle(
                    style = SpanStyle(
                        color = OrangeAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Mart")
                }
            }
        )
    }
}

@Composable
fun AutoChangingPromoBanner(
    banners: List<PromoBannerItem>,
    modifier: Modifier = Modifier,
    onShopNowClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Change banner every 3 seconds
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            PromoBannerCard(
                banner = banners[page],
                onShopNowClick = onShopNowClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == pagerState.currentPage) PrimaryBlue
                            else Color.LightGray
                        )
                )
                if (index < banners.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun PromoBannerCard(
    banner: PromoBannerItem,
    modifier: Modifier = Modifier,
    onShopNowClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = banner.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = banner.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = banner.subtitle,
                    fontSize = 14.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onShopNowClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = banner.buttonColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = banner.buttonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // Right icon/image
            Icon(
                imageVector = banner.icon,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color.Black.copy(alpha = 0.08f)
            )
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
        modifier = modifier.fillMaxWidth(),
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
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardGrey
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                // Product image
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Add button
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .shadow(2.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product name
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rating
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

            Spacer(modifier = Modifier.height(6.dp))

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
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
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
            fontSize = 11.sp,
            color = if (item.isSelected) PrimaryBlue else TextGray,
            fontWeight = if (item.isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
