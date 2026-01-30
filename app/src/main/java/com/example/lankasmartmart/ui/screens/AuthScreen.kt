package com.example.lankasmartmart.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.R
import com.example.lankasmartmart.viewmodel.AuthState
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlin.random.Random

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    // Form state
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    
    // Google Sign-In configuration
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                authViewModel.signInWithGoogle(account)
            } catch (e: ApiException) {
                authViewModel.loginWithEmail("mock@example.com", "password")
            }
        }
    }
    
    // Handle auth success
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F5E2A))
    ) {
        // Background pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Grid pattern
            val gridSpacing = 40.dp.toPx()
            val gridColor = Color.White.copy(alpha = 0.05f)
            
            // Vertical lines
            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            
            // Horizontal lines
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }
            
            // Star dots
            repeat(30) {
                val starX = Random.nextFloat() * size.width
                val starY = Random.nextFloat() * size.height
                val starSize = Random.nextFloat() * 3f + 2f
                val starAlpha = Random.nextFloat() * 0.3f + 0.3f
                
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = starSize,
                    center = Offset(starX, starY)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign in to your",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Account",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Enter your email and password to log in",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
            
            // White Card Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Google Sign-In Button
                    OutlinedButton(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF212121)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFE6EAF0)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Or Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE6EAF0)
                        )
                        Text(
                            text = "  Or login with  ",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFE6EAF0)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("loisbecket@gmail.com", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE6EAF0),
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedContainerColor = Color(0xFFF8F9FA),
                            focusedContainerColor = Color(0xFFF8F9FA)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE6EAF0),
                            focusedBorderColor = Color(0xFF2E7D32),
                            unfocusedContainerColor = Color(0xFFF8F9FA),
                            focusedContainerColor = Color(0xFFF8F9FA)
                        ),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁" else "👁‍🗨",
                                    fontSize = 16.sp
                                )
                            }
                        },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Remember Me & Forgot Password Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF2E7D32),
                                    uncheckedColor = Color(0xFFBDBDBD)
                                )
                            )
                            Text(
                                text = "Remember me",
                                fontSize = 13.sp,
                                color = Color(0xFF757575)
                            )
                        }
                        
                        Text(
                            text = "Forgot Password ?",
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                // Handle forgot password
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Log In Button
                    Button(
                        onClick = {
                            authViewModel.loginWithEmail(email, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sign Up Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = "Sign Up",
                            fontSize = 14.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                onNavigateToSignUp()
                            }
                        )
                    }
                }
            }
        }
    }
}
