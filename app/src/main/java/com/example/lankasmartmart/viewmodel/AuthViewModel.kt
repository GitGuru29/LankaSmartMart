package com.example.lankasmartmart.viewmodel

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserData(
    val name: String = "",
    val email: String = "",
    val phone: String = ""
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    init {
        // Check if user is already signed in
        currentUser?.let {
            _authState.value = AuthState.Success(it)
        }
    }
    
    // Email/Password Sign Up
    fun signUpWithEmail(email: String, password: String, userData: UserData) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                
                // Save user data to Firestore
                result.user?.let { user ->
                    saveUserToFirestore(user.uid, userData.copy(email = email))
                    _authState.value = AuthState.Success(user)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }
    
    // Email/Password Login
    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let {
                    _authState.value = AuthState.Success(it)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }
    
    // Google Sign-In
    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                println("🔐 Starting Google Sign-In for: ${account.email}")
                _authState.value = AuthState.Loading
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()
                
                println("🔐 Firebase sign-in successful!")
                
                // Set auth state to Success immediately
                result.user?.let { user ->
                    _authState.value = AuthState.Success(user)
                    println("🔐 Auth state set to Success")
                    
                    // Try to save user data to Firestore (non-blocking)
                    try {
                        val userData = UserData(
                            name = user.displayName ?: "",
                            email = user.email ?: "",
                            phone = ""
                        )
                        saveUserToFirestore(user.uid, userData)
                    } catch (e: Exception) {
                        println("🔐 Firestore save failed (non-blocking): ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("🔐 Google Sign-In error: ${e.message}")
                e.printStackTrace()
                _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }
    
    // Sign Out
    fun signOut(googleSignInClient: com.google.android.gms.auth.api.signin.GoogleSignInClient? = null) {
        auth.signOut()
        googleSignInClient?.signOut() // Sign out of Google as well
        _authState.value = AuthState.Idle
    }
    
    // Save user data to Firestore
    private suspend fun saveUserToFirestore(uid: String, userData: UserData) {
        try {
            val userMap = hashMapOf(
                "name" to userData.name,
                "email" to userData.email,
                "phone" to userData.phone,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(userMap).await()
        } catch (e: Exception) {
            // Log error but don't fail authentication
            println("Error saving user to Firestore: ${e.message}")
        }
    }
    
    // Reset auth state
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}
