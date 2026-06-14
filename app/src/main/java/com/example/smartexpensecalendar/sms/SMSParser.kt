package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.PaymentMethod
import com.example.smartexpensecalendar.domain.model.SenderType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.sms_engine.detector.MessageTypeDetector
import com.example.smartexpensecalendar.sms_engine.extractor.AmountExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.DirectionExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.FinancialEventTypeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.ModeExtractor
import com.example.smartexpensecalendar.sms_engine.extractor.AccountNameExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantExtractor
import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import com.example.smartexpensecalendar.sms.config.MessageTypePhrases
import com.example.smartexpensecalendar.sms_engine.detector.FinancialDetector
import java.util.regex.Pattern

data class ParsedSMS(
    val amount: Double,
    val merchant: String?,
    val isFinancial: Boolean,
    val messageType: MessageType = MessageType.UNKNOWN,
    val paymentMethod: PaymentMethod = PaymentMethod.UNKNOWN,
    val direction: TransactionDirection = TransactionDirection.UNKNOWN,
    val financialEventType: FinancialEventType = FinancialEventType.UNKNOWN,
    val type: TransactionType = TransactionType.DEBIT,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val accountSuffix: String? = null,
    val accountName: String? = null,
    val confidence: Int = 0,
    val senderType: SenderType = SenderType.UNKNOWN
)

object SMSParser {

    private val messageTypeDetector = MessageTypeDetector()

    private val accountSuffixRegex = Pattern.compile(
        "(?i)(?:A/c|Acct|Card|XX|X|No|no\\.?)\\s*\\*?([0-9]{4})",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(body: String): ParsedSMS? {
        // 1. Normalize Whitespace and Currency Symbols
        val normalizedBody = SMSNormalizer.normalize(body)
        val uppercaseBody = normalizedBody.uppercase()

        // --- STAGE 1: Fast-Pass Efficiency ---
        
        // 1.1 OTP Filter (Phrase-based with Exclusion)
        val isOtp = MessageTypePhrases.otpPhrases.any { uppercaseBody.contains(it) }
        val isOtpExclusion = MessageTypePhrases.otpExcludePhrases.any { uppercaseBody.contains(it) }
        if (isOtp && !isOtpExclusion) return null

        // 1.2 Amount Check
        val amount = AmountExtractor.extractAmount(body) ?: return null

        // 1.3 Broad Financial Signal
        val financialResult = FinancialDetector.detect(body)
        
        // --- STAGE 2: Unified Classification (Sequential Phrase Logic) ---
        
        val messageTypeResult = messageTypeDetector.detect(normalizedBody)
        var messageType = messageTypeResult.messageType

        // THE ZERO-MISS DEFAULT RULE:
        // If it's financial domain AND has an amount, but no phrase matched:
        // We promote it to TRANSACTION to ensure visibility.
        if (messageType == MessageType.UNKNOWN && financialResult.isFinancial) {
            messageType = MessageType.TRANSACTION
        }

        // If the detector determines it is NOT financial, we stop parsing.
        if (!financialResult.isFinancial) return null

        // --- STAGE 3: Direction & Event Type ---
        
        // Use classifier direction if found, otherwise fallback to extractor
        val direction = if (messageTypeResult.detectedDirection != TransactionDirection.UNKNOWN) {
            messageTypeResult.detectedDirection
        } else {
            DirectionExtractor.extractDirection(normalizedBody)
        }

        val mode = ModeExtractor.extractMode(body)
        
        // --- DIRECTION TO TYPE MAPPING ---
        // Crucial: Credit always results in TransactionType.CREDIT
        var type = when (direction) {
            TransactionDirection.CREDIT -> TransactionType.CREDIT
            TransactionDirection.DEBIT -> TransactionType.DEBIT
            TransactionDirection.UNKNOWN -> TransactionType.DEBIT // Default to Debit
        }
            
        var status = if (direction == TransactionDirection.UNKNOWN) 
            TransactionStatus.PENDING_REVIEW else TransactionStatus.COMPLETED

        val financialEventType = FinancialEventTypeExtractor.extract(body, direction, mode)
        
        if (financialEventType == FinancialEventType.REFUND) {
            status = TransactionStatus.REFUNDED
            type = TransactionType.CREDIT
        } else if (body.contains("failed", ignoreCase = true)) {
            status = TransactionStatus.FAILED
        }

        // --- STAGE 4: Final Extraction ---
        
        val rawMerchant = MerchantExtractor.extractMerchant(body)
        var merchant = rawMerchant?.let { MerchantNormalizer.normalize(it) }

        // --- ENHANCED MEAL CARD IDENTITY ---
        if (merchant.isNullOrBlank() && mode == TransactionMode.MEAL_CARD) {
            merchant = when {
                uppercaseBody.contains("PLUXEE") -> "Pluxee Transaction"
                uppercaseBody.contains("SODEXO") -> "Sodexo Transaction"
                uppercaseBody.contains("ZETA") -> "Zeta Transaction"
                else -> "Meal Card Transaction"
            }
        }

        val suffixMatcher = accountSuffixRegex.matcher(body)
        var accountSuffix: String? = null
        if (suffixMatcher.find()) {
            accountSuffix = suffixMatcher.group(1)
        }

        val accountName = AccountNameExtractor.extract(body)

        return ParsedSMS(
            amount = amount,
            merchant = merchant,
            isFinancial = true,
            messageType = messageType,
            paymentMethod = mapToPaymentMethod(mode),
            direction = direction,
            financialEventType = financialEventType,
            type = type,
            status = status,
            accountSuffix = accountSuffix,
            accountName = accountName,
            confidence = calculateConfidence(amount, merchant, direction, mode)
        )
    }

    private fun mapToPaymentMethod(mode: TransactionMode): PaymentMethod {
        return when (mode) {
            TransactionMode.UPI -> PaymentMethod.UPI
            TransactionMode.CARD -> PaymentMethod.CARD
            TransactionMode.CASH -> PaymentMethod.CASH
            TransactionMode.WALLET -> PaymentMethod.UNKNOWN
            TransactionMode.BANK_TRANSFER -> PaymentMethod.NEFT
            TransactionMode.AUTO_DEBIT -> PaymentMethod.ACH
            TransactionMode.EMI -> PaymentMethod.UNKNOWN
            TransactionMode.MEAL_CARD -> PaymentMethod.CARD
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
