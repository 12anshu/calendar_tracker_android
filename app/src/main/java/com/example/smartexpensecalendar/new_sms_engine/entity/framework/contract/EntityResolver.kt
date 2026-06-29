package com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityAssessment
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.ResolvedEntity

/**
 * Resolves the best entity from assessed candidates.
 */
fun interface EntityResolver {

    fun resolve(
        context: ExtractionContext,
        assessments: List<EntityAssessment>
    ): ResolvedEntity
}