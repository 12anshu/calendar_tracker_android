package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Status related vocabulary used across the SMS Engine.
 *
 * These signals describe the outcome or state of a
 * financial operation.
 */
object StatusSignals {

    /**
     * Success indicators.
     */
    val SUCCESS_SIGNALS = setOf(
        "SUCCESS",
        "SUCCESSFUL",
        "COMPLETED",
        "PROCESSED",
        "EXECUTED",
        "APPROVED",
        "CONFIRMED"
    )

    /**
     * Pending indicators.
     */
    val PENDING_SIGNALS = setOf(
        "PENDING",
        "PROCESSING",
        "INITIATED",
        "IN PROGRESS",
        "UNDER PROCESS"
    )

    /**
     * Failure indicators.
     */
    val FAILURE_SIGNALS = setOf(
        "FAILED",
        "DECLINED",
        "REJECTED",
        "UNSUCCESSFUL",
        "CANCELLED",
        "EXPIRED",
        "REVERSED"
    )

    /**
     * Future action indicators.
     */
    val FUTURE_SIGNALS = setOf(
        "DUE",
        "UPCOMING",
        "SCHEDULED",
        "WILL",
        "SHALL"
    )

    /**
     * Informational indicators.
     */
    val INFORMATION_SIGNALS = setOf(
        "STATEMENT",
        "BALANCE",
        "LIMIT",
        "SUMMARY",
        "STATUS",
        "ALERT",
        "NOTIFICATION"
    )
}