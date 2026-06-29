package com.example.smartexpensecalendar.new_sms_engine.classification.models

import com.example.smartexpensecalendar.new_sms_engine.common.enums.FinancialEventType

/**
 * Result produced by Financial Event Detection.
 */
data class FinancialEventResult(

    val event: FinancialEventType,

    val confidence: Int,

    val score: Int,

    val evidence: List<String> = emptyList()
) {

    fun isExpense() = event == FinancialEventType.EXPENSE

    fun isIncome() = event == FinancialEventType.INCOME

    fun isTransfer() = event == FinancialEventType.TRANSFER

    fun isRefund() = event == FinancialEventType.REFUND

    fun isUnknown() = event == FinancialEventType.UNKNOWN
}