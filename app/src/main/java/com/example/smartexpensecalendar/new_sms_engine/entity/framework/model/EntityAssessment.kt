package com.example.smartexpensecalendar.new_sms_engine.entity.framework.model

/**
 * Represents the assessment of a discovered entity window.
 */
data class EntityAssessment(

    /**
     * Window being assessed.
     */
    val window: EntityWindow,

    /**
     * Overall confidence (0-100).
     */
    val confidence: Int,

    /**
     * Overall assessment score.
     */
    val score: Int,

    /**
     * Assessment evidence collected during evaluation.
     */
    val evidence: List<String> = emptyList(),

    /**
     * Additional assessment metadata.
     */
    val metadata: Map<String, String> = emptyMap()
)