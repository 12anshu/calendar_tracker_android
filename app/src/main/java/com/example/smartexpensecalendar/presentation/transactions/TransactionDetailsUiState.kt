package com.example.smartexpensecalendar.presentation.transactions

import com.example.smartexpensecalendar.domain.model.Expense
import java.time.LocalDate

data class MerchantInsights(
    val totalTransactions: Int,
    val totalSpend: Double,
    val averageSpend: Double,
    val lastTransactionDate: LocalDate?
)

data class TransactionDetailsUiState(
    val expense: Expense? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val categories: List<String> = emptyList(),
    val merchantInsights: MerchantInsights? = null,
    val reportSubmitted: Boolean = false,
    val reportStatus: String? = null
)
