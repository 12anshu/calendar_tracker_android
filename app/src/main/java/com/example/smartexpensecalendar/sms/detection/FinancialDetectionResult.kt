package com.example.smartexpensecalendar.sms.detection

data class FinancialDetectionResult(

    val isFinancial: Boolean,

    val confidence: Int,

    val score: Int,

    val matchedSignals: Set<String>,

    val matchedKeywords: Set<String> = emptySet(),

    val matchedPatterns: Set<String> = emptySet(),

    val negativeSignals: Set<String> = emptySet(),

    val scoreBreakdown: Map<String, Int> = emptyMap()
)