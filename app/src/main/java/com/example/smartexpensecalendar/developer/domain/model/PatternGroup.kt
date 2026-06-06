package com.example.smartexpensecalendar.developer.domain.model

data class PatternGroup(
    val template: String,
    val count: Int,
    val sampleMessage: String,
    val financialCount: Int,
    val nonFinancialCount: Int,
    val averageScore: Double
)
