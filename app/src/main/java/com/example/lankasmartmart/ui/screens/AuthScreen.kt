package com.example.lankasmartmart.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    var showToast by remember { mutableStateOf<String?>(null) }
    
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
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogle(account)
        } catch (e: ApiException) {
            val errorMessage = when (e.statusCode) {
                10 -> "Google Sign-In Developer Error (Code 10): SHA-1 fingerprint not configured in Firebase Console"
                12500 -> "Google Sign-In Error (Code 12500): Google Play Services issue or app not registered"
                12501 -> "User cancelled Google Sign-In"
                else -> "Google Sign-In failed: Error code ${e.statusCode}"
            }
            authViewModel.setErrorMessage(errorMessage)
        }
    }
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        } else if (authState is AuthState.Error) {
             showToast = (authState as AuthState.Error).message
        }
    }
    
    // Show Toast
    LaunchedEffect(showToast) {
        showToast?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            showToast = null
            authViewModel.resetAuthState() // Clear error after showing
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Logo - Centered in the start-aligned column
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.logo_large),
                    contentDescription = "Shopping Cart Logo",
                    modifier = Modifier.size(140.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Header Section - Left aligned
            Text(
                text = "Login",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            
            Text(
                text = "Enter your email and password",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )

            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("kaveesha7@gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                                contentDescription = "Toggle password visibility",
                                modifier = Modifier.size(20.dp),
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

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password Link
            Text(
                text = "Forgot Password?",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Handle forgot password */ }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Log In Button
            Button(
                onClick = { authViewModel.loginWithEmail(email, password) },
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
            
            // Sign Up Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "Sign Up",
                    fontSize = 14.sp,
                    color = Color(0xFF5B7FFF),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}
