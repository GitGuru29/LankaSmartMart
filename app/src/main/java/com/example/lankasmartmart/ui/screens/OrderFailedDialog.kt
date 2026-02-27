package com.example.lankasmartmart.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.lankasmartmart.R
import com.example.lankasmartmart.ui.theme.GroceryTextDark
import com.example.lankasmartmart.ui.theme.GroceryTextGrey

@Composable
fun OrderFailedDialog(
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit,
    onBackToHome: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = GroceryTextDark
                        )
                    }
                }

                // Illustration
                // Note: Assuming there's an illustration in resources or using a placeholder
                // For the demo I'll use a placeholder if the image isn't available
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Try to use a common grocery bag illustration if available, else placeholder
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_dialog_info), // Placeholder
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color(0xFFE2E2E2)
                    )
                }

                // Title
                Text(
                    text = "Oops! Order Failed",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryTextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtitle
                Text(
                    text = "Something went tembly wrong.", // Keeping original typo if requested, or fixing it? 
                    // Let's fix it to "terribly" or keep as designed. I'll stick to design.
                    fontSize = 16.sp,
                    color = GroceryTextGrey,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Try Again Button
                Button(
                    onClick = onTryAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(67.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5383EC)
                    )
                ) {
                    Text(
                        text = "Please Try Again",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Back to home link
                TextButton(onClick = onBackToHome) {
                    Text(
                        text = "Back to home",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GroceryTextDark
                    )
                }
            }
        }
    }
}
