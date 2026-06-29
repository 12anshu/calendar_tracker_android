package com.example.smartexpensecalendar.features.developer_tools.data.source

import android.content.Context
import android.provider.Telephony
import com.example.smartexpensecalendar.features.developer_tools.data.entity.AnalyzedSMS
import com.example.smartexpensecalendar.features.developer_tools.engine.SMSPatternGroupingEngine
import com.example.smartexpensecalendar.sms.SMSNormalizer
import com.example.smartexpensecalendar.sms_engine.detector.FinancialDetector
import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.message_type.MessageTypeDetector
import com.example.smartexpensecalendar.sms_engine.extractor.AmountExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.direction.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.AccountNameExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.MerchantExtractor
import com.example.smartexpensecalendar.sms_engine.merchant.NewMerchantExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms_engine.detector.EntityTypeDetector
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import com.example.smartexpensecalendar.sms_engine.model.ExtractionResult
import com.example.smartexpensecalendar.new_sms_engine.qualification.pipeline.QualificationPipeline
import com.example.smartexpensecalendar.new_sms_engine.qualification.sender.SenderQualificationEngine
import com.example.smartexpensecalendar.new_sms_engine.qualification.sender.SenderConfidenceCalculator
import com.example.smartexpensecalendar.new_sms_engine.qualification.message.MessageQualificationEngine
import com.example.smartexpensecalendar.new_sms_engine.qualification.message.MessageQualificationEvaluator
import com.example.smartexpensecalendar.new_sms_engine.qualification.message.MessageConfidenceCalculator
import com.example.smartexpensecalendar.new_sms_engine.qualification.engine.QualificationEngine
import com.example.smartexpensecalendar.new_sms_engine.qualification.rules.*
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsProviderDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categorizer: com.example.smartexpensecalendar.sms.SMSCategorizer
) {
    private val messageTypeDetector = MessageTypeDetector()

    private val qualificationPipeline = QualificationPipeline(
        senderQualifier = SenderQualificationEngine(SenderConfidenceCalculator()),
        messageQualifier = MessageQualificationEngine(
            evaluator = MessageQualificationEvaluator(
                listOf(
                    SenderFormatRule(),
                    FinancialSignalRule(),
                    FinancialPatternRule(),
                    FinancialRegexRule()
                )
            ),
            confidenceCalculator = MessageConfidenceCalculator()
        ),
        engine = QualificationEngine()
    )

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

                // NEW: Qualification Pipeline Execution
                val qualificationInput = QualificationInput(sender = address, message = body)
                val qualificationContext = qualificationPipeline.execute(qualificationInput)
                val qual = qualificationContext.qualification
                
                val template = SMSPatternGroupingEngine.generateTemplate(body)
                
                val messageTypeResult =
                    if (financialResult.isFinancial)
                        messageTypeDetector.detect(normalized)
                    else
                        null

                // Extract Metadata for enriched SMS view
                val amount = AmountExtractor.extractAmount(body)

                val directionResult =
                    if (
                        financialResult.isFinancial &&
                        messageTypeResult?.messageType == MessageType.TRANSACTION
                    ) {
                        DirectionExtractor.extract(body)
                    } else {
                        emptyDirectionResult
                    }

                val direction = directionResult.value ?: TransactionDirection.UNKNOWN
                val directionEvidence = directionResult.evidence.map { evidence ->
                    "${evidence.source}:${evidence.matchedText}"
                }
                val mode = ModeExtractor.extractMode(body)
                val eventType = FinancialEventTypeExtractor.extract(body, direction, mode)
                
                val newMerchantResult = NewMerchantExtractor.extract(body)
                val rawMerchant = newMerchantResult.value ?: MerchantExtractor.extractMerchant(body)
                var normalizedMerchant = rawMerchant?.let { MerchantNormalizer.normalize(it) }
                
                // --- NEW: Source-Aware Merchant Fallback (Aligned with SMSParser) ---
                if (normalizedMerchant.isNullOrBlank()) {
                    val bank = AccountNameExtractor.getFriendlyBankName(address)
                    val suffix = AccountNameExtractor.getSuffix(body)
                    if (bank != null) {
                        normalizedMerchant = if (suffix != null) "$bank [A/C $suffix]" else "$bank Transaction"
                    }
                }

                // Meal Card Merchant Fallback (Aligned with SMSParser)
                if (normalizedMerchant.isNullOrBlank() && mode == com.example.smartexpensecalendar.domain.model.TransactionMode.MEAL_CARD) {
                    normalizedMerchant = when {
                        normalized.contains("PLUXEE") -> "Pluxee Transaction"
                        normalized.contains("SODEXO") -> "Sodexo Transaction"
                        normalized.contains("ZETA") -> "Zeta Transaction"
                        normalized.contains("EDENRED") -> "Edenred Transaction"
                        normalized.contains("SWILE") -> "Swile Transaction"
                        normalized.contains("AXIS") -> "Axis Meal Card"
                        normalized.contains("HDFC") -> "HDFC Food Card"
                        normalized.contains("ICICI") -> "ICICI Food Card"
                        else -> "Meal Card Transaction"
                    }
                }

                val accountName = AccountNameExtractor.extract(body, address)

                val entityType = EntityTypeDetector.detect(
                    merchant = normalizedMerchant,
                    eventType = eventType,
                    paymentMethod = mapToPaymentMethod(mode)
                )

                val category = if (financialResult.isFinancial) {
                    categorizer.categorize(
                        merchant = normalizedMerchant,
                        eventType = eventType,
                        paymentMethod = mapToPaymentMethod(mode)
                    )
                } else null

                analyzedList.add(
                    AnalyzedSMS(
                        id = id,
                        sender = address,
                        message = body,
                        timestamp = date,
                        normalizedMessage = normalized,
                        direction = direction,
                        directionConfidence = directionResult.confidence,
                        directionScore = directionResult.score,
                        directionEvidence = directionEvidence,
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
                        transactionScore = messageTypeResult?.scores?.get(MessageType.TRANSACTION) ?: 0,
                        obligationScore = messageTypeResult?.scores?.get(MessageType.OBLIGATION) ?: 0,
                        informationScore = messageTypeResult?.scores?.get(MessageType.INFORMATION) ?: 0,
                        merchantConfidence = newMerchantResult.confidence,
                        merchantScore = newMerchantResult.score,
                        merchantEvidence = newMerchantResult.evidence.map { "${it.source}:${it.matchedText}" },
                        isQualified = qual.qualified,
                        qualificationScore = qual.score,
                        qualificationConfidence = qual.confidence,
                        qualificationEvidence = qual.sender.evidence + qual.message.evidence,
                        qualificationRules = qual.sender.executedRules + qual.message.executedRules,
                        directionEvidenceList = directionResult.evidence,
                        financialEventType = eventType.name,
                        category = category,
                        amount = amount,
                        merchant = normalizedMerchant,
                        transactionMode = mode.name,
                        accountName = accountName,
                        entityType = entityType.name
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

    private val emptyDirectionResult =
        ExtractionResult(
            value = TransactionDirection.UNKNOWN,
            confidence = 0,
            score = 0,
            evidence = emptyList()
        )

    private fun mapToPaymentMethod(mode: com.example.smartexpensecalendar.domain.model.TransactionMode): com.example.smartexpensecalendar.domain.model.PaymentMethod {
        return when (mode) {
            com.example.smartexpensecalendar.domain.model.TransactionMode.UPI -> com.example.smartexpensecalendar.domain.model.PaymentMethod.UPI
            com.example.smartexpensecalendar.domain.model.TransactionMode.CARD -> com.example.smartexpensecalendar.domain.model.PaymentMethod.CARD
            com.example.smartexpensecalendar.domain.model.TransactionMode.CASH -> com.example.smartexpensecalendar.domain.model.PaymentMethod.CASH
            com.example.smartexpensecalendar.domain.model.TransactionMode.WALLET -> com.example.smartexpensecalendar.domain.model.PaymentMethod.UNKNOWN
            com.example.smartexpensecalendar.domain.model.TransactionMode.BANK_TRANSFER -> com.example.smartexpensecalendar.domain.model.PaymentMethod.NEFT
            com.example.smartexpensecalendar.domain.model.TransactionMode.AUTO_DEBIT -> com.example.smartexpensecalendar.domain.model.PaymentMethod.ACH
            com.example.smartexpensecalendar.domain.model.TransactionMode.EMI -> com.example.smartexpensecalendar.domain.model.PaymentMethod.UNKNOWN
            com.example.smartexpensecalendar.domain.model.TransactionMode.MEAL_CARD -> com.example.smartexpensecalendar.domain.model.PaymentMethod.MEAL_CARD
            com.example.smartexpensecalendar.domain.model.TransactionMode.UNKNOWN -> com.example.smartexpensecalendar.domain.model.PaymentMethod.UNKNOWN
        }
    }
}
