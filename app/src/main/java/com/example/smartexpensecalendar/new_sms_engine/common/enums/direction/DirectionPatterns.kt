package com.example.smartexpensecalendar.new_sms_engine.common.enums.direction

import com.example.smartexpensecalendar.new_sms_engine.common.signals.ActionSignals
import com.example.smartexpensecalendar.new_sms_engine.common.signals.StatusSignals
import com.example.smartexpensecalendar.new_sms_engine.common.utils.RegexUtils

object DirectionPatterns {

    val DEBIT_ACTION_PATTERN =
        RegexUtils.keywordPattern(DirectionSignals.DEBIT_ACTIONS)

    val TRANSACTION_ACTION_PATTERN =
        RegexUtils.keywordPattern(DirectionSignals.TRANSACTION_ACTIONS)

    val AUTO_DEBIT_PATTERN =
        RegexUtils.keywordPattern(DirectionSignals.AUTO_DEBIT)

    val CREDIT_ACTION_PATTERN =
        RegexUtils.keywordPattern(DirectionSignals.CREDIT_ACTIONS)

    val REFUND_ACTION_PATTERN =
        RegexUtils.keywordPattern(DirectionSignals.REFUND_ACTIONS)

    val NEGATIVE_PATTERN =
        RegexUtils.keywordPattern(StatusSignals.FAILURE_SIGNALS)
}