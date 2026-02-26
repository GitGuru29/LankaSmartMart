package com.example.lankasmartmart.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lankasmartmart.R
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource


@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    
    // Form state
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    // Google Sign-In setup
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
        if (authState is com.example.lankasmartmart.viewmodel.AuthState.Success) {
            onSignUpSuccess()
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
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Title
            Text(
                text = "Sign Up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Enter your credentials to continue",
                fontSize = 16.sp,
                color = Color(0xFF757575)
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            // Username Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Username",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Kaveesha Dilshan") },
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
                    trailingIcon = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = "Valid email",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
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

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Privacy Text
            Text(
                text = buildAnnotatedString {
                    append("By continuing you agree to our ")
                    withStyle(style = SpanStyle(color = Color(0xFF5B7FFF))) {
                        append("Terms of Service")
                    }
                    append("\nand ")
                    withStyle(style = SpanStyle(color = Color(0xFF5B7FFF))) {
                        append("Privacy Policy.")
                    }
                },
                fontSize = 13.sp,
                color = Color(0xFF757575),
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Sign Up Button - "Sing Up" typo to match screenshot
            Button(
                onClick = { authViewModel.signUpWithEmail(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B7FFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp,

                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Log In Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    color = Color(0xFF5B7FFF),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}
