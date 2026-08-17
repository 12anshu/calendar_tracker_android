package com.example.smartexpensecalendar.new_sms_engine.classification.model

/**
 * Semantic evidence produced by pattern builders.
 *
 * Evidence is consumed by MessageTypeClassifier
 * to determine the final message type.
 */
enum class EvidenceType {

    // ------------------------------------------------------------------------
    // Tense
    // ------------------------------------------------------------------------

    COMPLETED_ACTION,

    NEGATED_ACTION,

    CONDITIONAL_ACTION,

    FUTURE_ACTION,

    ONGOING_ACTION,

    FINANCIAL_COMPLETION,

    // ------------------------------------------------------------------------
    // Message Context
    // ------------------------------------------------------------------------

    TRANSACTION_CONTEXT,

    OBLIGATION_CONTEXT,

    INFORMATION_CONTEXT,

    MAB_ADVISORY_CONTEXT,

    // ------------------------------------------------------------------------
    // Structural Relationships
    // ------------------------------------------------------------------------

    SOURCE_ENTITY,

    DESTINATION_ENTITY,

    MERCHANT_CONTEXT,

    PAYMENT_MODE_CONTEXT,

    ACCOUNT_CONTEXT,

    // ------------------------------------------------------------------------
    // Financial Indicators
    // ------------------------------------------------------------------------

    ACTION_PRESENT,

    AMOUNT_PRESENT,

    ACCOUNT_PRESENT,

    REFERENCE_PRESENT,

    PAYMENT_MODE_PRESENT,

    ACCOUNT_MOVEMENT,
    // ------------------------------------------------------------------------
    // Status Indicators
    // ------------------------------------------------------------------------

    SUCCESS_STATUS,

    FAILURE_STATUS,

    PENDING_STATUS,

    PRE_DISBURSAL_WORKFLOW,

    // ------------------------------------------------------------------------
    // Security & Verification
    // ------------------------------------------------------------------------

    AUTHENTICATION_CONTEXT,
    AUTHORIZATION_CONTEXT,

    NEGATED_AUTHENTICATION_CONTEXT,

}