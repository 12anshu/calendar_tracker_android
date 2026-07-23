package com.example.smartexpensecalendar.new_sms_engine.classification.patterns

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.AnyWordToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.CategoryToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.OptionalToken
import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.Pattern
import com.example.smartexpensecalendar.new_sms_engine.common.tokenizer.TokenCategory

object ContextPatterns {


    /**
     * Due by
     * Pay before
     * Due on
     */
    val SCHEDULING = Pattern(
        name = "SCHEDULING",
        evidenceType = EvidenceType.OBLIGATION_CONTEXT,
        strength = EvidenceStrength.VERY_HIGH,
        tokens = listOf(

            CategoryToken(
                TokenCategory.TEMPORAL_SCHEDULING
            ),

            AnyWordToken
        )
    )

    val ALL = listOf(
        SCHEDULING
    )
}