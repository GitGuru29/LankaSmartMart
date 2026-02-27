package com.example.lankasmartmart.utils

import androidx.compose.ui.graphics.Color

/**
 * Utility functions for UI components
 */

fun getCategoryEmoji(categoryId: String): String {
    return when (categoryId) {
        "groceries" -> "🛒"
        "vegetables" -> "🥬"
        "fruits" -> "🍎"
        "dairy" -> "🥛"
        "beverages" -> "☕"
        "snacks" -> "🍿"
        "personal_care" -> "🧴"
        "household" -> "🧹"
        "stationery" -> "✏️"
        "bakery" -> "🍞"
        "meat" -> "🥩"
        else -> "📦"
    }
}

fun getCategoryColor(categoryId: String): Color {
    return when (categoryId) {
        "groceries" -> Color(0xFFE8F5E9)
        "vegetables" -> Color(0xFFE8F5E9)
        "fruits" -> Color(0xFFFFEBEE)
        "dairy" -> Color(0xFFE3F2FD)
        "beverages" -> Color(0xFFFFF3E0)
        "snacks" -> Color(0xFFFFF9C4)
        "personal_care" -> Color(0xFFF3E5F5)
        "household" -> Color(0xFFE0F7FA)
        "stationery" -> Color(0xFFFFF8E1)
        "bakery" -> Color(0xFFFDF5E6)
        "meat" -> Color(0xFFFFF0F0)
        else -> Color(0xFFECEFF1)
    }
}

fun getProductImageRes(context: android.content.Context, productName: String): Int? {
    // Convert product name to drawable resource name: "Organic Bananas" -> "img_bananas"
    val simpleName = productName
        .lowercase()
        .replace("organic ", "")
        .replace("fresh ", "")
        .replace("ceylon ", "ceylon_")
        .replace("basmati ", "basmati_")
        .replace("wheat ", "wheat_")
        .replace("anchor ", "anchor_")
        .replace("mango ", "mango_")
        .replace("red ", "red_")
        .replace("rin ", "rin_")
        .replace("rani ", "rani_")
        .replace("misumi ", "misumi_")
        .replace("cream ", "cream_")
        .replace("coconut ", "coconut_")
        .replace("sunlight ", "sunlight_")
        .replace("blue ", "blue_")
        .replace("atlas ", "atlas_")
        .replace("bellpepper", "bellpaper")
        .replace("egg noodles", "eggnoodles")
        .replace("cooking oil", "oil")
        .replace("instant noodles", "noodles")
        .replace("strawberries", "stewberry")
        .replace("navel orange", "orange")
        .replace("yellow lemon", "lemon")
        .replace("apple & grape juice", "apple_grape_juice")
        .replace("coca-cola can", "coke_can")
        .replace("diet coke can", "diet_coke")
        .replace("pepsi can", "pepsi_can")
        .replace("sprite can", "sprite")
        .trim()
        .replace(" ", "_")

    val resName = "img_$simpleName"
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) resId else null
}
