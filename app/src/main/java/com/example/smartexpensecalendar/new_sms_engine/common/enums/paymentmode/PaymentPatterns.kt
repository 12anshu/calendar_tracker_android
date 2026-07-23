package com.example.smartexpensecalendar.new_sms_engine.common.enums.paymentmode

import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.utils.RegexUtils

object PaymentPatterns {

    val UPI_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.UPI_INDICATORS)})\b"""
    )
    val CARD_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.CARD_INDICATORS)})\b"""
    )

    val BANK_TRANSFER_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.BANK_TRANSFER_INDICATORS)})\b"""
    )

    val WALLET_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.WALLET_INDICATORS)})\b"""
    )

    val AUTO_DEBIT_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.AUTO_DEBIT_INDICATORS)})\b"""
    )

    val MEAL_CARD_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.MEAL_CARD_INDICATORS)})\b"""
    )

    val CASH_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.CASH_INDICATORS)})\b"""
    )

    val CHEQUE_PATTERN = Regex(
        """(?i)\b(?:${RegexUtils.keywordPattern(PaymentSignals.CHEQUE_INDICATORS)})\b"""
    )
    val UPI_ID_PATTERN = Regex(
        """(?i)\b[a-z0-9.\-_]{2,}@[a-z]{2,}\b"""
    )
}