package com.example.smartexpensecalendar.utils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconsUtils {
    fun getIcon(category: String): ImageVector {
        return when (category.lowercase()) {
            "food" -> Icons.Rounded.Restaurant
            "groceries" -> Icons.Rounded.ShoppingCart
            "upi / digital", "upi" -> Icons.Rounded.Payments
            "online spending", "online shopping" -> Icons.Rounded.ShoppingBag
            "bill payment" -> Icons.AutoMirrored.Rounded.ReceiptLong
            "credit card payment" -> Icons.Rounded.CreditCard
            "travel" -> Icons.Rounded.TravelExplore
            "fuel" -> Icons.Rounded.LocalGasStation
            "entertainment" -> Icons.Rounded.Theaters
            "medical" -> Icons.Rounded.MedicalServices
            "utilities" -> Icons.Rounded.Bolt
            "rent" -> Icons.Rounded.Apartment
            "investments" -> Icons.AutoMirrored.Rounded.ShowChart
            "insurance" -> Icons.Rounded.Shield
            "emi", "loan" -> Icons.Rounded.CreditCard
            "subscription" -> Icons.Rounded.Subscriptions
            "miscellaneous" -> Icons.Rounded.Category
            else -> Icons.Rounded.Category
        }
    }
}