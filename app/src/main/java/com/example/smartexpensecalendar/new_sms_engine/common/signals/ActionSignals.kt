package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Action related vocabulary used across the SMS Engine.
 *
 * These signals represent actions performed in a financial
 * transaction and are shared across Qualification,
 * Classification and Entity Intelligence.
 */
object ActionSignals {

    /**
     * Debit related actions.
     */
    val DEBIT_ACTION_SIGNALS = setOf(
        "DEBITED",
        "SPENT",
        "PAID",
        "PURCHASE",
        "PURCHASED",
        "WITHDRAWN",
        "WITHDRAWAL",
        "DEDUCTED",
        "CHARGED",
        "SWIPED",
        "USED"
    )

    /**
     * Credit related actions.
     */
    val CREDIT_ACTION_SIGNALS = setOf(
        "CREDITED",
        "RECEIVED",
        "DEPOSITED",
        "ADDED",
        "LOADED"
    )

    /**
     * Transfer related actions.
     */
    val TRANSFER_ACTION_SIGNALS = setOf(
        "TRANSFER",
        "TRANSFERRED",
        "SENT",
        "REMITTED"
    )

    /**
     * Refund related actions.
     */
    val REFUND_ACTION_SIGNALS = setOf(
        "REFUND",
        "REFUNDED",
        "REVERSAL",
        "REVERSED",
        "CASHBACK"
    )

    /**
     * Reward related actions.
     */
    val REWARD_ACTION_SIGNALS = setOf(
        "REWARD",
        "REWARDED",
        "POINTS",
        "MILES",
        "BONUS"
    )

    /**
     * Income related actions.
     */
    val INCOME_ACTION_SIGNALS = setOf(
        "SALARY",
        "INTEREST",
        "DIVIDEND",
        "PENSION"
    )
}