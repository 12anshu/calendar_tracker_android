package com.example.smartexpensecalendar.new_sms_engine.category.rules


interface CategoryRule {

    fun evaluate(
        context: CategoryRuleContext
    ): CategoryCandidate?
}