package com.example.smartexpensecalendar.new_sms_engine.developer.presentation

data class DashboardSummary(

    val totalSms: Int = 0,

    val qualified: Int = 0,

    val notQualified: Int = 0,

    // Classification Summary
    val transactionCount: Int = 0,
    val obligationCount: Int = 0,
    val informationCount: Int = 0,
    val unknownTypeCount: Int = 0,

    // Evidence Stats
    val evidenceStats: Map<String, Int> = emptyMap(),

    // Pattern Stats
    val patternStats: Map<String, Int> = emptyMap(),

    // Token Category Stats
    val tokenCategoryStats: Map<String, Int> = emptyMap(),

    // Extraction Summary
    val amountFound: Int = 0,
    val amountMissing: Int = 0,
    val merchantFound: Int = 0,
    val merchantMissing: Int = 0,
    val directionFound: Int = 0,
    val directionMissing: Int = 0,
    val modeFound: Int = 0,
    val modeMissing: Int = 0,
    val accountFound: Int = 0,
    val accountMissing: Int = 0,
    val referenceFound: Int = 0,
    val referenceMissing: Int = 0,

    val debit: Int = 0,
    val credit: Int = 0,
    val unknownDirection: Int = 0,

    val paymentMode: Map<String, Int> = emptyMap()
)
