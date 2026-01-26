package com.example.lankasmartmart.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lankasmartmart.model.Address
import com.example.lankasmartmart.viewmodel.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewAddressScreen(
    addressToEdit: Address? = null,
    onBackClick: () -> Unit = {},
    onAddressSaved: () -> Unit = {},
    addressViewModel: AddressViewModel = viewModel()
) {
    val context = LocalContext.current
    
    var name by remember { mutableStateOf(addressToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(addressToEdit?.phone ?: "") }
    var addressLine1 by remember { mutableStateOf(addressToEdit?.addressLine1 ?: "") }
    var addressLine2 by remember { mutableStateOf(addressToEdit?.addressLine2 ?: "") }
    var city by remember { mutableStateOf(addressToEdit?.city ?: "") }
    var postalCode by remember { mutableStateOf(addressToEdit?.postalCode ?: "") }
    var isDefault by remember { mutableStateOf(addressToEdit?.isDefault ?: false) }
    
    val isLoading by addressViewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (addressToEdit == null) "Add New Address" else "Edit Address",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // Phone field
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // Address Line 1
            OutlinedTextField(
                value = addressLine1,
                onValueChange = { addressLine1 = it },
                label = { Text("Address Line 1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // Address Line 2
            OutlinedTextField(
                value = addressLine2,
                onValueChange = { addressLine2 = it },
                label = { Text("Address Line 2 (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // City
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // Postal Code
            OutlinedTextField(
                value = postalCode,
                onValueChange = { postalCode = it },
                label = { Text("Postal Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            
            // Set as default checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    enabled = !isLoading
                )
                Text(
                    text = "Set as default address",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = {
                    // Validation
                    when {
                        name.isBlank() -> {
                            Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show()
                        }
                        phone.isBlank() -> {
                            Toast.makeText(context, "Please enter phone number", Toast.LENGTH_SHORT).show()
                        }
                        addressLine1.isBlank() -> {
                            Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
                        }
                        city.isBlank() -> {
                            Toast.makeText(context, "Please enter city", Toast.LENGTH_SHORT).show()
                        }
                        postalCode.isBlank() -> {
                            Toast.makeText(context, "Please enter postal code", Toast.LENGTH_SHORT).show()
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
                                isDefault = isDefault
                            )
                            
                            if (addressToEdit == null) {
                                // Add new
                                addressViewModel.addAddress(
                                    address,
                                    onSuccess = {
                                        Toast.makeText(context, "Address added!", Toast.LENGTH_SHORT).show()
                                        onAddressSaved()
                                    },
                                    onError = {
                                        Toast.makeText(context, "Failed to add address", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                // Update existing
                                addressViewModel.updateAddress(
                                    address,
                                    onSuccess = {
                                        Toast.makeText(context, "Address updated!", Toast.LENGTH_SHORT).show()
                                        onAddressSaved()
                                    },
                                    onError = {
                                        Toast.makeText(context, "Failed to update address", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (addressToEdit == null) "Save Address" else "Update Address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
