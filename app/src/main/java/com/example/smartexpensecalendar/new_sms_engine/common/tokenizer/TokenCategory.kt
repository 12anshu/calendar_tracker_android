package com.example.smartexpensecalendar.new_sms_engine.common.tokenizer

/**
 * Semantic categories assigned to individual tokens.
 *
 * TokenClassifier is responsible for assigning these
 * categories exactly once. All downstream components
 * consume these categories instead of raw keywords.
 */
enum class TokenCategory {

    // ------------------------------------------------------------------------
    // Language
    // ------------------------------------------------------------------------

    AUXILIARY_PRESENT,
    AUXILIARY_PAST,
    AUXILIARY_FUTURE,
    AUXILIARY_PARTICIPLE,

    TEMPORAL_RELATIVE,
    TEMPORAL_SCHEDULING,
    NEGATION,
    CONDITIONAL,
    PREPOSITION_SOURCE,
    PREPOSITION_DESTINATION,
    PREPOSITION_LOCATION,
    PREPOSITION_MEDIUM,
    PREPOSITION_PURPOSE,
    PREPOSITION_REFERENCE,

    // ------------------------------------------------------------------------
    // Financial Actions
    // ------------------------------------------------------------------------

    DEBIT_ACTION,
    CREDIT_ACTION,

    TRANSACTION_ACTION,
    REFUND_ACTION,
    REWARD_ACTION,

    // ------------------------------------------------------------------------
    // Financial Lifecycle Actions
    // ------------------------------------------------------------------------

    EMI_CONVERSION_REQUEST,
    EMI_CONVERSION_COMPLETED,
    EMI_CONVERSION_NOUN,

    DISBURSEMENT_TERM,
    DISBURSEMENT_STATE,

    // ------------------------------------------------------------------------
    // Financial Status
    // ------------------------------------------------------------------------

    SUCCESS_STATUS,
    FAILURE_STATUS,
    PENDING_STATUS,

    // ------------------------------------------------------------------------
    // Financial Concepts
    // ------------------------------------------------------------------------

    PAYMENT_MODE,

    MANDATE,
    AUTOPAY,
    BALANCE,
    ACCOUNT_BALANCE,
    MAB_REQUIREMENT,
    CURRENCY,

    BILLING_OBJECT,
    DUE_KEYWORD,
    AMOUNT_DESCRIPTOR,

    // ------------------------------------------------------------------------
    // Negative Financial
    // ------------------------------------------------------------------------
    AUTHENTICATION,
    AUTHORIZATION,
    FAILURE,

    // ------------------------------------------------------------------------
    // Obligation
    // ------------------------------------------------------------------------
    LIABILITY_OBJECT,
    LIABILITY_STATE,
    REQUEST_ACTION,

    REVISION_ACTION,

    // ------------------------------------------------------------------------
    // Information
    // ------------------------------------------------------------------------
    STATEMENT,
    ACCOUNT_UPDATE,
    SECURITY,
    REWARD,
    RETIREMENT,
    INVESTMENT,
    TAX,
    DOCUMENT,
    LOAN_LIFECYCLE,
    PROCESS_STATE,
    // ------------------------------------------------------------------------
    // Generic
    // ------------------------------------------------------------------------

    UNKNOWN
}