package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

import com.example.smartexpensecalendar.new_sms_engine.common.knowledge.QualificationKnowledge
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * Detects financial signals in an SMS.
 */
class FinancialSignalRule : QualificationRule {

    override fun evaluate(
        input: QualificationInput
    ): QualificationRuleResult {

        val message = input.message.uppercase()
        val matchedEvidences = mutableListOf<String>()
        var score = 0

        QualificationKnowledge.SIGNAL_GROUPS.forEach { signals ->
            if (signals.any(message::contains)) {
                score = 20
                matchedEvidences.add("FINANCIAL_SIGNAL")
                return@forEach
            }
        }

        return QualificationRuleResult(score = score, evidences = matchedEvidences)
    }
}