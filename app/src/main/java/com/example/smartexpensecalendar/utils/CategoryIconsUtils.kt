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
            "card payment", "credit card payment" -> Icons.Rounded.CreditCard
            "travel" -> Icons.Rounded.TravelExplore
            "fuel" -> Icons.Rounded.LocalGasStation
            "entertainment" -> Icons.Rounded.Theaters
            "medical" -> Icons.Rounded.MedicalServices
            "utilities" -> Icons.Rounded.Bolt
            "rent", "rent & maintenance" -> Icons.Rounded.Apartment
            "investment", "investments" -> Icons.AutoMirrored.Rounded.ShowChart
            "insurance" -> Icons.Rounded.Shield
            "emi & loans", "emi", "loan" -> Icons.Rounded.CreditCard
            "emi conversion" -> Icons.Rounded.History
            "transfer" -> Icons.Rounded.SwapHoriz
            "refund" -> Icons.Rounded.Replay
            "settlement" -> Icons.Rounded.CheckCircle
            "income", "money received" -> Icons.Rounded.AddCircle
            "cash withdrawal" -> Icons.Rounded.Atm
            "cash deposit" -> Icons.Rounded.AccountBalance
            "subscription" -> Icons.Rounded.Subscriptions
            "meal card" -> Icons.Rounded.Fastfood
            "payment" -> Icons.Rounded.AccountBalanceWallet
            "miscellaneous" -> Icons.Rounded.Category
            "services" -> Icons.Rounded.Handyman
            else -> Icons.Rounded.Category
        }
    }
}
