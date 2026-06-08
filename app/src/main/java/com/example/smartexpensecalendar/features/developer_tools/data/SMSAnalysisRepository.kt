package com.example.smartexpensecalendar.features.developer_tools.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.data.entity.MisclassifiedMessage
import com.example.smartexpensecalendar.features.developer_tools.data.source.SmsProviderDataSource
import com.example.smartexpensecalendar.features.developer_tools.domain.model.PatternGroup
import com.example.smartexpensecalendar.sms_engine.detector.FinancialDetector
import com.example.smartexpensecalendar.sms_engine.detector.MessageType
import com.example.smartexpensecalendar.sms_engine.extractor.AmountExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSAnalysisRepository @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val dao: SMSAnalysisDao,
    private val smsProvider: SmsProviderDataSource
) {
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

    suspend fun getTransactionSMS() = dao.getTransactionSMS()
    
    suspend fun getAllAnalyzedSMSList() = dao.getAllAnalyzedSMSList()

    suspend fun exportDiagnosticData(
        month: java.time.YearMonth? = null,
        minimal: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val messages = if (month != null) {
            val start = month.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val end = month.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            dao.getAnalyzedSMSInRange(start, end)
        } else {
            dao.getAllAnalyzedSMSList()
        }

        if (messages.isEmpty()) return@withContext "No messages found for the selected period."

        val dataToExport = if (minimal) {
            messages.map { 
                mapOf(
                    "sender" to it.sender,
                    "message" to it.message,
                    "timestamp" to it.timestamp
                )
            }
        } else {
            messages
        }

        val json = com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(dataToExport)
        val fileName = if (month != null) {
            "sms_diag_${month}_${if (minimal) "min" else "full"}.json"
        } else {
            "sms_diagnostic_data_${if (minimal) "min" else "full"}.json"
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { os: java.io.OutputStream ->
                        os.write(json.toByteArray())
                    }
                    return@withContext "Diagnostic data ready: Downloads/$fileName"
                }
            } else {
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(downloadsDir, fileName)
                file.writeText(json)
                return@withContext "Diagnostic data ready: ${file.absolutePath}"
            }
        } catch (e: Exception) {
            return@withContext "Export failed: ${e.message}"
        }
        return@withContext "Export failed"
    }

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
        smsProvider.fetchAndAnalyzeAllSms(
            onBatchReady = { dao.insertAnalyzedSMS(it) },
            onProgress = onProgress
        )
    }
}
