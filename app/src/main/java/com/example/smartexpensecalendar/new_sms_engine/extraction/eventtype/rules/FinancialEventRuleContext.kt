package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.common.matcher.BankingEntityMatcher
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode

data class FinancialEventRuleContext(

    val extractionContext: ExtractionContext,

    val direction: Direction,

    val paymentMode: PaymentMode
) {

    val message: String
        get() = extractionContext.message

    fun containsSignal(
        signals: Set<String>
    ): Boolean {

        return BankingEntityMatcher.containsSignal(
            message,
            signals
        )
    }
}