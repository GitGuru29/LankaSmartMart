package com.example.lankasmartmart.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.ui.theme.DesignSystem
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToAuth: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    // Smooth breathing animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )
    
    // Fade in animation
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "fade_in"
    )
    
    // Slide up animation for content
    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "slide_up"
    )
    
    // Start animations and navigate
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) // 2.5 seconds splash
        onNavigateToAuth()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(alpha)
                .offset(y = offsetY.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with breathing animation
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(breathingScale),
                contentAlignment = Alignment.Center
            ) {
                // Background glow effect
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(1.2f)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
                
                // Logo (using emoji for now, will be replaced with actual logo)
                Text(
                    text = "🛒",
                    fontSize = 90.sp
                )
            }
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xl))
            
            // App Name with gradient text effect
            Text(
                text = "LankaSmartMart",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            
            // Tagline
            Text(
                text = "Your Smart Shopping Partner",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                letterSpacing = 0.3.sp
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.huge))
            
            // Modern loading indicator
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
        
        // Footer with fade-in
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = DesignSystem.Spacing.xxxl)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Made for Sri Lanka 🇱🇰",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.xs))
            
            Text(
                text = "v1.0.0",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

