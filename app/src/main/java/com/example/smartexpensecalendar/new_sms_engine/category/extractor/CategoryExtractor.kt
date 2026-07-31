package com.example.smartexpensecalendar.new_sms_engine.category.extractor

import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId
import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryResult
import com.example.smartexpensecalendar.new_sms_engine.category.rules.CategoryRuleContext
import com.example.smartexpensecalendar.new_sms_engine.category.rules.CategoryRuleEngine
import com.example.smartexpensecalendar.new_sms_engine.category.rules.ExpenseCategoryRule
import com.example.smartexpensecalendar.new_sms_engine.category.rules.FallbackCategoryRule
import com.example.smartexpensecalendar.new_sms_engine.category.rules.IncomeCategoryRule
import com.example.smartexpensecalendar.new_sms_engine.category.rules.MerchantCategoryRule
import com.example.smartexpensecalendar.new_sms_engine.category.rules.TransferCategoryRule
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext

class CategoryExtractor {

    private val ruleEngine = CategoryRuleEngine(

        listOf(

            MerchantCategoryRule(),

            ExpenseCategoryRule(),

            IncomeCategoryRule(),

            TransferCategoryRule(),

            FallbackCategoryRule()

        )

    )

    fun extract(
        context: ExtractionContext
    ): CategoryResult {

        val winner = ruleEngine.evaluate(CategoryRuleContext(context))

        return CategoryResult(
            categoryId = winner?.categoryId ?: CategoryId.UNKNOWN,
            confidence = winner?.confidence ?: 0f,
            evidence = winner?.evidence ?: emptyList()
        )
    }
}