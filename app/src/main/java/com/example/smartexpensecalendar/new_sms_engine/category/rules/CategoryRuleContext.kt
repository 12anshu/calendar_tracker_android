package com.example.smartexpensecalendar.new_sms_engine.category.rules

import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode

data class CategoryRuleContext(
    val context: ExtractionContext
) {

    val message
        get() = context.message

    val merchant
        get() = context.merchant

    val direction
        get() = context.direction

    val paymentMode
        get() = context.paymentMode

    val merchantDefinition
        get() = context.merchantDefinition
}