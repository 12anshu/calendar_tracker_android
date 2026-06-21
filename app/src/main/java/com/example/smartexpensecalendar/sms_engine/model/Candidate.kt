package com.example.smartexpensecalendar.sms_engine.model

data class Candidate<T>(
    val value: T,
    val evidence: MutableList<ExtractionEvidence> = mutableListOf()
)