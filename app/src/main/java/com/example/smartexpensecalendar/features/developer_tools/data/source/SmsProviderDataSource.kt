package com.example.smartexpensecalendar.features.developer_tools.data.source

import android.content.Context
import android.provider.Telephony
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.engine.SMSPatternGroupingEngine
import com.example.smartexpensecalendar.sms.SMSNormalizer
import com.example.smartexpensecalendar.sms_engine.detector.FinancialDetector
import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.sms_engine.detector.MessageTypeDetector
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsProviderDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val messageTypeDetector = MessageTypeDetector()

    suspend fun fetchAndAnalyzeAllSms(onBatchReady: suspend (List<AnalyzedSMS>) -> Unit, onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
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

                // Extract Metadata for enriched SMS view
                val direction = DirectionExtractor.extractDirection(body)
                val mode = ModeExtractor.extractMode(body)
                val eventType = FinancialEventTypeExtractor.extract(body, direction, mode)
                val rawMerchant = MerchantExtractor.extractMerchant(body)
                val normalizedMerchant = rawMerchant?.let { MerchantNormalizer.normalize(it) }
                
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
                        messageType = messageTypeResult?.messageType?.name ?: MessageType.UNKNOWN.name,
                        financialEventType = eventType.name,
                        merchant = normalizedMerchant,
                        transactionMode = mode.name
                    )
                )

                processed++
                if (analyzedList.size >= batchSize) {
                    onBatchReady(analyzedList.toList())
                    analyzedList.clear()
                    onProgress(processed.toFloat() / total)
                }
            }
            if (analyzedList.isNotEmpty()) {
                onBatchReady(analyzedList.toList())
                onProgress(1.0f)
            }
        }
    }
}
