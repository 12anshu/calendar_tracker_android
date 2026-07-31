package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryEvidence
import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryScores
import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryId
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction

class FallbackCategoryRule : CategoryRule {

    override fun evaluate(
        context: CategoryRuleContext
    ): CategoryCandidate? {

        return when (context.direction) {

            Direction.DEBIT ->
                CategoryCandidateBuilder(CategoryId.MISC_EXPENSE)
                    .addPrimary(
                        condition = true,
                        score = CategoryScores.FALLBACK,
                        evidence = CategoryEvidence.FALLBACK.name
                    )
                    .build()

            Direction.CREDIT ->
                CategoryCandidateBuilder(CategoryId.MISC_INCOME)
                    .addPrimary(
                        condition = true,
                        score = CategoryScores.FALLBACK,
                        evidence = CategoryEvidence.FALLBACK.name
                    )
                    .build()

            else -> null
        }
    }
}