package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.common.utils.displayName
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleScores
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

class FinancialEventTitleRule : TransactionTitleRule {

    override fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate? {

        val eventType = context.financialEventType

//        if (eventType == FinancialEventType.UNKNOWN) {
//            return null
//        }

        return TransactionTitleCandidate(
            title = eventType.displayName(),
            confidence = TitleScores.FINANCIAL_EVENT,
            evidence = listOf(
                TransactionTitleEvidence.FINANCIAL_EVENT
            )
        )
    }
}