package com.example.smartexpensecalendar.new_sms_engine.developer.export

data class AnalyzerExportRow(
    val date: String,
    val sender: String,
    val message: String,

    val qualified: Boolean,
    val qualificationReason: String,

    val tokens: String,
    val tokenCategories: String,
    val matchedPatterns: String,
    val evidence: String,

    val messageType: String,

    val amount: String,
    val amountConfidence: String,

    val direction: String,
    val directionConfidence: String,

    val paymentMode: String,
    val paymentModeConfidence: String,

    val merchant: String,
    val merchantSourceSegment: String?,
    val merchantConfidence: String,
    val merchantAnchorUsed: String,

    val category: String?,
    val categoryConfidence: String?,
    val categoryEvidence: String?,

    val account: String,
    val reference: String,
    val messageSegments: String? = null
)
