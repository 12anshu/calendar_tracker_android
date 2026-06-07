package com.example.smartexpensecalendar.sms_engine.detector

import com.example.smartexpensecalendar.sms.config.DetectionConstants
import com.example.smartexpensecalendar.sms.config.SMSKeywordRegistry

object FinancialDetector {

    fun detect(
        sms: String
    ): FinancialDetectionResult {

        val text = sms.uppercase()
        val matchedSignals = mutableSetOf<String>()
        val matchedKeywords = mutableSetOf<String>()
        val matchedPatterns = mutableSetOf<String>()
        val negativeSignals = mutableSetOf<String>()
        val scoreBreakdown = mutableMapOf<String, Int>()

        var totalScore = 0

        // 1. Strong Keywords
        val strongKeywordGroups = listOf(
            SMSKeywordRegistry.expenseKeywords,
            SMSKeywordRegistry.incomeKeywords,
            SMSKeywordRegistry.transferKeywords,
            SMSKeywordRegistry.refundKeywords,
            SMSKeywordRegistry.salaryKeywords,
            SMSKeywordRegistry.investmentKeywords,
            SMSKeywordRegistry.interestKeywords,
            SMSKeywordRegistry.cashbackKeywords,
            SMSKeywordRegistry.feeKeywords,
            SMSKeywordRegistry.emiKeywords,
            SMSKeywordRegistry.cardPaymentKeywords
        )

        strongKeywordGroups.forEach { keywords ->
            keywords.forEach { keyword ->
                if (containsKeyword(text, keyword)) {
                    val score = DetectionConstants.STRONG_SIGNAL_SCORE
                    totalScore += score
                    matchedSignals.add(keyword)
                    matchedKeywords.add(keyword)
                    scoreBreakdown[keyword] = (scoreBreakdown[keyword] ?: 0) + score
                }
            }
        }

        // 2. Medium Keywords
        SMSKeywordRegistry.financialSignals.forEach { keyword ->
            if (containsKeyword(text, keyword)) {
                val score = DetectionConstants.MEDIUM_SIGNAL_SCORE
                totalScore += score
                matchedSignals.add(keyword)
                matchedKeywords.add(keyword)
                scoreBreakdown[keyword] = (scoreBreakdown[keyword] ?: 0) + score
            }
        }

        // 3. Patterns
        if (DetectionPatterns.amountRegex.containsMatchIn(text)) {
            val score = DetectionConstants.AMOUNT_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("AMOUNT_PATTERN")
            matchedPatterns.add("AMOUNT_PATTERN")
            scoreBreakdown["AMOUNT_PATTERN"] = score
        }
        if (DetectionPatterns.upiRegex.any{ it.containsMatchIn(text)}) {
            val score = DetectionConstants.UPI_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("UPI_PATTERN")
            matchedPatterns.add("UPI_PATTERN")
            scoreBreakdown["UPI_PATTERN"] = score
        }
        if (DetectionPatterns.accountPatterns.any { it.containsMatchIn(text) }) {
            val score = DetectionConstants.ACCOUNT_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("ACCOUNT_PATTERN")
            matchedPatterns.add("ACCOUNT_PATTERN")
            scoreBreakdown["ACCOUNT_PATTERN"] = score
        }
        if (DetectionPatterns.cardPatterns.any { it.containsMatchIn(text) }) {
            val score = DetectionConstants.CARD_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("CARD_PATTERN")
            matchedPatterns.add("CARD_PATTERN")
            scoreBreakdown["CARD_PATTERN"] = score
        }
        if (DetectionPatterns.balancePatterns.any { it.containsMatchIn(text) }) {
            val score = DetectionConstants.BALANCE_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("BALANCE_PATTERN")
            matchedPatterns.add("BALANCE_PATTERN")
            scoreBreakdown["BALANCE_PATTERN"] = score
        }

        // 4. Negative Signals
        SMSKeywordRegistry.negativeFinancialKeywords.forEach { keyword ->
            if (containsKeyword(text, keyword)) {
                val score = DetectionConstants.NEGATIVE_SIGNAL_SCORE
                totalScore += score
                matchedSignals.add("NEGATIVE:$keyword")
                negativeSignals.add(keyword)
                scoreBreakdown["NEGATIVE:$keyword"] = score
            }
        }

        // 5. Multiple Signal Bonus
        if (matchedSignals.size >= 3) {
            val score = DetectionConstants.MULTIPLE_SIGNAL_BONUS
            totalScore += score
            matchedSignals.add("MULTIPLE_SIGNAL_BONUS")
            scoreBreakdown["MULTIPLE_SIGNAL_BONUS"] = score
        }

        val confidence = totalScore.coerceIn(0, 100)

        return FinancialDetectionResult(
            isFinancial = totalScore >= DetectionConstants.FINANCIAL_THRESHOLD,
            confidence = confidence,
            score = totalScore,
            matchedSignals = matchedSignals,
            matchedKeywords = matchedKeywords,
            matchedPatterns = matchedPatterns,
            negativeSignals = negativeSignals,
            scoreBreakdown = scoreBreakdown
        )
    }

    private fun containsKeyword(
        text: String,
        keyword: String
    ): Boolean {
        return Regex(
            "\\b${Regex.escape(keyword)}\\b"
        ).containsMatchIn(text)
    }
}
