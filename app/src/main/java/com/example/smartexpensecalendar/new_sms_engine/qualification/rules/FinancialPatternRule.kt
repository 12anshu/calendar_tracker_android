package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

import com.example.smartexpensecalendar.new_sms_engine.common.knowledge.QualificationKnowledge
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * Detects financial patterns in an SMS.
 */
class FinancialPatternRule : QualificationRule {

    override fun evaluate(
        input: QualificationInput
    ): QualificationRuleResult {

        val message = input.message.uppercase()
        val evidences = mutableListOf<String>()
        var score = 0

        QualificationKnowledge.PATTERN_GROUPS.forEach { patterns ->
            if (patterns.any(message::contains)) {
                score = 30
                evidences.add("FINANCIAL_PATTERN")
                return@forEach
            }
        }

        return QualificationRuleResult(score = score, evidences = evidences)
    }
}