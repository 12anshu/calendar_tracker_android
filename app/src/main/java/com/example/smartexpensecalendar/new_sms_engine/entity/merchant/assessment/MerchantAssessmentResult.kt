package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.assessment

/**
 * Result produced by an individual assessment rule.
 */
data class MerchantAssessmentResult(

    val confidence: Int,

    val score: Int,

    val evidence: List<String> = emptyList(),

    val metadata: Map<String, String> = emptyMap()
)