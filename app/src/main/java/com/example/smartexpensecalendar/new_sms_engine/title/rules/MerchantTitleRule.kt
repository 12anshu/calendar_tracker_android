package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.title.constants.TransactionTitleEvidence
import com.example.smartexpensecalendar.new_sms_engine.title.constants.TitleScores

class MerchantTitleRule : TransactionTitleRule {

    override fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate? {

        val merchant = context.merchant
            ?.trim()
            ?.takeIf { it.isNotBlank() }

        return merchant?.let {
            TransactionTitleCandidate(
                title = it,
                confidence = TitleScores.MERCHANT,
                evidence = listOf(
                    TransactionTitleEvidence.MERCHANT
                )
            )
        }
    }
}