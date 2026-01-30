package com.example.lankasmartmart.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToAuth: () -> Unit = {}
) {
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    
    // Trigger animations
    LaunchedEffect(Unit) {
        delay(300) // Small delay before starting
        logoVisible = true
        delay(500) // Delay before showing text
        textVisible = true
        delay(3000) // Show content for 3 seconds
        onNavigateToAuth()
    }
    
    // Scale animation for logo
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    // Alpha animation for logo
    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )
    
    // Alpha animation for text
    val textAlpha by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "text_alpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color.White,
                        Color(0xFFF8F9FA)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with animations
            Image(
                painter = painterResource(id = R.drawable.logo_large),
                contentDescription = "LankaSmartMart Logo",
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .scale(logoScale)
                    .graphicsLayer(alpha = logoAlpha)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name with fade-in
            Text(
                text = "LankaSmartMart",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline with fade-in
            Text(
                text = "Fresh to Your Door",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF757575),
                modifier = Modifier.graphicsLayer(alpha = textAlpha)
            )
        }
    }
}
