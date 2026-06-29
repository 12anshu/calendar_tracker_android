package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

import com.example.smartexpensecalendar.new_sms_engine.common.regex.SenderRegex
import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * Validates SMS sender format.
 */
class SenderFormatRule : QualificationRule {

    override fun evaluate(
        input: QualificationInput
    ): QualificationRuleResult {

        val sender = input.sender.trim().uppercase()

        if (SenderRegex.STANDARD_SENDER_REGEX.matches(sender)) {

            return QualificationRuleResult(
                score = 40,
                evidences = listOf("VALID_SENDER_FORMAT")
            )
        }

        return QualificationRuleResult(
            score = 0
        )
    }
}