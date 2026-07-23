package com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models

/**
 * Final amount extraction result.
 */
data class AmountResult(

    /**
     * Selected transaction amount.
     */
    val money: Money?,

    /**
     * Extraction confidence.
     */
    val confidence: Float
)