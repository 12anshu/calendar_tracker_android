package com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model

import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceStrength
import com.example.smartexpensecalendar.new_sms_engine.classification.model.EvidenceType

/**
 * Defines a reusable language pattern.
 */
data class Pattern(

    val name: String,

    val evidenceType: EvidenceType,

    val strength: EvidenceStrength,

    val maxGap: Int = 5,

    val tokens: List<PatternToken>
)