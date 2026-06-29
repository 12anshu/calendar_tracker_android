package com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityAssessment
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

fun interface EntityAssessor {

    fun assess(
        context: ExtractionContext,
        windows: List<EntityWindow>
    ): List<EntityAssessment>
}