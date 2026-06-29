package com.example.smartexpensecalendar.new_sms_engine.classification.models

import com.example.smartexpensecalendar.new_sms_engine.common.enums.TransactionDirection

/**
 * Result produced by Direction Classification.
 */
data class DirectionResult(

    val direction: TransactionDirection,

    val confidence: Int,

    val score: Int,

    val evidence: List<String> = emptyList(),

    val matches: List<String> = emptyList()
) {

    fun isDebit() = direction == TransactionDirection.DEBIT

    fun isCredit() = direction == TransactionDirection.CREDIT

    fun isUnknown() = direction == TransactionDirection.UNKNOWN
}