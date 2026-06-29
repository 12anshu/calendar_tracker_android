package com.example.smartexpensecalendar.new_sms_engine.entity.framework.model

/**
 * Final immutable output of the Entity Intelligence Pipeline.
 */
data class EntityResult(

    /**
     * Normalized entity produced by the pipeline.
     */
    val normalizedEntity: NormalizedEntity,

    /**
     * Final confidence (0-100).
     */
    val confidence: Int,

    /**
     * Final score.
     */
    val score: Int,

    /**
     * Discovery method used.
     */
    val discoveryMethod: String,

    /**
     * Assessment summary.
     */
    val assessmentSummary: String,

    /**
     * Resolution summary.
     */
    val resolutionSummary: String,

    /**
     * Normalization summary.
     */
    val normalizationSummary: String,

    /**
     * Additional result metadata.
     */
    val metadata: Map<String, String> = emptyMap()
)