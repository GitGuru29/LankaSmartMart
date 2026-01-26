package com.example.lankasmartmart.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * LankaSmartMart Design System
 * Centralized spacing, shapes, and design tokens for consistent UI
 */
object DesignSystem {
    
    // ========================================================================
    // SPACING SCALE
    // ========================================================================
    object Spacing {
        val none = 0.dp
        val xxxs = 2.dp
        val xxs = 4.dp
        val xs = 6.dp
        val sm = 8.dp
        val md = 12.dp
        val base = 16.dp
        val lg = 20.dp
        val xl = 24.dp
        val xxl = 32.dp
        val xxxl = 40.dp
        val huge = 48.dp
        val massive = 64.dp
    }
    
    // ========================================================================
    // CORNER RADIUS / SHAPES
    // ========================================================================
    object CornerRadius {
        val none = 0.dp
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val base = 16.dp
        val lg = 20.dp
        val xl = 24.dp
        val xxl = 32.dp
        val round = 1000.dp // Fully rounded (pill shape)
    }
    
    object Shapes {
        val extraSmall = RoundedCornerShape(CornerRadius.xs)
        val small = RoundedCornerShape(CornerRadius.sm)
        val medium = RoundedCornerShape(CornerRadius.md)
        val base = RoundedCornerShape(CornerRadius.base)
        val large = RoundedCornerShape(CornerRadius.lg)
        val extraLarge = RoundedCornerShape(CornerRadius.xl)
        val xxLarge = RoundedCornerShape(CornerRadius.xxl)
        val pill = RoundedCornerShape(CornerRadius.round)
        
        // Top-only rounded (for cards with bottom content)
        val topRounded = RoundedCornerShape(
            topStart = CornerRadius.base,
            topEnd = CornerRadius.base
        )
        
        // Bottom-only rounded
        val bottomRounded = RoundedCornerShape(
            bottomStart = CornerRadius.base,
            bottomEnd = CornerRadius.base
        )
    }
    
    // ========================================================================
    // ELEVATION / SHADOWS
    // ========================================================================
    object Elevation {
        val none = 0.dp
        val xs = 1.dp
        val sm = 2.dp
        val md = 4.dp
        val base = 6.dp
        val lg = 8.dp
        val xl = 12.dp
        val xxl = 16.dp
    }
    
    // ========================================================================
    // ICON SIZES
    // ========================================================================
    object IconSize {
        val xs = 12.dp
        val sm = 16.dp
        val md = 20.dp
        val base = 24.dp
        val lg = 28.dp
        val xl = 32.dp
        val xxl = 48.dp
        val huge = 64.dp
    }
    
    // ========================================================================
    // COMPONENT SIZES
    // ========================================================================
    object ComponentSize {
        // Buttons
        val buttonHeightSmall = 36.dp
        val buttonHeightMedium = 48.dp
        val buttonHeightLarge = 56.dp
        
        // Input fields
        val inputHeight = 56.dp
        val inputHeightSmall = 48.dp
        
        // Bottom Navigation
        val bottomNavHeight = 64.dp
        
        // Top App Bar
        val appBarHeight = 64.dp
        
        // Cards
        val productCardMinHeight = 200.dp
        val categoryCardHeight = 130.dp
        val bannerCardHeight = 85.dp
    }
    
    // ========================================================================
    // ANIMATION DURATIONS (in milliseconds)
    // ========================================================================
    object Animation {
        const val instant = 0
        const val quick = 150
        const val normal = 300
        const val slow = 500
        const val verySlow = 800
    }
}
