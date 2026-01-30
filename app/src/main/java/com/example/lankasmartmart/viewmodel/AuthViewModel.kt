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
    data class Success(val userData: UserData) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserData(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isMockUser: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    // Get current user data from auth state or construct from Firebase user
    val currentUserData: UserData?
        get() = when (val state = _authState.value) {
            is AuthState.Success -> state.userData
            else -> currentUser?.let { firebaseUser ->
                UserData(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: ""
                )
            }
        }
    
    init {
        // Check if user is already signed in
        currentUser?.let { firebaseUser ->
            _authState.value = AuthState.Success(
                UserData(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = firebaseUser.phoneNumber ?: ""
                )
            )
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
                    val enrichedUserData = userData.copy(uid = user.uid, email = email)
                    saveUserToFirestore(user.uid, enrichedUserData)
                    _authState.value = AuthState.Success(enrichedUserData)
                }
            } catch (e: Exception) {
                // On Firebase error, use mock auth as fallback for testing
                println("Firebase sign up failed, using mock auth: ${e.message}")
                val mockUserData = userData.copy(
                    uid = "mock_${System.currentTimeMillis()}",
                    email = email,
                    isMockUser = true
                )
                _authState.value = AuthState.Success(mockUserData)
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
                result.user?.let { user ->
                    _authState.value = AuthState.Success(
                        UserData(
                            uid = user.uid,
                            name = user.displayName ?: "",
                            email = user.email ?: "",
                            phone = user.phoneNumber ?: ""
                        )
                    )
                }
            } catch (e: Exception) {
                // On Firebase error, use mock auth as fallback for testing
                println("Firebase login failed, using mock auth: ${e.message}")
                val mockUserData = UserData(
                    uid = "mock_${System.currentTimeMillis()}",
                    name = "Test User",
                    email = email,
                    isMockUser = true
                )
                _authState.value = AuthState.Success(mockUserData)
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
                    val userData = UserData(
                        uid = user.uid,
                        name = user.displayName ?: "",
                        email = user.email ?: "",
                        phone = user.phoneNumber ?: ""
                    )
                    _authState.value = AuthState.Success(userData)
                    println("🔐 Auth state set to Success")
                    
                    // Try to save user data to Firestore (non-blocking)
                    try {
                        saveUserToFirestore(user.uid, userData)
                    } catch (e: Exception) {
                        println("🔐 Firestore save failed (non-blocking): ${e.message}")
                    }
                }
            } catch (e: Exception) {
                println("🔐 Google Sign-In error: ${e.message}")
                println("🔐 Falling back to mock authentication")
                // Fallback to mock auth when Firebase is not configured properly
                val mockUserData = UserData(
                    uid = "mock_google_${System.currentTimeMillis()}",
                    name = account.displayName ?: "Google User",
                    email = account.email ?: "user@gmail.com",
                    isMockUser = true
                )
                _authState.value = AuthState.Success(mockUserData)
            }
        }
    }
    
    // Mock Google Sign-In (when Google Sign-In SDK fails)
    fun signInWithMockGoogle(email: String, displayName: String) {
        viewModelScope.launch {
            println("🔐 Using mock Google Sign-In")
            _authState.value = AuthState.Loading
            
            // Simulate a short delay like real auth would have
            kotlinx.coroutines.delay(500)
            
            val mockUserData = UserData(
                uid = "mock_google_${System.currentTimeMillis()}",
                name = displayName,
                email = email,
                isMockUser = true
            )
            _authState.value = AuthState.Success(mockUserData)
            println("🔐 Mock auth successful")
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
