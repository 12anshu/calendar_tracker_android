package com.example.smartexpensecalendar.new_sms_engine.title.extractor

import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.title.model.TransactionTitleResult
import com.example.smartexpensecalendar.new_sms_engine.title.rules.BankTitleRule
import com.example.smartexpensecalendar.new_sms_engine.title.rules.DefaultTitleRule
import com.example.smartexpensecalendar.new_sms_engine.title.rules.FinancialEventTitleRule
import com.example.smartexpensecalendar.new_sms_engine.title.rules.MerchantTitleRule
import com.example.smartexpensecalendar.new_sms_engine.title.rules.PaymentInstrumentTitleRule
import com.example.smartexpensecalendar.new_sms_engine.title.rules.TransactionTitleRuleContext
import com.example.smartexpensecalendar.new_sms_engine.title.rules.TransactionTitleRuleEngine

class TransactionTitleResolver {

    private val ruleEngine = TransactionTitleRuleEngine(
        rules = listOf(
            MerchantTitleRule(),
            PaymentInstrumentTitleRule(),
            FinancialEventTitleRule(),
            BankTitleRule(),
            DefaultTitleRule()
        )
    )

    fun resolve(
        context: ExtractionContext
    ): TransactionTitleResult? {

        val candidate = ruleEngine.evaluate(
            TransactionTitleRuleContext(context)
        ) ?: return null

        return TransactionTitleResult(
            title = candidate.title,
            confidence = candidate.confidence,
            evidence = candidate.evidence
        )
    }
}