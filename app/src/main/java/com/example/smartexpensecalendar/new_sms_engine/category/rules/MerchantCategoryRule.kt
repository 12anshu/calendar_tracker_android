package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryEvidence
import com.example.smartexpensecalendar.new_sms_engine.category.constants.CategoryScores
import com.example.smartexpensecalendar.new_sms_engine.common.merchant.repository.MerchantRepository

class MerchantCategoryRule : CategoryRule {

    override fun evaluate(
        context: CategoryRuleContext
    ): CategoryCandidate? {

        val merchantDefinition = context.merchantDefinition ?: return null

        return CategoryCandidateBuilder(merchantDefinition.category)

            .addPrimary(
                condition = true,
                score = CategoryScores.MERCHANT_MAPPING,
                evidence = "${CategoryEvidence.MERCHANT_MAPPING} (${merchantDefinition.id}})"
            )

            .build()
    }
}