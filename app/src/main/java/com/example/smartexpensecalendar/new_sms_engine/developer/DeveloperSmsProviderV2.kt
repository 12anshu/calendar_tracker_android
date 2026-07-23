package com.example.smartexpensecalendar.new_sms_engine.developer

import android.content.Context
import android.provider.Telephony
import com.example.smartexpensecalendar.new_sms_engine.classification.ClassificationEngine
import com.example.smartexpensecalendar.new_sms_engine.developer.model.DeveloperSmsResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.AmountExtractor
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.DirectionExtractor
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.PaymentModeExtractor
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.MerchantExtractor
import com.example.smartexpensecalendar.new_sms_engine.qualification.QualificationEngine
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.MessageTypeClassifier
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageType
import com.example.smartexpensecalendar.new_sms_engine.classification.model.MessageTypeResult
import com.example.smartexpensecalendar.new_sms_engine.common.matcher.PatternMatch
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegment
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.MessageSegmentBuilder
import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.SegmentRelation
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.Token
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenNormalizer
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models.AmountResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.DirectionResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentModeResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.merchant.models.MerchantResult
import com.example.smartexpensecalendar.sms_engine.extractor.AccountNameExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeveloperSmsProviderV2 @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val qualificationEngine = QualificationEngine()
    private val amountExtractor = AmountExtractor()
    private val directionExtractor = DirectionExtractor()
    private val paymentModeExtractor = PaymentModeExtractor()
    private val merchantExtractor = MerchantExtractor()
    private val messageTypeClassifier = ClassificationEngine

    private val messageSegment = MessageSegmentBuilder

    private val financialEventTypeExtractor = FinancialEventTypeExtractor()

    suspend fun fetchSms(
        onBatchReady: suspend (List<DeveloperSmsResult>) -> Unit,
        onProgress: (Float) -> Unit
    ) = withContext(Dispatchers.IO) {

        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {

            val idIndex = it.getColumnIndex(Telephony.Sms._ID)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)

            val total = it.count
            val batchSize = 100

            val batch = mutableListOf<DeveloperSmsResult>()

            var processed = 0

            val whitespaceRegex = Regex("\\s+")

            while (it.moveToNext()) {

                val id = it.getLong(idIndex)
                val sender = it.getString(addressIndex) ?: ""
                val body = it.getString(bodyIndex) ?: ""
                val timestamp = it.getLong(dateIndex)

                val qualification = qualificationEngine.qualify(
                    QualificationInput(
                        sender = sender,
                        message = body
                    )
                )

                // Default values for non-qualified messages
                var tokens = emptyList<Token>()
                var messageSegments = emptyList<MessageSegment>()
                var matchedPatterns = emptyList<PatternMatch>()
                var messageTypeResult = MessageTypeResult(
                    messageType = MessageType.UNKNOWN,
                    evidence = emptyList()
                )

                var amountResult: AmountResult? = null
                var directionResult = DirectionResult(
                    direction = Direction.UNKNOWN,
                    confidence = 0f
                )

                var paymentModeResult = PaymentModeResult(
                    mode = PaymentMode.UNKNOWN,
                    confidence = 0f
                )
                
                var merchantResult = MerchantResult(
                    merchant = null,
                    confidence = 0f,
                    anchor = null
                )
                var account: String? = null
                var financialEventResult = FinancialEventResult(
                    type = FinancialEventType.UNKNOWN,
                    confidence = 0f,
                    evidences = emptySet()
                )

                if (qualification.qualified) {

                    val tokenInputs = mutableListOf<Pair<String, Int>>()

                    body.lines().forEachIndexed { lineIndex, line ->

                        val words = line
                            .split(whitespaceRegex)
                            .filter { it.isNotEmpty() }

                        val normalizedWords = TokenNormalizer.normalize(words)

                        normalizedWords.forEach { word ->
                            tokenInputs += word to lineIndex
                        }
                    }

                    tokens = TokenClassifier.classify(tokenInputs)

                    messageSegments = MessageSegmentBuilder.build(tokens)

                    messageTypeResult = messageTypeClassifier.classify(tokens)

                    // Optimization: derive matched patterns from evidence
                    matchedPatterns = messageTypeResult.evidence.map { ev ->
                        PatternMatch(patternName = ev.source, matchedIndices = emptyList())
                    }

                    // Perform Extraction only for TRANSACTION type
                    if (messageTypeResult.messageType == MessageType.TRANSACTION) {
                        val extractionContext = ExtractionContext(
                            message = body,
                            sender = sender,
                            tokens = tokens,
                            segments = messageSegments,
                            messageType = messageTypeResult.messageType
                        )

                        amountResult = amountExtractor.extract(extractionContext)
                        directionResult = directionExtractor.extract(extractionContext)
                        paymentModeResult = paymentModeExtractor.extract(extractionContext)
                        merchantResult = merchantExtractor.extract(extractionContext)
                        account = AccountNameExtractor.extract(body, sender)
                        financialEventResult = financialEventTypeExtractor.extract(extractionContext)
                    }
                }

                batch.add(
                    DeveloperSmsResult(
                        id = id,
                        sender = sender,
                        message = body,
                        timestamp = timestamp,
                        qualified = qualification.qualified,
                        tokens = tokens,
                        matchedPatterns = matchedPatterns,
                        evidence = messageTypeResult.evidence,
                        messageType = messageTypeResult.messageType,
                        amount = amountResult?.money,
                        amountConfidence = amountResult?.confidence ?: 0f,
                        direction = directionResult.direction,
                        directionConfidence = directionResult.confidence,
                        paymentMode = paymentModeResult.mode,
                        paymentModeConfidence = paymentModeResult.confidence,
                        merchant = merchantResult.merchant,
                        merchantSourceSegment = merchantResult.sourceSegment,
                        merchantAnchorUsed = merchantResult.anchor,
                        merchantConfidence = merchantResult.confidence,
                        financialEventType = financialEventResult.type,
                        financialEventConfidence = financialEventResult.confidence,
                        financialEventEvidence = financialEventResult.evidences,
                        account = account,
                        messageSegments = messageSegments
                    )
                )

                processed++

                if (batch.size >= batchSize) {
                    onBatchReady(batch.toList())
                    batch.clear()
                    onProgress(processed.toFloat() / total)
                }
            }

            if (batch.isNotEmpty()) {
                onBatchReady(batch.toList())
            }

            onProgress(1f)
        }
    }
}
