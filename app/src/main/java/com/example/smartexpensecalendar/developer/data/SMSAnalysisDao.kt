package com.example.smartexpensecalendar.developer.data

import androidx.paging.PagingSource
import androidx.room.*
import com.example.smartexpensecalendar.developer.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.developer.data.entity.MisclassifiedMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface SMSAnalysisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyzedSMS(sms: List<AnalyzedSMS>)

    @Query("""
        SELECT * FROM analyzed_sms 
        WHERE (message LIKE '%' || :query || '%' OR sender LIKE '%' || :query || '%')
        AND (:financial IS NULL OR isFinancial = :financial)
        AND (
            :messageType IS NULL
            OR (
                :messageType = 'UNKNOWN'
                AND (
                    messageType IS NULL
                    OR messageType = ''
                    OR messageType = 'UNKNOWN'
                )
            )
            OR messageType = :messageType
        )
        ORDER BY CASE WHEN :isAsc = 1 THEN score END ASC, CASE WHEN :isAsc = 0 THEN score END DESC
    """)
    fun getAllAnalyzedSMS(
        query: String = "", 
        isAsc: Boolean = false,
        financial: Boolean? = null,
        messageType: String? = null
    ): PagingSource<Int, AnalyzedSMS>

    @Query("SELECT COUNT(*) FROM analyzed_sms")
    fun getAnalyzedSMSCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyzed_sms WHERE isFinancial = 1")
    fun getFinancialSMSCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyzed_sms WHERE isFinancial = 0")
    fun getNonFinancialSMSCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyzed_sms WHERE score > 70")
    fun getHighConfidenceFinancialCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyzed_sms WHERE score BETWEEN 1 AND 30 AND isFinancial = 1")
    fun getLowConfidenceFinancialCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analyzed_sms WHERE messageType = :type")
    fun getMessageTypeCount(type: String): Flow<Int>

    @Query("""
        SELECT COUNT(*)
        FROM analyzed_sms
        WHERE isFinancial = 1
        AND (
            messageType IS NULL
            OR messageType = ''
            OR messageType = 'UNKNOWN'
        )
    """)
    fun getUnknownFinancialCount(): Flow<Int>

    @Query("SELECT * FROM analyzed_sms WHERE score BETWEEN :minScore AND :maxScore")
    fun getBorderlineMessages(minScore: Int, maxScore: Int): Flow<List<AnalyzedSMS>>

    @Query("SELECT * FROM analyzed_sms WHERE (isFinancial = 0 AND template LIKE '%{AMOUNT}%') OR (score BETWEEN 70 AND 80) OR (LENGTH(matchedSignals) < 20 AND isFinancial = 1)")
    fun getPotentialMisclassifications(): Flow<List<AnalyzedSMS>>

    @Query("SELECT template, COUNT(*) as count, MAX(message) as sampleMessage, SUM(CASE WHEN isFinancial = 1 THEN 1 ELSE 0 END) as financialCount, SUM(CASE WHEN isFinancial = 0 THEN 1 ELSE 0 END) as nonFinancialCount, AVG(score) as averageScore FROM analyzed_sms GROUP BY template ORDER BY count DESC")
    fun getPatternGroups(): Flow<List<PatternGroupQueryResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMisclassifiedMessage(message: MisclassifiedMessage)

    @Query("SELECT * FROM misclassified_messages ORDER BY reviewTimestamp DESC")
    fun getAllMisclassifiedMessages(): Flow<List<MisclassifiedMessage>>

    @Query("DELETE FROM analyzed_sms")
    suspend fun clearAnalyzedSMS()

    // Returning String to let Repository handle splitting (Room can't Flow<List<Set<String>>> easily)
    @Query("SELECT matchedKeywords FROM analyzed_sms WHERE isFinancial = 1")
    fun getRawFinancialKeywords(): Flow<List<String>>

    @Query("SELECT negativeSignals FROM analyzed_sms")
    fun getRawNegativeSignals(): Flow<List<String>>

    @Query("SELECT sender, COUNT(*) as count FROM analyzed_sms WHERE isFinancial = 1 GROUP BY sender ORDER BY count DESC LIMIT 10")
    fun getTopFinancialSenders(): Flow<List<SenderCount>>

    @Query("SELECT sender, COUNT(*) as count FROM analyzed_sms WHERE isFinancial = 0 GROUP BY sender ORDER BY count DESC LIMIT 10")
    fun getTopNonFinancialSenders(): Flow<List<SenderCount>>

    @Query("SELECT matchedPatterns FROM analyzed_sms WHERE isFinancial = 1")
    fun getRawFinancialPatterns(): Flow<List<String>>
}

data class SenderCount(val sender: String, val count: Int)

data class PatternGroupQueryResult(
    val template: String,
    val count: Int,
    val sampleMessage: String,
    val financialCount: Int,
    val nonFinancialCount: Int,
    val averageScore: Double
)
