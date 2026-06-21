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

        // 3. Patterns from Registry
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
        
        // Dynamic Structural Check (Bank + Instrument + Suffix)
        if (DetectionPatterns.bankStructureRegex.containsMatchIn(text)) {
            val score = DetectionConstants.ACCOUNT_PATTERN_SCORE
            totalScore += score
            matchedSignals.add("BANK_STRUCTURE")
            matchedPatterns.add("BANK_STRUCTURE")
            scoreBreakdown["BANK_STRUCTURE"] = score
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
        
        // --- Contextual Penalties (Registry Driven) ---
        
        // 1. Explicit Reporting/Utility Context
        val isUtilityContext = DetectionPatterns.reportingIdentifiers.any { it.containsMatchIn(text) }
        val hasExplicitBankAnchor = DetectionPatterns.explicitAnchors.any { it.containsMatchIn(text) }
        
        if (isUtilityContext && !hasExplicitBankAnchor) {
            val penalty = DetectionConstants.REPORTING_CONTEXT_PENALTY
            totalScore += penalty
            matchedSignals.add("REPORTING_CONTEXT_PENALTY")
            scoreBreakdown["REPORTING_CONTEXT_PENALTY"] = penalty
        }

        // 2. Failed/Cancelled Transactions
        val isFailed = DetectionPatterns.failureKillSwitches.any { it.containsMatchIn(text) }
        val isRefund = DetectionPatterns.refundOverrides.any { it.containsMatchIn(text) }
        
        if (isFailed && !isRefund) {
            val penalty = DetectionConstants.FAILED_TXN_PENALTY
            totalScore += penalty
            matchedSignals.add("FAILED_TXN_PENALTY")
            scoreBreakdown["FAILED_TXN_PENALTY"] = penalty
        }

        // 3. No Anchor Penalty
        val hasBroadAnchor = DetectionPatterns.broadAnchors.any { text.contains(it) }
        if (!hasBroadAnchor && !hasExplicitBankAnchor) {
            val penalty = DetectionConstants.NO_ANCHOR_PENALTY
            totalScore += penalty
            matchedSignals.add("NO_ANCHOR_PENALTY")
            scoreBreakdown["NO_ANCHOR_PENALTY"] = penalty
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
