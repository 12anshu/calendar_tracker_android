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
    suspend fun isSmsIdProcessed(smsId: Long): Boolean
    
    suspend fun findSimilarExpense(amount: Double, date: LocalDate, type: com.example.smartexpensecalendar.domain.model.TransactionType): Expense?
    suspend fun findMatchingExpense(amount: Double, date: LocalDate, daysBack: Long): Expense?
    suspend fun findExpensesInRange(type: com.example.smartexpensecalendar.domain.model.TransactionType, startDate: LocalDate, endDate: LocalDate): List<Expense>
    suspend fun updateExpenseStatus(id: Long, status: com.example.smartexpensecalendar.domain.model.TransactionStatus, linkedId: Long?)
    suspend fun updateExpenseCategory(id: Long, category: String)

    // Merchant Mapping
    suspend fun getCategoryForMerchant(merchant: String): String?
    suspend fun saveMerchantMapping(mapping: MerchantMapping)
    suspend fun deleteMerchantMapping(mapping: MerchantMapping)
    fun getAllMerchantMappings(): Flow<List<MerchantMapping>>

    // SMS Log
    suspend fun logSMSProcessing(log: SMSProcessingLog)
    suspend fun isSMSSimilarProcessed(body: String): Boolean
    fun getProcessedSMSCount(): Flow<Int>
    fun getSMSLogsForMonth(year: Int, month: Int): Flow<List<SMSProcessingLog>>
    fun getAllSMSLogs(): Flow<List<SMSProcessingLog>>
    suspend fun clearSMSLogs()
    suspend fun clearMerchantMappings()
    suspend fun clearAllData()
    suspend fun clearMonthData(yearMonth: java.time.YearMonth)

    // Budget operations
    suspend fun upsertBudget(month: java.time.YearMonth, category: String, amount: Double)
    fun getBudgetsForMonth(month: java.time.YearMonth): Flow<Map<String, Double>>
    suspend fun getBudgetForCategory(month: java.time.YearMonth, category: String): Double
    
    // Custom Categories
    fun getCustomCategories(): Flow<List<String>>
    suspend fun addCustomCategory(name: String)

    fun getActiveMerchantStats(): Flow<List<com.example.smartexpensecalendar.data.local.ActiveMerchantEntity>>
}
