package com.example.smartexpensecalendar.new_sms_engine.common.knowledge

import com.example.smartexpensecalendar.new_sms_engine.common.patterns.PaymentPatterns
import com.example.smartexpensecalendar.new_sms_engine.common.regex.AmountRegex
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BankingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals

/**
 * Central knowledge used by Qualification.
 *
 * Keeps the QualificationEvaluator independent from
 * individual signal/pattern classes.
 */
object QualificationKnowledge {

    val SIGNAL_GROUPS = listOf(

        ActionSignals.DEBIT_ACTION_SIGNALS,
        ActionSignals.CREDIT_ACTION_SIGNALS,

        PaymentSignals.UPI_INDICATORS,
        PaymentSignals.CARD_INDICATORS,
        PaymentSignals.BANK_TRANSFER_INDICATORS,

        BankingSignals.BANK_INDICATORS,
        BankingSignals.ACCOUNT_INDICATORS,

        StatusSignals.SUCCESS_SIGNALS
    )

    val PATTERN_GROUPS = listOf(

        PaymentPatterns.DEBIT_PATTERNS,
        PaymentPatterns.CREDIT_PATTERNS
    )

    val REGEX_GROUPS = listOf(

        AmountRegex.AMOUNT_REGEX,
        AmountRegex.AMOUNT_WITH_LABEL_REGEX
    )
}