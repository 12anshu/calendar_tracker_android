package com.example.smartexpensecalendar.sms_engine.model

data class ExtractionEvidence(
    val source: String,
    val score: Int,
    val matchedText: String,
    val explanation: String
)