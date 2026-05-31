package com.example.smartexpensecalendar.data.local

import androidx.room.*
import com.example.smartexpensecalendar.data.local.entity.BudgetEntity
import com.example.smartexpensecalendar.data.local.entity.ExpenseEntity
import com.example.smartexpensecalendar.data.local.entity.MerchantMappingEntity
import com.example.smartexpensecalendar.data.local.entity.SMSLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE date = :date")
    fun getExpensesForDate(date: String): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date LIKE :monthPrefix || '%'")
    fun getExpensesForMonth(monthPrefix: String): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE category = :category AND date = :date LIMIT 1")
    suspend fun getExpenseByCategoryAndDate(category: String, date: String): ExpenseEntity?

    @Query("SELECT * FROM expenses WHERE amount = :amount AND date = :date LIMIT 1")
    suspend fun findSimilarExpense(amount: Double, date: String): ExpenseEntity?

    @Query("""
        SELECT * FROM expenses 
        WHERE amount = :amount 
        AND type = 'DEBIT' 
        AND status = 'COMPLETED' 
        AND date >= :startDate 
        AND date <= :endDate 
        ORDER BY date DESC LIMIT 1
    """)
    suspend fun findMatchingDebit(amount: Double, startDate: String, endDate: String): ExpenseEntity?

    @Query("UPDATE expenses SET status = :status, linkedId = :linkedId WHERE id = :id")
    suspend fun updateExpenseStatus(id: Long, status: String, linkedId: Long?)

    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE originalSmsId = :smsId LIMIT 1)")
    suspend fun isSmsIdProcessed(smsId: Long): Boolean

    @Query("SELECT category FROM merchant_mappings WHERE merchantKeyword = :merchant LIMIT 1")
    suspend fun getCategoryForMerchant(merchant: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMerchantMapping(mapping: MerchantMappingEntity)

    @Delete
    suspend fun deleteMerchantMapping(mapping: MerchantMappingEntity)

    @Query("SELECT * FROM merchant_mappings")
    fun getAllMerchantMappings(): Flow<List<MerchantMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logSMSProcessing(log: SMSLogEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM sms_logs WHERE body = :body LIMIT 1)")
    suspend fun isSMSSimilarProcessed(body: String): Boolean

    @Query("SELECT COUNT(*) FROM sms_logs")
    fun getProcessedSMSCount(): Flow<Int>

    @Query("SELECT * FROM sms_logs ORDER BY date DESC")
    fun getAllSMSLogs(): Flow<List<SMSLogEntity>>

    @Query("SELECT * FROM sms_logs WHERE date >= :startMillis AND date <= :endMillis")
    fun getSMSLogsForRange(startMillis: Long, endMillis: Long): Flow<List<SMSLogEntity>>

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()

    @Query("DELETE FROM sms_logs")
    suspend fun clearAllSMSLogs()

    @Query("DELETE FROM merchant_mappings")
    suspend fun clearAllMerchantMappings()

    // Budget Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetsForMonth(month: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE month < :month ORDER BY month DESC LIMIT 50")
    suspend fun getPreviousBudgets(month: String): List<BudgetEntity>

    @Query("SELECT amount FROM budgets WHERE month = :month AND category = :category LIMIT 1")
    suspend fun getBudgetForCategory(month: String, category: String): Double?

    // Custom Categories
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomCategory(category: com.example.smartexpensecalendar.data.local.entity.CustomCategoryEntity)

    @Query("SELECT name FROM custom_categories")
    fun getAllCustomCategories(): Flow<List<String>>

    @Query("SELECT merchant, category, COUNT(*) as frequency FROM expenses WHERE merchant IS NOT NULL GROUP BY merchant ORDER BY frequency DESC")
    fun getActiveMerchantStats(): Flow<List<ActiveMerchantEntity>>
}

data class ActiveMerchantEntity(
    val merchant: String,
    val category: String,
    val frequency: Int
)
