package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Vocabulary representing loan disbursement lifecycle language.
 *
 * These signals describe the disbursement process itself.
 * They do NOT imply that money has actually moved.
 */
object DisbursementSignals {

    /**
     * Bank-neutral terminology for the disbursement event/process.
     *
     * Examples:
     * - disbursement
     * - disbursal
     * - disburse
     * - disbursed
     */
    val TERM_SIGNALS = setOf(
        "DISBURSE",
        "DISBURSED",
        "DISBURSAL",
        "DISBURSEMENT",
        "DISBURSEMENTS"
    )

    /**
     * States indicating that disbursement is still part of
     * a workflow rather than an independently completed movement.
     */
    val STATE_SIGNALS = setOf(
        "READY",
        "INITIATED",
        "PENDING",
        "PROCESSING",
        "SCHEDULED",
        "APPROVED"
    )
}