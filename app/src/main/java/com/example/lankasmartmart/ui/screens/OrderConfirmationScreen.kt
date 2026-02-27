package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.ui.theme.GroceryTextDark
import com.example.lankasmartmart.ui.theme.GroceryTextGrey

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    onGoToHome: () -> Unit = {},
    onTrackOrder: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Success Illustration with Accents
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background shapes (squiggles and circles from design)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Small circles
                    drawCircle(color = Color(0xFF53B175).copy(alpha = 0.6f), radius = 8f, center = Offset(40f, 60f))
                    drawCircle(color = Color(0xFFF8A44C).copy(alpha = 0.6f), radius = 10f, center = Offset(200f, 40f))
                    drawCircle(color = Color(0xFF5383EC).copy(alpha = 0.6f), radius = 8f, center = Offset(180f, 200f))
                    
                    // Outlined circles
                    drawCircle(
                        color = Color(0xFFF8A44C).copy(alpha = 0.4f),
                        radius = 12f,
                        center = Offset(60f, 180f),
                        style = Stroke(width = 4f)
                    )
                }

                // Main Success Circle
                Surface(
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    color = Color(0xFF5383EC).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = Color(0xFF5383EC)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Title
            Text(
                text = "Your Order has been\naccepted",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryTextDark,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle (keeping typo as per design "placcd")
            Text(
                text = "Your items has been placcd and is on\nit’s way to being processed",
                fontSize = 16.sp,
                color = GroceryTextGrey,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Bottom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = onTrackOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(67.dp),
                shape = RoundedCornerShape(19.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5383EC)
                )
            ) {
                Text(
                    text = "Track Order",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onGoToHome,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Back to home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GroceryTextDark
                )
            }
        }
    }
}

