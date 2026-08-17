package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleScores
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

class BankTitleRule : TransactionTitleRule {

    override fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate? {

        val sender = context.sender
            .trim()
            .takeIf { it.isNotBlank() }
            ?: return null

        return TransactionTitleCandidate(
            title = sender,
            confidence = TitleScores.BANK,
            evidence = listOf(
                TransactionTitleEvidence.BANK
            )
        )
    }
}