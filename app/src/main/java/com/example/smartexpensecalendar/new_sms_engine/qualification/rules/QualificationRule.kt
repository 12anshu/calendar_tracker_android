package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

import com.example.smartexpensecalendar.new_sms_engine.qualification.models.QualificationInput

/**
 * A single rule used during message qualification.
 */
fun interface QualificationRule {

    fun evaluate(
        input: QualificationInput
    ): QualificationRuleResult
}