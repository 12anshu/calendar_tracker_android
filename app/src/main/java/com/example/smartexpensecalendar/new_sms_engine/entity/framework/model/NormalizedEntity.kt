package com.example.smartexpensecalendar.new_sms_engine.entity.framework.model

/**
 * Represents the canonical form of a resolved entity.
 */
data class NormalizedEntity(

    /**
     * Resolved entity selected during Resolution.
     */
    val resolvedEntity: ResolvedEntity,

    /**
     * Canonical entity name.
     */
    val canonicalName: String,

    /**
     * Original extracted value.
     */
    val originalName: String,

    /**
     * Indicates whether normalization modified the original value.
     */
    val normalized: Boolean,

    /**
     * Additional normalization metadata.
     */
    val metadata: Map<String, String> = emptyMap()
)