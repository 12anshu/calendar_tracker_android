package com.example.smartexpensecalendar.sms_engine.model

data class ExtractionResult<T>(
    val value: T?,
    val confidence: Int,
    val score: Int,
    val evidence: List<ExtractionEvidence>
)