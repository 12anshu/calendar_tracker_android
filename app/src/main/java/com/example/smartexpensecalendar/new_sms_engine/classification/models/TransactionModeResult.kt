package com.example.smartexpensecalendar.new_sms_engine.classification.models

import com.example.smartexpensecalendar.new_sms_engine.common.enums.TransactionMode

/**
 * Result produced by Transaction Mode Detection.
 */
data class TransactionModeResult(

    val mode: TransactionMode,

    val confidence: Int,

    val score: Int,

    val evidence: List<String> = emptyList()
) {

    fun isUpi() = mode == TransactionMode.UPI

    fun isCard() = mode == TransactionMode.CARD

    fun isWallet() = mode == TransactionMode.WALLET

    fun isBankTransfer() = mode == TransactionMode.BANK_TRANSFER

    fun isUnknown() = mode == TransactionMode.UNKNOWN
}