package com.example.smartexpensecalendar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartexpensecalendar.ui.BudgetDetailScreen
import com.example.smartexpensecalendar.ui.HomeScreen
import com.example.smartexpensecalendar.ui.SpendingAnalysisScreen
import com.example.smartexpensecalendar.ui.TransactionsScreen
import com.example.smartexpensecalendar.ui.InsightsScreen
import com.example.smartexpensecalendar.ui.MerchantRulesScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
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
        composable(Screen.Insights.route) {
            InsightsScreen(navController = navController)
        }
        composable(Screen.MerchantRules.route) {
            MerchantRulesScreen(navController = navController)
        }
    }
}
