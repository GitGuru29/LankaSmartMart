package com.example.lankasmartmart.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lankasmartmart.model.PaymentCard
import com.example.lankasmartmart.repository.CardRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardRepository = remember { CardRepository() }
    
    
    var cards by remember { mutableStateOf<List<PaymentCard>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }
    var scannedCardData by remember { mutableStateOf<com.example.lankasmartmart.model.ScannedCardData?>(null) }
    
    // Load cards
    LaunchedEffect(Unit) {
        isLoading = true
        cardRepository.getAllCards().onSuccess {
            cards = it
        }
        isLoading = false
    }
    
    if (showScanner) {
        // Show full-screen scanner
        CardScannerScreen(
            onDismiss = { showScanner = false },
            onCardScanned = { scannedData ->
                // Store scanned data and show add dialog
                scannedCardData = scannedData
                showScanner = false
                showAddDialog = true
            }
        )
    } else if (showAddDialog) {
        // Show add dialog
        AddCardDialog(
            onDismiss = { 
                showAddDialog = false
                scannedCardData = null // Clear scanned data
            },
            onOpenScanner = {
                showAddDialog = false
                showScanner = true
            },
            initialData = scannedCardData, // Pass scanned data
            onCardAdded = { card ->
                scope.launch {
                    cardRepository.addCard(card).onSuccess {
                        // Reload cards
                        cardRepository.getAllCards().onSuccess {
                            cards = it
                        }
                        Toast.makeText(context, "Card added!", Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Failed to add card", Toast.LENGTH_SHORT).show()
                    }
                }
                showAddDialog = false
                scannedCardData = null // Clear scanned data
            },
            cardRepository = cardRepository
        )
    } else {
        // Show normal UI
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Payment Methods", fontWeight = FontWeight.Bold) },
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, "Add Card", tint = Color.White)
                }
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (cards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "No cards saved yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Add a card to get started",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cards) { card ->
                    PaymentCardItem(
                        card = card,
                        onDelete = {
                            scope.launch {
                                cardRepository.deleteCard(card.id).onSuccess {
                                    cards = cards.filter { it.id != card.id }
                                    Toast.makeText(context, "Card deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onSetDefault = {
                            scope.launch {
                                cardRepository.setDefaultCard(card.id).onSuccess {
                                    // Reload cards
                                    cardRepository.getAllCards().onSuccess {
                                        cards = it
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    }
}


@Composable
fun PaymentCardItem(
    card: PaymentCard,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isDefault) MaterialTheme.colorScheme.primaryContainer else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = card.cardType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "**** **** **** ${card.last4Digits}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${card.expiryMonth}/${card.expiryYear}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!card.isDefault) {
                        IconButton(onClick = onSetDefault) {
                            Icon(
                                Icons.Default.Star,
                                "Set Default",
                                tint = Color.Gray
                            )
                        }
                    } else {
                        Icon(
                            Icons.Default.Star,
                            "Default",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFE53935))
                    }
                }
            }
            
            if (card.cardHolder.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.cardHolder,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AddCardDialog(
    onDismiss: () -> Unit,
    onOpenScanner: () -> Unit,
    initialData: com.example.lankasmartmart.model.ScannedCardData? = null,
    onCardAdded: (PaymentCard) -> Unit,
    cardRepository: CardRepository
) {
    val context = LocalContext.current
    var cardNumber by remember { mutableStateOf(initialData?.cardNumber ?: "") }
    var cardHolder by remember { mutableStateOf(initialData?.cardHolder ?: "") }
    var expiryMonth by remember { mutableStateOf(initialData?.expiryMonth ?: "") }
    var expiryYear by remember { mutableStateOf(initialData?.expiryYear ?: "") }
    var cvv by remember { mutableStateOf("") } // CVV never scanned for security
    var isDefault by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Camera permission launcher
    val cameraPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onOpenScanner()
        } else {
            Toast.makeText(context, "Camera permission required for scanning", Toast.LENGTH_SHORT).show()
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Payment Card") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Scan Card Button
                item {
                    OutlinedButton(
                        onClick = {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Card with Camera")
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f))
                        Text(
                            text = " OR ",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Divider(modifier = Modifier.weight(1f))
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = {
                            if (it.length <= 16) cardNumber = it.filter { char -> char.isDigit() }
                        },
                        label = { Text("Card Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("1234567890123456") }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it.uppercase() },
                        label = { Text("Card Holder Name") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("JOHN DOE") }
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = expiryMonth,
                            onValueChange = {
                                if (it.length <= 2) expiryMonth = it.filter { char -> char.isDigit() }
                            },
                            label = { Text("MM") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("12") }
                        )
                        OutlinedTextField(
                            value = expiryYear,
                            onValueChange = {
                                if (it.length <= 4) expiryYear = it.filter { char -> char.isDigit() }
                            },
                            label = { Text("YYYY") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("2026") }
                        )
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = {
                                if (it.length <= 3) cvv = it.filter { char -> char.isDigit() }
                            },
                            label = { Text("CVV") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("123") }
                        )
                    }
                }
                
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it }
                        )
                        Text("Set as default payment method")
                    }
                }
                
                if (errorMessage.isNotEmpty()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFE53935),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    errorMessage = ""
                    
                    // Validation
                    when {
                        cardNumber.length != 16 -> errorMessage = "Card number must be 16 digits"
                        !cardRepository.validateCardNumber(cardNumber) -> errorMessage = "Invalid card number"
                        cardHolder.isBlank() -> errorMessage = "Enter card holder name"
                        expiryMonth.toIntOrNull() !in 1..12 -> errorMessage = "Invalid month (01-12)"
                        expiryYear.length != 4 -> errorMessage = "Enter full year (YYYY)"
                        cvv.length != 3 -> errorMessage = "CVV must be 3 digits"
                        else -> {
                            val card = PaymentCard(
                                last4Digits = cardNumber.takeLast(4),
                                cardHolder = cardHolder,
                                cardType = cardRepository.detectCardType(cardNumber),
                                expiryMonth = expiryMonth,
                                expiryYear = expiryYear,
                                isDefault = isDefault
                            )
                            onCardAdded(card)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add Card")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun CardScannerScreen(
    onDismiss: () -> Unit,
    onCardScanned: (com.example.lankasmartmart.model.ScannedCardData) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    
    var isProcessing by remember { mutableStateOf(false) }
    var detectedText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    val cameraProviderFuture = remember { androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context) }
    val textRecognizer = remember { com.google.mlkit.vision.text.TextRecognition.getClient(com.google.mlkit.vision.text.latin.TextRecognizerOptions.DEFAULT_OPTIONS) }
    
    var imageCapture: androidx.camera.core.ImageCapture? by remember { mutableStateOf(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            textRecognizer.close()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = androidx.camera.view.PreviewView(ctx)
                val executor = androidx.core.content.ContextCompat.getMainExecutor(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    val imageCaptureBuilder = androidx.camera.core.ImageCapture.Builder()
                    imageCapture = imageCaptureBuilder.build()
                    
                    val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("CardScanner", "Camera binding failed", e)
                        errorMessage = "Camera error: ${e.message}"
                    }
                }, executor)
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Overlay with card frame
        Canvas(modifier = Modifier.fillMaxSize()) {
            val frameWidth = size.width * 0.85f
            val frameHeight = frameWidth * 0.6f
            val left = (size.width - frameWidth) / 2
            val top = (size.height - frameHeight) / 2
            
            // Draw semi-transparent overlay
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, top)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, top + frameHeight),
                size = Size(size.width, size.height - (top + frameHeight))
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(0f, top),
                size = Size(left, frameHeight)
            )
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(left + frameWidth, top),
                size = Size(size.width - (left + frameWidth), frameHeight)
            )
            
            // Draw card frame
            drawRoundRect(
                color = Color(0xFF1976D2),  // Primary blue - can't use MaterialTheme inside Canvas
                topLeft = Offset(left, top),
                size = Size(frameWidth, frameHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
            )
        }
        
        // Instructions
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Place card in frame",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap capture button when ready",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        // Error message
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE53935))
            ) {
                Text(
                    text = errorMessage,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Detected text preview
        if (detectedText.isNotEmpty() && !isProcessing) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Detected:",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detectedText,
                        fontSize = 14.sp,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { detectedText = "" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retry")
                        }
                        Button(
                            onClick = {
                                val parsed = parseCardData(detectedText)
                                if (parsed.cardNumber.isNotEmpty()) {
                                    onCardScanned(parsed)
                                } else {
                                    errorMessage = "No card number found. Try again."
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Use This")
                        }
                    }
                }
            }
        }
        
        // Capture button
        if (detectedText.isEmpty()) {
            FloatingActionButton(
                onClick = {
                    imageCapture?.let { capture ->
                        isProcessing = true
                        errorMessage = ""
                        
                        capture.takePicture(
                            androidx.core.content.ContextCompat.getMainExecutor(context),
                            object : androidx.camera.core.ImageCapture.OnImageCapturedCallback() {
                                @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                                override fun onCaptureSuccess(imageProxy: androidx.camera.core.ImageProxy) {
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = com.google.mlkit.vision.common.InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        
                                        textRecognizer.process(image)
                                            .addOnSuccessListener { visionText ->
                                                detectedText = visionText.text
                                                android.util.Log.d("CardScanner", "Captured text: $detectedText")
                                                isProcessing = false
                                            }
                                            .addOnFailureListener { e ->
                                                android.util.Log.e("CardScanner", "OCR failed", e)
                                                errorMessage = "Scan failed. Try again."
                                                isProcessing = false
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                        isProcessing = false
                                        errorMessage = "Image capture failed"
                                    }
                                }
                                
                                override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                                    android.util.Log.e("CardScanner", "Capture failed", exception)
                                    errorMessage = "Capture failed: ${exception.message}"
                                    isProcessing = false
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Capture",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
        
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }
}


private fun parseCardData(text: String): com.example.lankasmartmart.model.ScannedCardData {
    var cardNumber = ""
    var expiryMonth = ""
    var expiryYear = ""
    var cardHolder = ""
    
    android.util.Log.d("CardScanner", "Parsing text: $text")
    
    // Extract card number - SIMPLIFIED: Just find longest sequence of digits (with optional spaces)
    // Remove all spaces first, then find digit sequences
    val digitsOnly = text.replace("\\s+".toRegex(), " ") // normalize spaces
    
    // Pattern 1: Look for 13-19 digits with optional spaces (most flexible)
    val cardRegex1 = "(\\d[\\s]*){13,19}".toRegex()
    cardRegex1.find(digitsOnly)?.let {
        val extracted = it.value.replace(Regex("\\D"), "") // Remove all non-digits
        if (extracted.length in 13..19) {
            cardNumber = extracted
            android.util.Log.d("CardScanner", "Found card number (flexible): $cardNumber")
        }
    }
    
    // Pattern 2: Fallback - find any sequence of 12+ digits (even partial cards)
    if (cardNumber.isEmpty()) {
        val allDigits = text.replace(Regex("[^\\d]"), "") // Get all digits
        // Find longest substring of consecutive digits
        val digitSequences = text.split(Regex("[^\\d]+")).filter { it.length >= 12 }
        if (digitSequences.isNotEmpty()) {
            cardNumber = digitSequences.maxByOrNull { it.length } ?: ""
            android.util.Log.d("CardScanner", "Found card number (fallback): $cardNumber")
        }
    }
    
    // Extract expiry - multiple patterns
    // Pattern 1: MM/YY or MM/YYYY or MM YY
    val expiryRegex1 = "(0[1-9]|1[0-2])[/\\s]+(\\d{2}(?:\\d{2})?)".toRegex()
    expiryRegex1.find(text)?.let {
        expiryMonth = it.groupValues[1]
        val year = it.groupValues[2]
        expiryYear = if (year.length == 2) "20$year" else year
        android.util.Log.d("CardScanner", "Found expiry (pattern 1): $expiryMonth/$expiryYear")
    }
    
    // Pattern 2: Look for TM or TMO followed by MM/YY (common on cards)
    if (expiryMonth.isEmpty()) {
        val expiryRegex2 = "TM[O]?\\s+(0[1-9]|1[0-2])[/\\s]+(\\d{2})".toRegex(RegexOption.IGNORE_CASE)
        expiryRegex2.find(text)?.let {
            expiryMonth = it.groupValues[1]
            val year = it.groupValues[2]
            expiryYear = "20$year"
            android.util.Log.d("CardScanner", "Found expiry (TMO pattern): $expiryMonth/$expiryYear")
        }
    }
    
    // Pattern 3: VALID THRU followed by MM/YY
    if (expiryMonth.isEmpty()) {
        val expiryRegex3 = "(?:VALID\\s*THRU|EXPIRES|GOOD\\s*THRU)\\s*(0[1-9]|1[0-2])[/\\s]+(\\d{2}|\\d{4})".toRegex(RegexOption.IGNORE_CASE)
        expiryRegex3.find(text)?.let {
            expiryMonth = it.groupValues[1]
            val year = it.groupValues[2]
            expiryYear = if (year.length == 2) "20$year" else year
            android.util.Log.d("CardScanner", "Found expiry (VALID THRU pattern): $expiryMonth/$expiryYear")
        }
    }
    
    // Extract name - look for uppercase text (2+ words)
    val lines = text.split("\n")
    for (line in lines) {
        val trimmed = line.trim()
        // Must be uppercase, have a space (first and last name), and be 4-26 chars
        // Avoid matching common card text like "YOUTH", "CARD", etc.
        if (trimmed.matches("[A-Z][A-Z ]{3,25}".toRegex()) && 
            trimmed.contains(" ") &&
            !trimmed.matches(".*(CARD|BANK|DEBIT|CREDIT|YOUTH|VALID|THRU|MEMBER).*".toRegex())) {
            cardHolder = trimmed
            android.util.Log.d("CardScanner", "Found cardholder name: $cardHolder")
            break
        }
    }
    
    return com.example.lankasmartmart.model.ScannedCardData(
        cardNumber = cardNumber,
        cardHolder = cardHolder,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "Notification Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Configure your notification preferences\n(Coming Soon)",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Language", fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
                Text(
                    text = "Language Selection",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose your preferred language\n(Coming Soon)",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
