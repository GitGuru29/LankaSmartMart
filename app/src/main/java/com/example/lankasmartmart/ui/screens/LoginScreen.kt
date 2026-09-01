package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Background color updated to white
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start // Start alignment for content
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo - Centered in the start-aligned column
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.logo_large),
                    contentDescription = "Shopping Cart Logo",
                    modifier = Modifier.size(140.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Title - "Loging" typo to match screenshot
            Text(
                text = "Loging",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle - "emails" typo to match screenshot
            Text(
                text = "Enter your emails and password",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field - Switching to underlined style
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(bottom = 0.dp) // Adjusted padding
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("kaveesha7@gmail.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFFE0E0E0),
                        unfocusedIndicatorColor = Color(0xFFE0E0E0),
                        focusedLabelColor = Color.Transparent,
                        unfocusedLabelColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Password Field - Switching to underlined style
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(bottom = 0.dp) // Adjusted padding
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF9E9E9E)
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFFE0E0E0),
                        unfocusedIndicatorColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Forgot Password?",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log In Button
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B7FFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Link - Bottom centered
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
}
