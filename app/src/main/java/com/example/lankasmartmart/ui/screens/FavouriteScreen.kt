package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.R
import com.example.lankasmartmart.model.Product
import com.example.lankasmartmart.viewmodel.ShopViewModel

// Data model for favorite items
data class FavouriteItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val imageRes: Int,
    val category: String
)

@Composable
fun FavouriteScreen(
    modifier: Modifier = Modifier,
    shopViewModel: ShopViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onBottomNavClick: (String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val favouriteItems = remember {
        listOf(
            FavouriteItem("1", "Sprite Can", "325ml, Price", 160, R.drawable.img_sprite, "beverages"),
            FavouriteItem("2", "Diet Coke", "355ml, Price", 200, R.drawable.img_diet_coke, "beverages"),
            FavouriteItem("3", "Apple & Grape Juice", "2L, Price", 790, R.drawable.img_apple_grape_juice, "beverages"),
            FavouriteItem("4", "Coca Cola Can", "325ml, Price", 280, R.drawable.img_coke_can, "beverages"),
            FavouriteItem("5", "Pepsi Can", "330ml, Price", 210, R.drawable.img_pepsi_can, "beverages")
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Favourite",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E2E2), thickness = 1.dp)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Button(
                        onClick = {
                            favouriteItems.forEach { item ->
                                val resName = context.resources.getResourceEntryName(item.imageRes)
                                val product = Product(
                                    id = "fav_${item.id}",
                                    name = item.name,
                                    description = item.description,
                                    price = item.price.toDouble(),
                                    category = item.category,
                                    imageUrl = "android.resource://com.example.lankasmartmart/drawable/$resName"
                                )
                                shopViewModel.addToCart(product, 1)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5B7FFA)
                        )
                    ) {
                        Text(
                            text = "Add All To Cart",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                BottomNavBar(
                    items = listOf(
                        BottomNavItem("Shop", Icons.Outlined.Store, false),
                        BottomNavItem("Explore", Icons.Outlined.Search, false),
                        BottomNavItem("Cart", Icons.Outlined.ShoppingCart, false),
                        BottomNavItem("Favourite", Icons.Outlined.FavoriteBorder, true),
                        BottomNavItem("Account", Icons.Outlined.Person, false)
                    ),
                    onItemClick = onBottomNavClick
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(favouriteItems) { item ->
                FavouriteItemRow(item = item)
                HorizontalDivider(
                    color = Color(0xFFE2E2E2),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }
        }
    }
}

@Composable
fun FavouriteItemRow(item: FavouriteItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        
        Spacer(modifier = Modifier.width(20.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color(0xFF7C7C7C)
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LKR ${item.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Black
            )
        }
    }
}
