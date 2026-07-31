package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryEvidence
import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryScores
import com.example.smartexpensecalendar.new_sms_engine.category.definition.CategoryDefinition
import com.example.smartexpensecalendar.new_sms_engine.category.definition.DefaultCategories
import com.example.smartexpensecalendar.new_sms_engine.category.model.CategoryGroup

abstract class KeywordCategoryRule(
    private val group: CategoryGroup
) : CategoryRule {

    override fun evaluate(
        context: CategoryRuleContext
    ): CategoryCandidate? {

        return DefaultCategories.ALL
            .asSequence()

            .filter {
                it.group == group
            }

            .mapNotNull {
                buildCandidate(
                    definition = it,
                    context = context
                )
            }
            .maxByOrNull {
                it.confidence
            }
    }

    private fun buildCandidate(
        definition: CategoryDefinition,
        context: CategoryRuleContext
    ): CategoryCandidate? {

        return CategoryCandidateBuilder(
            definition.categoryId
        )

            .addPrimary(
                condition = definition.matches(context.message),
                score = CategoryScores.PRIMARY_KEYWORD,
                evidence = CategoryEvidence.PRIMARY_KEYWORD.name
            )

            .addSupporting(
                condition = isDirectionMatched(context),
                score = CategoryScores.DIRECTION,
                evidence = CategoryEvidence.DIRECTION.name
            )

            .build()
    }

    protected abstract fun isDirectionMatched(
        context: CategoryRuleContext
    ): Boolean
}