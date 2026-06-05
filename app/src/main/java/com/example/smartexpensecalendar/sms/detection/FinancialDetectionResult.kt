package com.example.smartexpensecalendar.sms.detection

data class FinancialDetectionResult(

    val isFinancial: Boolean,

    val confidence: Int,

    val score: Int,

    val matchedSignals: Set<String>
)