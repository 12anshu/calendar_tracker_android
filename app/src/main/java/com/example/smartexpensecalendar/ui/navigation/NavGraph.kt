package com.example.smartexpensecalendar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.smartexpensecalendar.ui.BudgetDetailScreen
import com.example.smartexpensecalendar.ui.HomeScreen
import com.example.smartexpensecalendar.ui.SpendingAnalysisScreen
import com.example.smartexpensecalendar.ui.TransactionsScreen
import com.example.smartexpensecalendar.ui.InsightsScreen
import com.example.smartexpensecalendar.ui.MerchantRulesScreen
import com.example.smartexpensecalendar.ui.AuthScreen
import com.example.smartexpensecalendar.ui.ProfileScreen
import com.example.smartexpensecalendar.ui.SplashScreen
import com.example.smartexpensecalendar.ui.SubscriptionScreen
import com.example.smartexpensecalendar.ui.SmsInboxScreen
import com.example.smartexpensecalendar.features.developer_tools.presentation.DeveloperDashboardScreen
import com.example.smartexpensecalendar.features.developer_tools.presentation.SMSAnalysisDashboardScreen
import com.example.smartexpensecalendar.features.developer_tools.presentation.TransactionExtractionScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(
            route = Screen.Auth.route,
            arguments = listOf(navArgument("force") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            val force = backStackEntry.arguments?.getBoolean("force") ?: false
            AuthScreen(navController = navController, forceShow = force)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.SpendingAnalysis.route) {
            SpendingAnalysisScreen(navController = navController)
        }
        composable(Screen.Budget.route) {
            BudgetDetailScreen(navController = navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController = navController)
        }
        composable(Screen.SmsInbox.route) {
            SmsInboxScreen(navController = navController)
        }
        composable(Screen.Insights.route) {
            InsightsScreen(navController = navController)
        }
        composable(Screen.MerchantRules.route) {
            MerchantRulesScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Subscription.route) {
            SubscriptionScreen(navController = navController)
        }

        // DEVELOPER MODULE ROUTES
        // --------------------------------------------------
        composable(Screen.DeveloperDashboard.route) {
            DeveloperDashboardScreen(navController = navController)
        }
        composable(Screen.FinancialDetectionLab.route) {
            SMSAnalysisDashboardScreen(navController = navController)
        }
        composable(Screen.TransactionExtractionLab.route) {
            TransactionExtractionScreen(navController = navController)
        }
        // --------------------------------------------------
    }
}
