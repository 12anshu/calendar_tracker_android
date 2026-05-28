package com.example.smartexpensecalendar.data.local

import androidx.room.*
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

    @Query("SELECT EXISTS(SELECT 1 FROM expenses WHERE originalSmsId = :smsId LIMIT 1)")
    suspend fun isSmsIdProcessed(smsId: Long): Boolean

    @Query("SELECT category FROM merchant_mappings WHERE merchantKeyword = :merchant LIMIT 1")
    suspend fun getCategoryForMerchant(merchant: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMerchantMapping(mapping: MerchantMappingEntity)

    @Query("SELECT * FROM merchant_mappings")
    fun getAllMerchantMappings(): Flow<List<MerchantMappingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun logSMSProcessing(log: SMSLogEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM sms_logs WHERE body = :body LIMIT 1)")
    suspend fun isSMSSimilarProcessed(body: String): Boolean

    @Query("SELECT COUNT(*) FROM sms_logs")
    fun getProcessedSMSCount(): Flow<Int>

    @Query("SELECT * FROM sms_logs WHERE date >= :startMillis AND date <= :endMillis")
    fun getSMSLogsForRange(startMillis: Long, endMillis: Long): Flow<List<SMSLogEntity>>
}
