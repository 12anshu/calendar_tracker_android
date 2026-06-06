package com.example.smartexpensecalendar.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth?force={force}") {
        fun createRoute(force: Boolean = false) = "auth?force=$force"
    }
    object Home : Screen("home")
    object SpendingAnalysis : Screen("spending_analysis") // Comparison (Analytics Card click)
    object Insights : Screen("insights")         // Behavior (Bottom Nav)
    object Budget : Screen("budget")             // Settings (Bottom Nav - replacing Report)
    object Transactions : Screen("transactions")
    object Profile : Screen("profile")
    object MerchantRules : Screen("merchant_rules")
    object Subscription : Screen("subscription")

    // DEVELOPER SCREENS
    object DeveloperDashboard : Screen("developer_dashboard")
}
