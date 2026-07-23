package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype

import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventResult
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules.FinancialEventRuleEngine

class FinancialEventTypeExtractor(

    private val ruleEngine: FinancialEventRuleEngine = FinancialEventRuleEngine()

) {

    fun extract(
        context: ExtractionContext
    ): FinancialEventResult {

        val candidates = ruleEngine.evaluate(context)

        val winner = candidates
            .maxByOrNull { it.confidence }
            ?: return unknownResult()

        return FinancialEventResult(
            type = winner.type,
            confidence = winner.confidence,
            evidences = winner.evidences
        )
    }

    private fun unknownResult() =
        FinancialEventResult(
            type = FinancialEventType.UNKNOWN,
            confidence = 0f,
            evidences = setOf(
                FinancialEventEvidence.DEFAULT_UNKNOWN
            )
        )
}