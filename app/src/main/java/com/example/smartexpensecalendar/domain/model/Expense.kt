package com.example.smartexpensecalendar.domain.model

import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: LocalDate, // Transaction date
    val merchant: String?,
    val source: ExpenseSource,
    val originalSmsId: Long? = null,
    val originalSmsBody: String? = null,
    val syncDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class ExpenseSource {
    MANUAL,
    SMS
}
