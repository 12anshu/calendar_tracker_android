package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.rules

import com.example.smartexpensecalendar.new_sms_engine.common.scoring.FinancialEventScores
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidate
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventCandidateBuilder
import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model.FinancialEventEvidence

class DepositRule : FinancialEventRule {

    override fun evaluate(
        context: FinancialEventRuleContext
    ): FinancialEventCandidate? {

        return FinancialEventCandidateBuilder(
            FinancialEventType.DEPOSIT
        )

            .addPrimary(
                condition = context.containsSignal(ActionSignals.CREDIT_ACTION_SIGNALS),
                score = FinancialEventScores.PRIMARY_ACTION,
                evidence = FinancialEventEvidence.CREDIT_ACTION
            )

            .addSupporting(
                condition = context.direction == Direction.CREDIT,
                score = FinancialEventScores.DIRECTION,
                evidence = FinancialEventEvidence.DIRECTION
            )
            .addSupporting(
                condition = context.containsSignal(ActionSignals.REFUND_ACTION_SIGNALS),
                score = FinancialEventScores.SECONDARY_ACTION,
                evidence = FinancialEventEvidence.REFUND_ACTION
            )
            .addSupporting(
                condition = context.containsSignal(ActionSignals.REWARD_ACTION_SIGNALS),
                score = FinancialEventScores.SECONDARY_ACTION,
                evidence = FinancialEventEvidence.REWARD_ACTION
            )
            .build()
    }
}