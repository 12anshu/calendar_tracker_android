package com.example.smartexpensecalendar.new_sms_engine.common.knowledge

import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.CommonSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.PaymentSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals

/**
 * Shared knowledge used for Transaction Direction classification.
 *
 * This class aggregates relevant vocabulary from multiple
 * signal sources. It does not introduce any new business
 * vocabulary.
 */
object DirectionKnowledge {

    /**
     * Vocabulary indicating money leaving the account.
     */
    val DEBIT_SIGNALS = buildSet {

        addAll(ActionSignals.DEBIT_ACTION_SIGNALS)

        add("DEBIT")
        add("DR")
        add("DR.")

        addAll(PaymentSignals.AUTO_DEBIT_INDICATORS)
    }

    /**
     * Vocabulary indicating money entering the account.
     */
    val CREDIT_SIGNALS = buildSet {

        addAll(ActionSignals.CREDIT_ACTION_SIGNALS)

        add("CREDIT")
        add("CR")
        add("CR.")
    }

    /**
     * Indicates a successful transaction.
     */
    val SUCCESS_SIGNALS = StatusSignals.SUCCESS_SIGNALS

    /**
     * Indicates a failed transaction.
     */
    val FAILURE_SIGNALS = StatusSignals.FAILURE_SIGNALS

    /**
     * Direction anchor words.
     */
    val DIRECTION_ANCHORS =
        CommonSignals.DIRECTION_ANCHOR_SIGNALS
}