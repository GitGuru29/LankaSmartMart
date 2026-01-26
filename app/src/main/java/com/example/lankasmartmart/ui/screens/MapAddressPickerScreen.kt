package com.example.lankasmartmart.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Address
import com.example.lankasmartmart.viewmodel.AddressViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapAddressPickerScreen(
    addressToEdit: Address? = null,
    onBackClick: () -> Unit = {},
    onAddressSaved: () -> Unit = {},
    addressViewModel: AddressViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Address details
    var name by remember { mutableStateOf(addressToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(addressToEdit?.phone ?: "") }
    var isDefault by remember { mutableStateOf(addressToEdit?.isDefault ?: false) }
    
    // Map state
    var selectedLocation by remember {
        mutableStateOf(
            if (addressToEdit != null && addressToEdit.latitude != 0.0) {
                LatLng(addressToEdit.latitude, addressToEdit.longitude)
            } else {
                LatLng(6.9271, 79.8612) // Colombo center as default
            }
        )
    }
    
    // Address from geocoding
    var addressLine1 by remember { mutableStateOf(addressToEdit?.addressLine1 ?: "") }
    var addressLine2 by remember { mutableStateOf(addressToEdit?.addressLine2 ?: "") }
    var city by remember { mutableStateOf(addressToEdit?.city ?: "") }
    var postalCode by remember { mutableStateOf(addressToEdit?.postalCode ?: "") }
    
    var isLoading by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
    }
    
    // Geocoder function
    fun geocodeLocation(latLng: LatLng) {
        scope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    addressLine1 = address.getAddressLine(0) ?: ""
                    city = address.locality ?: address.subAdminArea ?: ""
                    postalCode = address.postalCode ?: ""
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to get address", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Get current location
            @SuppressLint("MissingPermission")
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    selectedLocation = currentLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                    geocodeLocation(currentLatLng)
                }
            }
        }
    }
    
    // Details dialog
    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Address Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name (Home, Work, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it }
                        )
                        Text("Set as default address")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            name.isBlank() -> {
                                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                            }
                            phone.isBlank() -> {
                                Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                            }
                            addressLine1.isBlank() -> {
                                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val address = Address(
                                    id = addressToEdit?.id ?: "",
                                    userId = addressToEdit?.userId ?: "",
                                    name = name,
                                    phone = phone,
                                    addressLine1 = addressLine1,
                                    addressLine2 = addressLine2,
                                    city = city,
                                    postalCode = postalCode,
                                    latitude = selectedLocation.latitude,
                                    longitude = selectedLocation.longitude,
                                    isDefault = isDefault
                                )
                                
                                if (addressToEdit == null) {
                                    addressViewModel.addAddress(
                                        address,
                                        onSuccess = {
                                            Toast.makeText(context, "Address saved!", Toast.LENGTH_SHORT).show()
                                            onAddressSaved()
                                        },
                                        onError = {
                                            Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    addressViewModel.updateAddress(
                                        address,
                                        onSuccess = {
                                            Toast.makeText(context, "Address updated!", Toast.LENGTH_SHORT).show()
                                            onAddressSaved()
                                        },
                                        onError = {
                                            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (addressToEdit == null) "Pick Location" else "Edit Location",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    geocodeLocation(latLng)
                }
            ) {
                Marker(
                    state = MarkerState(position = selectedLocation),
                    title = "Delivery Location",
                    snippet = "Drag to adjust"
                )
            }
            
            // Current location FAB
            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                containerColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Current Location",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Address card at bottom
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selected Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (addressLine1.isNotEmpty()) {
                        Text(
                            text = addressLine1,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        if (city.isNotEmpty()) {
                            Text(
                                text = "$city${if (postalCode.isNotEmpty()) ", $postalCode" else ""}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Text(
                            text = "Tap on map to select location",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    
                    Button(
                        onClick = { 
                            if (selectedLocation.latitude != 0.0) {
                                showDetailsDialog = true
                            } else {
                                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = addressLine1.isNotEmpty()
                    ) {
                        Text("Confirm Location")
                    }
                }
            }
        }
    }
}
