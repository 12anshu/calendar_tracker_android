package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.assessment

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

/**
 * Contract for merchant assessment rules.
 */
fun interface MerchantAssessmentRule {

    fun assess(
        context: ExtractionContext,
        window: EntityWindow
    ): MerchantAssessmentResult
}