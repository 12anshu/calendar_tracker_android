package com.example.smartexpensecalendar.sms.reconciliation.duplicate

import com.example.smartexpensecalendar.domain.model.Expense
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionType
import com.example.smartexpensecalendar.sms.sender.SenderValidationEngine
import kotlin.math.abs

object DuplicateMatcher {

    data class MatchResult(
        val score: Int,
        val decision: DuplicateDecision,
        val reason: String
    )

    fun calculateMatchScore(incoming: Expense, existing: Expense): MatchResult {
        val incomingFingerprint = createFingerprint(incoming)
        val existingFingerprint = createFingerprint(existing)

        var score = 0
        val reasons = mutableListOf<String>()

        // 1. Same Amount: +40
        if (abs(incomingFingerprint.amount - existingFingerprint.amount) < 0.01) {
            score += 40
            reasons.add("SAME_AMOUNT")
        }

        // 2. Same Direction: +20
        if (incomingFingerprint.direction == existingFingerprint.direction && 
            incomingFingerprint.direction != TransactionDirection.UNKNOWN) {
            score += 20
            reasons.add("SAME_DIRECTION")
        }

        // 3. Same Merchant: +20
        if (!incomingFingerprint.merchant.isNullOrBlank() && 
            incomingFingerprint.merchant == existingFingerprint.merchant) {
            score += 20
            reasons.add("SAME_MERCHANT")
        }

        // 4. Same Payment Method: +10
        if (incomingFingerprint.paymentMethod == existingFingerprint.paymentMethod && 
            incomingFingerprint.paymentMethod != com.example.smartexpensecalendar.domain.model.PaymentMethod.UNKNOWN) {
            score += 10
            reasons.add("SAME_METHOD")
        }

        // 5. Same Sender Family: +10
        if (!incomingFingerprint.senderFamily.isNullOrBlank() && 
            incomingFingerprint.senderFamily == existingFingerprint.senderFamily) {
            score += 10
            reasons.add("SAME_SENDER_FAMILY")
        }

        val decision = when {
            score >= 80 -> DuplicateDecision.DUPLICATE
            score >= 60 -> DuplicateDecision.POSSIBLE_DUPLICATE
            else -> DuplicateDecision.NOT_DUPLICATE
        }

        return MatchResult(score, decision, reasons.joinToString("|"))
    }

    private fun createFingerprint(expense: Expense): TransactionFingerprint {
        val direction = when (expense.type) {
            TransactionType.DEBIT -> TransactionDirection.DEBIT
            TransactionType.CREDIT -> TransactionDirection.CREDIT
            else -> TransactionDirection.UNKNOWN
        }
        
        return TransactionFingerprint(
            amount = expense.amount,
            direction = direction,
            merchant = expense.merchant,
            paymentMethod = expense.paymentMethod,
            senderFamily = SenderValidationEngine.resolveFamily(expense.senderId)
        )
    }
}
