package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryGroup
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction

class TransferCategoryRule: KeywordCategoryRule(
    CategoryGroup.TRANSFER,
) {

    override fun isDirectionMatched(
        context: CategoryRuleContext
    ) = context.direction == Direction.DEBIT
}