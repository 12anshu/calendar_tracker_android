package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

import com.example.smartexpensecalendar.new_sms_engine.common.knowledge.QualificationKnowledge
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * Detects financial regex matches in an SMS.
 */
class FinancialRegexRule : QualificationRule {

    override fun evaluate(
        input: QualificationInput
    ): QualificationRuleResult {

        val message = input.message.uppercase()
        val evidences = mutableListOf<String>()
        var score = 0

        QualificationKnowledge.REGEX_GROUPS.forEach { regex ->
            if (regex.containsMatchIn(message)) {
                score = 50
                evidences.add("FINANCIAL_REGEX")
                return@forEach
            }
        }

        return QualificationRuleResult(score = score, evidences = evidences)
    }
}