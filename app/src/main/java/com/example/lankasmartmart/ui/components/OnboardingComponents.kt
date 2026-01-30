package com.example.lankasmartmart.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Progress indicator dots for onboarding screens
 * @param currentPage Current page index (0-based)
 * @param totalPages Total number of pages
 */
@Composable
fun OnboardingProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) Color(0xFF2E7D32) else Color(0xFFE0E0E0),
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Skip button for onboarding screens
 * @param onSkip Callback when skip is clicked
 */
@Composable
fun OnboardingSkipButton(
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onSkip,
        modifier = modifier
    ) {
        Text(
            text = "Skip",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF757575)
        )
    }
}
