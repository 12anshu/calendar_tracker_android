package com.example.smartexpensecalendar.new_sms_engine.classification.financial

import com.example.smartexpensecalendar.new_sms_engine.qualification.context.QualificationContext

fun interface FinancialDetectionRule {

    fun evaluate(
        context: QualificationContext
    ): FinancialRuleResult
}