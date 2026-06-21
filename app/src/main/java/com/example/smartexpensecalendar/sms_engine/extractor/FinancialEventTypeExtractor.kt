package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import com.example.smartexpensecalendar.sms_engine.direction.DirectionPatterns

object FinancialEventTypeExtractor {

    fun extract(
        smsText: String,
        direction: TransactionDirection,
        mode: TransactionMode
    ): FinancialEventType {

        val text = smsText.uppercase()

        // 1. HIGH-CONFIDENCE PHRASES from Registry
        if (anyMatch(text, DetectionPatterns.PHRASES_REFUND)) return FinancialEventType.REFUND
        if (anyMatch(text, DetectionPatterns.PHRASES_CC_PAYMENT)) return FinancialEventType.CREDIT_CARD_PAYMENT
        if (anyMatch(text, DetectionPatterns.PHRASES_TRANSFER)) return FinancialEventType.TRANSFER
        if (anyMatch(text, DetectionPatterns.PHRASES_EMI)) return FinancialEventType.EMI_PAYMENT
        if (anyMatch(text, DetectionPatterns.PHRASES_INVESTMENT)) return FinancialEventType.INVESTMENT
        
        // Specific checks for instruments
        if (mode == TransactionMode.MEAL_CARD) return FinancialEventType.MEAL_CARD
        if (mode == TransactionMode.CASH) {
            return if (direction == TransactionDirection.DEBIT) FinancialEventType.CASH_WITHDRAWAL else FinancialEventType.CASH_DEPOSIT
        }

        // 2. KEYWORD FALLBACK (Using Registry Verbs)
        if (DirectionPatterns.VERBS_CREDIT.any { text.contains(it) } &&
            (text.contains("SALARY") || text.contains("PAYROLL"))) return FinancialEventType.SALARY
            
        if (text.contains("CASHBACK")) return FinancialEventType.CASHBACK

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

    private fun anyMatch(text: String, phrases: List<String>): Boolean {
        return phrases.any { smartMatch(text, it) }
    }

    private fun smartMatch(text: String, phrase: String): Boolean {
        val currencyPattern = DetectionConstants.CURRENCY_SYMBOLS
        val marker = "___CUR_PLACEHOLDER___"

        // Use a loose match for currency placeholder as originally intended (allows some padding)
        val replacement = "\\E.{0,50}$currencyPattern.{0,50}\\Q"

        val regexStr = Regex.escape(phrase.replace("{CUR}", marker))
            .replace(marker, replacement)

        return Regex(regexStr, RegexOption.IGNORE_CASE).containsMatchIn(text)
    }
}
