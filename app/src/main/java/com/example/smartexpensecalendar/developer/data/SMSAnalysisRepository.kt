package com.example.smartexpensecalendar.developer.data

import android.content.Context
import android.provider.Telephony
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.smartexpensecalendar.developer.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.developer.data.entity.MisclassifiedMessage
import com.example.smartexpensecalendar.developer.domain.model.PatternGroup
import com.example.smartexpensecalendar.developer.engine.SMSPatternGroupingEngine
import com.example.smartexpensecalendar.sms.SMSNormalizer
import com.example.smartexpensecalendar.sms.detection.FinancialDetector
import com.example.smartexpensecalendar.sms.detection.MessageType
import com.example.smartexpensecalendar.sms.detection.MessageTypeDetector
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSAnalysisRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: SMSAnalysisDao,
) {

    private val messageTypeDetector = MessageTypeDetector()
    fun getAnalyzedSMSCount() = dao.getAnalyzedSMSCount()
    fun getFinancialSMSCount() = dao.getFinancialSMSCount()
    fun getNonFinancialSMSCount() = dao.getNonFinancialSMSCount()
    fun getHighConfidenceFinancialCount() = dao.getHighConfidenceFinancialCount()
    fun getLowConfidenceFinancialCount() = dao.getLowConfidenceFinancialCount()
    
    fun getTransactionCount() = dao.getMessageTypeCount("TRANSACTION")
    fun getObligationCount() = dao.getMessageTypeCount("OBLIGATION")
    fun getInformationCount() = dao.getMessageTypeCount("INFORMATION")
    fun getPromotionalCount() = dao.getMessageTypeCount("PROMOTIONAL")

    fun getUnknownFinancialCount() = dao.getUnknownFinancialCount()

    fun getAnalyzedSMSPaged(
        query: String = "", 
        isAsc: Boolean = false,
        financial: Boolean? = null,
        messageType: String? = null
    ): Flow<PagingData<AnalyzedSMS>> {
        return Pager(
            config = PagingConfig(pageSize = 50),
            pagingSourceFactory = { dao.getAllAnalyzedSMS(query, isAsc, financial, messageType) }
        ).flow
    }

    fun getPatternGroups(): Flow<List<PatternGroup>> {
        return dao.getPatternGroups().map { list ->
            list.map { 
                PatternGroup(
                    template = it.template,
                    count = it.count,
                    sampleMessage = it.sampleMessage,
                    financialCount = it.financialCount,
                    nonFinancialCount = it.nonFinancialCount,
                    averageScore = it.averageScore
                )
            }
        }
    }

    fun getBorderlineMessages() = dao.getBorderlineMessages(40, 60)
    fun getPotentialMisclassifications() = dao.getPotentialMisclassifications()
    fun getMisclassifiedMessages() = dao.getAllMisclassifiedMessages()

    fun getTopFinancialKeywords(): Flow<List<Pair<String, Int>>> {
        return dao.getRawFinancialKeywords().map { list ->
            list.flatMap { if (it.isBlank()) emptyList() else it.split(",") }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(15)
        }
    }

    fun getTopNegativeKeywords(): Flow<List<Pair<String, Int>>> {
        return dao.getRawNegativeSignals().map { list ->
            list.flatMap { if (it.isBlank()) emptyList() else it.split(",") }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(15)
        }
    }

    fun getTopFinancialSenders() = dao.getTopFinancialSenders()
    fun getTopNonFinancialSenders() = dao.getTopNonFinancialSenders()

    suspend fun flagMisclassification(sms: AnalyzedSMS, expectedClassification: String) {
        val misclassified = MisclassifiedMessage(
            message = sms.message,
            sender = sms.sender,
            score = sms.score,
            matchedSignals = sms.matchedSignals,
            currentClassification = if (sms.isFinancial) "FINANCIAL" else "NON_FINANCIAL",
            expectedClassification = expectedClassification,
            reviewTimestamp = System.currentTimeMillis()
        )
        dao.insertMisclassifiedMessage(misclassified)
    }

    suspend fun runFullAnalysis(onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        dao.clearAnalyzedSMS()
        
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val idIndex = it.getColumnIndex(Telephony.Sms._ID)

            val total = it.count
            val batchSize = 100
            val analyzedList = mutableListOf<AnalyzedSMS>()

            var processed = 0
            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val body = it.getString(bodyIndex) ?: ""
                val address = it.getString(addressIndex) ?: "UNKNOWN"
                val date = it.getLong(dateIndex)

                val senderInfo = SenderValidationEngine.validate(address)
                val normalized = SMSNormalizer.normalize(body)
                val financialResult = FinancialDetector.detect(body)
                val template = SMSPatternGroupingEngine.generateTemplate(body)
                val messageTypeResult =
                    if (financialResult.isFinancial)
                        messageTypeDetector.detect(normalized)
                    else
                        null
                analyzedList.add(
                    AnalyzedSMS(
                        id = id,
                        sender = address,
                        message = body,
                        timestamp = date,
                        normalizedMessage = normalized,
                        senderType = senderInfo.senderType.name,
                        isFinancial = financialResult.isFinancial,
                        score = financialResult.score,
                        confidence = financialResult.confidence,
                        matchedSignals = financialResult.matchedSignals,
                        matchedKeywords = financialResult.matchedKeywords,
                        matchedPatterns = financialResult.matchedPatterns,
                        negativeSignals = financialResult.negativeSignals,
                        scoreBreakdown = financialResult.scoreBreakdown,
                        template = template,
                        messageType =
                            messageTypeResult?.messageType?.name
                                ?: MessageType.UNKNOWN.name
                    )
                )

                processed++
                if (analyzedList.size >= batchSize) {
                    dao.insertAnalyzedSMS(analyzedList)
                    analyzedList.clear()
                    onProgress(processed.toFloat() / total)
                }
            }
            if (analyzedList.isNotEmpty()) {
                dao.insertAnalyzedSMS(analyzedList)
                onProgress(1.0f)
            }
        }
    }
}
