package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun LoginSelectionScreen(
    onLoginClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp) // Increased height to accommodate the image better
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with styled text
            Text(
                text = buildAnnotatedString {
                    append("Get your groceries\nwith ")
                    withStyle(style = SpanStyle(color = Color(0xFF5B7FFF))) {
                        append("LankaSmartMart")
                    }
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Log in Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B7FFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Log in",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with text
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
                Text(
                    text = "Or connect with social media",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In Button
            OutlinedButton(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                // Google icon placeholder - you can add actual Google icon here
                Text(
                    text = "G",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Sign up with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Text
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                TextButton(
                    onClick = onSignUpClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5B7FFF)
                    )
                }
            }
        }
    }
}
