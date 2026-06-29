package com.example.smartexpensecalendar.new_sms_engine.context

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityResult

/**
 * Shared context consumed by Business Intelligence.
 */
data class BusinessContext(

    /**
     * Extraction context.
     */
    val extractionContext: ExtractionContext,

    /**
     * Entity results produced by Entity Intelligence.
     */
    val entityResults: List<EntityResult>
)