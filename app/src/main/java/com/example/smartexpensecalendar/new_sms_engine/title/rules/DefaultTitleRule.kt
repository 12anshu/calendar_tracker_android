package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleDefaults
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleScores
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence

class DefaultTitleRule : TransactionTitleRule {

    override fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate {

        return TransactionTitleCandidate(
            title = TitleDefaults.BANK_TRANSACTION,
            confidence = TitleScores.DEFAULT,
            evidence = listOf(
                TransactionTitleEvidence.DEFAULT
            )
        )
    }
}