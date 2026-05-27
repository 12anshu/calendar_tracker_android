package com.example.smartexpensecalendar.domain.repository

import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.MerchantMapping
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExpenseRepository {
    // Expense operations
    fun getExpensesForDate(date: LocalDate): Flow<List<Expense>>
    fun getExpensesForMonth(year: Int, month: Int): Flow<List<Expense>>
    suspend fun upsertExpense(expense: Expense)
    suspend fun deleteExpense(expense: Expense)
    suspend fun getExpenseByCategoryAndDate(category: String, date: LocalDate): Expense?

    // Merchant Mapping
    suspend fun getCategoryForMerchant(merchant: String): String?
    suspend fun saveMerchantMapping(mapping: MerchantMapping)
    fun getAllMerchantMappings(): Flow<List<MerchantMapping>>

    // SMS Log
    suspend fun logSMSProcessing(log: SMSProcessingLog)
    suspend fun isSMSSimilarProcessed(body: String): Boolean
    fun getProcessedSMSCount(): Flow<Int>
}
