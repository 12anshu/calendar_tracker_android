package com.example.smartexpensecalendar.new_sms_engine.category.rules

class CategoryRuleEngine(
    private val rules: List<CategoryRule>
) {

    fun evaluate(
        context: CategoryRuleContext
    ): CategoryCandidate? {

        return rules.mapNotNull {
            it.evaluate(context)
        }
            .maxByOrNull {
                it.confidence
            }
    }
}