package com.example.smartexpensecalendar.new_sms_engine.title.rules

class TransactionTitleRuleEngine(

    private val rules: List<TransactionTitleRule>
) {

    fun evaluate(
        context: TransactionTitleRuleContext
    ): TransactionTitleCandidate? {

        // Higher confidence wins.
        // If confidence is equal, the first registered rule wins.
        return rules
            .mapNotNull { it.evaluate(context) }
            .maxByOrNull { it.confidence }
    }
}