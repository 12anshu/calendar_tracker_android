package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.DirectionPhrases

object DirectionExtractor {

    // Regex patterns for high-confidence direction detection
    private val debitPatterns = listOf(
        Regex("DEBITED.*FROM", RegexOption.IGNORE_CASE),
        Regex("SPENT.*ON", RegexOption.IGNORE_CASE),
        Regex("SPENT.*AT", RegexOption.IGNORE_CASE),
        Regex("PAID.*TO", RegexOption.IGNORE_CASE),
        Regex("PAID.*VIA", RegexOption.IGNORE_CASE),
        Regex("WITHDRAWN.*FROM", RegexOption.IGNORE_CASE),
        Regex("SENT.*TO", RegexOption.IGNORE_CASE),
        Regex("SENT.*FROM", RegexOption.IGNORE_CASE),
        Regex("WAS.*DEBITED", RegexOption.IGNORE_CASE),
        Regex("TRANSACTION.*ON.*CARD", RegexOption.IGNORE_CASE)
    )

    private val creditPatterns = listOf(
        Regex("CREDITED.*TO", RegexOption.IGNORE_CASE),
        Regex("CREDITED.*INTO", RegexOption.IGNORE_CASE),
        Regex("CREDITED.*IN", RegexOption.IGNORE_CASE),
        Regex("RECEIVED.*IN", RegexOption.IGNORE_CASE),
        Regex("RECEIVED.*ON", RegexOption.IGNORE_CASE),
        Regex("REFUND.*CREDITED", RegexOption.IGNORE_CASE),
        Regex("CASHBACK.*CREDITED", RegexOption.IGNORE_CASE),
        Regex("DEPOSITED.*TO", RegexOption.IGNORE_CASE),
        Regex("DEPOSITED.*IN", RegexOption.IGNORE_CASE),
        Regex("GOT.*FRESH.*FUNDS", RegexOption.IGNORE_CASE)
    )

    fun extractDirection(
        smsText: String
    ): TransactionDirection {

        val text = smsText.uppercase()

        // 1. Regex Pattern Matching (Handles amounts/words in between)
        if (creditPatterns.any { it.containsMatchIn(text) }) {
            return TransactionDirection.CREDIT
        }
        if (debitPatterns.any { it.containsMatchIn(text) }) {
            return TransactionDirection.DEBIT
        }

        // 2. Phrase matching fallback
        val creditPhraseMatches = DirectionPhrases.creditPhrases.count { text.contains(it) }
        val debitPhraseMatches = DirectionPhrases.debitPhrases.count { text.contains(it) }

        if (creditPhraseMatches > 0 && creditPhraseMatches >= debitPhraseMatches) {
            return TransactionDirection.CREDIT
        }
        if (debitPhraseMatches > 0) {
            return TransactionDirection.DEBIT
        }

        // 3. Keyword fallback
        val creditKeywordMatches = DirectionKeywords.creditKeywords.count { text.contains(it) }
        val debitKeywordMatches = DirectionKeywords.debitKeywords.count { text.contains(it) }

        return when {
            creditKeywordMatches > 0 && creditKeywordMatches >= debitKeywordMatches ->
                TransactionDirection.CREDIT
            debitKeywordMatches > 0 ->
                TransactionDirection.DEBIT
            else ->
                TransactionDirection.UNKNOWN
        }
    }
}
