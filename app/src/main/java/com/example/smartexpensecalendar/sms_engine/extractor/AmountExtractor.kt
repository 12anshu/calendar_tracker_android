package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms.config.DetectionConstants
import java.util.regex.Pattern

object AmountExtractor {

    /**
     * Contextual Amount Extraction Engine.
     * Identifies all amounts in a message and scores them based on proximity to action verbs
     * vs reporting keywords (Balance/Limit).
     */
    fun extractAmount(text: String): Double? {
        val upperText = text.uppercase()
        val candidates = mutableListOf<AmountCandidate>()

        // 1. Find all potential amount matches using the expanded currency list
        // Pattern matches: [Currency] [Amount] or [Amount] [Currency]
        val amountRegex = Pattern.compile(
            "(?:${DetectionConstants.CURRENCY_SYMBOLS}\\s*([0-9,]+(?:\\.[0-9]{1,2})?))|(([0-9,]+(?:\\.[0-9]{1,2})?)\\s*${DetectionConstants.CURRENCY_SYMBOLS})",
            Pattern.CASE_INSENSITIVE
        )
        
        val matcher = amountRegex.matcher(upperText)
        while (matcher.find()) {
            val amountStr = matcher.group(1) ?: matcher.group(2)
            val amount = amountStr?.replace(",", "")?.toDoubleOrNull() ?: continue
            
            // Skip zero or micro amounts to reduce noise
            if (amount <= 0.0) continue

            candidates.add(
                AmountCandidate(
                    value = amount,
                    startIndex = matcher.start(),
                    endIndex = matcher.end()
                )
            )
        }

        if (candidates.isEmpty()) return null
        if (candidates.size == 1) return candidates.first().value

        // 2. Score candidates based on context proximity
        candidates.forEach { candidate ->
            // Look at a window of 40 characters around the amount
            val lookBackStart = (candidate.startIndex - 40).coerceAtLeast(0)
            val contextWindow = upperText.substring(lookBackStart, candidate.endIndex)

            // +100 Bonus for Action Verbs (The transaction intent)
            if (Regex("SPENT|PAID|DEBITED|PURCHASE|CREDITED|RECEIVED|TXN|TRANSFER").containsMatchIn(contextWindow)) {
                candidate.score += 100
            }

            // -200 Penalty for Reporting Context (The balance/limit noise)
            if (Regex("LIMIT|BALANCE|AVL|BAL|OUTSTANDING|TOTAL").containsMatchIn(contextWindow)) {
                candidate.score -= 200
            }
        }

        // 3. The Winner: Return the highest scoring amount
        // If there's a tie, return the first one found.
        return candidates.maxByOrNull { it.score }?.value
    }

    private data class AmountCandidate(
        val value: Double,
        val startIndex: Int,
        val endIndex: Int,
        var score: Int = 0
    )
}
