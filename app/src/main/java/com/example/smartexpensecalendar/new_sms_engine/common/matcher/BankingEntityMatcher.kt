package com.example.smartexpensecalendar.new_sms_engine.common.matcher

import com.example.smartexpensecalendar.new_sms_engine.common.enums.amount.AmountRegex
import com.example.smartexpensecalendar.new_sms_engine.common.regex.DateRegex.DATE_PATTERNS
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BalanceSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BankingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CurrencySignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.FinancialSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals.CARD_INDICATORS
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals.SHORT_CARD_INDICATORS
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals

object BankingEntityMatcher {

    fun containsSignal(
        text: String,
        signals: Set<String>
    ): Boolean {

        val upper = text.uppercase()

        return signals.any(upper::contains)
    }

    fun containsAmount(
        text: String
    ): Boolean {

        return (AmountRegex.CURRENCY_PREFIX_REGEX.containsMatchIn(text) ||
                AmountRegex.CURRENCY_SUFFIX_REGEX.containsMatchIn(text))
    }

    fun containsDate(
        text: String
    ): Boolean {

        return DATE_PATTERNS.any {
            it.containsMatchIn(text)
        }
    }

    fun containsAccount(
        text: String
    ): Boolean {

        val upper = text.uppercase()

        return CARD_INDICATORS.any {
            upper.contains(it)
        } ||
                SHORT_CARD_INDICATORS.any {
                    Regex("""(?<![A-Z0-9])${Regex.escape(it)}(?![A-Z0-9])""")
                        .containsMatchIn(upper)
                } ||
                FinancialSignals.INFORMATION_SIGNALS.any {
                    Regex("""(?<![A-Z0-9])${Regex.escape(it)}(?![A-Z0-9])""")
                        .containsMatchIn(upper)
                }
    }

    fun containsBalance(text: String): Boolean {

        val lower = text.lowercase()

        return BalanceSignals.BALANCE_INDICATORS.any {
            lower.contains(it.lowercase())
        } ||
                BalanceSignals.LIMIT_INDICATORS.any {
                    lower.contains(it.lowercase())
                } ||
                BalanceSignals.DUE_INDICATORS.any {
                    lower.contains(it.lowercase())
                }
    }

    fun containsBank(text: String): Boolean {

        val upper = text.uppercase()

        return BankingSignals.BANKS.any { upper.contains(it) } ||
                BankingSignals.BANK_INDICATORS.any { upper.contains(it) }
    }

    fun containsAction(text: String): Boolean {

        val upper = text.uppercase()

        return ActionSignals.DEBIT_ACTION_SIGNALS.any {
            upper.contains(it)
        } ||
                ActionSignals.CREDIT_ACTION_SIGNALS.any {
                    upper.contains(it)
                } ||
                ActionSignals.TRANSACTION_ACTION_SIGNALS.any {
                    upper.contains(it)
                }
    }

    fun containsFailure(text: String) =
        containsSignal(text, StatusSignals.FAILURE_SIGNALS)

    fun containsCurrency(text: String) =
        containsSignal(text, CurrencySignals.CURRENCY_INDICATORS)

    private fun containsAny(
        text: String,
        signals: Set<String>
    ): Boolean {

        val upper = text.uppercase()

        return signals.any(upper::contains)
    }
}