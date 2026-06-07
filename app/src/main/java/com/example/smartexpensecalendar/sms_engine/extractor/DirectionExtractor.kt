package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.DirectionPhrases

object DirectionExtractor {

    fun extractDirection(
        smsText: String
    ): TransactionDirection {

        val text =
            smsText.uppercase()

        val creditPhraseMatches =
            DirectionPhrases.creditPhrases.count {
                text.contains(it)
            }

        val debitPhraseMatches =
            DirectionPhrases.debitPhrases.count {
                text.contains(it)
            }

        if (creditPhraseMatches > debitPhraseMatches) {
            return TransactionDirection.CREDIT
        }

        if (debitPhraseMatches > creditPhraseMatches) {
            return TransactionDirection.DEBIT
        }

        // FALLBACK TO KEYWORDS

        val creditKeywordMatches =
            DirectionKeywords.creditKeywords.count {
                text.contains(it)
            }

        val debitKeywordMatches =
            DirectionKeywords.debitKeywords.count {
                text.contains(it)
            }

        return when {
            creditKeywordMatches > debitKeywordMatches ->
                TransactionDirection.CREDIT

            debitKeywordMatches > creditKeywordMatches ->
                TransactionDirection.DEBIT

            else ->
                TransactionDirection.UNKNOWN
        }
    }
}
