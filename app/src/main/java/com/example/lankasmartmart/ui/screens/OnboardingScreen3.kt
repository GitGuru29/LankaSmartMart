package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.ui.components.OnboardingProgressIndicator
import com.example.lankasmartmart.ui.components.OnboardingSkipButton

@Composable
fun OnboardingScreen3(
    onLetsGo: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button on top-right  
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            OnboardingSkipButton(onSkip = onSkip)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top curved section with illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 40.dp,
                            bottomEnd = 40.dp
                        )
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.onboarding_delivery),
                    contentDescription = "Grocery Delivery Illustration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress indicator
            OnboardingProgressIndicator(
                currentPage = 2,
                totalPages = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // "Your Daily Grocery Partner" text with green styling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Your Daily ")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF4CAF50), // Green color
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Grocery Partner")
                        }
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF212121)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Subtitle
                Text(
                    text = "Save time, shop smart, and get everything you need in just a few taps.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF757575),
                    lineHeight = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.weight(0.35f))
            
            // Lets go button
            Button(
                onClick = onLetsGo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 48.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32) // Dark green
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
