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
import com.example.smartexpensecalendar.sms_engine.extractor.MerchantExtractor
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
    val quality: Int = 1,
    val confidence: Int = 0,
    val senderType: SenderType = SenderType.UNKNOWN
)

object SMSParser {

    private val messageTypeDetector = MessageTypeDetector()

    private val accountSuffixRegex = Pattern.compile(
        "(?i)(?:Card|Account|A/c|Acct|Acc|XX|X|No|ending|ending\\sin)\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b",
        Pattern.CASE_INSENSITIVE
    )

    fun parse(body: String, sender: String = ""): ParsedSMS? {
        val normalizedBody = SMSNormalizer.normalize(body)
        val uppercaseBody = normalizedBody.uppercase()

        // 1. Filter out Outward Transfer Confirmations (Status Updates)
        // These say "successfully credited TO [recipient]", but it's our money that was debited.
        // We don't want to count this as new Income (Credit).
        val isOutwardConfirmation = Regex("TO\\s+[A-Za-z0-9 .]+\\s+IS\\s+SUCCESSFULLY\\s+CREDITED").containsMatchIn(uppercaseBody) ||
                                   Regex("TRANSFER\\s+OF\\s+INR\\s+[0-9.]+\\s+TO\\s+[A-Za-z0-9 .]+\\s+IS\\s+SUCCESSFUL").containsMatchIn(uppercaseBody)
        
        if (isOutwardConfirmation) return null

        val isOtp = MessageTypePhrases.otpPhrases.any { uppercaseBody.contains(it) }
        val isOtpExclusion = MessageTypePhrases.otpExcludePhrases.any { uppercaseBody.contains(it) }
        if (isOtp && !isOtpExclusion) return null

        val amount = AmountExtractor.extractAmount(body) ?: return null
        val financialResult = FinancialDetector.detect(body)
        
        val messageTypeResult = messageTypeDetector.detect(normalizedBody)
        var messageType = messageTypeResult.messageType

        if (messageType == MessageType.UNKNOWN && financialResult.isFinancial) {
            messageType = MessageType.TRANSACTION
        }

        if (!financialResult.isFinancial) return null

        val direction = if (messageTypeResult.detectedDirection != TransactionDirection.UNKNOWN) {
            messageTypeResult.detectedDirection
        } else {
            DirectionExtractor.extractDirection(normalizedBody)
        }

        val mode = ModeExtractor.extractMode(body)
        
        var type = when (direction) {
            TransactionDirection.CREDIT -> TransactionType.CREDIT
            TransactionDirection.DEBIT -> TransactionType.DEBIT
            TransactionDirection.UNKNOWN -> TransactionType.DEBIT
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

        val rawMerchant = MerchantExtractor.extractMerchant(body)
        var merchant = rawMerchant?.let { MerchantNormalizer.normalize(it) }

        // --- NEW: Source-Aware Merchant Fallback ---
        if (merchant.isNullOrBlank()) {
            val bank = AccountNameExtractor.getFriendlyBankName(sender)
            val suffix = AccountNameExtractor.getSuffix(body)
            if (bank != null) {
                merchant = if (suffix != null) "$bank [A/C $suffix]" else "$bank Transaction"
            }
        }

        if (merchant.isNullOrBlank() && mode == TransactionMode.MEAL_CARD) {
            merchant = when {
                uppercaseBody.contains("PLUXEE") -> "Pluxee Transaction"
                uppercaseBody.contains("SODEXO") -> "Sodexo Transaction"
                uppercaseBody.contains("ZETA") -> "Zeta Transaction"
                uppercaseBody.contains("EDENRED") -> "Edenred Transaction"
                uppercaseBody.contains("SWILE") -> "Swile Transaction"
                uppercaseBody.contains("AXIS") -> "Axis Meal Card"
                uppercaseBody.contains("HDFC") -> "HDFC Food Card"
                uppercaseBody.contains("ICICI") -> "ICICI Food Card"
                else -> "Meal Card Transaction"
            }
        }

        val suffixMatcher = accountSuffixRegex.matcher(body)
        var accountSuffix: String? = null
        if (suffixMatcher.find()) {
            accountSuffix = suffixMatcher.group(1)
        }

        val accountName = AccountNameExtractor.extract(body, sender)
        val quality = calculateQuality(body)

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
            quality = quality,
            confidence = calculateConfidence(amount, merchant, direction, mode)
        )
    }

    private fun calculateQuality(body: String): Int {
        val upper = body.uppercase()
        var score = 1
        if (Regex("UPI\\s*REF|VPA|PAID\\s*VIA|SUCCESS|TXN\\s*ID|REF#").containsMatchIn(upper)) score = 2
        if (Regex("VALUE\\s*DATE|AVL\\s*BAL|AVAILABLE\\s*BALANCE|A/C\\s*ENDING|CREDITED\\s*TO\\s*YOUR\\s*CARD").containsMatchIn(upper)) score = 3
        return score
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
            TransactionMode.MEAL_CARD -> PaymentMethod.MEAL_CARD
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
