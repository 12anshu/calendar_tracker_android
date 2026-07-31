package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryGroup
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction

class IncomeCategoryRule: KeywordCategoryRule(
    CategoryGroup.INCOME
) {

    override fun isDirectionMatched(
        context: CategoryRuleContext
    ) = context.direction == Direction.CREDIT
}