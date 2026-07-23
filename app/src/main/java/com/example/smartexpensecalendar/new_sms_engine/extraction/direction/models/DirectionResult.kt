package com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models

/**
 * Final direction extraction result.
 */
data class DirectionResult(

    /**
     * Extracted transaction direction.
     */
    val direction: Direction,

    /**
     * Extraction confidence.
     */
    val confidence: Float
)