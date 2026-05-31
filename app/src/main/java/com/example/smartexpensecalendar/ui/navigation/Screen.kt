package com.example.smartexpensecalendar.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SpendingAnalysis : Screen("spending_analysis") // Comparison (Analytics Card click)
    object Insights : Screen("insights")         // Behavior (Bottom Nav)
    object Budget : Screen("budget")             // Settings (Bottom Nav - replacing Report)
    object Transactions : Screen("transactions")
    object Profile : Screen("profile")
    object MerchantRules : Screen("merchant_rules")
}
