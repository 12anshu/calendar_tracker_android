package com.example.smartexpensecalendar.new_sms_engine.qualification.models

/**
 * Internal result produced after executing all
 * qualification rules.
 */
data class QualificationEvaluationResult(

    val score: Int,

    val evidence: List<String>,

    val executedRules: List<String> = emptyList()
)
