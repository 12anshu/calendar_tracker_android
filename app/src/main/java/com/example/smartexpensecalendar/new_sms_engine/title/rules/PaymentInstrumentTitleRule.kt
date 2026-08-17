package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.common.utils.displayName
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleScores
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

class PaymentInstrumentTitleRule : TransactionTitleRule {

    override fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate? {

        val paymentMode = context.paymentMode

        if (paymentMode !in setOf(
                PaymentMode.MEAL_CARD,
                PaymentMode.CREDIT_CARD,
                PaymentMode.DEBIT_CARD,
                PaymentMode.ATM
            )
        ) {
            return null
        }

        return TransactionTitleCandidate(
            title = paymentMode.displayName(),
            confidence = TitleScores.PAYMENT_INSTRUMENT,
            evidence = listOf(
                TransactionTitleEvidence.PAYMENT_INSTRUMENT
            )
        )
    }
}