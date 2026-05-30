package com.example.smartexpensecalendar.domain.model

import java.time.LocalDate

enum class TransactionType {
    DEBIT,
    CREDIT
}

enum class TransactionStatus {
    COMPLETED,
    SETTLEMENT, // CC Bill Payment or Transfers
    REFUNDED,   // Transaction was reversed or refunded
    FAILED      // Transaction failed
}

data class Expense(
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: LocalDate, // Transaction date
    val merchant: String?,
    val source: ExpenseSource,
    val type: TransactionType = TransactionType.DEBIT,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val accountSuffix: String? = null, // e.g., "9490" or "XX47"
    val linkedId: Long? = null,        // Link between debit and credit/settlement
    val originalSmsId: Long? = null,
    val originalSmsBody: String? = null,
    val syncDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class ExpenseSource {
    MANUAL,
    SMS
}
