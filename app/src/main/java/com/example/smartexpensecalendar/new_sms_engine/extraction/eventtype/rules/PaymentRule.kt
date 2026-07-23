package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.common.scoring.FinancialEventScores
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ObligationSignals
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidateBuilder
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence

class PaymentRule : FinancialEventRule {

    override fun evaluate(
        context: FinancialEventRuleContext
    ): FinancialEventCandidate? {

        return FinancialEventCandidateBuilder(
            FinancialEventType.PAYMENT
        )

            .addPrimary(
                condition = context.containsSignal(
                    ActionSignals.DEBIT_ACTION_SIGNALS
                ),
                score = FinancialEventScores.PRIMARY_ACTION,
                evidence = FinancialEventEvidence.PAYMENT_ACTION
            )
            .addSupporting(
                condition = context.direction == Direction.DEBIT,
                score = FinancialEventScores.DIRECTION,
                evidence = FinancialEventEvidence.DIRECTION
            )
            .addSupporting(
                condition = context.containsSignal(
                    ActionSignals.TRANSACTION_ACTION_SIGNALS
                ),
                score = FinancialEventScores.SECONDARY_ACTION,
                evidence = FinancialEventEvidence.OBLIGATION
            )

            .build()
    }
}