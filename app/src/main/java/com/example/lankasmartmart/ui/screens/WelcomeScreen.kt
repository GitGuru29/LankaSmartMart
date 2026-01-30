package com.example.lankasmartmart.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lankasmartmart.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToAuth: () -> Unit = {}
) {
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    
    // Trigger animations
    LaunchedEffect(Unit) {
        delay(300) // Small delay before starting
        logoVisible = true
        delay(2500) // Show logo for 2.5 seconds
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
        // Logo with animations
        Image(
            painter = painterResource(id = R.drawable.logo_large),
            contentDescription = "LankaSmartMart Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .scale(logoScale)
                .graphicsLayer(alpha = logoAlpha)
        )
    }
}
