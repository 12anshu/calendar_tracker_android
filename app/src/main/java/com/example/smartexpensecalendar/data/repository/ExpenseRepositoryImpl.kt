package com.example.smartexpensecalendar.data.repository

import com.example.smartexpensecalendar.data.local.ExpenseDao
import com.example.smartexpensecalendar.data.mapper.toDomain
import com.example.smartexpensecalendar.data.mapper.toEntity
import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.MerchantMapping
import com.example.smartexpensecalendar.domain.model.SMSProcessingLog
import com.example.smartexpensecalendar.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getExpensesForDate(date: LocalDate): Flow<List<Expense>> {
        return dao.getExpensesForDate(date.toString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getExpensesForMonth(year: Int, month: Int): Flow<List<Expense>> {
        val monthStr = String.format("%04d-%02d", year, month)
        return dao.getExpensesForMonth(monthStr).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertExpense(expense: Expense) {
        dao.upsertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntity())
    }

    override suspend fun getExpenseByCategoryAndDate(category: String, date: LocalDate): Expense? {
        return dao.getExpenseByCategoryAndDate(category, date.toString())?.toDomain()
    }

    override suspend fun isSmsIdProcessed(smsId: Long): Boolean {
        return dao.isSmsIdProcessed(smsId)
    }

    override suspend fun findSimilarExpense(amount: Double, date: LocalDate): Expense? {
        return dao.findSimilarExpense(amount, date.toString())?.toDomain()
    }

    override suspend fun findMatchingExpense(amount: Double, date: LocalDate, daysBack: Long): Expense? {
        val startDate = date.minusDays(daysBack).toString()
        val endDate = date.toString()
        return dao.findMatchingDebit(amount, startDate, endDate)?.toDomain()
    }

    override suspend fun updateExpenseStatus(
        id: Long,
        status: com.example.smartexpensecalendar.domain.model.TransactionStatus,
        linkedId: Long?
    ) {
        dao.updateExpenseStatus(id, status.name, linkedId)
    }

    override suspend fun getCategoryForMerchant(merchant: String): String? {
        return dao.getCategoryForMerchant(merchant)
    }

    override suspend fun saveMerchantMapping(mapping: MerchantMapping) {
        dao.saveMerchantMapping(mapping.toEntity())
    }

    override fun getAllMerchantMappings(): Flow<List<MerchantMapping>> {
        return dao.getAllMerchantMappings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun logSMSProcessing(log: SMSProcessingLog) {
        dao.logSMSProcessing(log.toEntity())
    }

    override suspend fun isSMSSimilarProcessed(body: String): Boolean {
        return dao.isSMSSimilarProcessed(body)
    }

    override fun getProcessedSMSCount(): Flow<Int> {
        return dao.getProcessedSMSCount()
    }

    override fun getSMSLogsForMonth(year: Int, month: Int): Flow<List<SMSProcessingLog>> {
        val startOfMonth = java.time.YearMonth.of(year, month).atDay(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = java.time.YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        return dao.getSMSLogsForRange(startOfMonth, endOfMonth).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearAllData() {
        dao.clearAllExpenses()
        dao.clearAllSMSLogs()
    }
}
