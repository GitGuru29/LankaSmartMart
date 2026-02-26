package com.example.lankasmartmart

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.ui.screens.CartScreen
import com.example.lankasmartmart.ui.screens.HomeScreen
import com.example.lankasmartmart.ui.screens.ProductDetailsScreen
import com.example.lankasmartmart.ui.screens.ProductListScreen
import com.example.lankasmartmart.ui.screens.ProfileScreen
import com.example.lankasmartmart.ui.screens.SearchScreen
import com.example.lankasmartmart.ui.screens.FindProductsScreen
import com.example.lankasmartmart.ui.screens.FavouriteScreen
import com.example.lankasmartmart.ui.screens.LoadingScreen
import com.example.lankasmartmart.ui.screens.OnboardingScreen1
import com.example.lankasmartmart.ui.screens.OnboardingScreen2
import com.example.lankasmartmart.ui.screens.OnboardingScreen3
import com.example.lankasmartmart.ui.screens.LoginSelectionScreen
import com.example.lankasmartmart.ui.screens.LoginScreen
import com.example.lankasmartmart.ui.screens.SignUpScreen
import com.example.lankasmartmart.ui.screens.PersonalInfoScreen
import com.example.lankasmartmart.ui.screens.AddressesScreen
import com.example.lankasmartmart.ui.screens.MapAddressPickerScreen
import com.example.lankasmartmart.ui.screens.CheckoutScreen
import com.example.lankasmartmart.ui.screens.PaymentMethodScreen
import com.example.lankasmartmart.ui.screens.OrderConfirmationScreen
import com.example.lankasmartmart.ui.screens.OrderHistoryScreen
import com.example.lankasmartmart.ui.screens.OrderDetailsScreen
import com.example.lankasmartmart.ui.screens.TrackOrderScreen
import com.example.lankasmartmart.ui.screens.HelpSupportScreen
import com.example.lankasmartmart.ui.screens.PaymentMethodsScreen
import com.example.lankasmartmart.ui.screens.NotificationsScreen
import com.example.lankasmartmart.ui.screens.LanguageScreen
import com.example.lankasmartmart.ui.theme.LankaSmartMartTheme
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.example.lankasmartmart.utils.ShakeDetector
import com.example.lankasmartmart.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Hide system navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.apply {
            hide(WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        setContent {
            LankaSmartMartTheme {
                LankaSmartMartApp()
            }
        }
    }
}

sealed class Screen {
    object Loading : Screen()
    object Onboarding1 : Screen()
    object Onboarding2 : Screen()
    object Onboarding3 : Screen()
    object LoginSelection : Screen()
    object Login : Screen()
    object SignUp : Screen()
    object Home : Screen()
    object Cart : Screen()
    object Search : Screen()
    object FindProducts : Screen()
    object Profile : Screen()
    object PersonalInfo : Screen()
    object Addresses : Screen()
    object AddNewAddress : Screen()
    data class EditAddress(val address: com.example.lankasmartmart.model.Address) : Screen()
    object Checkout : Screen()
    data class PaymentMethod(val address: com.example.lankasmartmart.model.Address) : Screen()
    data class OrderConfirmation(val orderId: String) : Screen()
    object OrderHistory : Screen()
    data class OrderDetails(val order: com.example.lankasmartmart.model.Order) : Screen()
    data class TrackOrder(val order: com.example.lankasmartmart.model.Order) : Screen()
    object TrackOrderGeneric : Screen()
    object PaymentMethods : Screen()
    object Notifications : Screen()
    object Language : Screen()
    object HelpSupport : Screen()
    object Favourite : Screen()
    data class ProductList(val categoryId: String, val categoryName: String) : Screen()
    data class ProductDetails(val productId: String) : Screen()
}

@Composable
fun LankaSmartMartApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Loading) }
    val shopViewModel: ShopViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    // ── Shake-to-Cart Gesture ──────────────────────────────────────────────
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val shakeDetector = ShakeDetector(context) {
            // Only navigate to cart if user is past auth screens
            val isOnMainScreen = currentScreen is Screen.Home ||
                    currentScreen is Screen.Profile ||
                    currentScreen is Screen.FindProducts ||
                    currentScreen is Screen.Search ||
                    currentScreen is Screen.Cart ||
                    currentScreen is Screen.ProductList ||
                    currentScreen is Screen.Favourite ||
                    currentScreen is Screen.ProductDetails

            if (isOnMainScreen && currentScreen !is Screen.Cart) {
                currentScreen = Screen.Cart
                Toast.makeText(context, "🛒 Shake detected! Opening Cart...", Toast.LENGTH_SHORT).show()
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> shakeDetector.start()
                Lifecycle.Event.ON_PAUSE -> shakeDetector.stop()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            shakeDetector.stop()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // ── Bottom Navigation Click Handler ──────────────────────────────────────
    val handleBottomNavClick: (String) -> Unit = { label ->
        when (label) {
            "Shop" -> currentScreen = Screen.Home
            "Explore" -> currentScreen = Screen.FindProducts
            "Cart" -> currentScreen = Screen.Cart
            "Favourite" -> currentScreen = Screen.Favourite
            "Account" -> currentScreen = Screen.Profile
        }
    }

    when (val screen = currentScreen) {
        is Screen.Loading -> {
            LoadingScreen(
                onNavigateToOnboarding = {
                    currentScreen = Screen.Onboarding1
                }
            )
        }
        is Screen.Onboarding1 -> {
            OnboardingScreen1(
                onGetStarted = {
                    currentScreen = Screen.Onboarding2
                },
                onSkip = {
                    currentScreen = Screen.Home
                }
            )
        }
        is Screen.Onboarding2 -> {
            OnboardingScreen2(
                onNext = {
                    currentScreen = Screen.Onboarding3
                }
            )
        }
        is Screen.Onboarding3 -> {
            OnboardingScreen3(
                onLetsGo = {
                    currentScreen = Screen.LoginSelection
                }
            )
        }
        is Screen.LoginSelection -> {
            LoginSelectionScreen(
                onLoginClick = {
                    currentScreen = Screen.Login
                },
                onGoogleSignInClick = {
                    currentScreen = Screen.Home
                },
                onSignUpClick = {
                    currentScreen = Screen.SignUp
                }
            )
        }
        is Screen.Login -> {
            LoginScreen(
                onLoginClick = { email, password ->
                    // Handle login logic here
                    currentScreen = Screen.Home
                },
                onSignUpClick = {
                    currentScreen = Screen.SignUp
                },
                onForgotPasswordClick = {
                    // Handle forgot password
                }
            )
        }
        is Screen.SignUp -> {
            SignUpScreen(
                onSignUpClick = { name, email, password ->
                    // Handle sign up logic here
                    currentScreen = Screen.Home
                },
                onSignInClick = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Home -> {
            HomeScreen(
                shopViewModel = shopViewModel,
                onCategoryClick = { categoryId ->
                    val category = shopViewModel.categories.value.find { it.id == categoryId }
                    currentScreen = Screen.ProductList(
                        categoryId = categoryId,
                        categoryName = category?.name ?: "Products"
                    )
                },
                onSearchClick = {
                    currentScreen = Screen.FindProducts
                },
                onCartClick = {
                    currentScreen = Screen.Cart
                },
                onProfileClick = {
                    currentScreen = Screen.Profile
                },
                onProductClick = { productItem ->
                    currentScreen = Screen.ProductDetails(productItem.id.toString())
                },
                onAddToCart = { productItem ->
                    // Map ProductItem to Product for ViewModel
                    val product = shopViewModel.products.value.find { it.id == productItem.id.toString() }
                    if (product != null) {
                        shopViewModel.addToCart(product, 1)
                        Toast.makeText(context, "Added ${product.name} to cart", Toast.LENGTH_SHORT).show()
                    }
                },
                onBottomNavClick = handleBottomNavClick
            )
        }
        is Screen.Cart -> {
            CartScreen(
                shopViewModel = shopViewModel,
                onBackClick = {
                    currentScreen = Screen.Home
                },
                onCheckoutClick = {
                    currentScreen = Screen.Checkout
                }
            )
        }
        is Screen.Search -> {
            SearchScreen(
                shopViewModel = shopViewModel,
                onBackClick = {
                    currentScreen = Screen.Home
                },
                onProductClick = { product ->
                    currentScreen = Screen.ProductDetails(product.id)
                }
            )
        }
        is Screen.FindProducts -> {
            FindProductsScreen(
                onCategoryClick = { categoryId, categoryName ->
                    currentScreen = Screen.ProductList(
                        categoryId = categoryId,
                        categoryName = categoryName
                    )
                },
                onSearchClick = {
                    currentScreen = Screen.Search
                },
                onCartClick = {
                    currentScreen = Screen.Cart
                },
                onProfileClick = {
                    currentScreen = Screen.Profile
                },
                onBottomNavClick = handleBottomNavClick
            )
        }
        is Screen.Favourite -> {
            FavouriteScreen(
                shopViewModel = shopViewModel,
                onBottomNavClick = handleBottomNavClick
            )
        }
        is Screen.ProductList -> {
            ProductListScreen(
                categoryId = screen.categoryId,
                categoryName = screen.categoryName,
                shopViewModel = shopViewModel,
                onBackClick = {
                    currentScreen = Screen.Home
                },
                onProductClick = { product ->
                    currentScreen = Screen.ProductDetails(product.id)
                }
            )
        }
        is Screen.ProductDetails -> {
            ProductDetailsScreen(
                productId = screen.productId,
                shopViewModel = shopViewModel,
                onBackClick = {
                    // Go back to previous screen (would be ProductList typically)
                    currentScreen = Screen.Home // Simplified: go to home
                },
                onCartClick = {
                    currentScreen = Screen.Cart
                }
            )
        }
        is Screen.Profile -> {
            ProfileScreen(
                authViewModel = authViewModel,
                onBackClick = { currentScreen = Screen.Home },
                onLogout = {
                    // Stay on home/profile or go back to welcome?
                    // Setting to Home for now as Auth is removed.
                    currentScreen = Screen.Home
                },
                onNavigateToPersonalInfo = { currentScreen = Screen.PersonalInfo },
                onNavigateToAddresses = { currentScreen = Screen.Addresses },
                onNavigateToPaymentMethods = { currentScreen = Screen.PaymentMethods },
                onNavigateToOrderHistory = { currentScreen = Screen.OrderHistory },
                onNavigateToTrackOrder = { currentScreen = Screen.TrackOrderGeneric },
                onNavigateToNotifications = { currentScreen = Screen.Notifications },
                onNavigateToLanguage = { currentScreen = Screen.Language },
                onNavigateToHelpSupport = { currentScreen = Screen.HelpSupport }
            )
        }
        is Screen.PersonalInfo -> {
            PersonalInfoScreen(
                authViewModel = authViewModel,
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
        is Screen.Addresses -> {
            AddressesScreen(
                onBackClick = { currentScreen = Screen.Profile },
                onAddAddressClick = { currentScreen = Screen.AddNewAddress },
                onEditAddressClick = { address -> currentScreen = Screen.EditAddress(address) }
            )
        }
        is Screen.AddNewAddress -> {
            MapAddressPickerScreen(
                onBackClick = { currentScreen = Screen.Addresses },
                onAddressSaved = { currentScreen = Screen.Addresses }
            )
        }
        is Screen.EditAddress -> {
            MapAddressPickerScreen(
                addressToEdit = screen.address,
                onBackClick = { currentScreen = Screen.Addresses },
                onAddressSaved = { currentScreen = Screen.Addresses }
            )
        }
        is Screen.Checkout -> {
            CheckoutScreen(
                onBackClick = { currentScreen = Screen.Cart },
                onAddAddressClick = {  currentScreen = Screen.AddNewAddress },
                onProceedToPayment = { address ->
                    currentScreen = Screen.PaymentMethod(address)
                },
                shopViewModel = shopViewModel
            )
        }
        is Screen.PaymentMethod -> {
            PaymentMethodScreen(
                address = screen.address,
                onBackClick = { currentScreen = Screen.Checkout },
                onOrderPlaced = { orderId ->
                    currentScreen = Screen.OrderConfirmation(orderId)
                },
                shopViewModel = shopViewModel
            )
        }
        is Screen.OrderConfirmation -> {
            OrderConfirmationScreen(
                orderId = screen.orderId,
                onGoToHome = { currentScreen = Screen.Home },
                onTrackOrder = { currentScreen = Screen.TrackOrderGeneric }
            )
        }
        is Screen.OrderHistory -> {
            OrderHistoryScreen(
                onBackClick = { currentScreen = Screen.Profile },
                onOrderClick = { order ->
                    currentScreen = Screen.OrderDetails(order)
                }
            )
        }
        is Screen.OrderDetails -> {
            OrderDetailsScreen(
                order = screen.order,
                onBackClick = { currentScreen = Screen.OrderHistory },
                onTrackOrder = { currentScreen = Screen.TrackOrder(screen.order) }
            )
        }
        is Screen.TrackOrder -> {
            TrackOrderScreen(
                order = screen.order,
                onBackClick = { currentScreen = Screen.OrderDetails(screen.order) }
            )
        }
        is Screen.HelpSupport -> {
            HelpSupportScreen(
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
        is Screen.PaymentMethods -> {
            PaymentMethodsScreen(
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
        is Screen.TrackOrderGeneric -> {
            // Generic track order - show message to go to order history
            HelpSupportScreen(
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
        is Screen.Notifications -> {
            NotificationsScreen(
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
        is Screen.Language -> {
            LanguageScreen(
                onBackClick = { currentScreen = Screen.Profile }
            )
        }
    }
}