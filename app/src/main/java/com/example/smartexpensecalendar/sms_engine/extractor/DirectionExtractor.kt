package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.DirectionPhrases
import com.example.smartexpensecalendar.sms.config.DetectionConstants

object DirectionExtractor {

    /**
     * Extracts direction using proximity-aware phrase matching.
     */
    fun extractDirection(smsText: String): TransactionDirection {
        val text = smsText.uppercase()

        // 1. Check Credit Phrases (Inward)
        if (DirectionPhrases.creditPhrases.any { smartMatch(text, it) }) {
            return TransactionDirection.CREDIT
        }

        // 2. Check Debit Phrases (Outward)
        if (DirectionPhrases.debitPhrases.any { smartMatch(text, it) }) {
            return TransactionDirection.DEBIT
        }

        return TransactionDirection.UNKNOWN
    }

    private fun smartMatch(text: String, phrase: String): Boolean {
        // Use the same proximity logic as MessageTypeDetector
        val escaped = phrase.replace(".", "\\.")
        // Allow proximity on both sides of the currency symbol to consume amounts/accounts
        val regexStr = escaped.replace("{CUR}", ".{0,50}" + DetectionConstants.CURRENCY_SYMBOLS + ".{0,50}")
        return Regex(regexStr, RegexOption.IGNORE_CASE).containsMatchIn(text)
    }
}
