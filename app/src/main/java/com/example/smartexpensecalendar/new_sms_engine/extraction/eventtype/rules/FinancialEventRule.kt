package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidate

interface FinancialEventRule {

    fun evaluate(
        context: FinancialEventRuleContext
    ): FinancialEventCandidate?
}