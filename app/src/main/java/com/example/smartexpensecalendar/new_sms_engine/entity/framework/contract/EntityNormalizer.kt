package com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.NormalizedEntity
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.ResolvedEntity

fun interface EntityNormalizer {

    fun normalize(
        context: ExtractionContext,
        resolvedEntity: ResolvedEntity
    ): NormalizedEntity
}