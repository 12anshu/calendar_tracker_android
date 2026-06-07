package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.PaymentMethod
import com.example.smartexpensecalendar.domain.model.SenderType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms_engine.detector.FinancialDetector
import com.example.smartexpensecalendar.sms_engine.detector.MessageType
import com.example.smartexpensecalendar.sms_engine.detector.MessageTypeDetector
import com.example.smartexpensecalendar.sms_engine.extractor.AmountExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import java.util.regex.Pattern

data class ParsedSMS(
    val amount: Double,
    val merchant: String?,
    val isFinancial: Boolean,

    val paymentMethod: PaymentMethod = PaymentMethod.UNKNOWN,

    val direction: TransactionDirection =
        TransactionDirection.UNKNOWN,

    val financialEventType: FinancialEventType =
        FinancialEventType.UNKNOWN,

    val type: TransactionType =
        TransactionType.DEBIT,

    val status: TransactionStatus =
        TransactionStatus.COMPLETED,

    val accountSuffix: String? = null,

    val confidence: Int = 0,

    val senderType: SenderType = SenderType.UNKNOWN
)
object SMSParser {

    private val messageTypeDetector = MessageTypeDetector()

    // Regex for Account/Card Suffix (Last 4 digits)
    private val accountSuffixRegex = Pattern.compile(
        "(?i)(?:A/c|Acct|Card|XX|X|No|no\\.?)\\s*\\*?([0-9]{4})",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(body: String): ParsedSMS? {
        val lowercaseBody = body.lowercase()

        // 1. Production Specific Filters (Preserving existing production behavior)
        if (lowercaseBody.contains("6038")) {
            val isKeepTransaction = lowercaseBody.contains("emi") || 
                                   lowercaseBody.contains("cash") || 
                                   lowercaseBody.contains("withdrawal") ||
                                   lowercaseBody.contains("ach")
            if (lowercaseBody.contains("9490") || !isKeepTransaction) return null
        }

        // Hard ignore cases that might bypass the scoring engine
        val isHardIgnore = lowercaseBody.contains("total amount due") || 
                          lowercaseBody.contains("minimum amount due") ||
                          lowercaseBody.contains("will be") ||
                          lowercaseBody.contains("recharge of")
        if (isHardIgnore) return null

        val normalizedBody = SMSNormalizer.normalize(body)
        
        // 2. Detection using shared engine
        val financialResult = FinancialDetector.detect(body)
        if (!financialResult.isFinancial) return null
        
        val messageTypeResult = messageTypeDetector.detect(normalizedBody)
        if (messageTypeResult.messageType != MessageType.TRANSACTION) {
            return null
        }

        // 3. Extraction using shared engine
        val amount = AmountExtractor.extractAmount(body) ?: return null
        val direction = DirectionExtractor.extractDirection(body)
        val mode = ModeExtractor.extractMode(body)
        val rawMerchant = MerchantExtractor.extractMerchant(body)
        val merchant = rawMerchant?.let { MerchantNormalizer.normalize(it) }
        
        val financialEventType = FinancialEventTypeExtractor.extract(
            smsText = body,
            direction = direction,
            mode = mode
        )

        // 4. Status and Type Mapping
        val type = if (direction == TransactionDirection.CREDIT) 
            TransactionType.CREDIT else TransactionType.DEBIT
            
        var status = TransactionStatus.COMPLETED
        if (financialEventType == FinancialEventType.REFUND) {
            status = TransactionStatus.REFUNDED
        } else if (body.contains("failed", ignoreCase = true)) {
            status = TransactionStatus.FAILED
        }

        // 5. Account Suffix
        val suffixMatcher = accountSuffixRegex.matcher(body)
        var accountSuffix: String? = null
        if (suffixMatcher.find()) {
            accountSuffix = suffixMatcher.group(1)
        }

        return ParsedSMS(
            amount = amount,
            merchant = merchant,
            isFinancial = true,
            paymentMethod = mapToPaymentMethod(mode),
            direction = direction,
            financialEventType = financialEventType,
            type = type,
            status = status,
            accountSuffix = accountSuffix,
            confidence = calculateConfidence(amount, merchant, direction, mode)
        )
    }

    private fun mapToPaymentMethod(mode: TransactionMode): PaymentMethod {
        return when (mode) {
            TransactionMode.UPI -> PaymentMethod.UPI
            TransactionMode.CARD -> PaymentMethod.CARD
            TransactionMode.CASH -> PaymentMethod.CASH
            TransactionMode.WALLET -> PaymentMethod.UNKNOWN // Or add WALLET to PaymentMethod
            TransactionMode.BANK_TRANSFER -> PaymentMethod.NEFT // Generic mapping
            TransactionMode.AUTO_DEBIT -> PaymentMethod.ACH
            TransactionMode.EMI -> PaymentMethod.UNKNOWN
            TransactionMode.UNKNOWN -> PaymentMethod.UNKNOWN
        }
    }

    private fun calculateConfidence(
        amount: Double?,
        merchant: String?,
        direction: TransactionDirection,
        mode: TransactionMode
    ): Int {
        var score = 0
        if (amount != null) score += 40
        if (direction != TransactionDirection.UNKNOWN) score += 20
        if (mode != TransactionMode.UNKNOWN) score += 20
        if (!merchant.isNullOrBlank()) score += 20
        return score
    }
}
