package com.example.smartexpensecalendar.new_sms_engine.common.knowledge

import com.example.smartexpensecalendar.new_sms_engine.common.patterns.InformationPatterns
import com.example.smartexpensecalendar.new_sms_engine.common.patterns.ObligationPatterns
import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.BankingSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.FinancialSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals

/**
 * Shared knowledge used for Message Type classification.
 */
object MessageTypeKnowledge {

    /**
     * Signals indicating completed financial transactions.
     */
    val TRANSACTION_SIGNALS = buildSet {

        addAll(ActionSignals.DEBIT_ACTION_SIGNALS)
        addAll(ActionSignals.CREDIT_ACTION_SIGNALS)
        addAll(PaymentSignals.BANK_TRANSFER_SIGNALS)
        addAll(ActionSignals.TRANSACTION_ACTION_SIGNALS)
        addAll(ActionSignals.REWARD_ACTION_SIGNALS)
        addAll(ActionSignals.REFUND_ACTION_SIGNALS)
    }

    /**
     * Signals indicating payment obligations.
     */
    val OBLIGATION_SIGNALS =
        FinancialSignals.OBLIGATION_SIGNALS

    /**
     * Obligation related phrases.
     */
    val OBLIGATION_PATTERNS =
        ObligationPatterns.OBLIGATION_PATTERNS

    /**
     * Signals indicating informational messages.
     */
    val INFORMATION_SIGNALS =
        FinancialSignals.INFORMATION_SIGNALS

    /**
     * Information related phrases.
     */
    val INFORMATION_PATTERNS =
        InformationPatterns.INFORMATION_PATTERNS
}