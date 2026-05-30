package com.example.smartexpensecalendar.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "budgets",
    primaryKeys = ["month", "category"]
)
data class BudgetEntity(
    val month: String,    // "YYYY-MM" format
    val category: String, // "Total" or category name
    val amount: Double
)
