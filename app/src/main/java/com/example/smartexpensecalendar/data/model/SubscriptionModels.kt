package com.example.smartexpensecalendar.data.model

import androidx.compose.ui.graphics.Color

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val price: String,
    val period: String,
    val color: Color,
    val isPopular: Boolean = false,
    val features: List<String>
)

data class PlanFeatureInfo(
    val name: String,
    val includedInFree: Boolean,
    val includedInPro: Boolean,
    val includedInProPlus: Boolean
)
