package com.example.smartexpensecalendar.new_sms_engine.title.rules

interface TransactionTitleRule {

    fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate?
}