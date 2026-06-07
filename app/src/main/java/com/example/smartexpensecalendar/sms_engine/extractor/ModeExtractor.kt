package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms.config.ModePhrases

object ModeExtractor {

    fun extractMode(
        smsText: String
    ): TransactionMode {

        val text = smsText.uppercase()

        val scores = mutableMapOf(
            TransactionMode.UPI to 0,
            TransactionMode.CARD to 0,
            TransactionMode.BANK_TRANSFER to 0,
            TransactionMode.EMI to 0,
            TransactionMode.AUTO_DEBIT to 0,
            TransactionMode.WALLET to 0,
            TransactionMode.CASH to 0
        )

        // Phrases
        scorePhrases(
            text,
            ModePhrases.cardPhrases,
            TransactionMode.CARD,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.upiPhrases,
            TransactionMode.UPI,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.bankTransferPhrases,
            TransactionMode.BANK_TRANSFER,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.emiPhrases,
            TransactionMode.EMI,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.autoDebitPhrases,
            TransactionMode.AUTO_DEBIT,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.walletPhrases,
            TransactionMode.WALLET,
            scores
        )

        scorePhrases(
            text,
            ModePhrases.cashPhrases,
            TransactionMode.CASH,
            scores
        )

        // Keywords
        scoreKeywords(
            text,
            ModeKeywords.cardKeywords,
            TransactionMode.CARD,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.upiKeywords,
            TransactionMode.UPI,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.bankTransferKeywords,
            TransactionMode.BANK_TRANSFER,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.emiKeywords,
            TransactionMode.EMI,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.autoDebitKeywords,
            TransactionMode.AUTO_DEBIT,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.walletKeywords,
            TransactionMode.WALLET,
            scores
        )

        scoreKeywords(
            text,
            ModeKeywords.cashKeywords,
            TransactionMode.CASH,
            scores
        )

        return scores.maxByOrNull { it.value }
            ?.takeIf { it.value > 0 }
            ?.key
            ?: TransactionMode.UNKNOWN
    }

    private fun scorePhrases(
        text: String,
        phrases: Set<String>,
        mode: TransactionMode,
        scores: MutableMap<TransactionMode, Int>
    ) {
        phrases.forEach {
            if (text.contains(it)) {
                scores[mode] = scores.getValue(mode) + 5
            }
        }
    }

    private fun scoreKeywords(
        text: String,
        keywords: Set<String>,
        mode: TransactionMode,
        scores: MutableMap<TransactionMode, Int>
    ) {
        keywords.forEach {
            if (text.contains(it)) {
                scores[mode] = scores.getValue(mode) + 1
            }
        }
    }
}
