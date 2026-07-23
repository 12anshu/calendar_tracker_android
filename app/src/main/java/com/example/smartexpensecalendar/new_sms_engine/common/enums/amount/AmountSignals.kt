package com.example.smartexpensecalendar.new_sms_engine.common.enums.amount

import com.example.smartexpensecalendar.new_sms_engine.common.utils.RegexUtils
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BalanceSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BankingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CurrencySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals

object AmountSignals {

    val TRANSACTION_SIGNALS = buildSet {

        addAll(ActionSignals.DEBIT_ACTION_SIGNALS)

        addAll(ActionSignals.CREDIT_ACTION_SIGNALS)

        addAll(ActionSignals.TRANSACTION_ACTION_SIGNALS)

        addAll(PaymentSignals.BANK_TRANSFER_INDICATORS)
    }

    val REPORTING_SIGNALS = buildSet {

        addAll(BalanceSignals.BALANCE_INDICATORS)

        addAll(BalanceSignals.LIMIT_INDICATORS)

        addAll(BalanceSignals.DUE_INDICATORS)

        addAll(BalanceSignals.REWARD_INDICATORS)
    }

    val TRANSACTION_PATTERN =
        RegexUtils.keywordPattern(TRANSACTION_SIGNALS)

    val CURRENCY_PATTERN =
        RegexUtils.keywordPattern(CurrencySignals.CURRENCY_INDICATORS)

    val REPORTING_PATTERN =
        RegexUtils.keywordPattern(REPORTING_SIGNALS)
}