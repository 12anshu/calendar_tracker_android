package com.example.smartexpensecalendar.core.designsystem.theme

import androidx.compose.ui.graphics.Color

fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food" -> CategoryColors.Food
        "groceries" -> CategoryColors.Groceries
        "upi / digital", "upi" -> CategoryColors.UPI
        "online shopping" -> CategoryColors.OnlineSpending
        "bill payment" -> CategoryColors.BillPayment
        "card payment", "credit card payment" -> CategoryColors.CreditCardPayment
        "travel" -> CategoryColors.Travel
        "fuel" -> CategoryColors.Fuel
        "entertainment" -> CategoryColors.Entertainment
        "medical" -> CategoryColors.Medical
        "utilities" -> CategoryColors.Utilities
        "rent", "rent & maintenance" -> CategoryColors.Rent
        "investment", "investments" -> CategoryColors.Investments
        "insurance" -> CategoryColors.Insurance
        "emi & loans", "emi", "loan" -> CategoryColors.EMI
        "emi conversion" -> CategoryColors.EMI
        "transfer" -> CategoryColors.Transfer
        "refund" -> CategoryColors.Refund
        "settlement" -> CategoryColors.Settlement
        "income", "money received" -> CategoryColors.Inflow
        "cash withdrawal" -> CategoryColors.Cash
        "cash deposit" -> CategoryColors.Cash
        "subscription" -> CategoryColors.Subscription
        "meal card" -> CategoryColors.MealCard
        "payment" -> CategoryColors.Payment
        "services" -> CategoryColors.Services
        else -> CategoryColors.Miscellaneous
    }
}

object CategoryColors {
    val Food = Color(0xFFFF8A3D)
    val Groceries = Color(0xFF4CAF50)
    val UPI = Color(0xFF3B82F6)
    val OnlineSpending = Color(0xFF6366F1)
    val BillPayment = Color(0xFF06B6D4)
    val CreditCardPayment = Color(0xFF64748B)
    val Travel = Color(0xFF8B5CF6)
    val Fuel = Color(0xFFEF4444)
    val Entertainment = Color(0xFFEC4899)
    val Medical = Color(0xFF14B8A6)
    val Utilities = Color(0xFFF59E0B)
    val Rent = Color(0xFF7C3AED)
    val Miscellaneous = Color(0xFF6B7280)
    val Investments = Color(0xFF0EA5E9)
    val Insurance = Color(0xFF2563EB)
    val EMI = Color(0xFF9333EA)
    val Loan = Color(0xFF9333EA)
    val Subscription = Color(0xFFF97316)
    val MealCard = Color(0xFFFF5722)
    val Payment = Color(0xFF607D8B)
    val Transfer = Color(0xFF10B981)
    val Refund = Color(0xFF14B8A6)
    val Settlement = Color(0xFF3B82F6)
    val Inflow = Color(0xFF4CAF50)
    val Cash = Color(0xFF6B7280)
    val Services = Color(0xFFF43F5E)
}
