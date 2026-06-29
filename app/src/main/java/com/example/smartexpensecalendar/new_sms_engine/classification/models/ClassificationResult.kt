package com.example.smartexpensecalendar.new_sms_engine.classification.models

/**
 * Final output of Classification phase.
 */
data class ClassificationResult(

    /**
     * Direction classification.
     */
    val direction: DirectionResult,

    /**
     * Message type classification.
     */
    val messageType: MessageTypeResult,

    /**
     * Financial event classification.
     */
    val financialEvent: FinancialEventResult,

    /**
     * Transaction mode classification.
     */
    val transactionMode: TransactionModeResult
)