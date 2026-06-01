package com.example.smartexpensecalendar.data.model

import com.example.smartexpensecalendar.ui.theme.*

object SubscriptionData {
    val plans = listOf(
        SubscriptionPlan(
            id = "free",
            name = "FREE",
            price = "₹0",
            period = "Forever",
            color = TextSecondary,
            features = listOf("Expense Tracking", "Auto SMS Sync", "Budget Tracking", "History (3 Months)")
        ),
        SubscriptionPlan(
            id = "pro",
            name = "PRO",
            price = "₹399",
            period = "Yearly",
            color = PrimaryAccent,
            isPopular = true,
            features = listOf("History (12 Months)", "PDF/Excel Export", "Cloud Backup", "Category Budgets", "Merchant Analysis", "Ad-Free Experience")
        ),
        SubscriptionPlan(
            id = "pro_plus",
            name = "PRO+",
            price = "₹1499",
            period = "Yearly",
            color = ColorShopping,
            features = listOf("AI Insights", "Future Predictions", "Unlimited History")
        )
    )

    val allFeatures = listOf(
        PlanFeatureInfo("Expense Tracking", true, true, true),
        PlanFeatureInfo("Auto SMS Sync", true, true, true),
        PlanFeatureInfo("Budget Tracking", true, true, true),
        PlanFeatureInfo("History (3 Months)", true, true, true),
        PlanFeatureInfo("History (12 Months)", false, true, true),
        PlanFeatureInfo("PDF/Excel Export", false, true, true),
        PlanFeatureInfo("Cloud Backup", false, true, true),
        PlanFeatureInfo("Category Budgets", false, true, true),
        PlanFeatureInfo("Merchant Analysis", false, true, true),
        PlanFeatureInfo("Ad-Free Experience", false, true, true),
        PlanFeatureInfo("AI Insights", false, false, true),
        PlanFeatureInfo("Future Predictions", false, false, true),
        PlanFeatureInfo("Unlimited History", false, false, true),
    )
}
