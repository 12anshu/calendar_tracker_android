package com.example.smartexpensecalendar.sms_engine.message_type

import com.example.smartexpensecalendar.domain.model.MessageType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import com.example.smartexpensecalendar.sms_engine.detector.MessageTypeDetectionResult
import com.example.smartexpensecalendar.sms_engine.direction.DirectionPatterns

class MessageTypeDetector {

    /**
     * Determines if the SMS is a real-time transaction, a future obligation (due),
     * or purely informational (balance update).
     */
    fun detect(normalizedText: String): MessageTypeDetectionResult {
        val text = normalizedText.uppercase()

        // =====================================================
        // Semantic Evidence Scoring
        // =====================================================

        val transactionScore =
            countMatches(text, MessageTypePatterns.STRONG_TRANSACTION_SIGNALS) * 100 +
            countMatches(text, MessageTypePatterns.COMPLETED_ACTION_SIGNALS) * 60 +
            countMatches(text, MessageTypePatterns.STATUS_SIGNALS) * 30

        val obligationScore =
            countMatches(text, MessageTypePatterns.FUTURE_SIGNALS) * 80

        val informationScore =
            countMatches(text, MessageTypePatterns.REPORTING_SIGNALS) * 80

        val scoresMap = mapOf(
            MessageType.TRANSACTION to transactionScore,
            MessageType.OBLIGATION to obligationScore,
            MessageType.INFORMATION to informationScore
        )

        // 1. Check for OBLIGATION (Future Debt) - Priority 1
        if (matchesAny(text, MessageTypePatterns.PHRASES_OBLIGATION)) {
            return MessageTypeDetectionResult(MessageType.OBLIGATION, confidence = 90, scores = scoresMap)
        }
        if (obligationScore >= 80) {
            return MessageTypeDetectionResult(
                MessageType.OBLIGATION,
                confidence = 60,
                scores = scoresMap
            )
        }

        // 2. Check for Transaction Signals - Priority 2
        // We prioritize ANY transaction verb (debited/paid) over informational keywords (balance/avl)
        val activeDebit = matchesSemantic(text, DetectionPatterns.AUX_PRESENT, DirectionPatterns.VERBS_DEBIT)
        val activeCredit = matchesSemantic(text, DetectionPatterns.AUX_PRESENT, DirectionPatterns.VERBS_CREDIT)
        val genericDebit = DirectionPatterns.VERBS_DEBIT.any { text.contains(it) }
        val genericCredit = DirectionPatterns.VERBS_CREDIT.any { text.contains(it) }

        if (activeDebit || activeCredit || genericDebit || genericCredit) {
            val direction = when {
                activeDebit || genericDebit -> TransactionDirection.DEBIT
                else -> TransactionDirection.CREDIT
            }
            val confidence = if (activeDebit || activeCredit) 100 else 70
            return MessageTypeDetectionResult(
                MessageType.TRANSACTION,
                confidence = confidence,
                detectedDirection = direction,
                scores = scoresMap
            )
        }

        if (
            transactionScore > obligationScore &&
            transactionScore > informationScore &&
            transactionScore >= 100
        ) {
            return MessageTypeDetectionResult(
                MessageType.TRANSACTION,
                confidence = 60,
                scores = scoresMap
            )
        }

        // 3. Check for Informational (Pure Balance updates) - Priority 3
        // Only if no transaction verbs were found above
        if (matchesAny(text, MessageTypePatterns.PHRASES_INFORMATION)) {
            return MessageTypeDetectionResult(MessageType.INFORMATION, confidence = 80, scores = scoresMap)
        }
        if (informationScore >= 80) {
            return MessageTypeDetectionResult(
                MessageType.INFORMATION,
                confidence = 60,
                scores = scoresMap
            )
        }

        return MessageTypeDetectionResult(MessageType.UNKNOWN, confidence = 0, scores = scoresMap)
    }

    private fun matchesAny(text: String, phrases: List<String>): Boolean {
        return phrases.any { text.contains(it) }
    }

    private fun matchesSemantic(text: String, auxList: List<String>, verbList: List<String>): Boolean {
        val auxPart = auxList.joinToString("|") { Regex.escape(it) }
        val verbPart = verbList.joinToString("|") { Regex.escape(it) }
        // Resilient Match: Allows up to 30 characters (including symbols like *) between verbs.
        val regex = Regex("\\b($auxPart)\\b.{0,30}?\\b($verbPart)\\b")
        return regex.containsMatchIn(text)
    }

    private fun countMatches(text: String, patterns: List<String>): Int {
        return patterns.count { text.contains(it) }
    }
}
