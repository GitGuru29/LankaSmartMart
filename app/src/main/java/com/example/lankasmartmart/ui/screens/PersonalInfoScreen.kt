package com.example.lankasmartmart.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.lankasmartmart.utils.extractName
import com.example.lankasmartmart.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun PersonalInfoScreen(
    authViewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUserData = authViewModel.currentUserData
    val scope = rememberCoroutineScope()
    
    // Initialize Cloudinary once
    LaunchedEffect(Unit) {
        try {
            val config = hashMapOf(
                "cloud_name" to "dqyilatyp",
                "api_key" to "783769987877648",
                "api_secret" to "OyCHobStXYXvp0LWOhaaRfMhLUw"
            )
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // Already initialized
        }
    }
    
    var name by remember { mutableStateOf(currentUserData?.name?.extractName() ?: "") }
    var email by remember { mutableStateOf(currentUserData?.email ?: "") }
    var phone by remember { mutableStateOf(currentUserData?.phone ?: "") }
    var isEditing by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            isUploading = true
            
            // Upload to Cloudinary
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user"
            
            MediaManager.get().upload(it)
                .option("public_id", "lankasmart/users/$userId/profile")
                .option("folder", "lankasmart/users/$userId")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        scope.launch(Dispatchers.Main) {
                            val photoUrl = resultData["secure_url"] as? String
                            profileImageUrl = photoUrl
                            isUploading = false
                            
                            // Update Firebase Auth profile with new photo URL
                            photoUrl?.let { url ->
                                try {
                                    val user = FirebaseAuth.getInstance().currentUser
                                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                        .setPhotoUri(android.net.Uri.parse(url))
                                        .build()
                                    
                                    user?.updateProfile(profileUpdates)?.await()
                                    Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Photo uploaded, but profile update failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    
                    override fun onError(requestId: String, error: ErrorInfo) {
                        scope.launch(Dispatchers.Main) {
                            isUploading = false
                            Toast.makeText(context, "Upload failed: ${error.description}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    
                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
    }
    
    Scaffold(
        containerColor = Color(0xFFF8FFFE),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            PersonalInfoTopBar(onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Box(
                modifier = Modifier.padding(vertical = 16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(8.dp, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            Color(0xFF004D40)
                                        )
                                    )
                                )
                        ) {
                            Text(
                                text = name.firstOrNull()?.uppercase() ?: "U",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    
                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                }
                
                // Camera icon badge
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { imagePickerLauncher.launch("image/*") }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Text(
                text = "Tap to change profile picture",
                fontSize = 13.sp,
                color = Color(0xFF757575)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Name Field
            InfoField(
                label = "Full Name",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Default.Person,
                enabled = isEditing
            )
            
            // Email Field
            InfoField(
                label = "Email",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Default.Email,
                enabled = false // Can't change email
            )
            
            // Phone Field
            InfoField(
                label = "Phone Number",
                value = phone,
                onValueChange = { phone = it },
                icon = Icons.Default.Phone,
                enabled = isEditing
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Edit/Save Button
            Button(
                onClick = {
                    if (isEditing) {
                        // TODO: Save to Firestore
                        isEditing = false
                        Toast.makeText(context, "Changes saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Edit Profile",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PersonalInfoTopBar(onBackClick: () -> Unit) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
            .padding(top = statusBarHeight)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Personal Information",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun InfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}
