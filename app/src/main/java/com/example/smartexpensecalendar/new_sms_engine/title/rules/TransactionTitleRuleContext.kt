package com.example.smartexpensecalendar.new_sms_engine.title.rules

import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext

class TransactionTitleRuleContext(

    val extractionContext: ExtractionContext

) {

    val merchant: String?
        get() = extractionContext.merchant

    val paymentMode
        get() = extractionContext.paymentMode

    val financialEventType
        get() = extractionContext.financialEventType

    val direction
        get() = extractionContext.direction

    val sender: String
        get() = extractionContext.sender

    val message: String
        get() = extractionContext.message
}