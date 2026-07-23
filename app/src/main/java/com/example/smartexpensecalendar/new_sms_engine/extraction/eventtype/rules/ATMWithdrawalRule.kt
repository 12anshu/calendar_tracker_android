package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.common.scoring.FinancialEventScores
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidateBuilder
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence

class ATMWithdrawalRule : FinancialEventRule {

    override fun evaluate(
        context: FinancialEventRuleContext
    ): FinancialEventCandidate? {

        return FinancialEventCandidateBuilder(
            FinancialEventType.ATM_WITHDRAWAL
        )

            .addPrimary(
                condition = context.containsSignal(
                    ActionSignals.ATM_WITHDRAWAL_ACTION_SIGNALS
                ),
                score = FinancialEventScores.PRIMARY_ACTION,
                evidence = FinancialEventEvidence.ATM_WITHDRAWAL_ACTION
            )

            .addSupporting(
                condition = context.containsSignal(
                    PaymentSignals.CASH_INDICATORS
                ),
                score = FinancialEventScores.SECONDARY_ACTION,
                evidence = FinancialEventEvidence.CASH
            )

            .addSupporting(
                condition = context.direction == Direction.DEBIT,
                score = FinancialEventScores.DIRECTION,
                evidence = FinancialEventEvidence.DIRECTION
            )

            .build()
    }
}