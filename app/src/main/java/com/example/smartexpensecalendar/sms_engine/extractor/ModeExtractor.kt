package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns

object ModeExtractor {

    fun extractMode(smsText: String): TransactionMode {
        val text = smsText.uppercase()

        val scores = mutableMapOf(
            TransactionMode.UPI to 0,
            TransactionMode.CARD to 0,
            TransactionMode.BANK_TRANSFER to 0,
            TransactionMode.EMI to 0,
            TransactionMode.AUTO_DEBIT to 0,
            TransactionMode.WALLET to 0,
            TransactionMode.CASH to 0,
            TransactionMode.MEAL_CARD to 0
        )

        // 1. UPI Scoring
        scoreWords(text, DetectionPatterns.MODE_UPI, TransactionMode.UPI, scores, 5)
        if (text.contains("@")) scores[TransactionMode.UPI] = scores.getValue(TransactionMode.UPI) + 10

        // 2. Card Scoring
        scoreWords(text, DetectionPatterns.INSTRUMENT_CARD, TransactionMode.CARD, scores, 5)

        // 3. Meal Card Scoring (Higher weight for specific providers)
        scoreWords(text, DetectionPatterns.INSTRUMENT_MEAL, TransactionMode.MEAL_CARD, scores, 15)

        // 4. Bank Transfer Scoring
        scoreWords(text, DetectionPatterns.MODE_BANK_TRANSFER, TransactionMode.BANK_TRANSFER, scores, 5)
        scoreWords(text, DetectionPatterns.INSTRUMENT_ACCOUNT, TransactionMode.BANK_TRANSFER, scores, 2)

        // 5. Auto Debit Scoring
        scoreWords(text, DetectionPatterns.MODE_AUTO_DEBIT, TransactionMode.AUTO_DEBIT, scores, 10)

        // 6. Wallet Scoring
        scoreWords(text, DetectionPatterns.INSTRUMENT_WALLET, TransactionMode.WALLET, scores, 5)

        // 7. Cash Scoring
        scoreWords(text, DetectionPatterns.MODE_CASH, TransactionMode.CASH, scores, 5)

        // 8. EMI Scoring
        if (text.contains("EMI")) scores[TransactionMode.EMI] = scores.getValue(TransactionMode.EMI) + 10

        // Final Decision: Return the highest score, favoring UPI in case of ties
        return scores.maxWithOrNull { a, b ->
            if (a.value == b.value) {
                if (a.key == TransactionMode.UPI) 1 else -1
            } else {
                a.value.compareTo(b.value)
            }
        }?.takeIf { it.value > 0 }?.key ?: TransactionMode.UNKNOWN
    }

    private fun scoreWords(
        text: String,
        words: List<String>,
        mode: TransactionMode,
        scores: MutableMap<TransactionMode, Int>,
        weight: Int
    ) {
        words.forEach { word ->
            if (text.contains(word.uppercase())) {
                scores[mode] = scores.getValue(mode) + weight
            }
        }
    }
}
