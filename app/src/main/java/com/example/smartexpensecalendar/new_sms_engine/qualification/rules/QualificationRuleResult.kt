package com.example.smartexpensecalendar.new_sms_engine.qualification.rules

/**
 * Result returned by a QualificationRule.
 */
data class QualificationRuleResult(

    val score: Int,

    val evidences: List<String> = emptyList()
)
