package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.DirectionExtractor
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.PaymentModeExtractor

class FinancialEventRuleEngine(
    private val rules: List<FinancialEventRule> = defaultRules()
) {

    private val directionExtractor = DirectionExtractor()

    private val paymentModeExtractor = PaymentModeExtractor()

    fun evaluate(
        context: ExtractionContext
    ): List<FinancialEventCandidate> {

        val ruleContext = FinancialEventRuleContext(
            extractionContext = context,
            direction = directionExtractor.extract(context).direction,
            paymentMode = paymentModeExtractor.extract(context).mode
        )

        return rules.mapNotNull {
            it.evaluate(ruleContext)
        }
    }

    companion object {

        private fun defaultRules(): List<FinancialEventRule> {

            return listOf(
                PaymentRule(),
                DepositRule(),
                WithdrawalRule()
            )
        }
    }
}