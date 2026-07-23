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
        "SENT",
        "TRANSFERRED",
        "PAID",
        "PURCHASED",
        "WITHDRAWN",
        "WITHDRAWAL",
        "DEDUCTED",
        "CHARGED",
        "SWIPED",
        "USED",
        "PAYMENT",
        "REMITTED",
        "COLLECTED",
        "RECOVERED",
        "SETTLED",
        "LIQUIDATED",
        "REDEEMED",
        "SUBSCRIBED"
    )

    /**
     * General transaction indicators.
     */
    val TRANSACTION_ACTION_SIGNALS = setOf(
        "TRANSACTION",
        "TXN",
        "TRANS",
        "TRN"
    )

    /**
     * Credit related actions.
     */
    val CREDIT_ACTION_SIGNALS = setOf(
        "CREDITED",
        "RECEIVED",
        "DEPOSITED",
        "ADDED",
        "LOADED",
        "DISBURSED",
        "ALLOTTED",
        "ISSUED",
        "POSTED"
    )


    /**
     * Refund related actions.
     */
    val REFUND_ACTION_SIGNALS = setOf(
        "REFUND",
        "REFUNDED",
        "REVERSAL",
        "REVERSED",
        "REIMBURSED"
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
     * ==========================================================
     * Financial Event Signals
     *
     * Business-oriented action groups used by extractors.
     * These are composed from the primitive action signals above.
     * ==========================================================
     */

    val PURCHASE_ACTION_SIGNALS = setOf(
        "SPENT",
        "PURCHASED",
        "SWIPED",
        "USED",
        "DEBITED",
        "TXN"
    )

    val PAYMENT_ACTION_SIGNALS = setOf(
        "PAID",
        "PAYMENT",
        "SETTLED",
        "DEDUCTED"
    )

    val TRANSFER_ACTION_SIGNALS = setOf(
        "TRANSFERRED",
        "REMITTED"
    )

    val ATM_WITHDRAWAL_ACTION_SIGNALS = setOf(
        "WITHDRAWN",
        "WITHDRAWAL"
    )

    val CASHBACK_ACTION_SIGNALS = setOf(
        "CASHBACK"
    )

    val CHARGE_ACTION_SIGNALS = setOf(
        "CHARGED",
        "DEDUCTED",
        "COLLECTED",
        "RECOVERED"
    )
}