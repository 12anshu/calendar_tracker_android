package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.assessment

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

/**
 * Evaluates merchant candidates using surrounding transaction context.
 */
class MerchantContextAssessmentRule : MerchantAssessmentRule {

    override fun assess(
        context: ExtractionContext,
        window: EntityWindow
    ): MerchantAssessmentResult {

        var confidence = 0
        var score = 0
        val evidence = mutableListOf<String>()

        when {

            context.direction.isDebit() -> {
                confidence += 25
                score += 50
                evidence.add("DEBIT")
            }

            context.direction.isCredit() -> {
                confidence += 10
                score += 20
                evidence.add("CREDIT")
            }
        }

        if (context.messageType.isTransaction()) {
            confidence += 25
            score += 50
            evidence.add("TRANSACTION")
        }

        if (context.financialEvent.isExpense()) {
            confidence += 20
            score += 40
            evidence.add("EXPENSE")
        }

        if (context.transactionMode.isUpi()) {
            confidence += 10
            score += 20
            evidence.add("UPI")
        }

        if (context.transactionMode.isCard()) {
            confidence += 10
            score += 20
            evidence.add("CARD")
        }

        return MerchantAssessmentResult(
            confidence = confidence.coerceAtMost(100),
            score = score,
            evidence = evidence
        )
    }
}