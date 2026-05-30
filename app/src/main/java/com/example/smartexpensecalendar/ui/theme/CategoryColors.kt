package com.example.smartexpensecalendar.ui.theme

import androidx.compose.ui.graphics.Color

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> ColorFood
        "groceries" -> ColorGroceries
        "bills & utilities", "bill payment", "utilities" -> ColorBills
        "shopping", "online shopping" -> ColorShopping
        "transport", "travel", "fuel" -> ColorTransport
        else -> ColorOthers
    }
}
