package com.example.smartexpensecalendar.new_sms_engine.entity.framework.contract

import com.example.smartexpensecalendar.new_sms_engine.entity.context.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.entity.framework.model.EntityWindow

fun interface EntityDiscovery {

    fun discover(
        context: ExtractionContext
    ): List<EntityWindow>
}