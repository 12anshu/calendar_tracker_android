package com.example.smartexpensecalendar.new_sms_engine.common.enums.direction

import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals

object DirectionSignals {

    // Debit Signals

    val DEBIT_ACTIONS =
        ActionSignals.DEBIT_ACTION_SIGNALS

    val TRANSACTION_ACTIONS =
        ActionSignals.TRANSACTION_ACTION_SIGNALS

    val AUTO_DEBIT =
        PaymentSignals.AUTO_DEBIT_SIGNALS

    // Credit Signals

    val CREDIT_ACTIONS =
        ActionSignals.CREDIT_ACTION_SIGNALS

    val REFUND_ACTIONS =
        ActionSignals.REFUND_ACTION_SIGNALS

    // Existing groups (keep for compatibility)

    val DEBIT_SIGNALS = buildSet {

        addAll(DEBIT_ACTIONS)
        addAll(TRANSACTION_ACTIONS)
        addAll(AUTO_DEBIT)
    }

    val CREDIT_SIGNALS = buildSet {

        addAll(CREDIT_ACTIONS)
        addAll(REFUND_ACTIONS)
    }
}