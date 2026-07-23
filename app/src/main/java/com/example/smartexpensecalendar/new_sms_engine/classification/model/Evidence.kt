package com.example.smartexpensecalendar.new_sms_engine.classification.model

/**
 * A single semantic clue discovered while analysing an SMS.
 */
data class Evidence(

    val type: EvidenceType,

    val strength: EvidenceStrength,

    /**
     * Builder that produced this evidence.
     *
     * Example:
     * - TenseEvidenceBuilder
     * - StructuralEvidenceBuilder
     * - ContextEvidenceBuilder
     */
    val source: String,

    /**
     * Original matched text.
     */
    val matchedText: String,

    /**
     * Inclusive start index inside message.
     */
    val startIndex: Int = -1,

    /**
     * Inclusive end index inside message.
     */
    val endIndex: Int = -1
)