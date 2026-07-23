package com.example.smartexpensecalendar.new_sms_engine.common.signals

object BalanceSignals {

    /**
     * General balance related indicators.
     */
    val BALANCE_INDICATORS = setOf(
        "balance",
        "available balance",
        "avl bal",
        "ledger balance",
        "closing balance",
        "opening balance",
        "current balance",
        "remaining balance",
        "balance amount"
    )

    /**
     * Credit / spending limit indicators.
     */
    val LIMIT_INDICATORS = setOf(
        "credit limit",
        "available limit",
        "remaining limit",
        "cash limit",
        "cash withdrawal limit",
        "limit available",
        "limit utilised",
        "limit used"
    )

    /**
     * Outstanding / due amount indicators.
     */
    val DUE_INDICATORS = setOf(
        "outstanding",
        "minimum due",
        "total due",
        "amount due",
        "due amount",
        "statement balance",
        "payment due",
        "bill due",
        "due on"
    )

    /**
     * Rewards / loyalty related amounts.
     */
    val REWARD_INDICATORS = setOf(
        "reward",
        "reward points",
        "points",
        "cashback earned",
        "reward balance",
        "loyalty points"
    )

    val ALL = setOf(
        BALANCE_INDICATORS,
        LIMIT_INDICATORS,
        DUE_INDICATORS,
        REWARD_INDICATORS
    ).flatten().toSet()
}