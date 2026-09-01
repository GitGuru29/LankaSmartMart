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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onNavigateToOnboarding: () -> Unit
) {
    // Auto-navigate after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onNavigateToOnboarding()
    }

    // Animated progress for loading indicator
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2)), // Very light grey background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Spacer to push content to center
            Spacer(modifier = Modifier.weight(1f))

            // Logo - centered
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Lanka SmartMart Logo",
                modifier = Modifier
                    .size(280.dp)
            )

            // Spacer to push loading indicator to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Loading indicator at bottom
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 48.dp),
                color = Color(0xFF4CAF50), // Green color matching "Lanka"
                trackColor = Color(0xFFE0E0E0), // Light grey track
            )
        }
    }
}
