package com.example.smartexpensecalendar.new_sms_engine.common.tokenizer

import com.example.smartexpensecalendar.new_sms_engine.common.enums.amount.AmountRegex

/**
 * Performs lexical normalization before semantic classification.
 *
 * Responsibilities:
 * - Split attached currency values
 * - (Future) Split attached account numbers
 * - (Future) Normalize UPI IDs
 *
 * NOTE:
 * This class changes token boundaries.
 * It does NOT assign semantic categories.
 */
object TokenNormalizer {

    /**
     * Normalizes raw whitespace tokens into lexical tokens.
     */
    fun normalize(words: List<String>): List<String> {

        val normalized = mutableListOf<String>()

        words.forEach { word ->

            val split = splitAttachedCurrencyToken(word)

            if (split != null) {
                normalized += split
            } else {
                normalized += word
            }
        }

        return normalized
    }

    /**
     * Splits attached currency tokens.
     *
     * Examples:
     *
     * Rs.294.78 -> [Rs., 294.78]
     * Rs500     -> [Rs, 500]
     * INR500    -> [INR, 500]
     * ₹500      -> [₹, 500]
     */
    private fun splitAttachedCurrencyToken(
        word: String
    ): List<String>? {

        val match =
            AmountRegex.ATTACHED_CURRENCY_TOKEN_REGEX.matchEntire(word)
                ?: return null

        val currency = match.groupValues[1]

        val amount = match.groupValues[2]

        return listOf(currency, amount)
    }
}
