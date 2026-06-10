package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms.config.EventTypePhrases
import com.example.smartexpensecalendar.sms.config.DetectionConstants

object FinancialEventTypeExtractor {

    fun extract(
        smsText: String,
        direction: TransactionDirection,
        mode: TransactionMode
    ): FinancialEventType {

        val text = smsText.uppercase()

        // 1. HIGH-CONFIDENCE PHRASES (Proximity Aware)
        if (anyMatch(text, EventTypePhrases.refundPhrases)) return FinancialEventType.REFUND
        if (anyMatch(text, EventTypePhrases.creditCardPaymentPhrases)) return FinancialEventType.CREDIT_CARD_PAYMENT
        if (anyMatch(text, EventTypePhrases.emiConversionPhrases)) return FinancialEventType.EMI_CONVERSION
        if (anyMatch(text, EventTypePhrases.emiPhrases)) return FinancialEventType.EMI_PAYMENT
        if (anyMatch(text, EventTypePhrases.transferPhrases)) return FinancialEventType.TRANSFER
        if (anyMatch(text, EventTypePhrases.mealCardPhrases)) return FinancialEventType.MEAL_CARD
        if (anyMatch(text, EventTypePhrases.cashWithdrawalPhrases)) return FinancialEventType.CASH_WITHDRAWAL
        if (anyMatch(text, EventTypePhrases.cashDepositPhrases)) return FinancialEventType.CASH_DEPOSIT
        if (anyMatch(text, EventTypePhrases.investmentPhrases)) return FinancialEventType.INVESTMENT
        if (anyMatch(text, EventTypePhrases.incomePhrases)) return FinancialEventType.INCOME

        // 2. KEYWORD FALLBACK
        if (containsAny(text, EventTypeKeywords.refundKeywords)) return FinancialEventType.REFUND
        if (containsAny(text, EventTypeKeywords.creditCardPaymentKeywords)) return FinancialEventType.CREDIT_CARD_PAYMENT
        if (containsAny(text, EventTypeKeywords.emiKeywords)) return FinancialEventType.EMI_PAYMENT
        if (containsAny(text, EventTypeKeywords.transferKeywords)) return FinancialEventType.TRANSFER
        if (containsAny(text, EventTypeKeywords.cashWithdrawalKeywords)) return FinancialEventType.CASH_WITHDRAWAL

        // 3. LOGICAL INFERENCE
        if (mode == TransactionMode.CARD && direction == TransactionDirection.DEBIT) {
            return FinancialEventType.CREDIT_CARD_SPEND
        }

        return when (direction) {
            TransactionDirection.DEBIT -> FinancialEventType.EXPENSE
            TransactionDirection.CREDIT -> FinancialEventType.INCOME
            else -> FinancialEventType.UNKNOWN
        }
    }

    private fun anyMatch(text: String, phrases: Set<String>): Boolean {
        return phrases.any { smartMatch(text, it) }
    }

    private fun smartMatch(text: String, phrase: String): Boolean {
        val escaped = phrase.replace(".", "\\.")
        val regexStr = escaped.replace("{CUR}", ".{0,50}" + DetectionConstants.CURRENCY_SYMBOLS + ".{0,50}")
        return Regex(regexStr, RegexOption.IGNORE_CASE).containsMatchIn(text)
    }

    private fun containsAny(text: String, keywords: Set<String>): Boolean {
        return keywords.any { text.contains(it) }
    }
}
