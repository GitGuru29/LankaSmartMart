package com.example.lankasmartmart.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        startAnimation = true
        delay(2800) // Show for ~3 seconds total
        onNavigateToAuth()
    }

    // Logo fade and scale animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )

    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_scale"
    )

    // Progress bar animation
    val progressAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 600),
        label = "progress_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Centered Logo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_large),
                contentDescription = "LankaSmartMart Logo",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .graphicsLayer(
                        alpha = logoAlpha,
                        scaleX = logoScale,
                        scaleY = logoScale
                    )
            )
        }

        // Progress Bar at Bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .graphicsLayer(alpha = progressAlpha)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(220.dp)
                    .height(3.5.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE8E8E8)
            )
        }
    }
}

