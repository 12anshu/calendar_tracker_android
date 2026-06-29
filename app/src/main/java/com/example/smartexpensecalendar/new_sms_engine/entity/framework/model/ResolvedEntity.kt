package com.example.smartexpensecalendar.new_sms_engine.entity.framework.model

/**
 * Represents the entity selected during the Resolution stage.
 */
data class ResolvedEntity(

    /**
     * Winning assessment.
     */
    val assessment: EntityAssessment,

    /**
     * Resolution method used.
     */
    val resolutionMethod: String,

    /**
     * Reason for selecting this entity.
     */
    val resolutionReason: String,

    /**
     * Additional resolution metadata.
     */
    val metadata: Map<String, String> = emptyMap()
)