package com.example.lankasmartmart.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.lankasmartmart.ui.theme.DesignSystem

/**
 * Shimmer loading placeholder for product cards
 */
@Composable
fun ShimmerProductCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(DesignSystem.ComponentSize.productCardMinHeight),
        shape = DesignSystem.Shapes.base,
        elevation = CardDefaults.cardElevation(defaultElevation = DesignSystem.Elevation.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignSystem.Spacing.md)
        ) {
            // Image placeholder
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Title placeholder
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            
            // Price placeholder
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Button placeholder
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
        }
    }
}

/**
 * Shimmer loading placeholder for category cards
 */
@Composable
fun ShimmerCategoryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(DesignSystem.ComponentSize.categoryCardHeight),
        shape = DesignSystem.Shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = DesignSystem.Elevation.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignSystem.Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon placeholder (circle)
            Shimmer(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.md))
            
            // Text placeholder
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
            )
        }
    }
}

/**
 * Grid of shimmer category loading placeholders
 */
@Composable
fun ShimmerCategoryGrid(count: Int = 9) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.md),
        contentPadding = PaddingValues(
            start = DesignSystem.Spacing.lg,
            end = DesignSystem.Spacing.lg,
            bottom = DesignSystem.Spacing.base
        ),
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) {
        items(count) {
            ShimmerCategoryCard()
        }
    }
}

/**
 * Shimmer loading placeholder for banner cards
 */
@Composable
fun ShimmerBannerCard() {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(DesignSystem.ComponentSize.bannerCardHeight),
        shape = DesignSystem.Shapes.base,
        elevation = CardDefaults.cardElevation(defaultElevation = DesignSystem.Elevation.md)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignSystem.Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Shimmer(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.width(DesignSystem.Spacing.md))
            
            Column {
                // Title
                Shimmer(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                )
                
                Spacer(modifier = Modifier.height(DesignSystem.Spacing.xs))
                
                // Subtitle
                Shimmer(
                    modifier = Modifier
                        .width(70.dp)
                        .height(12.dp)
                )
            }
        }
    }
}

/**
 * Shimmer loading placeholder for list items
 */
@Composable
fun ShimmerListItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(DesignSystem.Spacing.base),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image placeholder
        Shimmer(
            modifier = Modifier
                .size(60.dp)
                .clip(DesignSystem.Shapes.medium)
        )
        
        Spacer(modifier = Modifier.width(DesignSystem.Spacing.base))
        
        Column(modifier = Modifier.weight(1f)) {
            // Title
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
            )
            
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.sm))
            
            // Subtitle
            Shimmer(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(12.dp)
            )
        }
        
        // Price
        Shimmer(
            modifier = Modifier
                .width(60.dp)
                .height(20.dp)
        )
    }
}
