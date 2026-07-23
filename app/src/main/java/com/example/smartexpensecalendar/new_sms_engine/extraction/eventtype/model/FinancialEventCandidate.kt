package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model

import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType

data class FinancialEventCandidate(

    val type: FinancialEventType,

    val confidence: Float,

    val evidences: Set<FinancialEventEvidence> = emptySet()
)